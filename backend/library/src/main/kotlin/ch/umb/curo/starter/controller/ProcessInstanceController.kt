package ch.umb.curo.starter.controller

import ch.umb.curo.starter.models.FlowToNextResult
import ch.umb.curo.starter.models.request.ProcessStartRequest
import ch.umb.curo.starter.models.response.ProcessStartResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Tag(name = "process-instance", description = "Curo Process Instance API")
@RequestMapping("/curo-api/process-instances")
interface ProcessInstanceController {

    @Operation(
        summary = "Start new process instance",
        operationId = "startProcess",
        description = "",
        security = [SecurityRequirement(name = "CuroBasic")]
    )
    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun startProcess(
        @Parameter(description = "Process instance start model", required = false)
        @RequestBody
        body: ProcessStartRequest,

        @Parameter(description = "Define if variables should be returned on success", required = false)
        @RequestParam("returnVariables", required = false, defaultValue = "false")
        returnVariables: Boolean = false,

        @Parameter(description = "Define if flowToNext should be returned on success.", required = false)
        @RequestParam("flowToNext", required = false, defaultValue = "false")
        flowToNext: Boolean = false,

        @Parameter(description = "Define if flowToNext should ignore the first task assignee.", required = false)
        @RequestParam("flowToNextIgnoreAssignee", required = false)
        flowToNextIgnoreAssignee: Boolean? = null,

        @Parameter(description = "Define how long in seconds flowToNext should wait.", required = false)
        @RequestParam("flowToNextTimeOut", required = false)
        flowToNextTimeOut: Int? = null
    ): ProcessStartResponse?

    @Operation(
        summary = "Start new process instance (from multipart)",
        operationId = "startProcessMultiPart",
        description = "",
        security = [SecurityRequirement(name = "CuroBasic")]
    )
    @PostMapping("", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun startProcessMultiPart(
        @Parameter(description = "Process instance start model", required = true)
        @RequestPart("processStartData")
        body: String,

        @Parameter(description = "Define if variables should be returned on success", required = false)
        @RequestParam("returnVariables", required = false, defaultValue = "false")
        returnVariables: Boolean = false,

        @Parameter(description = "Define if flowToNext should be returned on success.", required = false)
        @RequestParam("flowToNext", required = false, defaultValue = "false")
        flowToNext: Boolean = false,

        @Parameter(description = "Define if flowToNext should ignore the first task assignee.", required = false)
        @RequestParam("flowToNextIgnoreAssignee", required = false)
        flowToNextIgnoreAssignee: Boolean? = null,

        @Parameter(description = "Define how long in seconds flowToNext should wait.", required = false)
        @RequestParam("flowToNextTimeOut", required = false)
        flowToNextTimeOut: Int? = null,

        request: HttpServletRequest,
        response: HttpServletResponse
    ): ProcessStartResponse?

    @Operation(
        summary = "Save variables for the given instance",
        operationId = "saveVariables",
        description = "",
        security = [SecurityRequirement(name = "CuroBasic")]
    )
    @PatchMapping("/{id}/variables", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun saveVariables(

        @Parameter(description = "ID of task", required = true)
        @PathVariable("id", required = true)
        id: String,

        @Parameter(description = "Body with variables", required = false)
        @RequestBody
        body: HashMap<String, Any?>,

        response: HttpServletResponse
    )

    @Operation(
        summary = "Save variables (from multipart) for the given instance",
        operationId = "saveVariablesMultiPart",
        description = "",
        security = [SecurityRequirement(name = "CuroBasic")]
    )
    @PatchMapping("/{id}/variables", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun saveVariablesMultiPart(

        @Parameter(description = "ID of task", required = true)
        @PathVariable("id", required = true)
        id: String,

        @Parameter(description = "Variables", required = false)
        @RequestPart("variables")
        body: String?,

        request: HttpServletRequest,
        response: HttpServletResponse
    )

    @Operation(
        summary = "Get next task",
        operationId = "nextTask",
        description = "",
        security = [SecurityRequirement(name = "CuroBasic")]
    )
    @GetMapping("/{id}/next", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun nextTask(
        @Parameter(description = "ID of the process instance", required = true)
        @PathVariable("id", required = true)
        id: String,

        @Parameter(description = "Define if flowToNext should ignore task assignee.", required = false)
        @RequestParam("flowToNextIgnoreAssignee", required = false, defaultValue = "false")
        flowToNextIgnoreAssignee: Boolean? = null
    ): FlowToNextResult
}
