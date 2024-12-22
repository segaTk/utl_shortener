package faang.school.urlshortenerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class EncoderService {

    private String generateUniqueSalt() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public void encode(List<Long> seq) {
        seq.stream()
                .map(this::encodeNumber)
                .toList();
    }

    /**
     * Кодируем одну последовательность.
     *
     * @param sequence последовательность (AtomicLong)
     * @return hash
     */
    private String encodeNumber(Long sequence) {
        try {
            String hashedValue = hashWithSha256(generateUniqueSalt() + sequence);
            log.info("hash: {}", hashedValue);

            sequence++;

            String hashedValueWithIncrement = hashWithSha256(generateUniqueSalt() + sequence);
            log.info("hash with sequence increment:{}", hashedValueWithIncrement);
            return hashedValueWithIncrement;
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while hashing sequence: {}", sequence, e);
            throw new RuntimeException("Failed to encode number", e);
        }
    }

    /**
     * Хэшируем строку с использованием SHA-256
     *
     * @param input строка для хэша.
     * @return хэш в виде строки.
     * @throws NoSuchAlgorithmException если SHA-256 недоступен.
     */
    public static String hashWithSha256(String input) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] encodeHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(encodeHash);
    }

    /**
     * Преобразуем массив байтов в строку (в шестнадцатеричном формате).
     *
     * @param hash массив байтов.
     * @return строковое представление.
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
