package ch.umb.curo.demo

import ch.umb.curo.starter.interceptor.BasicSuccessInterceptor
import ch.umb.curo.starter.interceptor.Oauth2SuccessInterceptor
import ch.umb.curo.starter.property.CuroProperties
import com.auth0.jwt.interfaces.DecodedJWT
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class LoginLoggerInterceptor(val properties: CuroProperties) : Oauth2SuccessInterceptor {
    override val async: Boolean
        get() = true
    override val name: String
        get() = "LoginLogger"
    override val order: Int
        get() = 1000

    private val logger = LoggerFactory.getLogger(LoginLoggerInterceptor::class.java)

    override fun onIntercept(jwt: DecodedJWT, jwtRaw: String, request: HttpServletRequest): Boolean {
        logger.info("User ${jwt.getClaim(properties.auth.oauth2.userIdClaim)} just logged in")
        return true
    }
}
