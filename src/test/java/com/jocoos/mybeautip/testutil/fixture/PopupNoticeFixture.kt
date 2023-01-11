package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.popup.code.ButtonLinkType
import com.jocoos.mybeautip.domain.popup.code.PopupDisplayType
import com.jocoos.mybeautip.domain.popup.code.PopupStatus
import com.jocoos.mybeautip.domain.popup.code.PopupType
import com.jocoos.mybeautip.domain.popup.persistence.domain.Popup
import com.jocoos.mybeautip.domain.popup.persistence.domain.PopupButton
import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeLinkType
import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeLinkType.HOME
import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeStatus
import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeStatus.ACTIVE
import com.jocoos.mybeautip.domain.popupnotice.persistence.domain.PopupNotice
import java.time.ZonedDateTime

fun makePopupNotice(
    status: PopupNoticeStatus = ACTIVE,
    filename: String = "filename",
    linkType: PopupNoticeLinkType = HOME,
    linkArgument: String = "linkArgument",
    startedAt: ZonedDateTime = ZonedDateTime.now().minusDays(1),
    endedAt: ZonedDateTime = ZonedDateTime.now().plusDays(1)
) : PopupNotice {
    return PopupNotice(status, filename, linkType, linkArgument, startedAt, endedAt)
}

fun makePopup(
    id: Long? = null,
    type: PopupType,
    status: PopupStatus = PopupStatus.ACTIVE,
    displayType: PopupDisplayType = PopupDisplayType.ONCE,
    imageFile: String = "imageFile",
    description: String = "description",
    startedAt: ZonedDateTime = ZonedDateTime.now().minusDays(1),
    endedAt: ZonedDateTime = ZonedDateTime.now().plusDays(1)
) : Popup {
    return Popup(id, type, status, displayType, imageFile, description, startedAt, endedAt, listOf(makePopupButton()))
}

fun makePopupButton(
    id: Long? = null,
    name: String = "name",
    linkType: ButtonLinkType = ButtonLinkType.PREVIOUS,
    linkArgument: String? = null
) : PopupButton {
    return PopupButton(id, name, linkType, linkArgument, null)
}
