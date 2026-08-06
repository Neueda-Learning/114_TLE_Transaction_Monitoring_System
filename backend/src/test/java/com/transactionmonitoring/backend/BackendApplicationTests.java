package com.transactionmonitoring.backend;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.transactionmonitoring.backend.controller.TransactionController;
import com.transactionmonitoring.backend.repository.AlertRepository;
import com.transactionmonitoring.backend.repository.RulesRepository;
import com.transactionmonitoring.backend.repository.TransactionRepository;
import com.transactionmonitoring.backend.service.TransactionService;
import javax.sql.DataSource;
import java.sql.Connection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:tmtestdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.hibernate.ddl-auto=create-drop",
	"spring.jpa.show-sql=false"
})
class BackendApplicationTests {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private RulesRepository rulesRepository;

	@Autowired
	private AlertRepository alertRepository;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext);
	}

	@Test
	void criticalBeansAreAvailable() {
		assertNotNull(applicationContext.getBean(TransactionController.class));
		assertNotNull(applicationContext.getBean(TransactionService.class));
		assertNotNull(applicationContext.getBean(TransactionRepository.class));
		assertNotNull(applicationContext.getBean(RulesRepository.class));
		assertNotNull(applicationContext.getBean(AlertRepository.class));
	}

	@Test
	void usesH2DatasourceForTests() throws Exception {
		try (Connection connection = dataSource.getConnection()) {
			String url = connection.getMetaData().getURL();
			assertTrue(url.contains("jdbc:h2:mem:tmtestdb"));
		}
	}

	@Test
	void repositoriesAreReachable() {
		assertTrue(transactionRepository.count() >= 0);
		assertTrue(rulesRepository.count() >= 0);
		assertTrue(alertRepository.count() >= 0);
	}

}
