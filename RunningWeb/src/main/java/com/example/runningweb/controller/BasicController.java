package com.example.runningweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BasicController {

    @GetMapping("/main")
    public String main(){
        return "/main";
    }

}
