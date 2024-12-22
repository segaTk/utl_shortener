package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper( componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    @Mapping(target = "shortUrl", expression = "java(shortLink + url.getHash())")
    ResponseUrlDto toResponseUrlDto(Url url, String shortLink);

}
