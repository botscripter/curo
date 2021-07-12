package ch.umb.curo.starter.controller

import ch.umb.curo.starter.models.FlowToNextResult
import ch.umb.curo.starter.models.request.ProcessStartRequest
import ch.umb.curo.starter.models.response.ProcessStartResponse
import ch.umb.curo.starter.service.CuroProcessInstanceService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class DefaultProcessInstanceController(val curoProcessInstanceService: CuroProcessInstanceService) :
    ProcessInstanceController {

    override fun startProcess(
        body: ProcessStartRequest,
        returnVariables: Boolean,
        flowToNext: Boolean,
        flowToNextIgnoreAssignee: Boolean?,
        flowToNextTimeOut: Int?
    ): ProcessStartResponse {
        return curoProcessInstanceService.startProcess(
            body,
            null,
            returnVariables,
            flowToNext,
            flowToNextIgnoreAssignee,
            flowToNextTimeOut
        )
    }

    override fun startProcessMultiPart(
        body: String,
        returnVariables: Boolean,
        flowToNext: Boolean,
        flowToNextIgnoreAssignee: Boolean?,
        flowToNextTimeOut: Int?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ProcessStartResponse {
        val parsedObject = body.let {
            ObjectMapper().readValue(
                body,
                ProcessStartRequest::class.java
            ) as ProcessStartRequest
        }

        val multipartFiles: Map<String, MultipartFile> = (request as MultipartHttpServletRequest).fileMap
        return curoProcessInstanceService.startProcess(
            parsedObject,
            multipartFiles,
            returnVariables,
            flowToNext,
            flowToNextIgnoreAssignee,
            flowToNextTimeOut
        )
    }

    override fun saveVariables(id: String, body: HashMap<String, Any?>, response: HttpServletResponse) {
        curoProcessInstanceService.saveVariables(id, body, null, response)
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
        curoProcessInstanceService.saveVariables(id, parsedObject, multipartFiles, response)
    }

    override fun nextTask(id: String, flowToNextIgnoreAssignee: Boolean?): FlowToNextResult {
        return curoProcessInstanceService.nextTask(id, flowToNextIgnoreAssignee)
    }

}
