package com.example.runningweb.controller;


import com.example.runningweb.exception.BadRequestException;
import com.example.runningweb.service.FileService;
import com.example.runningweb.util.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileUtil fileUtil;


    //비동기로 처리
    @DeleteMapping("/file/{serverFileName}")
    @ResponseBody
    @PreAuthorize("isAuthenticated()") //로그인 한 멤버만 가능하게
    public ResponseEntity<String> deleteFile(@PathVariable("serverFileName")
                                             String serverFileName) {

        try {
            fileService.deleteFile(serverFileName);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                    .body(ex.getMessage());
        }

        return ResponseEntity.ok().body("삭제되었습니다.");
    }

    @GetMapping("/file/{serverFileName}")
    public void fileDownload(@PathVariable("serverFileName") String serverFileName,
                             HttpServletResponse response) throws IOException {

        File downloadFile = fileUtil.loadFile(serverFileName);
        if (downloadFile == null) {
            throw new BadRequestException("파일이 존재하지 않습니다.");
        }

        response.setContentType("application/download");
        response.setContentLength((int) downloadFile.length());
        response.setHeader("Content-disposition", "attachment;filename=\"" + downloadFile + "\"");

        FileInputStream fis = new FileInputStream(downloadFile);
        OutputStream os = response.getOutputStream();
        FileCopyUtils.copy(fis, os);
        fis.close();
        os.close();
    }

}
