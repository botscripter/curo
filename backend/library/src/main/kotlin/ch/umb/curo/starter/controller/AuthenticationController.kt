package ch.umb.curo.starter.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.camunda.bpm.engine.rest.util.EngineUtil
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@Tag(name = "auth", description = "Curo Auth API")
@RequestMapping("/curo-api")
class AuthenticationController {

    @PostMapping("/authenticate", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun authenticate(@RequestParam username: String, @RequestParam password: String, response: HttpServletResponse): ResponseEntity<String> {
        val engine = EngineUtil.lookupProcessEngine(null)
        val checkPassword = engine.identityService.checkPassword(username, password)
        return if (checkPassword) {
            ResponseEntity("{}", HttpStatus.OK)
        } else {
            ResponseEntity("{}", HttpStatus.UNAUTHORIZED)
        }
    }

}
