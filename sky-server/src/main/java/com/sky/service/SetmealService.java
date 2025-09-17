package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void save(SetmealDTO setmealDTO);
    
    void setStatus(Integer status, Long id);
    
    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return 套餐VO对象
     */
    SetmealVO getSetmealById(Long id);
    
    /**
     * 修改套餐
     * @param setmealDTO 套餐数据
     */
    void update(SetmealDTO setmealDTO);
    
    /**
     * 批量删除套餐
     * @param ids 套餐id列表
     */
    void delete(List<Long> ids);
}