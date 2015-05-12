package net.unicon.iam.shibboleth.idp.authn.duo.authn

import groovy.util.logging.Slf4j
import net.shibboleth.idp.authn.AbstractValidationAction
import net.shibboleth.idp.authn.AuthnEventIds
import net.shibboleth.idp.authn.context.AuthenticationContext
import net.shibboleth.idp.authn.context.SubjectContext
import net.shibboleth.idp.profile.context.SpringRequestContext
import net.shibboleth.idp.session.context.SessionContext
import org.opensaml.profile.context.ProfileRequestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service

import javax.annotation.Nonnull
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.security.auth.Subject


/**
 * A validation action to validate responses from Duo Security
 */
@Service('ValidateDuoResponse')
@Scope('prototype')
@Slf4j
class ValidateDuoResponse extends AbstractValidationAction {
    @Autowired
    DuoAuthenticationService duoAuthenticationService

    private String username
    private DuoCredential duoCredential

    @Override
    protected boolean doPreExecute(
            @Nonnull ProfileRequestContext profileRequestContext,
            @Nonnull AuthenticationContext authenticationContext) {
        if (!super.doPreExecute(profileRequestContext, authenticationContext)) {
            return false
        }

        // Check for a duo credential in the flow scope
        this.duoCredential = authenticationContext?.parent?.getSubcontext(SpringRequestContext)?.requestContext?.flowScope?.duoCredential
        if (!this.duoCredential) {
            log.info("${logPrefix} No Duo response in the context")
            handleError(profileRequestContext, authenticationContext, 'NoCredentials', AuthnEventIds.NO_CREDENTIALS)
            return false
        }

        // Check for a username/principalName in the Context
        this.username = (authenticationContext.parent.getSubcontext(SubjectContext)?:authenticationContext.parent.getSubcontext(SessionContext).idPSession).principalName
		log.debug("${logPrefix} Username is ${this.username}")
        if (!(authenticationContext.parent.getSubcontext(SubjectContext)?:authenticationContext.parent.getSubcontext(SessionContext).idPSession).principalName) {
            log.info("${logPrefix} No previous principal name. This is bad")
            handleError(profileRequestContext, authenticationContext, 'NoCredentials', AuthnEventIds.NO_CREDENTIALS)
            return false
        }

        return true
    }

    @Override
    protected void doExecute(
            @Nonnull ProfileRequestContext profileRequestContext,
            @Nonnull AuthenticationContext authenticationContext) {
        def response = duoAuthenticationService.authenticate(this.duoCredential.signedDuoResponse)

        if (response != this.username) {
            log.info("${logPrefix} Duo response does not match ${this.username}")
            handleError(profileRequestContext, authenticationContext, 'InvalidCredentials', AuthnEventIds.INVALID_CREDENTIALS)
            return
        } else {
            log.info("${logPrefix} Duo Login succeeded")
            buildAuthenticationResult(profileRequestContext, authenticationContext)
        }
    }

    @Override
    protected Subject populateSubject(@Nonnull Subject subject) {
        subject.principals.add(new DuoPrincipal(this.username))
        return subject
    }

    @PostConstruct
    def setup() {
        this.initialize()
    }

    @PreDestroy
    def desetup() {
        this.destroy()
    }
}
