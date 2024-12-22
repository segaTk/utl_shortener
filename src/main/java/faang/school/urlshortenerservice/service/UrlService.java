package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${url.shortName}")
    private String urlShortPrefix;

    private final HashCache hashCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public List<Url> findAndReturnExpiredUrls(int years) {
        return urlRepository.deleteAndReturnExpiredUrls(years);
    }

    @Transactional
    public String getFullRedirectionLink(String hash) {
        log.info("CONVERT ------------- {}", hash);

        String fullLink = findUrlInCacheByHash(hash);

        if(fullLink == null || fullLink.isBlank()) {
            Url url = findUrlInDatabaseHash(hash)
                    .orElseThrow( () -> new EntityNotFoundException("Url with hash " + hash + " not found"));
            fullLink = url.getUrl();
        }
        log.info("convertShortLinkToFullLink finish, link - {}", fullLink);
        return fullLink;
    }

    @Transactional
    public ResponseUrlDto convertUrlToShort(RequestUrlDto requestUrlDto) {

        String fullLink = requestUrlDto.getUrl();

        Url url = findUrlInDatabaseFullUrl(fullLink)
                .orElseGet(() -> createUrl(fullLink));
        log.info("URL SERVICE. Create url - {}", fullLink);

        return urlMapper.toResponseUrlDto(url, urlShortPrefix + url.getHash());
    }

    @Transactional
    protected Url createUrl(String fullLink) {
        Url url = Url.builder()
                .hash(hashCache.getHash())
                .url(fullLink)
                .createdAt(LocalDateTime.now())
                .build();
        url = urlRepository.save(url);

        urlCacheRepository.save(url);
        return url;
    }


    @Transactional
    public Optional<Url> findUrlInDatabaseFullUrl(String fullLink) {
        log.debug("Trying to get url from database by full url - {}", fullLink);
        return urlRepository.findByUrlIgnoreCase(fullLink);
    }

    @Transactional
    public Optional<Url> findUrlInDatabaseHash(String hash) {
        log.debug("Trying to get url from database by hash - {}", hash);
        return urlRepository.findByHashIgnoreCase(hash);
    }

    public String findUrlInCacheByHash(String hash) {
        log.debug("Trying to get url from cache by hash - {}", hash);
        return urlCacheRepository.find(hash);
    }
}
