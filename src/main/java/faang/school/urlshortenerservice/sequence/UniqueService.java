package faang.school.urlshortenerservice.sequence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniqueService {

    private final UniqueRepositoryImpl uniqueRepository;

    public List<Long> getUniqueNumber() {
        try {
            return uniqueRepository.getUniqueNumbers();
        } catch (Exception e) {
            throw new RuntimeException("UNIQUE SERVICE. Get unique numbers ERROR", e);
        }
    }
}
