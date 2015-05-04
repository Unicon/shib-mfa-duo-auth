package net.unicon.iam.shibboleth.idp.authn.duo.c14n

import com.google.common.base.Predicate
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext
import org.opensaml.profile.context.ProfileRequestContext

import javax.annotation.Nullable

/**
 * Determines whether the duo c14n should happen.
 */
class DuoSubjectCanonicalizationActivationCondition implements Predicate<ProfileRequestContext> {
    @Override
    boolean apply(@Nullable ProfileRequestContext profileRequestContext) {
        if (profileRequestContext) {
            final def duoPrincipals = profileRequestContext.getSubcontext(SubjectCanonicalizationContext, false)?.getSubject()?.getPrincipals(DuoPrincipal)
            return duoPrincipals && duoPrincipals.size() == 1
        }
        return false
    }
}
