package com.example.runningweb.util;

import com.example.runningweb.domain.AttachFile;
import com.example.runningweb.domain.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class FileUtil {

    @Value("${spring.file.path}")
    private String innerPath;

    // 파일 업로드 후 DB에 저장할 객체 반환
    public AttachFile attachFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile == null || multipartFile.isEmpty()){
            return null;
        }

        String originalName = multipartFile.getOriginalFilename(); //유저가 등록한 이름
        String serverFileName = createServerFileName(originalName);
        multipartFile.transferTo(new File(innerPath + serverFileName)); //파일 저장


        return AttachFile.builder()
                .originalName(originalName)
                .serverFileName(serverFileName)
                .build();
    }

    // 서버에 저장할 파일 이름 저장
    private String createServerFileName(String originalName) {
        int pos = originalName.lastIndexOf('.'); //마지막 . --> 확장자 위치
        String ext = originalName.substring(pos+1); //확장자 정보 추출
        String UUID = java.util.UUID.randomUUID().toString();
        return UUID + '.' + ext;
    }

    public String getFullPath(String fileName){
        return innerPath + fileName;
    }

    //삭제한다 --> 만약 삭제하려다가 해당 파일을 사용하려는 경우가 있을까?????
    // 없을듯 --> 이용하려는 경우는 다운로드밖에 없는데 이는 안됨
    public void deleteFiles(List<String> storedServerFileName) {
        for (String fileName : storedServerFileName) {
            deleteFile(fileName);
        }

    }

    public void deleteFile(String serverFileName) {
        try{
            Path filePath = Paths.get(innerPath + serverFileName);
            Files.delete(filePath);
        } catch (IOException e){
            log.info("파일 삭제 실패.. {}", e.getMessage());
            throw new RuntimeException("잘못된 파일 삭제 요청입니다.");
        }
    }



}
