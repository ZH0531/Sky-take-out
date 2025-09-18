package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    @Autowired
    private  AliOssUtil aliOssUtil;
    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        try {
            String url = aliOssUtil.upload(file.getBytes(), Objects.requireNonNull(file.getOriginalFilename()));
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
