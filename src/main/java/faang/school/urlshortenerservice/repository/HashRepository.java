package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepository {

    void saveHashes(List<String> hashes);

}
