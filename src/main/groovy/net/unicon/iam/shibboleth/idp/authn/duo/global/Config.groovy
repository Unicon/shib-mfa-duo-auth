package net.unicon.iam.shibboleth.idp.authn.duo.global

import groovy.util.logging.Slf4j
import net.shibboleth.idp.authn.AuthenticationFlowDescriptor
import net.shibboleth.idp.authn.SubjectCanonicalizationFlowDescriptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

/**
 * Spring configuration for shibboleth global for Duo Security authentication
 */

@Component
@Slf4j
class Config {
    @Resource(name = "shibboleth.AvailableAuthenticationFlows")
    private final List<AuthenticationFlowDescriptor> shibbolethAvailableAuthenticationFlows

    @Autowired
    @Qualifier("authn/Duo")
    private final AuthenticationFlowDescriptor duoAuthnDescriptor

    @Resource(name = "shibboleth.PostLoginSubjectCanonicalizationFlows")
    private final List<SubjectCanonicalizationFlowDescriptor> shibbolethPostLoginSubjectCanonicalizationFlows

    @Autowired
    @Qualifier('c14n/Duo')
    private final SubjectCanonicalizationFlowDescriptor duoCanonicalizationDescriptor

    @PostConstruct
    def setup() {
        // add the flow descriptors for use in shibboleth
        shibbolethAvailableAuthenticationFlows.add(duoAuthnDescriptor)
        shibbolethPostLoginSubjectCanonicalizationFlows.add(duoCanonicalizationDescriptor)
    }
}
