package nl.cqit.tools.buildnumbergenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BuildNumberGeneratorApplication

fun main(args: Array<String>) {
    runApplication<BuildNumberGeneratorApplication>(*args)
}
