package com.jocoos.mybeautip.domain.broadcast.dto;

import lombok.Builder;

@Builder
public record BroadcastStatisticsResponse(int totalViewerCount,
                                          int maxViewerCount,
                                          int viewerCount,
                                          int memberViewerCount,
                                          int guestViewerCount,
                                          int reportCount,
                                          int heartCount,
                                          long duration) {
}