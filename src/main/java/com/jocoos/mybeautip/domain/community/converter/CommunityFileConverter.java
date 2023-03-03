package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.global.dto.FileDto;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = "spring")
public interface CommunityFileConverter {

    // FLIP FLOP 업로드는 콜백 형식 밖에 없으므르 MYBEAUTIP 상수 처리
    @Mapping(target = "file", source = "url", qualifiedByName = "toFilename")
    @Mapping(target = "domain", constant = "MYBEAUTIP")
    CommunityFile toEntity(FileDto fileDto);

    @IterableMapping(nullValueMappingStrategy = RETURN_DEFAULT)
    List<CommunityFile> toEntity(List<FileDto> fileDto);

    @Mapping(target = "operation", ignore = true)
    @Mapping(target = "url", source = "fileUrl")
    FileDto toDto(CommunityFile communityFile);

    @IterableMapping(nullValueMappingStrategy = RETURN_DEFAULT)
    List<FileDto> toDto(List<CommunityFile> communityFiles);

    @Named("toFilename")
    default String toFilename(String url) {
        return getFileName(url);
    }
}
