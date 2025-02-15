package com.example.ciba.ad;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationDeviceController {

    @GetMapping
    public String index() {
        return "index";
    }
}
