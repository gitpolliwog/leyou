package com.leyou.upload.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class UploadService {

    private static final List<String> ALLOW_TYPES=Arrays.asList("image/jpeg","image/png");

    public String uploadImage(MultipartFile file) {
        try {
            String contentType=file.getContentType();
            System.out.println(contentType);
            if(!ALLOW_TYPES.contains(contentType)){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            File des = new File("C:\\Users\\taok\\Desktop", file.getOriginalFilename());
            file.transferTo(des);
            return "http://image.leyou.com"+file.getOriginalFilename();
        }catch (IOException e){
           throw  new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }

    }
}
