package ch.umb.curo.demo

import ch.umb.curo.starter.helper.camunda.FilterValueGenerator
import ch.umb.curo.starter.models.response.CuroFilterResponse
import org.camunda.bpm.engine.IdentityService
import org.camunda.bpm.engine.filter.Filter
import org.springframework.stereotype.Service

@Service
class AssignableUsersGenerator(
    private val identityService: IdentityService
) : FilterValueGenerator {
    override fun generate(filter: Filter): List<CuroFilterResponse.FilterProperty.Values> {
        return identityService.createUserQuery().list().map { CuroFilterResponse.FilterProperty.Values(it.id, "${it.firstName} ${it.lastName} | ${it.email}") }
    }
}
