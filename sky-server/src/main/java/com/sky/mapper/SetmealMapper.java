package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id 分类id

     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO 查询参数
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     * @param setmeal 套餐数据
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 批量插入套餐和菜品的关联关系
     * @param setmealDTO 套餐数据
     */
    void insertDish(SetmealDTO setmealDTO);
    
    /**
     * 更新套餐状态
     * @param setmeal 套餐数据
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);
    
    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return 套餐VO对象
     */
    @Select("select * from setmeal where id = #{id}")
    SetmealVO getById(Long id);
    
    /**
     * 根据套餐id查询套餐菜品关系
     * @param setmealId 套餐id
     * @return 套餐菜品关系列表
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getSetmealDishBySetmealId(Long setmealId);

    /**
     * 批量删除套餐
     * @param ids 套餐id列表
     */
    void delete(List<Long> ids);
    
    /**
     * 批量删除套餐菜品关系
     * @param ids 套餐id列表
     */
    void deleteSetmealDishBySetmealIds(List<Long> ids);
}