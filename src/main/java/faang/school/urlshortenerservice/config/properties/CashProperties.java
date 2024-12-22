package faang.school.urlshortenerservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "cache")
public class CashProperties {

    private int capacity;
    private int minLimitCapacity;
    private int yearsToUrlExpiration;

}
