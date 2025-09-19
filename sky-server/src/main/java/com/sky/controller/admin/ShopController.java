package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {
    @Autowired
    private ShopService shopService;

    /**
     * 设置店铺营业状态
     *
     * @param status 营业状态
     */
    @PutMapping("/{status}")
    public Result<Void> setStatus(@PathVariable Integer status) {
        log.info("修改店铺营业状态：{}", status == 1 ? "营业中" : "打烊中");
        shopService.setStatus(status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = shopService.getStatus();
        log.info("查询店铺营业状态：{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }

}
