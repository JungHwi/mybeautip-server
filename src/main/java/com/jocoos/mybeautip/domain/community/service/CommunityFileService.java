package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY_COMMENT;
import static com.jocoos.mybeautip.global.exception.ErrorCode.FILE_NOT_EDITABLE;

@RequiredArgsConstructor
@Service
public class CommunityFileService {

    private final AwsS3Handler awsS3Handler;

    @Transactional
    public void write(List<FileDto> files, Long communityId) {
        awsS3Handler.copy(files, COMMUNITY.getDirectory(communityId));
    }

    @Transactional
    public void write(FileDto file, Long commentId) {
        if (file != null) {
            awsS3Handler.copy(file, COMMUNITY_COMMENT.getDirectory(commentId));
        }
    }

    @Transactional
    public void editFiles(Community community, List<FileDto> fileDtoList) {

        if(community.isVoteAndIncludeFile()) {
            throw new BadRequestException(FILE_NOT_EDITABLE);
        }

        if (CollectionUtils.isEmpty(fileDtoList)) {
            return;
        }

        for (FileDto fileDto : fileDtoList) {
            switch (fileDto.getOperation()) {
                case UPLOAD:
                    community.addFile(fileDto.getType(), fileDto.getUrl());
                    break;
                case DELETE:
                    community.removeFile(fileDto.getUrl());
                    break;
            }
        }

        community.validWrite();
        awsS3Handler.editFiles(fileDtoList, COMMUNITY.getDirectory(community.getId()));
    }
}
