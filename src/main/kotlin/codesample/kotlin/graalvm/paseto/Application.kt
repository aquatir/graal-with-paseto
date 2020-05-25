package codesample.kotlin.graalvm.paseto

import dev.paseto.jpaseto.Pasetos
import dev.paseto.jpaseto.lang.Keys
import java.security.SecureRandom
import java.time.Instant

object Application {

    @JvmStatic // Not external function for graalvm
    fun main(args: Array<String>) {

        System.setProperty("sun.arch.data.model", "64");

        println("os.arch=${System.getProperty("os.arch")}")
        println("sun.arch.data.model=${System.getProperty("sun.arch.data.model")}")
        println("sun.arch.data.model=${Integer.getInteger("sun.arch.data.model")}")
        val dataModel: Int = Integer.getInteger("sun.arch.data.model")

        println("not null data model: $dataModel")

        val byteArray = ByteArray(32)
        val key = SecureRandom.getInstanceStrong().nextBytes(byteArray)

        val token= Pasetos.V2.LOCAL.builder()
                .setSharedSecret(Keys.secretKey(byteArray))
                .claim("some-claim", "some-claim-value")
                .footerClaim("some-footer-claim", "some-footer-claim-value")
                .setIssuedAt(Instant.now())
                .compact()

        println("Generated token: $token")

        val parser = Pasetos.parserBuilder().setSharedSecret(Keys.secretKey(byteArray)).build()
        val parsedToken = parser.parse(token)
        println("Parsed token")
        println("token claim: ${parsedToken.claims["some-claim"] as String}")
        println("token footer claim: ${parsedToken.footer["some-footer-claim"]}")
    }
}
