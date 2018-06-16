package io.moquette.kilim

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KilimApplication

fun main(args: Array<String>) {
    runApplication<KilimApplication>(*args /*"--debug"*/)
}
