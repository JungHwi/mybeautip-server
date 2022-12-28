package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.Builder;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.stream.Stream;

import static com.jocoos.mybeautip.global.code.FileOperationType.DELETE;
import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;
import static org.springframework.util.CollectionUtils.isEmpty;

@Getter
@Builder
@SuppressWarnings("FieldMayBeFinal")
public class PatchCommunityRequest {
    private JsonNullable<String> title;
    private JsonNullable<String> contents;
    private List<FileDto> files;

    public List<FileDto> getFileDto(List<CommunityFile> communityFiles) {
        List<FileDto> originalFiles = toFileDto(communityFiles);
        List<FileDto> deleteFiles = getDeleteFileDto(originalFiles, files);
        List<FileDto> uploadFiles = getUploadFileDto(originalFiles, files);
        return concatList(deleteFiles, uploadFiles);
    }

    @JsonIgnore
    public List<String> getImageUrls() {
        return files.stream().map(FileDto::getUrl).toList();
    }

    private List<FileDto> toFileDto(List<CommunityFile> communityFiles) {
        return communityFiles.stream().map(FileDto::from).toList();
    }

    private List<FileDto> getDeleteFileDto(List<FileDto> originalFiles, List<FileDto> editFiles) {
        List<FileDto> deletedFiles = originalFiles.stream()
                .filter(originalFile -> !editFiles.contains(originalFile))
                .toList();
        deletedFiles.forEach(file -> file.setOperation(DELETE));
        return deletedFiles;
    }

    private List<FileDto> getUploadFileDto(List<FileDto> originalFiles, List<FileDto> editFiles) {
        List<FileDto> uploadFiles = editFiles.stream()
                .filter(editFile -> !originalFiles.contains(editFile))
                .toList();
        uploadFiles.forEach(file -> file.setOperation(UPLOAD));
        return uploadFiles;
    }

    private List<FileDto> concatList(List<FileDto> deleteFiles, List<FileDto> uploadFiles) {
        return Stream.concat(deleteFiles.stream(), uploadFiles.stream()).toList();
    }

    public boolean containFile() {
        return !isEmpty(files);
    }
}
