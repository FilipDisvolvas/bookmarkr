package net.sakrak.reactivebookmarks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReactiveBookmarksApplication

fun main(args: Array<String>) {
    runApplication<ReactiveBookmarksApplication>(*args)
}
