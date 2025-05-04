package nl.cqit.tools.buildnumbergenerator

import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

private val DATE = LocalDate.of(2023, 10, 1)

@WebMvcTest(BuildNumberController::class)
class BuildNumberControllerTest(@Autowired val mockMvc: MockMvc) {
    @Test
    fun getBuildNumber() {
        mockkStatic(::clock) {
            every { clock() } returns Clock.fixed(
                DATE.atStartOfDay(ZoneOffset.UTC).toInstant(),
                ZoneOffset.UTC
            )

            mockMvc.perform(get("/build-number/test"))
                .andExpect(status().isOk)
                .andExpect(content().string("2023-10-01.1"))
            mockMvc.perform(get("/build-number/test"))
                .andExpect(status().isOk)
                .andExpect(content().string("2023-10-01.2"))
            mockMvc.perform(get("/build-number/test2"))
                .andExpect(status().isOk)
                .andExpect(content().string("2023-10-01.1"))
            mockMvc.perform(get("/build-number/test"))
                .andExpect(status().isOk)
                .andExpect(content().string("2023-10-01.3"))

            every { clock() } returns Clock.fixed(
                DATE.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                ZoneOffset.UTC
            )

            mockMvc.perform(get("/build-number/test"))
                .andExpect(status().isOk)
                .andExpect(content().string("2023-10-02.1"))
        }
    }
}