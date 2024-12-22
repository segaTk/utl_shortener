package faang.school.urlshortenerservice.sequence;

import faang.school.urlshortenerservice.config.properties.SequenceProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UniqueRepositoryImpl implements UniqueRepository {

    private final SequenceProperty sequenceProperty;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> getUniqueNumbers() {

        int batchSize = sequenceProperty.getBatchSize();

        String sql = String.format("SELECT nextval('public.unique_hash_seq') FROM generate_series(1, %d)", batchSize);

        try {
            return jdbcTemplate.queryForList(sql, Long.class);
        } catch (DataAccessException dae) {
            log.error("While getUniqueNumbers() an error occurred: {}", dae.getMessage(), dae);
            throw new RuntimeException("Error fetching unique numbers: " + dae.getMessage(), dae);
        }
    }
}
