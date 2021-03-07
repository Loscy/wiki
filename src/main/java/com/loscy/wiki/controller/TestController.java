package com.loscy.wiki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    /**
     * GTE,POST,PUT,DELETE
     *
     *
     * @return
     */
    @GetMapping ("/hello")
    public String hello(){
        return "Hello World!";
    }

    @PostMapping ("/hello/post")
    public String helloPost(String name){
        return "Hello World!Post, " + name;
    }
}
