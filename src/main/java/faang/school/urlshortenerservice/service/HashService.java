package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.executor.ExecutorConfig;
import faang.school.urlshortenerservice.repository.HashRepositoryIml;
import faang.school.urlshortenerservice.sequence.UniqueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashService {

    private final EncoderService encoderService;
    private final HashRepositoryIml hashRepository;
    private final UniqueService uniqueService;
    private final ExecutorConfig executorConfig;

    public CompletableFuture<List<String>> getHashesAsync() {
        return CompletableFuture.supplyAsync(this::getHashes);
    }

    public List<String> getHashes() {
        List<Long> sequence = uniqueService.getUniqueNumber();
        List<CompletableFuture<Boolean>> futures = sequence.stream()
                .map(s -> CompletableFuture.supplyAsync(() -> {
                                    try {
                                        encoderService.encode(sequence);
                                        return true;
                                    } catch (Exception e) {
                                        log.error("Error encoding element: {}", s, e);
                                        return false;
                                    }
                                },
                                executorConfig.executorService())
                        .exceptionally(ex -> {
                            log.error("Error encoding element: {}", s, ex);
                            return null;
                        }))
                .toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error during encoding batch process", ex);
                    } else {
                        log.info("Encoding completed for batch: {}", sequence);
                    }
                });
        try {
            allFutures.join();
        } catch (Exception e) {
            log.error("Error during encoding", e);
        }

        return save(sequence);
    }

    private List<String> save(List<Long> hashes) {
        if (hashes.isEmpty()) {
            log.warn("No hashes to save.");
            return Collections.emptyList();
        }
        List<String> strings = hashes.stream()
                .map(String::valueOf)
                .toList();
        hashRepository.saveHashes(strings);
        return strings;
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        hashRepository.saveHashes(hashes);
    }
}


