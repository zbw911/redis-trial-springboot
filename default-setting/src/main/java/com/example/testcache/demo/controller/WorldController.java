package com.example.testcache.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author zhangbaowei
 * Create  on 2019/11/20 10:19.
 */
@RestController
@RequestMapping("/w")
//@CacheConfig(cacheNames = "hello")
public class WorldController {

    @RequestMapping("/now")

    public String now() {
        return new Date().toString();
    }

    @RequestMapping("/now2")

    public String now2() {
        return new Date().toString();
    }
}
