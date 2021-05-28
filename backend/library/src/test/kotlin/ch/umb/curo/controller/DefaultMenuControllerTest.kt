package ch.umb.curo.controller


import com.fasterxml.jackson.databind.ObjectMapper
import org.camunda.bpm.engine.FilterService
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.filter.Filter
import org.camunda.bpm.engine.rest.dto.runtime.FilterDto
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.*


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("menu")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class DefaultMenuControllerTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var filterService: FilterService

    @Autowired
    lateinit var engine: ProcessEngine

    private val basicLogin: String = Base64.getEncoder().encodeToString("demo:demo".toByteArray())

    @BeforeAll
    fun initialize(){
        createFilter("My Tasks", "#000", 10, "home", "description 1")
        createFilter("Overdue Tasks", "#F00", 20, "clock", "description 2")
        createFilter("Cool Tasks", "#0F0", 30, "car", "description 3")
        createFilter("Boring Tasks", "#00F", 40, "gear", "description 4")
    }

    @Test
    fun `getMenu() - loading menu without authorization should not work`() {
        mockMvc.get("/curo-api/menus") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `getMenu() - loading menu should work`() {
        mockMvc.get("/curo-api/menus") {
            accept = MediaType.APPLICATION_JSON
            header("Authorization", "CuroBasic $basicLogin")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$[0].name") { value("\uD83C\uDF0D All tasks") }
            jsonPath("$[0].order") { value(0) }
            jsonPath("$[1].name") { value("My Tasks") }
            jsonPath("$[1].order") { value(10) }
            jsonPath("$[2].name") { value("Overdue Tasks") }
            jsonPath("$[2].order") { value(20) }
            jsonPath("$[3].name") { value("Cool Tasks") }
            jsonPath("$[3].order") { value(30) }
            jsonPath("$[4].name") { value("Boring Tasks") }
            jsonPath("$[4].order") { value(40) }
        }
    }

    @Test
    fun `getMenu() - loading menu with additional attributes should work`() {
        mockMvc.get("/curo-api/menus") {
            accept = MediaType.APPLICATION_JSON
            header("Authorization", "CuroBasic $basicLogin")
            param("additionalAttributes", "description")
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$[0].additionalAttributes") { isEmpty() }
            jsonPath("$[1].additionalAttributes.description") { value("description 1") }
            jsonPath("$[2].additionalAttributes.description") { value("description 2") }
            jsonPath("$[3].additionalAttributes.description") { value("description 3") }
            jsonPath("$[4].additionalAttributes.description") { value("description 4") }

        }
    }

    private fun createFilter(
        name: String,
        color: String,
        priority: Int,
        icon: String,
        description: String = ""
    ): Filter {
        val filterDto = mapper.readValue(
            """{"resourceType": "Task",
                                              "name": "$name",
                                              "owner": "demo",
                                              "query": {},
                                              "properties": {
                                                "color": "$color",
                                                "priority": $priority,
                                                "icon": "$icon",
                                                "description": "$description"
                                              }
                                            }""".trimIndent(), FilterDto::class.java
        )
        val filter = filterService.newTaskFilter()
        filterDto.updateFilter(filter, engine)
        filterService.saveFilter(filter)

        return filter
    }
}
