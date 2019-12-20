package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {
    @Autowired
    private FastFileStorageClient storageClient;

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/gif","image/jpeg");
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);
    public String uploadImage(MultipartFile file) {
        String originalFilename = null;
        try {
            originalFilename = file.getOriginalFilename();
            //校验文件类型
            String contentType = file.getContentType();
            if(!CONTENT_TYPES.contains(contentType)){
                logger.info("上传的不是图片文件" + contentType);
                return null;
            }
            //校验文件内容是否符合图片规范
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null) {
                logger.info("上传的文件不符合规范");
                return null;
            }
            //保存到服务器
            //file.transferTo(new File("D:\\project_liu\\mall_online\\code\\image\\"+originalFilename));
            //获取后缀名
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            //返回url,进行回显
            return "http://image.leyou.com/"+storePath.getFullPath();
        } catch (IOException e) {
            logger.info("服务器内部错误"+originalFilename);
            e.printStackTrace();
        }
       return null;
    }
}
