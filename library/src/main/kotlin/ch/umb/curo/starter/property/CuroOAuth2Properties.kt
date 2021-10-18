package ch.umb.curo.starter.property

import org.springframework.boot.context.properties.NestedConfigurationProperty

class CuroOAuth2Properties {

    /**
     * Allowed issuer (iss)
     */
    var allowedIssuers: ArrayList<String> = arrayListOf()

    /**
     * Should Curo check the signature against the defined public key
     */
    var verifyJwt: Boolean = true

    /**
     * JWK Url to use for signature checking
     */
    var jwkUrl: String = ""

    /**
     * Should Curo always use the algorithm defined in `defaultAlgorithm` to verify Jwt
     */
    var useDefaultAlgorithm: Boolean = false

    /**
     * Default algorithm to use if `useDefaultAlgorithm` is active.
     */
    var defaultAlgorithm: String = ""

    /**
     * Allow algorithm fallback to jwt claim if jwk does not contain algorithm.
     * Using 'alg' claim of jwt is a potential security risk. Use this only if not other possible.
     */
    var allowAlgorithmFallbackToJWTClaim: Boolean = false

    /**
     * Claim which is used to check against Camunda (user id)
     * Common claims: email, preferred_username
     */
    var userIdClaim: String = "email"

    /**
     * Print warnings for wrong tokens and verification failures
     */
    var printErrorsToLog: Boolean = false

    /**
     * Curo user federation
     */
    @NestedConfigurationProperty
    var userFederation: CuroUserFederationProperties = CuroUserFederationProperties()

}
