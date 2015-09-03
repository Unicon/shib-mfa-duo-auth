package net.unicon.iam.shibboleth.idp.authn.duo.webflow

import groovy.util.logging.Slf4j
import net.shibboleth.idp.authn.AuthenticationFlowDescriptor
import net.shibboleth.idp.authn.SubjectCanonicalizationFlowDescriptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry
import org.springframework.webflow.engine.builder.support.FlowBuilderServices

import javax.annotation.PostConstruct
import javax.annotation.Resource

/**
 * Spring configuration for shibboleth webflows for Duo Security authentication
 */

@Component
@Slf4j
class Config {
    @Autowired
    @Qualifier('flowRegistry')
    private final FlowDefinitionRegistry flowRegistry

    @Autowired
    private final ApplicationContext applicationContext

    @Autowired(required = false)
    private final FlowBuilderServices flowBuilderServices

    @PostConstruct
    def setup() {
        // Build the flows and register them
        def flowDefinitionRegistry = new FlowDefinitionRegistryBuilder(applicationContext, flowBuilderServices).with {
            setParent(this.flowRegistry)
            if (!this.flowRegistry.containsFlowDefinition('authn/Duo')) {
                addFlowLocation('classpath:/META-INF/shibboleth-idp/flows/authn/Duo/duo-authn-flow.xml', 'authn/Duo')
            }
            if (!this.flowRegistry.containsFlowDefinition('c14n/Duo')) {
                addFlowLocation('classpath:/META-INF/shibboleth-idp/flows/c14n/Duo/duo-c14n-flow.xml', 'c14n/Duo')
            }
            build()
        }
        flowDefinitionRegistry.flowDefinitionIds.each {
            flowRegistry.registerFlowDefinition(flowDefinitionRegistry.getFlowDefinition(it))
        }

    }
}
