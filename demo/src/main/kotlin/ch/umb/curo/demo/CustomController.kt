package ch.umb.curo.demo

import org.camunda.bpm.engine.IdentityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomController {

    @Autowired
    private lateinit var identityService: IdentityService

    @GetMapping("/custom/api/test")
    fun test(): String {
        val username = identityService.currentAuthentication.userId
        return "Hello $username"
    }
    
}
