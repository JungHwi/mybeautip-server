package com.jocoos.mybeautip.domain.system.dto;

import com.jocoos.mybeautip.domain.system.code.SystemOptionType;

public record SystemOptionRequest(SystemOptionType id,
                                  boolean value) {
}
