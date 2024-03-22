package com.example.runningweb.util;

import com.example.runningweb.domain.AttachFile;
import com.example.runningweb.domain.Board;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
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


//    public void deleteFiles(List<AttachFile> deleteFiles){
//
//
//    }

}
