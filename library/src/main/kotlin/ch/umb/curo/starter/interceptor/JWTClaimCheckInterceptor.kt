package ch.umb.curo.starter.interceptor

import com.auth0.jwt.interfaces.DecodedJWT
import javax.servlet.http.HttpServletRequest

/**
 * JWTClaimCheckInterceptors are called during the OAuth2 token check.
 * The check will fail if the onIntercept() method throws an exception of any kind.
 */
interface JWTClaimCheckInterceptor {

    val name: String

    /**
     * Execution is ordered from lowest to highest
     */
    val order: Int

    /**
     * On intercept method
     *
     * @param jwt Decoded JWT
     * @param jwtRaw Raw JWT extracted from the request
     * @param request Request which got intercepted
     */
    fun onIntercept(jwt: DecodedJWT, jwtRaw: String, request: HttpServletRequest)
}
