package org.lamisplus.modules.hiv.integration.service;


import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;


@ExtendWith({SpringExtension.class})
@DirtiesContext
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
class HivIntegrationServiceTest {

/*
    @Test
    void shouldCreateHivEnrollment() {

    }


    @Configuration
    @AcrossTestConfiguration(modulePackages = {"org.lamisplus"},
            expose = {PasswordEncoder.class},
            modules = {BaseModule.NAME, PatientModule.NAME}
    )
    @PropertySource(value = "classpath:across-test.properties")
    static class Config implements AcrossContextConfigurer {

        @Override
        public void configure(AcrossContext context) {
            context.addModule (new PatientModule ());
            context.addModule (new TriageModule ());
            context.addModule (new HivModule ());
        }
    }*/
}
