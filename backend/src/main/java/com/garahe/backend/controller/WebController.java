package com.garahe.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        // Redirect to the login/landing page
        return "redirect:/home.html";
    }
}