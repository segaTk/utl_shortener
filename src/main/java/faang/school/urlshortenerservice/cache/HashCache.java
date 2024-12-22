package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.properties.CashProperties;
import faang.school.urlshortenerservice.service.HashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {

    private static final double ONE_HUNDRED = 100.0;
    private final double commonCapacityPercentage;

    private final CashProperties cashProperties;
    private final HashService hashService;

    private final AtomicBoolean filling = new AtomicBoolean(false);
    private final Queue<String> hashes;

    @Autowired
    public HashCache(
            CashProperties cashProperties,
            HashService hashService) {

        this.cashProperties = cashProperties;
        this.hashService = hashService;
        this.hashes = new ConcurrentLinkedQueue<>();
        this.commonCapacityPercentage = cashProperties.getCapacity() / ONE_HUNDRED;


        log.info("Starting initial cache fill...");
        hashService.getHashesAsync()
                .thenAccept(hashes::addAll)
                .thenRun(() -> log.info("Initial cache fill completed."))
                .exceptionally(e -> {
                    log.error("Error while refilling hash cache", e);
                    return null;
                });
    }

    public String getHash() {
        double currentCapacity = hashes.size() / commonCapacityPercentage;
        log.info("Current cache capacity: {}%", currentCapacity);

        if (currentCapacity <= cashProperties.getMinLimitCapacity()
                && filling.compareAndSet(false, true)) {
            log.info("Cache below minimum capacity. Starting refill...");
            hashService.getHashesAsync().thenAccept(heashesList -> {
                for (String hash : heashesList) {
                    hashes.offer(hash);
                }
            }).thenRun(() -> {
                filling.set(false);
                log.info("Cache refill completed.");
            }).exceptionally(e -> {
                log.error("Error while refilling hash cache", e);
                filling.set(false);
                return null;
            });
        }
        return String.valueOf(hashes.poll());
    }
}
