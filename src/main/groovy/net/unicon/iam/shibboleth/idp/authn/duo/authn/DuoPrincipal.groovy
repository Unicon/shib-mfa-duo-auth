package net.unicon.iam.shibboleth.idp.authn.duo.authn

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import net.shibboleth.idp.authn.principal.CloneablePrincipal
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty
import net.shibboleth.utilities.java.support.logic.Constraint
import net.shibboleth.utilities.java.support.primitive.StringSupport

import javax.annotation.Nonnull

@AutoClone
@EqualsAndHashCode
class DuoPrincipal implements CloneablePrincipal {
    @Nonnull
    @NotEmpty
    private final String name

    public DuoPrincipal(@Nonnull @NotEmpty final String name) {
        this.name = Constraint.isNotNull(StringSupport.trimOrNull(name), 'username caonnot be null or empty')
    }

    //TODO: there's a better way of doing this
    @Override
    String getName() {return this.name}
}
