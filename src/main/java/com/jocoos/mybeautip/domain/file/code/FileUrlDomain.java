package com.jocoos.mybeautip.domain.file.code;

import com.jocoos.mybeautip.global.util.FileUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum FileUrlDomain {
    MYBEAUTIP(FileUtil::getFileName),
    FLIPFLOP(url -> {
        String[] split = url.split("/");
        return split[split.length - 2];
    })
    ;

    private final Function<String, String> getFilename;
}
