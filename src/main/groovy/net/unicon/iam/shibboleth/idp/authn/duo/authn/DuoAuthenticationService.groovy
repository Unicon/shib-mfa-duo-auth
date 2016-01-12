package net.unicon.iam.shibboleth.idp.authn.duo.authn

import com.duosecurity.client.Http
import com.duosecurity.duoweb.DuoWeb
import groovy.json.JsonSlurper
import net.shibboleth.idp.authn.context.AuthenticationContext
import net.shibboleth.idp.authn.context.SubjectContext
import net.shibboleth.idp.session.context.SessionContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Service for handling Duo Security authentication. Will need the following keys in your <em>idp.properties</em> file:
 * <ul>
 *     <li>duo.integrationKey</li>
 *     <li>duo.secrectKey</li>
 *     <li>duo.applicationKey</li>
 *     <li>duo.apiHost</li>
 * </ul>
 */
class DuoAuthenticationService {
    private final String integrationKey

    private final String secretKey

    private final String applicationKey

    private final String apiHost

    public DuoAuthenticationService() {

    }

    public DuoAuthenticationService(String integrationKey, String secretKey, String applicationKey, String apiHost) {
        this.integrationKey = integrationKey
        this.secretKey = secretKey
        this.applicationKey = applicationKey
        this.apiHost = apiHost
    }

    String generateSignedRequestToken(final String username) {
        DuoWeb.signRequest(this.integrationKey, this.secretKey, this.applicationKey, username)
    }

    String generateSignedRequestToken(final AuthenticationContext authenticationContext) {
        return generateSignedRequestToken((authenticationContext.parent.getSubcontext(SubjectContext)?:authenticationContext.parent.getSubcontext(SessionContext).idPSession).principalName)
    }

    String authenticate(final String signedRequestToken) {
        DuoWeb.verifyResponse(this.integrationKey, this.secretKey, this.applicationKey, signedRequestToken)
    }

    String getApiHost() {
        return this.apiHost
    }

    boolean hasDuoAccount(final String username) {
        def response = new Http('POST', this.apiHost, '/auth/v2/preauth', 10).with {
            addParam('username', username)
            signRequest(this.integrationKey, this.secretKey)
            executeHttpRequest()
        }
        if (!(response.code() == 200 && response.message() == 'OK')) {
            // there was a problem. right now we just fake like the user doesn't have an account
            return false
        }
        def json = new JsonSlurper().parse(response.body().byteStream())
        return json.response.result != 'deny'
    }
}
