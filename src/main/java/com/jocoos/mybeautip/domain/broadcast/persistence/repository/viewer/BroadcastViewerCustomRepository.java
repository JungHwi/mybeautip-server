package com.jocoos.mybeautip.domain.broadcast.persistence.repository.viewer;

import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;

import java.util.List;

public interface BroadcastViewerCustomRepository {

    List<ViewerSearchResult> search(ViewerSearchCondition condition);
}
