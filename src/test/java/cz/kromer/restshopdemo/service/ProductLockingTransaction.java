package cz.kromer.restshopdemo.service;

import cz.kromer.restshopdemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
class ProductLockingTransaction {

    ProductRepository productRepository;

    CountDownLatch latch = new CountDownLatch(1);

    @Transactional
    @SneakyThrows(InterruptedException.class)
    public void lockAndSleep(UUID productId, long sleepMillis) {
        log.info("Locking Product id: {}", productId);
        productRepository.findAndLockById(productId);
        log.info("Product locked. Product id: {}", productId);

        latch.countDown();

        sleep(sleepMillis);
        log.info("Releasing lock; Product id: {}", productId);
    }

    @SneakyThrows(InterruptedException.class)
    void waitUntilProductLocked() {
        latch.await();
    }
}
