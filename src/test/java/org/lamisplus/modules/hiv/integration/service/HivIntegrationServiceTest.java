package org.lamisplus.modules.hiv.integration.service;


import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lamisplus.modules.base.BaseModule;
import org.lamisplus.modules.hiv.HivModule;
import org.lamisplus.modules.patient.PatientModule;
import org.lamisplus.modules.triage.TriageModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@AcrossWebAppConfiguration
@AutoConfigureEmbeddedDatabase(beanName = "acrossDataSource")
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
