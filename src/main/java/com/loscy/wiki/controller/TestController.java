package com.loscy.wiki.controller;

import com.loscy.wiki.domain.Test;
import com.loscy.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {

    @Resource
    private TestService testService;

    @Value("${test.hello}")
    private String testHello;

    /**
     * GTE,POST,PUT,DELETE
     *
     * @return
     */
    @GetMapping ("/hello")
    public String hello(){
        return "Hello World!" + testHello;
    }

    @PostMapping ("/hello/post")
    public String helloPost(String name){
        return "Hello World!Post, " + name;
    }

    @GetMapping ("/test/list")
    public List<Test> list(){
        return testService.list();
    }
}
