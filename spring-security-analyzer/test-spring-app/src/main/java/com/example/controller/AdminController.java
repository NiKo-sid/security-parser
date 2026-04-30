package com.example.controller;

import jakarta.annotation.security.PermitAll;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard() {
        return "dashboard";
    }

    @PostMapping("/create")
    @Secured("ROLE_ADMIN")
    public String createUser() {
        return "create user";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile() {
        return "profile";
    }

    @GetMapping("/open")
    @PermitAll
    public String open() {
        return "open";
    }
}