package ch.umb.curo.starter.helper.camunda

/**
 * Definition Convention of a Camunda variable which than ca be used with the Curo [VariableHelper][CamundaVariableHelper]
 */
interface CamundaVariableDefinitionConvention<T> {
    /**
     * Name of the variable
     */
    val variableName: String

    /**
     * Type of the variable
     */
    val type: Class<T>
}

/**
 * Extends [CamundaVariableDefinitionConvention] to be used for lists
 */
interface CamundaVariableListDefinition<T> : CamundaVariableDefinitionConvention<T>
/**
 * Extends [CamundaVariableDefinitionConvention] to be used for type other than lists
 */
interface CamundaVariableDefinition<T> : CamundaVariableDefinitionConvention<T>
