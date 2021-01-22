package ch.umb.curo.starter.models.response

import io.swagger.v3.oas.annotations.media.Schema

class CompleteTaskResponse {

    /**
     * Variables related to this task
     **/
    @Schema(description = "Variables related to this task")
    var variables: HashMap<String, Any?>? = null

    /**
     * Possible next tasks
     */
    @Schema(description = "Possible next tasks")
    var flowToNext: List<String>? = null

    /**
     * Defines if the end of the process is reached or no next task exists
     */
    @Schema(description = "Defines if the end of the process is reached or no next task exists")
    var flowToEnd: Boolean? = null

    /**
     * Defines if the timeout got exceeded and polling is needed
     */
    @Schema(description = "Defines if the timeout got exceeded and polling is needed")
    var flowToNextTimeoutExceeded: Boolean? = null

}
