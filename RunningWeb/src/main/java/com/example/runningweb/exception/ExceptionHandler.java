package com.example.runningweb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {


    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    public String badRequest(){
        return "error/400.html";
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public String internalServerError(Exception ex){
        log.info("", ex);
        return "error/500.html";
    }


}
