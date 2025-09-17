package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DishNotFoundException;
import com.sky.mapper.DishMapper;
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
        dishMapper.save(dish);
        // 获取菜品的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            // 遍历加上菜品id（id来自useGeneratedKeys="true" keyProperty="id"）
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            // 插入菜品口味数据
            dishMapper.saveFlavors(flavors);
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
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateDish(dish);

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
    public void delete(List<Long> ids) {
        dishMapper.delete(ids);
        dishMapper.deleteFlavorsByDishId(ids);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO 菜品数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DishDTO dishDTO) {
        // 更新菜品基础信息
        Dish dish = Dish.builder()
                .id(dishDTO.getId())
                .name(dishDTO.getName())
                .categoryId(dishDTO.getCategoryId())
                .price(dishDTO.getPrice())
                .image(dishDTO.getImage())
                .description(dishDTO.getDescription())
                .status(dishDTO.getStatus())
                .build();
        dishMapper.updateDish(dish);
        // 更新菜品口味数据
        updateFlavors(dishDTO);
    }

    /**
     * 更新菜品口味数据
     *
     * @param dishDTO 菜品数据
     */
    private void updateFlavors(DishDTO dishDTO) {
        // 删除当前菜品的口味数据(将单个ID转成列表 以适配批量删除)
        dishMapper.deleteFlavorsByDishId(Collections.singletonList(dishDTO.getId()));
        // 获取新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        log.info("菜品口味数据：{}", flavors);
        if (flavors != null && !flavors.isEmpty()) {
            // 遍历加上菜品id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            // 插入新的口味数据
            dishMapper.saveFlavors(flavors);
        }
    }


}
