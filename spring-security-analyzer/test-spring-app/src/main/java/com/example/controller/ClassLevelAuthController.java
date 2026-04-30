package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
public class ClassLevelAuthController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}