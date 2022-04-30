package de.debuglevel.spamclassifier.token

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("app.greetings.tokens")
class TokenProperties {
    var someDuration: Duration = Duration.ofSeconds(1)
    var someText: String = "default"
    var someInteger: Int = 1
    var someDouble: Double = 1.0
}
