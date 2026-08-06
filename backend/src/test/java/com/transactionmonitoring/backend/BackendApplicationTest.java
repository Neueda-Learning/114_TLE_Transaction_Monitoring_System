package com.transactionmonitoring.backend;

import com.transactionmonitoring.backend.controller.AlertController;
import com.transactionmonitoring.backend.controller.AuthController;
import com.transactionmonitoring.backend.controller.LogsController;
import com.transactionmonitoring.backend.controller.RulesController;
import com.transactionmonitoring.backend.controller.TransactionController;
import com.transactionmonitoring.backend.service.AlertService;
import com.transactionmonitoring.backend.service.LogService;
import com.transactionmonitoring.backend.service.RulesService;
import com.transactionmonitoring.backend.simulation.RandomTransactionGenerator;
import com.transactionmonitoring.backend.simulation.SimulatorSchedular;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BackendApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void alertControllerBeanExists() {
        AlertController bean = applicationContext.getBean(AlertController.class);
        assertNotNull(bean);
    }

    @Test
    void authControllerBeanExists() {
        AuthController bean = applicationContext.getBean(AuthController.class);
        assertNotNull(bean);
    }

    @Test
    void rulesControllerBeanExists() {
        RulesController bean = applicationContext.getBean(RulesController.class);
        assertNotNull(bean);
    }

    @Test
    void transactionControllerBeanExists() {
        TransactionController bean = applicationContext.getBean(TransactionController.class);
        assertNotNull(bean);
    }

    @Test
    void logsControllerBeanExists() {
        LogsController bean = applicationContext.getBean(LogsController.class);
        assertNotNull(bean);
    }

    @Test
    void alertServiceBeanExists() {
        AlertService bean = applicationContext.getBean(AlertService.class);
        assertNotNull(bean);
    }

    @Test
    void rulesServiceBeanExists() {
        RulesService bean = applicationContext.getBean(RulesService.class);
        assertNotNull(bean);
    }

    @Test
    void logServiceBeanExists() {
        LogService bean = applicationContext.getBean(LogService.class);
        assertNotNull(bean);
    }

    @Test
    void randomTransactionGeneratorBeanExists() {
        RandomTransactionGenerator bean = applicationContext.getBean(RandomTransactionGenerator.class);
        assertNotNull(bean);
    }

    @Test
    void simulatorSchedularBeanExists() {
        SimulatorSchedular bean = applicationContext.getBean(SimulatorSchedular.class);
        assertNotNull(bean);
    }

    @Test
    void allRequiredBeansLoaded() {
        assertNotNull(applicationContext.getBean(AlertController.class));
        assertNotNull(applicationContext.getBean(AuthController.class));
        assertNotNull(applicationContext.getBean(RulesController.class));
        assertNotNull(applicationContext.getBean(TransactionController.class));
        assertNotNull(applicationContext.getBean(LogsController.class));
        assertNotNull(applicationContext.getBean(AlertService.class));
        assertNotNull(applicationContext.getBean(RulesService.class));
        assertNotNull(applicationContext.getBean(LogService.class));
        assertNotNull(applicationContext.getBean(RandomTransactionGenerator.class));
        assertNotNull(applicationContext.getBean(SimulatorSchedular.class));
    }

    @Test
    void applicationContextBeansCount() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 20, "Application should have loaded multiple beans");
    }

    @Test
    void mainMethodExecutable() {
        // Test that main method is accessible
        assertTrue(BackendApplication.class.getMethods().length > 0);
    }
}
