package com.jocoos.mybeautip.domain.system.dto;

import com.jocoos.mybeautip.domain.system.code.SystemOptionType;

public record SystemOptionResponse(SystemOptionType id,
                                   boolean value) {
}
