package com.jocoos.mybeautip.domain.broadcast.dto;

public record BroadcastStatisticsResponse(int totalViewerCount,
                                          int maxViewerCount,
                                          int viewerCount,
                                          int memberViewerCount,
                                          int guestViewerCount,
                                          int reportCount,
                                          int heartCount) {
}