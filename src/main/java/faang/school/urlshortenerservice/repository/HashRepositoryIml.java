package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashRepositoryIml implements HashRepository{

    private final HashProperties hashProperties;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveHashes(List<String> hashes) {
        String sql = "INSERT INTO url(url) VALUES (?)";

        for (int i = 0; i < hashes.size(); i += hashProperties.getInsertBatch()) {
            int end = Math.min(i + hashProperties.getInsertBatch(), hashes.size());
                    List<String> batch = hashes.subList(i, end);

            try {
                jdbcTemplate.batchUpdate(sql, batch, batch.size(),
                        (PreparedStatement ps, String hash) -> {
                            ps.setString(1, hash);
                        });
                log.debug("Batch {} processed, containing {} hashes", i / hashProperties.getInsertBatch() + 1, batch.size());
            } catch (DataAccessException dae) {
                log.error("While saveHashes() some error occurred");
                throw new RuntimeException("Error " + dae.getMessage() + " ", dae);
            }
        }
    }
}
