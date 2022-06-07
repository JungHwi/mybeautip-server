package com.jocoos.mybeautip.restapi;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeController {

    @GetMapping("/")
    public RedirectView swaggerUi() {
        return new RedirectView("/swagger-ui.html");
    }
}
