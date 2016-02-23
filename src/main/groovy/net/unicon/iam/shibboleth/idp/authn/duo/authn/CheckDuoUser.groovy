package net.unicon.iam.shibboleth.idp.authn.duo.authn

import groovy.util.logging.Slf4j
import net.shibboleth.idp.authn.context.AuthenticationContext
import net.shibboleth.idp.authn.context.SubjectContext
import net.shibboleth.idp.profile.AbstractProfileAction
import net.shibboleth.idp.profile.ActionSupport
import net.shibboleth.idp.session.context.SessionContext
import org.opensaml.profile.context.ProfileRequestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import org.springframework.webflow.execution.Event
import org.springframework.webflow.execution.RequestContext

import javax.annotation.Nonnull

/**
 * A profile action that will check if a user exists in Duo
 */
@Service('CheckDuoUser')
@Scope('prototype')
@Slf4j
class CheckDuoUser extends AbstractProfileAction {
    @Autowired
    DuoAuthenticationService duoAuthenticationService

    @Override
    protected Event doExecute(
            @Nonnull RequestContext springRequestContext, @Nonnull ProfileRequestContext profileRequestContext) {
        def authenticationContext = profileRequestContext.getSubcontext(AuthenticationContext)
        if (!this.duoAuthenticationService.hasDuoAccount((authenticationContext.parent.getSubcontext(SubjectContext)?:authenticationContext.parent.getSubcontext(SessionContext).idPSession).principalName)) {
            return ActionSupport.buildEvent(profileRequestContext, 'NoDuoUser')
        }
        return ActionSupport.buildEvent(profileRequestContext, 'proceed')
    }
}
