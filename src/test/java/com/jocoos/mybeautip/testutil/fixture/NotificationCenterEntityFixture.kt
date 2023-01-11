package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.notification.code.MessageType
import com.jocoos.mybeautip.domain.notification.code.MessageType.CONTENT
import com.jocoos.mybeautip.domain.notification.code.NotificationLinkType
import com.jocoos.mybeautip.domain.notification.code.TemplateType
import com.jocoos.mybeautip.domain.notification.code.TemplateType.COMMUNITY_COMMENT
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessageCenterEntity
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessagePushEntity
import com.jocoos.mybeautip.global.code.Language
import com.jocoos.mybeautip.global.code.Language.KO


fun makeNotificationMessageCenterEntity(
    id: Long = 1,
    templateId: TemplateType = COMMUNITY_COMMENT,
    lang: Language = KO,
    messageType: MessageType = CONTENT,
    lastVersion: Boolean = true,
    message: String = "message",
    notificationLinkType: List<NotificationLinkType> = emptyList()
) : NotificationMessageCenterEntity {
    return NotificationMessageCenterEntity(id, templateId, lang, messageType, lastVersion, message, notificationLinkType)
}

fun makeNotificationMessagePushEntity(
    id: Long = 1,
    templateId: TemplateType = COMMUNITY_COMMENT,
    lang: Language = KO,
    messageType: MessageType = CONTENT,
    lastVersion: Boolean = true,
    title: String = "title",
    message: String = "message",
    notificationLinkType: List<NotificationLinkType> = emptyList()
) : NotificationMessagePushEntity {
    return NotificationMessagePushEntity(id, templateId, lang, messageType, lastVersion, title, message, notificationLinkType)
}
