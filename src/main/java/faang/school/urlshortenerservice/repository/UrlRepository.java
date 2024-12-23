package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

    @Modifying
    @Query(value = """
            DELETE from Url u WHERE u.create_at <= current_timestamp - INTERVAL ':years year' RETURNING u.*
            """, nativeQuery = true)
    List<Url> deleteAndReturnExpiredUrls(@Param("years") int years);

    Optional<Url> findByUrlIgnoreCase(String url);

    Optional<Url> findByHashIgnoreCase(String hash);
}
