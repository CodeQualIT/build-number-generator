package nl.cqit.tools.buildnumbergenerator

import nl.cqit.tools.buildnumbergenerator.config.LoggerDelegate
import nl.cqit.tools.buildnumbergenerator.storage.BuildNumbers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BuildNumberController(
    private val buildNumbers: BuildNumbers
) {
    companion object {
        val log by LoggerDelegate()
    }

    @GetMapping("/build-number/{applicationName}")
    fun getBuildNumber(@PathVariable("applicationName") applicationName: String): String {
        log.info("Received request for build number for application: {}", applicationName)
        val nextBuildNumber = buildNumbers[applicationName]
            ?.getNext()
            ?: newBuildNumber(applicationName)
        log.info("Generated new build number: {}", nextBuildNumber)
        buildNumbers[applicationName] = nextBuildNumber
        log.info("Stored build number: {}", nextBuildNumber)
        return nextBuildNumber.buildId()
    }
}