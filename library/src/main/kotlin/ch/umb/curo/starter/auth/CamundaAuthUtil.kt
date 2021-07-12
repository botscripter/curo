package ch.umb.curo.starter.auth

import org.camunda.bpm.engine.IdentityService
import java.util.concurrent.Callable

/**
 * Provides easy access to common used Camunda authentication functions.
 */
object CamundaAuthUtil {

    /**
     * Runs the provided callable outside of the current authentication.
     *
     * @param identityService Camunda IdentityService
     * @return Result of the callable itself
     */
    fun <T> Callable<T>.callWithoutAuthentication(identityService: IdentityService): T {
        return runWithoutAuthentication(this, identityService)
    }

    /**
     * Runs the provided callable outside of the current authentication.
     *
     * @param request Callable to execute
     * @param identityService Camunda IdentityService
     * @return Result of the callable itself
     */
    fun <T> runWithoutAuthentication(request: Callable<T>, identityService: IdentityService): T {
        val currentUser = identityService.currentAuthentication
        identityService.clearAuthentication()

        val output: T
        try {
            output = request.call()
        } finally {
            identityService.setAuthentication(currentUser)
        }

        return output
    }

}
