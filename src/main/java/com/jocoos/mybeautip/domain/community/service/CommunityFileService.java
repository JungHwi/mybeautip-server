package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.global.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;

@RequiredArgsConstructor
@Service
public class CommunityFileService {

    private final AwsS3Handler awsS3Handler;

    @Transactional
    public void write(List<FileDto> files, Long communityId) {
        awsS3Handler.copy(files, COMMUNITY.getDirectory(communityId));
    }

    @Transactional
    public void editFiles(Community community, List<FileDto> fileDtoList) {
        if (CollectionUtils.isEmpty(fileDtoList) || community.isVoteAndIncludeFile(fileDtoList.size())) {
            return;
        }

        for (FileDto fileDto : fileDtoList) {
            switch (fileDto.getOperation()) {
                case UPLOAD:
                    community.addFile(fileDto.getUrl());
                    break;
                case DELETE:
                    community.removeFile(fileDto.getUrl());
                    break;
            }
        }

        awsS3Handler.editFiles(fileDtoList, COMMUNITY.getDirectory(community.getId()));
    }
}
