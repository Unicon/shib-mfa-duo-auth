package net.unicon.iam.shibboleth.idp.authn.duo.authn

import com.duosecurity.duoweb.DuoWeb
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
@Service('duoAuthenticationService')
class DuoAuthenticationService {
    @Value('%{duo.integrationKey}')
    private final String integrationKey

    @Value('%{duo.secretKey}')
    private final String secretKey

    @Value('%{duo.applicationKey}')
    private final String applicationKey

    @Value('%{duo.apiHost}')
    private final String apiHost

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
}
