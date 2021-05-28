package ch.umb.curo.starter.service

import ch.umb.curo.starter.models.FlowToNextResult
import ch.umb.curo.starter.models.request.ProcessStartRequest
import ch.umb.curo.starter.models.response.ProcessStartResponse
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

interface CuroProcessInstanceService {

    fun startProcess(
        body: ProcessStartRequest,
        files: Map<String, MultipartFile>?,
        returnVariables: Boolean,
        flowToNext: Boolean,
        flowToNextIgnoreAssignee: Boolean?,
        flowToNextTimeOut: Int?
    ): ProcessStartResponse

    fun saveVariables(
        id: String,
        body: HashMap<String, Any?>?,
        files: Map<String, MultipartFile>?,
        response: HttpServletResponse
    )

    fun nextTask(id: String, flowToNextIgnoreAssignee: Boolean?): FlowToNextResult

}
