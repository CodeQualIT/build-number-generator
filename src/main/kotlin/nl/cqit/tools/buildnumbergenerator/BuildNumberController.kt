package nl.cqit.tools.buildnumbergenerator

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BuildNumberController {
    private val buildNumbers = mutableMapOf<String, DatedBuildNumber>()

    @GetMapping("/build-number/{applicationName}")
    fun getBuildNumber(@PathVariable("applicationName") applicationName: String): String {
        val nextBuildNumber = buildNumbers[applicationName]
            ?.getNext()
            ?: newBuildNumber(applicationName)
        buildNumbers[applicationName] = nextBuildNumber
        return nextBuildNumber.buildId()
    }
}