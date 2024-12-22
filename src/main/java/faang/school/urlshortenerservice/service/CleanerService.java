package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.properties.CashProperties;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanerService {

    private final UrlService urlService;
    private final HashService hashService;
    private final CashProperties cacheProperties;

    @Transactional
    public void clean() {
        List<Url> urls = urlService.findAndReturnExpiredUrls(cacheProperties.getYearsToUrlExpiration());
        List<String> releasedHashes = urls.stream()
                .map(Url::getUrl)
                .toList();
        hashService.saveHashes(releasedHashes);
        log.info("clearExpiredUrls - finish, released hashes size - {}", releasedHashes.size());
    }
}
