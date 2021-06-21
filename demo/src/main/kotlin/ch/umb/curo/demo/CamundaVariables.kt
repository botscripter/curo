package ch.umb.curo.demo

import ch.umb.curo.starter.helper.camunda.CamundaVariable
import ch.umb.curo.starter.helper.camunda.CamundaVariableDefinition
import ch.umb.curo.starter.helper.camunda.annotation.EnableInitCamundaVariables
import ch.umb.curo.starter.helper.camunda.annotation.InitWithEmpty

@EnableInitCamundaVariables
class CamundaVariables {
    companion object {
        val title: CamundaVariableDefinition<String> = CamundaVariable("title", String::class.java)
        val category: CamundaVariableDefinition<String> = CamundaVariable("category", String::class.java)
        @InitWithEmpty
        val description: CamundaVariableDefinition<String> = CamundaVariable("description", String::class.java)
        val url: CamundaVariableDefinition<String> = CamundaVariable("url", String::class.java)
        val comments: CamundaVariableDefinition<String> = CamundaVariable("comments", String::class.java)
        val suggestionAccept: CamundaVariableDefinition<Boolean> = CamundaVariable("suggestionAccept", Boolean::class.java)
        @InitWithEmpty
        val fakeDataObject: CamundaVariableDefinition<FakeDataObject> = CamundaVariable("fakeDataObject", FakeDataObject::class.java)
    }
}
