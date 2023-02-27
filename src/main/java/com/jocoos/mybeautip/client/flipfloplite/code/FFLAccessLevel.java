package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLAccessLevel {
    PUBLIC, APP, MEMBER, FRIEND, FOLLOWER, RESTRICTED, PRIVATE
}
