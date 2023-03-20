package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.devices.HealthCheck
import com.jocoos.mybeautip.member.Member
import java.util.*


fun makeHealthCheck(createdBy: Member,
                    id: Long = 1,
                    type: String = "1",
                    os: String = "android",
                    message: String = "app.force.update",
                    minVersion: String = "0",
                    maxVersion: String = "1.0.0",
                    modifiedAt: Date = Date(),
                    createdAt: Date = Date(),

): HealthCheck {
    return HealthCheck(
        id,
        type,
        os,
        message,
        minVersion,
        maxVersion,
        modifiedAt,
    )
}