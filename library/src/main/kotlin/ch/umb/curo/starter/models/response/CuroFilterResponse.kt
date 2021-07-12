package ch.umb.curo.starter.models.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class CuroFilterResponse(
    var name: String? = null,
    var description: String? = null,
    var refresh: Boolean? = null,
    var properties: Map<String, Any?>? = null,
    var filterProperties: List<FilterProperty>? = null,
    var total: Long,
    var items: List<Any>
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class FilterProperty(
        var label: String = "",
        var variable: String = "",

        @get:JsonProperty("isAttribute")
        @param:JsonProperty("isAttribute")
        var isAttribute: Boolean = false,
        var type: String = "",
        var operator: String? = null,
        var values: List<Values>? = null,
        var valuesGenerator: String? = null
    ) {

        class Values(
            var id: String = "",
            var value: String = ""
        )

    }
}
