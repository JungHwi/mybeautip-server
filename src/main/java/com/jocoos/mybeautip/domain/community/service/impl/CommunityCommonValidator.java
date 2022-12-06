package com.jocoos.mybeautip.domain.community.service.impl;

import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;

import static com.jocoos.mybeautip.domain.member.code.Role.USER;
import static com.jocoos.mybeautip.global.exception.ErrorCode.TOO_MANY_FILE;

public abstract class CommunityCommonValidator implements CommunityValidator {

    private static final int DEFAULT_AVAILABLE_FILE_SIZE = 5;

    @Override
    public void validContentsByRole(Role role, String contents) {
        if (USER.equals(role)) {
            validContents(contents);
        }
    }

    protected void validByRole(Role role, String contents, int fileSize) {
        validContentsByRole(role, contents);
        validFileSize(fileSize, DEFAULT_AVAILABLE_FILE_SIZE);
    }

    protected void validFileSize(int fileSize, int limit) {
        if (fileSize > limit) {
            throw new BadRequestException(TOO_MANY_FILE, "file size limit " + limit + " request file size " + fileSize);
        }
    }

    private void validContents(String contents) {
        if (StringUtils.isBlank(contents) || contents.replace(StringUtils.SPACE, StringUtils.EMPTY).length() < 5) {
            throw new BadRequestException("Content length must be at least 5.");
        }
    }
}
