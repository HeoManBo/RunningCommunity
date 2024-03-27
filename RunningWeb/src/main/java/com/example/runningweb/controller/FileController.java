package com.example.runningweb.controller;


import com.example.runningweb.repository.FileRepository;
import com.example.runningweb.service.FileService;
import com.example.runningweb.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;


    //비동기로 처리
    @DeleteMapping("/file/{serverFileName}")
    @ResponseBody
    @PreAuthorize("isAuthenticated()") //로그인 한 멤버만 가능하게
    public ResponseEntity<String> deleteFile(@PathVariable("serverFileName")
                                           String serverFileName){

        try{
            fileService.deleteFile(serverFileName);
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest()
                    .body(ex.getMessage());
        }

        return ResponseEntity.ok().body("삭제되었습니다.");
    }

}
