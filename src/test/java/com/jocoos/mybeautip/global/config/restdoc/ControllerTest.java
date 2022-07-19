package com.jocoos.mybeautip.global.config.restdoc;

import com.jocoos.mybeautip.domain.event.api.front.EventJoinController;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@Disabled
@WebMvcTest({
        EventJoinController.class,
})
public abstract class ControllerTest {


}
