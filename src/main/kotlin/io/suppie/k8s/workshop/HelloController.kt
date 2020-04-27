package io.suppie.k8s.workshop

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/hello")
    fun sayHello(@RequestParam("subject", required = false) subject: String?) = "Hello, ${subject ?: "World"}!"
}
