package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.system.code.SystemOptionType
import com.jocoos.mybeautip.domain.system.persistence.domain.SystemOption

fun makeSystemOption(
    id: SystemOptionType = SystemOptionType.FREE_LIVE_PERMISSION,
    value: Boolean = false
): SystemOption {
    return SystemOption(
        id,
        value
    )
}