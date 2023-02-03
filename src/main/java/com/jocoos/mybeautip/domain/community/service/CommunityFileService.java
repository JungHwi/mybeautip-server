package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.file.service.FlipFlopService;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.file.code.FileType.VIDEO;
import static com.jocoos.mybeautip.domain.file.code.FileUrlDomain.MYBEAUTIP;
import static com.jocoos.mybeautip.global.code.FileOperationType.UPLOAD;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY_COMMENT;
import static com.jocoos.mybeautip.global.exception.ErrorCode.FILE_NOT_EDITABLE;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@RequiredArgsConstructor
@Service
public class CommunityFileService {

    private final AwsS3Handler awsS3Handler;
    private final FlipFlopService flipFlopService;

    public void writeWithTranscode(List<FileDto> files, long communityId) {
        awsS3Handler.copy(files, COMMUNITY.getDirectory(communityId));

        List<String> videoUrls = files.stream()
                .filter(file -> file.getType().equals(VIDEO))
                .map(file -> toUrl(getFileName(file.getUrl()), COMMUNITY, communityId))
                .toList();
        flipFlopService.transcode(videoUrls, communityId);
    }

    public void write(List<FileDto> files, Long communityId) {
        awsS3Handler.copy(files, COMMUNITY.getDirectory(communityId));
    }

    public void write(FileDto file, Long commentId) {
        if (file != null) {
            awsS3Handler.copy(file, COMMUNITY_COMMENT.getDirectory(commentId));
        }
    }

    @Transactional
    public void editFiles(Community community, List<FileDto> fileDtoList) {

        if (community.isVoteAndIncludeFile()) {
            throw new BadRequestException(FILE_NOT_EDITABLE);
        }

        if (CollectionUtils.isEmpty(fileDtoList)) {
            return;
        }

        for (FileDto fileDto : fileDtoList) {
            switch (fileDto.getOperation()) {
                case UPLOAD:
                    community.addFile(fileDto.getType(), MYBEAUTIP, fileDto.getUrl());
                    break;
                case DELETE:
                    community.removeFile(fileDto.getUrl());
                    break;
            }
        }
        community.validWrite();

        List<FileDto> mybeautipFiles = fileDtoList.stream()
                .filter(fileDto -> fileDto.getUrl().contains("mybeautip"))
                .toList();
        awsS3Handler.editFiles(mybeautipFiles, COMMUNITY.getDirectory(community.getId()));
    }

    @Transactional
    public void editFilesWithTranscode(Community community, List<FileDto> files) {
        editFiles(community, files);
        List<String> videoUrls = files.stream()
                .filter(
                        fileDto -> fileDto.getOperation() == UPLOAD
                                && fileDto.getType() == VIDEO
                                && fileDto.isNeedTranscode()
                )
                .map(file -> toUrl(getFileName(file.getUrl()), COMMUNITY, community.getId()))
                .toList();
        flipFlopService.transcode(videoUrls, community.getId());
    }
}
