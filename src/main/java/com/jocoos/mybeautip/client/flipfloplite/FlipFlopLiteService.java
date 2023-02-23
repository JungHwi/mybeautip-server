package com.jocoos.mybeautip.client.flipfloplite;

import com.jocoos.mybeautip.client.flipfloplite.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class FlipFlopLiteService {

    private final FlipFlopLiteClient client;

    public TokenResponse loginGuest() {
        TokenResponse response = client.loginGuest();
        return response;
    }
}
