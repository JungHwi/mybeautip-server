package com.jocoos.mybeautip.domain.broadcast

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastCategoryRepository
import com.jocoos.mybeautip.global.config.restdoc.RestDocsIntegrationTestSupport
import com.jocoos.mybeautip.testutil.fixture.makeBroadcastCategory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired

@TestInstance(PER_CLASS)
class BroadcastTestSupport : RestDocsIntegrationTestSupport() {

    @Autowired private lateinit var broadcastCategoryRepository: BroadcastCategoryRepository
    protected lateinit var defaultBroadcastCategory: BroadcastCategory

    @BeforeAll
    fun beforeAllBroadcastTest() {
        defaultBroadcastCategory = broadcastCategoryRepository.save(makeBroadcastCategory(groupBroadcastCategory.id))
    }

    @AfterAll
    fun afterAllBroadcastTest() {
        broadcastCategoryRepository.delete(defaultBroadcastCategory)
    }

}
