package ch.umb.curo.starter.events

import ch.umb.curo.starter.property.CuroProperties
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType

class PostDeployListener(
    private val properties: CuroProperties,
    private val context: ConfigurableApplicationContext,
    private val processEngineConfiguration: SpringProcessEngineConfiguration
) {

    private val logger = LoggerFactory.getLogger(PostDeployListener::class.java)

    fun onPostDeployEvent() {

        checkAuthorization(processEngineConfiguration)
        setDefaultSerializationFormat(processEngineConfiguration)
        setEnableHistoricInstancePermissions(processEngineConfiguration)

        //Set patterns
        setCamundaUserIdPattern(processEngineConfiguration)
        setCamundaGroupIdPattern(processEngineConfiguration)
    }

    private fun setEnableHistoricInstancePermissions(processEngineConfiguration: SpringProcessEngineConfiguration) {
        val enableHistoricInstancePermissions = try {
            (context.environment.getProperty("camunda.bpm.generic-properties.enable-historic-instance-permissions") as Boolean?)
                ?: processEngineConfiguration.isEnableHistoricInstancePermissions
        } catch (e: Exception) {
            false
        }
        if (properties.enableHistoricInstancePermissions != null) {
            processEngineConfiguration.isEnableHistoricInstancePermissions =
                properties.enableHistoricInstancePermissions ?: false
            logger.info("CURO: Set enableHistoricInstancePermissions to '${processEngineConfiguration.isEnableHistoricInstancePermissions}'")
        } else {
            logger.warn("CURO: Camunda enableHistoricInstancePermissions is set to $enableHistoricInstancePermissions!")
        }
    }

    private fun checkAuthorization(processEngineConfiguration: SpringProcessEngineConfiguration) {
        if (!processEngineConfiguration.isAuthorizationEnabled) {
            logger.warn("CURO: ⚠️ Authorization is not enabled! ⚠️")
        }

        if (!processEngineConfiguration.isAuthorizationEnabledForCustomCode) {
            /*
             * <p>The following flag <code>authorizationEnabledForCustomCode</code> will
             * only be taken into account if <code>authorizationEnabled</code> is set
             * <code>true</code>.</p>
             *
             * <p>If the value of the flag <code>authorizationEnabledForCustomCode</code>
             * is set <code>true</code> then an authorization check will be performed by
             * executing commands inside custom code (e.g. inside {@link JavaDelegate}).</p>
             *
             * <p>The default value is <code>false</code>.</p>
             *
             */
            logger.debug("CURO: Authorization for custom code is not enabled!")
        }
    }

    private fun setDefaultSerializationFormat(processEngineConfiguration: SpringProcessEngineConfiguration) {
        val defaultSerializationFormat = context.environment.getProperty("camunda.bpm.default-serialization-format")
            ?: processEngineConfiguration.defaultSerializationFormat
        when {
            (defaultSerializationFormat == MediaType.APPLICATION_JSON_VALUE) -> {
                logger.debug("CURO: Default serialization format is already set to 'application/json'")
            }
            (properties.setDefaultSerializationFormat && defaultSerializationFormat != MediaType.APPLICATION_JSON_VALUE) -> {
                processEngineConfiguration.defaultSerializationFormat =
                    MediaType.APPLICATION_JSON_VALUE
                logger.info("CURO: Set default serialization format to 'application/json'")
            }
            (!properties.setDefaultSerializationFormat && defaultSerializationFormat != MediaType.APPLICATION_JSON_VALUE) -> {
                logger.warn("CURO: ⚠️ Default serialization format is set to '$defaultSerializationFormat' which is not supported by Curo ⚠️")
            }
        }
    }

    private fun setCamundaUserIdPattern(processEngineConfiguration: SpringProcessEngineConfiguration) {
        if (properties.camundaUserIdPattern != null) {
            logger.info("CURO: Set userResourceWhitelistPattern to: ${properties.camundaUserIdPattern}")
            processEngineConfiguration.userResourceWhitelistPattern = properties.camundaUserIdPattern
        }

        //show warning if userIdClaim is email or mail and userResourceWhitelistPattern is default
        val isDefaultPattern = processEngineConfiguration.userResourceWhitelistPattern == null ||
                processEngineConfiguration.userResourceWhitelistPattern == "[a-zA-Z0-9]+|camunda-admin"
        if (properties.auth.type == "oauth2" && properties.auth.oauth2.userIdClaim in arrayListOf(
                "mail",
                "email",
                "preferred_username"
            ) && isDefaultPattern
        ) {
            logger.warn("CURO: email seems to be used as userIdClaim but camundaUserIdPattern is no set. This may result in Curo not be able to authenticate users as the camunda default pattern does not allow email addresses.")
        }
    }

    private fun setCamundaGroupIdPattern(processEngineConfiguration: SpringProcessEngineConfiguration) {
        if (properties.camundaGroupIdPattern != null) {
            logger.info("CURO: Set groupResourceWhitelistPattern to: ${properties.camundaGroupIdPattern}")
            processEngineConfiguration.groupResourceWhitelistPattern = properties.camundaGroupIdPattern
        }
    }
}
