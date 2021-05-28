package ch.umb.curo.starter.service

import ch.umb.curo.starter.auth.CamundaAuthUtil
import ch.umb.curo.starter.exception.ApiException
import ch.umb.curo.starter.models.FlowToNextResult
import ch.umb.curo.starter.models.request.ProcessStartRequest
import ch.umb.curo.starter.models.response.ProcessStartResponse
import ch.umb.curo.starter.property.CuroProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.camunda.bpm.engine.*
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.variable.VariableMap
import org.camunda.bpm.engine.variable.Variables
import org.camunda.spin.impl.json.jackson.JacksonJsonNode
import org.camunda.spin.plugin.variable.value.impl.JsonValueImpl
import org.springframework.beans.BeanUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

class DefaultCuroProcessInstanceService(
    val properties: CuroProperties,
    val runtimeService: RuntimeService,
    val repositoryService: RepositoryService,
    val flowToNextService: FlowToNextService,
    val identityService: IdentityService
) : CuroProcessInstanceService {
    override fun startProcess(
        body: ProcessStartRequest,
        files: Map<String, MultipartFile>?,
        returnVariables: Boolean,
        flowToNext: Boolean,
        flowToNextIgnoreAssignee: Boolean?,
        flowToNextTimeOut: Int?
    ): ProcessStartResponse {
        try {

            if (repositoryService.createProcessDefinitionQuery().processDefinitionKey(body.processDefinitionKey)
                    .count() == 0L
            ) {
                throw ApiException.curoErrorCode(ApiException.CuroErrorCode.PROCESS_DEFINITION_NOT_FOUND)
                    .printException(properties.printStacktrace)
            }

            val newInstance =
                runtimeService.startProcessInstanceByKey(body.processDefinitionKey, body.businessKey, body.variables)

            //Save files to instance
            files?.filter { it.key != "processStartData" }?.forEach { entry ->
                saveMultiPart(entry, newInstance)
            }

            val response = ProcessStartResponse()
            response.processInstanceId = newInstance.rootProcessInstanceId
            response.businessKey = newInstance.businessKey
            if (returnVariables) {
                val variablesTyped = runtimeService.getVariablesTyped(newInstance.id)
                val variables: HashMap<String, Any?> = hashMapOf()

                variablesTyped.entries.forEach { variable ->
                    if (variable.value is JacksonJsonNode) {
                        variables[variable.key] = ObjectMapper().readValue(
                            (variable.value as JacksonJsonNode).toString(),
                            JsonNode::class.java
                        )
                    } else {
                        variables[variable.key] = variable.value
                    }
                }
                response.variables = variables
            }

            if (flowToNext) {
                val currentUser = identityService.currentAuthentication
                val assignee = if (!(flowToNextIgnoreAssignee
                        ?: properties.flowToNext.ignoreAssignee)
                ) currentUser.userId else null
                val flowToNextResult = CamundaAuthUtil.runWithoutAuthentication({
                    flowToNextService.getNextTask(
                        newInstance.rootProcessInstanceId,
                        assignee,
                        flowToNextTimeOut ?: properties.flowToNext.defaultTimeout
                    )
                }, identityService)
                response.flowToNext = flowToNextResult.flowToNext
                response.flowToEnd = flowToNextResult.flowToEnd
                response.flowToNextTimeoutExceeded = flowToNextResult.flowToNextTimeoutExceeded
            }

            return response
        } catch (e: AuthorizationException) {
            throw ApiException.unauthorized403("You are not allowed to start this process")
                .printException(properties.printStacktrace, e)
        } catch (e: ProcessEngineException) {
            throw ApiException.internal500(e.localizedMessage, e)
                .printException(properties.printStacktrace, e)
        }
    }

    override fun saveVariables(
        id: String,
        body: HashMap<String, Any?>?,
        files: Map<String, MultipartFile>?,
        response: HttpServletResponse
    ) {
        val instance = getInstance(id)

        //Save variables
        val instanceVariables = runtimeService.getVariablesTyped(instance.processInstanceId)
        val objectVariables =
            instanceVariables.filter { it.value != null && !BeanUtils.isSimpleValueType(it.value::class.java) }
        val objectVariablesNames = objectVariables.map { it.key }

        body?.entries?.forEach { entry ->
            saveSingleVariable(entry.key, entry.value, objectVariablesNames, instanceVariables, instance)
        }

        files?.filter { it.key != "variables" }?.forEach { entry ->
            saveMultiPart(entry, instance)
        }

        response.status = HttpStatus.OK.value()
    }

    override fun nextTask(id: String, flowToNextIgnoreAssignee: Boolean?): FlowToNextResult {
        val currentUser = identityService.currentAuthentication
        val assignee =
            if (!(flowToNextIgnoreAssignee ?: properties.flowToNext.ignoreAssignee)) currentUser.userId else null
        return CamundaAuthUtil.runWithoutAuthentication(
            { flowToNextService.searchNextTask(id, assignee) },
            identityService
        )
    }

    private fun saveMultiPart(entry: Map.Entry<String, MultipartFile>, instance: ProcessInstance) {
        val file = entry.value
        val typedValue = if (file.originalFilename?.isNotEmpty() == true) {
            Variables.fileValue(file.originalFilename)
                .file(file.inputStream)
                .mimeType(file.contentType)
                .encoding("UTF-8")
                .create()
        } else {
            Variables.byteArrayValue(file.bytes)
        }

        runtimeService.setVariable(instance.processInstanceId, entry.key, typedValue)
    }

    private fun saveSingleVariable(
        name: String,
        value: Any?,
        objectVariablesNames: List<String>,
        instanceVariables: VariableMap,
        instance: ProcessInstance
    ) {
        if (name in objectVariablesNames) {
            try {
                if (instanceVariables[name]!! is JacksonJsonNode) {
                    runtimeService.setVariable(
                        instance.processInstanceId,
                        name,
                        JsonValueImpl(ObjectMapper().writeValueAsString(value), MediaType.APPLICATION_JSON_VALUE)
                    )
                } else {
                    val obj = ObjectMapper().convertValue(value, instanceVariables[name]!!::class.java)
                    runtimeService.setVariable(instance.processInstanceId, name, obj)
                }
            } catch (e: Exception) {
                when (e) {
                    is InvalidDefinitionException,
                    is UnrecognizedPropertyException -> {
                        saveIfIgnoreObjectType(instance, name, value, e)
                    }
                    else -> {
                        throw ApiException.curoErrorCode(ApiException.CuroErrorCode.CANT_SAVE_IN_EXISTING_OBJECT)
                            .printException(properties.printStacktrace, e)
                    }
                }
            }
        } else {
            if (value != null && !BeanUtils.isSimpleValueType(value::class.java)) {
                try {
                    runtimeService.setVariable(
                        instance.processInstanceId,
                        name,
                        JsonValueImpl(ObjectMapper().writeValueAsString(value), MediaType.APPLICATION_JSON_VALUE)
                    )
                } catch (e: UnrecognizedPropertyException) {
                    saveIfIgnoreObjectType(instance, name, value, e)
                } catch (e: Exception) {
                    throw ApiException.curoErrorCode(ApiException.CuroErrorCode.CANT_SAVE_IN_EXISTING_OBJECT)
                        .printException(properties.printStacktrace, e)
                }
            } else {
                runtimeService.setVariable(instance.processInstanceId, name, value)
            }
        }
    }

    private fun saveIfIgnoreObjectType(
        instance: ProcessInstance,
        name: String,
        value: Any?,
        e: Exception
    ) {
        if (properties.ignoreObjectType) {
            runtimeService.setVariable(
                instance.processInstanceId,
                name,
                JsonValueImpl(ObjectMapper().writeValueAsString(value), MediaType.APPLICATION_JSON_VALUE)
            )
        } else {
            throw ApiException.curoErrorCode(ApiException.CuroErrorCode.CANT_SAVE_IN_EXISTING_OBJECT)
                .printException(properties.printStacktrace, e)
        }
    }

    /**
     * Get process instance and trow ApiException if the task does not exist
     *
     * @param id id of the instance
     * @return task
     * @throws ApiException if instance not found
     */
    @Throws(ApiException::class)
    private fun getInstance(id: String) =
        (runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult()
            ?: throw ApiException.curoErrorCode(ApiException.CuroErrorCode.PROCESS_INSTANCE_NOT_FOUND)
                .printException(properties.printStacktrace))
}
