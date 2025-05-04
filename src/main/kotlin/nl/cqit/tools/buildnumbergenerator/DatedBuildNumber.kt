package nl.cqit.tools.buildnumbergenerator

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset

data class DatedBuildNumber(
    val applicationName: String,
    val buildDate: LocalDate,
    val buildNumber: Int,
) {
    fun buildId(): String = "$buildDate.$buildNumber"

    fun getNext(): DatedBuildNumber {
        val now = LocalDate.now(clock())
        return if (buildDate == now) {
            DatedBuildNumber(applicationName, buildDate, buildNumber + 1)
        } else {
            DatedBuildNumber(applicationName, now, 1)
        }
    }
}

fun newBuildNumber(applicationName: String): DatedBuildNumber {
    return DatedBuildNumber(applicationName, LocalDate.now(clock()), 1)
}

fun clock(): Clock {
    return Clock.system(ZoneOffset.UTC)
}