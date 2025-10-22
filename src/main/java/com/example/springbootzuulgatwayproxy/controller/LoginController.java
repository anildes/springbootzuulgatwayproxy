package com.example.springbootzuulgatwayproxy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String login() {
        return "login"; // This will serve login.html from templates
    }
    
    @GetMapping("/")
    public String home() {
        return "home";
    }
}