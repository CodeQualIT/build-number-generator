package nl.cqit.tools.buildnumbergenerator

import io.mockk.every
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

private val DATE = LocalDate.of(2023, 10, 1)

class DatedBuildNumberTest {

    @Test
    fun buildId() {
        val buildNumber = DatedBuildNumber("test", DATE, 42)
        assertThat(buildNumber.buildId()).isEqualTo("20231001.42")
    }

    @Test
    fun getNext_sameDate() {
        mockkStatic(::clock) {
            every { clock() } returns Clock.fixed(
                DATE.atStartOfDay(ZoneOffset.UTC).toInstant(),
                ZoneOffset.UTC
            )

            val buildNumber = DatedBuildNumber("test", DATE, 42)
            val nextBuildNumber = buildNumber.getNext()
            assertThat(nextBuildNumber.buildDate).isEqualTo(DATE)
            assertThat(nextBuildNumber.buildNumber).isEqualTo(43)
        }
    }

    @Test
    fun getNext_differentDate() {
        mockkStatic(::clock) {
            every { clock() } returns Clock.fixed(
                DATE.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                ZoneOffset.UTC
            )

            val buildNumber = DatedBuildNumber("test", DATE, 42)
            val nextBuildNumber = buildNumber.getNext()
            assertThat(nextBuildNumber.buildDate).isEqualTo(DATE.plusDays(1))
            assertThat(nextBuildNumber.buildNumber).isEqualTo(1)
        }
    }

    @Test
    fun newBuildNumber() {
        mockkStatic(::clock) {
            every { clock() } returns Clock.fixed(
                DATE.atStartOfDay(ZoneOffset.UTC).toInstant(),
                ZoneOffset.UTC
            )

            val buildNumber = newBuildNumber("test")
            assertThat(buildNumber.applicationName).isEqualTo("test")
            assertThat(buildNumber.buildDate).isEqualTo(DATE)
            assertThat(buildNumber.buildNumber).isEqualTo(1)
        }
    }
}