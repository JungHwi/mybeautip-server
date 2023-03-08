package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record BroadcastParticipantInfo(@JsonUnwrapped ViewerResponse viewerResponse,
                                       BroadcastKey broadcastKey) { }
