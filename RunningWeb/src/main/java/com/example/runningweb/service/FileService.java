package com.example.runningweb.service;

import com.example.runningweb.repository.FileRepository;
import com.example.runningweb.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileUtil fileUtil;


    // 파일을 삭제한다.
    public void deleteFile(String serverFileName) {

        // DB 삭제 처리
        int deleted = fileRepository.deleteByServerFileName(serverFileName);
        fileUtil.deleteFile(serverFileName);

    }
}
