package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Builder;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.stream.Stream;

import static com.jocoos.mybeautip.global.code.FileOperationType.DELETE;
import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;

@Getter
@Builder
@SuppressWarnings("FieldMayBeFinal")
public class PatchCommunityRequest {
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<String> contents = JsonNullable.undefined();
    private JsonNullable<List<String>> imageUrls = JsonNullable.undefined();

    public List<FileDto> getFileDto(List<CommunityFile> communityFiles) {
        if (!imageUrls.isPresent()) {
            return List.of();
        }

        List<String> editImageUrls = imageUrls.get();
        List<String> originalUrls = toImageUrl(communityFiles);
        List<FileDto> deleteFiles = getDeleteFileDto(originalUrls, editImageUrls);
        List<FileDto> uploadFiles = getUploadFileDto(originalUrls, editImageUrls);
        return concatList(deleteFiles, uploadFiles);
    }

    private List<String> toImageUrl(List<CommunityFile> communityFiles) {
        return communityFiles.stream().map(CommunityFile::getFileUrl).toList();
    }

    private List<FileDto> getDeleteFileDto(List<String> originalUrls, List<String> editImageUrls) {
        return originalUrls.stream()
                .filter(url -> !editImageUrls.contains(url))
                .map(url -> new FileDto(DELETE, url))
                .toList();
    }

    private List<FileDto> getUploadFileDto(List<String> originalUrls, List<String> editImageUrls) {
        return editImageUrls.stream()
                .filter(url -> !originalUrls.contains(url))
                .map(url -> new FileDto(UPLOAD, url))
                .toList();
    }

    private List<FileDto> concatList(List<FileDto> deleteFiles, List<FileDto> uploadFiles) {
        return Stream.concat(deleteFiles.stream(), uploadFiles.stream()).toList();
    }
}
