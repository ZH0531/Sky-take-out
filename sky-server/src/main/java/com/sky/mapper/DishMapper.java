package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 菜品查询
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 插入菜品数据
     */
    @AutoFill(OperationType.INSERT)
    void save(Dish dish);

    /**
     * 批量插入菜品口味数据
     */
    void saveFlavors(List<DishFlavor> flavors);

    /**
     * 根据id查询菜品和口味
     */
    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);

    /**
     * 根据id删除菜品口味
     */
    void deleteFlavorsByDishId(Long id);

    /**
     * 根据id查询菜品
     */
    DishVO getDishById(Long id);

    /**
     * 根据菜品id查询菜品口味
     */
    List<DishFlavor> getFlavorsByDishId(Long id);
}
