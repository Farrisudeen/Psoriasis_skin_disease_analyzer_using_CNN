package com.example.skindetect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @PostMapping("/validate")
    public String validate(@RequestParam String uname,
                           @RequestParam String upass,
                           Model model) {
        if ("admin".equals(uname) && "123".equals(upass)) {
            return "index";
        } else {
            model.addAttribute("msg", "Invalid credentials");
            return "login";
        }
    }
}
