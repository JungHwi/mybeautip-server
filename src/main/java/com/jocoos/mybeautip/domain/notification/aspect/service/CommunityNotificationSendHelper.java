package com.jocoos.mybeautip.domain.notification.aspect.service;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.service.NotificationSendService;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CommunityNotificationSendHelper {

    private final NotificationSendService sendService;
    private final CommunityDao communityDao;

    @Transactional
    public void sendNotification(Long communityId, TemplateType templateType) {
        Community community = communityDao.get(communityId);
        Map<String, String> arguments = getArgument(community);
        String thumbnailFile = community.getCommunityFileList().stream()
                .findFirst()
                .map(CommunityFile::getFile)
                .orElse(null);
        String thumbnailFileUrl = ImageUrlConvertUtil.toUrl(thumbnailFile, UrlDirectory.COMMUNITY, community.getId());
        sendService.send(templateType, community.getMemberId(), thumbnailFileUrl, arguments);
    }

    private Map<String, String> getArgument(Community community) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.COMMUNITY_ID.name(), String.valueOf(community.getId()));
        return arguments;
    }
}
