package ch.umb.curo.demo

import ch.umb.curo.starter.helper.camunda.CamundaVariable
import ch.umb.curo.starter.helper.camunda.CamundaVariableDefinition
import ch.umb.curo.starter.helper.camunda.annotation.InitWithEmpty

object CamundaVariables_Two {

    val title2: CamundaVariableDefinition<String> = CamundaVariable("title2", String::class.java)
    val category2: CamundaVariableDefinition<String> = CamundaVariable("category2", String::class.java)
    val description2: CamundaVariableDefinition<String> = CamundaVariable("description2", String::class.java)
    val url2: CamundaVariableDefinition<String> = CamundaVariable("url2", String::class.java)
    val comments2: CamundaVariableDefinition<String> = CamundaVariable("comments2", String::class.java)
    @InitWithEmpty
    val suggestionAccept2: CamundaVariableDefinition<Boolean> = CamundaVariable("suggestionAccept2", Boolean::class.java)

}
