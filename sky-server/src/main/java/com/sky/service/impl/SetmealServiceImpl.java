package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询套餐
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开启分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        //查询数据
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        //封装结果并返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SetmealDTO setmealDTO) {
        // 属性拷贝
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 插入套餐基础数据
        setmealMapper.insert(setmeal);
        // 插入套餐菜品数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 设置套餐id
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        // 插入套餐菜品数据
        setmealMapper.insertDish(setmealDTO);
        log.info("插入套餐菜品数据：{}", setmealDishes);
    }
    
    /**
     * 套餐起售停售
     * @param status 状态
     * @param id 套餐id
     */
    @Override
    public void setStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
    
    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return 套餐VO对象
     */
    @Override
    public SetmealVO getSetmealById(Long id) {
        // 查询套餐基础数据
        SetmealVO setmealVO = setmealMapper.getById(id);
        // 查询套餐菜品关系数据
        List<SetmealDish> setmealDishes = setmealMapper.getSetmealDishBySetmealId(id);
        // 设置套餐菜品关系数据
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }
    
    /**
     * 修改套餐
     * @param setmealDTO 套餐数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SetmealDTO setmealDTO) {
        // 更新套餐基础信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        
        // 更新套餐菜品关系数据 - 全删后插入
        // 删除当前套餐的菜品关系数据（兼容批量删除）
        setmealMapper.deleteSetmealDishBySetmealIds(Collections.singletonList(setmealDTO.getId()));
        
        // 获取新的套餐菜品关系数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            // 设置套餐id
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
            // 插入新的套餐菜品关系数据
            setmealMapper.insertDish(setmealDTO);
        }
    }
    
    /**
     * 批量删除套餐
     * @param ids 套餐id列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 删除套餐
        setmealMapper.delete(ids);
        // 删除套餐菜品关系
        setmealMapper.deleteSetmealDishBySetmealIds(ids);
    }
}