package com.yuxuyao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuxuyao.domain.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuxuyao.dto.DishDto;

import java.util.List;

/**
 *
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表dish、dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应口味信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新对应口味信息
    void updateWithFlavor(DishDto dishDto);
    //设置起售状态
    void saleStatus(Integer status, List<Long> ids);
    //批量删除
    void deleteByIds(List<Long> ids);
    //分页查询
    Page<DishDto> pageFind(int page,int pageSize,String name);
}
