package com.yuxuyao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuxuyao.domain.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuxuyao.dto.SetmealDto;

import java.util.List;

/**
 *
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<SetmealDto> pageFind(int page, int pageSize, String name);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 更新套餐信息，同时更新对应菜品信息
     * @param setmealDto
     */
    void updateWithDish(SetmealDto setmealDto);

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    SetmealDto getByIdWithDish(Long id);

    void saleStatus(Integer status, List<Long> ids);
}
