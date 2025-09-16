package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private  AliOssUtil aliOssUtil;
    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        String url = aliOssUtil.upload(file.getBytes(), Objects.requireNonNull(file.getOriginalFilename()));
        return Result.success(url);
    }

}
