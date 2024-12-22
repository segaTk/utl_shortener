package faang.school.urlshortenerservice.config.executor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "executor")
public class ExecutorConfig {

    private int capacity;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(capacity);
    }
}
