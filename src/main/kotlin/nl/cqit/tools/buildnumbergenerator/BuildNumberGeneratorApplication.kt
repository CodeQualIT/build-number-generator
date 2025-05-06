package nl.cqit.tools.buildnumbergenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class BuildNumberGeneratorApplication

fun main(args: Array<String>) {
    runApplication<BuildNumberGeneratorApplication>(*args)
}
