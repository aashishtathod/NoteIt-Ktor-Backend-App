package aashishtathod.dev.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

class JWTController {

    private val secret = System.getenv("JWT_SECRET_KEY")
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    /**
     * Generates JWT token from [userId].
     */
    fun sign(data: String): String = JWT
        .create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim(ClAIM, data)
        .sign(algorithm)

    companion object {
        private const val ISSUER = "NotyKT-JWT-Issuer"
        private const val AUDIENCE = "https://noty-api.herokuapp.com"
        const val ClAIM = "userId"
    }

}