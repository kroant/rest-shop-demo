package cz.kromer.restshopdemo.service;

import static java.lang.Thread.sleep;
import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.kromer.restshopdemo.repository.ProductLockingRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
class ProductLockingTransaction {

    ProductLockingRepository productLockingRepository;

    @Transactional
    @SneakyThrows(InterruptedException.class)
    public void lockAndWait(UUID productId, long sleepMillis) {
        log.info("Locking Product id: {}", productId);
        productLockingRepository.findById(productId);
        sleep(sleepMillis);
        log.info("Releasing lock; Product id: {}", productId);
    }
}
