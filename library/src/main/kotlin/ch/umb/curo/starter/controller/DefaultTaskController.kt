package ch.umb.curo.starter.controller

import ch.umb.curo.starter.models.FlowToNextResult
import ch.umb.curo.starter.models.request.AssigneeRequest
import ch.umb.curo.starter.models.response.CompleteTaskResponse
import ch.umb.curo.starter.models.response.CuroFilterResponse
import ch.umb.curo.starter.models.response.CuroTask
import ch.umb.curo.starter.service.CuroTaskService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
class DefaultTaskController(private val curoTaskService: CuroTaskService) : TaskController {

    override fun getTasks(
        id: String,
        query: String?,
        attributes: ArrayList<String>,
        variables: List<String>,
        offset: Int,
        maxResult: Int,
        includeFilter: Boolean
    ): CuroFilterResponse {
        return curoTaskService.getTasks(id, query, attributes, variables, offset, maxResult, includeFilter)
    }

    override fun getTasksPost(
        id: String,
        query: String?,
        attributes: ArrayList<String>,
        variables: List<String>,
        offset: Int,
        maxResult: Int,
        includeFilter: Boolean
    ): CuroFilterResponse {
        return curoTaskService.getTasks(id, query, attributes, variables, offset, maxResult, includeFilter)
    }

    override fun getTask(
        id: String,
        attributes: ArrayList<String>,
        variables: List<String>,
        loadFromHistoric: Boolean
    ): CuroTask {
        return curoTaskService.getTask(id, attributes, variables, loadFromHistoric)
    }

    override fun getTaskFile(id: String, file: String): ResponseEntity<ByteArray> {
        return curoTaskService.getTaskFile(id, file)
    }

    override fun getTaskZipFile(
        id: String,
        files: List<String>?,
        name: String,
        ignoreNotExistingFiles: Boolean
    ): ResponseEntity<ByteArray> {
        return curoTaskService.getTaskZipFile(id, files, name, ignoreNotExistingFiles)
    }

    override fun completeTask(
        id: String,
        body: HashMap<String, Any?>?,
        returnVariables: Boolean,
        flowToNext: Boolean,
        flowToNextIgnoreAssignee: Boolean?,
        flowToNextTimeOut: Int?
    ): CompleteTaskResponse {
        return curoTaskService.completeTask(
            id,
            body,
            null,
            returnVariables,
            flowToNext,
            flowToNextIgnoreAssignee,
            flowToNextTimeOut
        )
    }

    override fun completeTaskMultiPart(
        id: String,
        body: String?,
        returnVariables: Boolean,
        flowToNext: Boolean,
        flowToNextIgnoreAssignee: Boolean?,
        flowToNextTimeOut: Int?,
        request: HttpServletRequest
    ): CompleteTaskResponse {

        val parsedObject = body?.let {
            ObjectMapper().readValue(
                body,
                ObjectMapper().typeFactory.constructMapLikeType(
                    HashMap::class.java,
                    String::class.java,
                    Any::class.java
                )
            ) as HashMap<String, Any?>
        }

        val multipartFiles: Map<String, MultipartFile> = (request as MultipartHttpServletRequest).fileMap

        return curoTaskService.completeTask(
            id,
            parsedObject,
            multipartFiles,
            returnVariables,
            flowToNext,
            flowToNextIgnoreAssignee,
            flowToNextTimeOut
        )
    }

    override fun assignTask(id: String, assigneeRequest: AssigneeRequest, response: HttpServletResponse) {
        curoTaskService.assignTask(id, assigneeRequest, response)
    }

    override fun saveVariables(id: String, body: HashMap<String, Any?>, response: HttpServletResponse) {
        curoTaskService.saveVariables(id, body, null, response)
    }

    override fun saveVariablesMultiPart(
        id: String,
        body: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val parsedObject = body?.let {
            ObjectMapper().readValue(
                body,
                ObjectMapper().typeFactory.constructMapLikeType(
                    HashMap::class.java,
                    String::class.java,
                    Any::class.java
                )
            ) as HashMap<String, Any?>
        }

        val multipartFiles: Map<String, MultipartFile> = (request as MultipartHttpServletRequest).fileMap
        curoTaskService.saveVariables(id, parsedObject, multipartFiles, response)
    }

    override fun nextTask(id: String, flowToNextIgnoreAssignee: Boolean?): FlowToNextResult {
        return curoTaskService.nextTask(id, flowToNextIgnoreAssignee)
    }
}
