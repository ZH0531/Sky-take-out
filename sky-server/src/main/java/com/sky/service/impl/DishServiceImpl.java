package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.*;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 菜品分页查询参数
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开启分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //查询数据
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        //封装结果并返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DishDTO dishDTO) {
        // 创建菜品对象
        Dish dish = new Dish();
        // 属性拷贝
        BeanUtils.copyProperties(dishDTO, dish);
        // 插入菜品数据
        dishMapper.insert(dish);
        // 获取菜品的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            // 遍历加上菜品id（id来自useGeneratedKeys="true" keyProperty="id"）
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            // 插入菜品口味数据
            dishMapper.insertFlavors(flavors);
        }
    }

    /**
     * 根据id查询菜品和对应的口味数据
     *
     * @param id 菜品id
     * @return 菜品数据
     */
    @Override
    public DishVO getDishById(Long id) {
        // 查询菜品数据
        DishVO dishVO = dishMapper.getDishById(id);
        // 判断菜品是否存在
        if (dishVO == null) throw new DishNotFoundException(MessageConstant.DISH_NOT_FOUND);
        // 获取菜品的口味数据
        dishVO.setFlavors(dishMapper.getFlavorsByDishId(id));
        return dishVO;
    }

    /**
     * 菜品起售停售
     *
     * @param status 状态
     * @param id     菜品id
     */
    @Override
    public void setStatus(Integer status, Long id) {
        // 已起售套餐内包含该菜品，无法停售
        if (status == 0) { // 拦截停售菜品操作
            setmealMapper.getSetmealByDishId(id).forEach(setmeal -> {
                if (setmeal.getStatus() == 1) {// 套餐已启售 且包含该菜品
                    throw new DishStopSellingFailedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
                }
            });
        }
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateDish(dish);
        log.info("更新菜品状态成功");
    }

    /**
     * 获取指定分类下的菜品
     *
     * @param id 分类id
     * @return 菜品列表
     */
    @Override
    public List<Dish> list(Long id) {
        return dishMapper.list(id);
    }

    /**
     * 批量删除菜品
     *
     * @param ids 菜品id列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 起售中的菜品不能删除
        ids.forEach(id -> {
            DishVO dish = dishMapper.getDishById(id);
            if (dish.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        // 套餐包含菜品时不能删除
        ids.forEach(id -> {
            // 获取包含该菜品的套餐信息
            List<Setmeal> setmealList = setmealMapper.getSetmealByDishId(id);
            if (!setmealList.isEmpty()) {
                throw new DeletionNotAllowedException(MessageConstant.DELETE_DISH_BE_RELATED_BY_SETMEAL);
            }
        });
        dishMapper.delete(ids);
        dishMapper.deleteFlavorsByDishId(ids);
        log.info("批量删除菜品口味：{}", ids);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO 菜品数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DishDTO dishDTO) {
        // 获取当前菜品的停售状态
        DishVO dishVO = dishMapper.getDishById(dishDTO.getId());
        if (dishVO.getStatus() == 1) {
            throw new DishChangeFailed(MessageConstant.DISH_CHANGE_FAILED);
        }
        // 更新菜品基础信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);
        log.info("更新菜品成功");

        // 更新菜品口味数据
        // 删除当前菜品的口味数据
        dishMapper.deleteFlavorsByDishId(Collections.singletonList(dishDTO.getId()));

        // 获取新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            // 遍历加上菜品id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            // 插入新的口味数据
            dishMapper.insertFlavors(flavors);
        }
        log.info("插入新的菜品口味数据成功：{}", flavors);
    }


}
