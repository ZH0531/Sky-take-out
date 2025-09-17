package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品分页参数
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询参数：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result<Void> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 菜品修改
     * @param dishDTO
     */
    @PutMapping
    public Result<Void> update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 根据菜品id查询菜品
     * @param id 菜品id
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("查询菜品详情：{}", id);
        DishVO dishVO = dishService.getDishById(id);
        log.info("查询到的菜品详情：{}", dishVO);
        return Result.success(dishVO);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类id
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Integer categoryId) {
        log.info("查询分类id：{}", categoryId);
        List<Dish> list = dishService.list(Long.valueOf(categoryId));
        return Result.success(list);
    }

    /**
     * 菜品批量删除
     * @param ids 菜品id
     */
    @DeleteMapping
    public Result<Void> delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品：{}", ids);
        dishService.delete(ids);
        return Result.success();
    }



    /**
     * 菜品起售停售
     */
    @PostMapping("/status/{status}")
    public Result<Void> status(@PathVariable Integer status, Long id) {
        log.info("菜品id：{}，操作：{}", id, status == 1 ? "启用" : "禁用");
        dishService.setStatus(status, id);
        return Result.success();
    }
}
