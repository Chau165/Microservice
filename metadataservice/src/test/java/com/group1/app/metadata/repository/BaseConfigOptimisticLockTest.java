package com.group1.app.metadata.repository;

import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import com.group1.app.metadata.entity.baseconfig.ConfigType;
import com.group1.app.metadata.repository.baseconfig.BaseConfigRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BaseConfigOptimisticLockTest {

    @Autowired
    private BaseConfigRepository repo;

    @Autowired
    private PlatformTransactionManager txManager;

    @Test
    void shouldThrowOptimisticLock_whenConcurrentUpdate() {

        BaseConfig config = repo.saveAndFlush(
                BaseConfig.builder()
                        .configKey("lock-" + UUID.randomUUID())
                        .configValue("v1")
                        .configType(ConfigType.STRING)
                        .configGroup("test")
                        .description("optimistic lock test")
                        .build()
        );

        TransactionTemplate tx1 = new TransactionTemplate(txManager);
        TransactionTemplate tx2 = new TransactionTemplate(txManager);

        BaseConfig c1 = tx1.execute(s -> repo.findById(config.getId()).orElseThrow());
        BaseConfig c2 = tx2.execute(s -> repo.findById(config.getId()).orElseThrow());

        tx1.execute(s -> {
            c1.setConfigValue("tx1");
            repo.saveAndFlush(c1);
            return null;
        });

        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> tx2.execute(s -> {
                    c2.setConfigValue("tx2");
                    repo.saveAndFlush(c2);
                    return null;
                }),
                "Second transaction must fail due to optimistic locking"
        );
    }
}
