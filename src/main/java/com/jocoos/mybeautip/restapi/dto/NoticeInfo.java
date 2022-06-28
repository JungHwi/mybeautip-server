package com.jocoos.mybeautip.restapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NoticeInfo {
    private String type;
    private String message;
}
