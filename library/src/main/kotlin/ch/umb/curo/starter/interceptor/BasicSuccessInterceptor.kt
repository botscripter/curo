package ch.umb.curo.starter.interceptor

import javax.servlet.http.HttpServletRequest

/**
 * AuthSuccessInterceptors are called on the /curo-api/auth/success endpoint.
 */
interface BasicSuccessInterceptor {

    val name: String

    /**
     * Defines if this intercept is called asynchronous
     */
    val async: Boolean

    /**
     * Execution is ordered from lowest to highest
     */
    val order: Int

    /**
     * On intercept method
     *
     * @param username Username of the calling client
     * @param request Request which got intercepted
     */
    fun onIntercept(username: String?, request: HttpServletRequest): Boolean
}
