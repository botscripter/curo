package ch.umb.curo.starter.helper.camunda

import ch.umb.curo.starter.models.response.CuroFilterResponse
import org.camunda.bpm.engine.filter.Filter

/**
 * This interface can be implemented to generate values for filter properties.
 */
interface FilterValueGenerator {

    fun generate(filter: Filter): List<CuroFilterResponse.FilterProperty.Values>

}
