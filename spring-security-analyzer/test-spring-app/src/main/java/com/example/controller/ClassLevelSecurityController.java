package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ClassLevelSecurityController {

    @GetMapping("/summary")
    public String summary() {
        return "summary";
    }

    @GetMapping("/details")
    public String details() {
        return "details";
    }
}