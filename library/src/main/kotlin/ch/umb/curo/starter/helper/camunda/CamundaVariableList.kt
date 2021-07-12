package ch.umb.curo.starter.helper.camunda

import kotlin.reflect.KClass

/**
 * Representation of a Camunda variable which than can be used with the Curo [VariableHelper][CamundaVariableHelper]
 *
 * @param variableName Name of the variable
 * @param internalType Type of the variable
 */
class CamundaVariableList<T : Any> constructor(override val variableName: String, private val internalType: KClass<T>) :
    CamundaVariableListDefinition<T> {
    override val type: Class<T>
        get() = internalType.javaObjectType

    /**
     * Internal makes sure that no one use the wrong constructor in Kotlin when using this lib
     *
     * @param variableName Name of the variable
     * @param type Type of the variable
     */
    internal constructor(variableName: String, type: Class<T>) : this(variableName, type.kotlin)
}
