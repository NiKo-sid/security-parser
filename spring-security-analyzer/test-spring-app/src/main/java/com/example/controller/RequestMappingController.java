package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class RequestMappingController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "hello";
    }

    @RequestMapping(path = "/save", method = RequestMethod.POST)
    public String save() {
        return "save";
    }

    @RequestMapping(value = "/multi", method = {RequestMethod.GET, RequestMethod.POST})
    public String multi() {
        return "multi";
    }

    @RequestMapping("/open")
    public String open() {
        return "open";
    }
}