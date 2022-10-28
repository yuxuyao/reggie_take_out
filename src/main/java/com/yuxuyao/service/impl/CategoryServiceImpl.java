package com.yuxuyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuxuyao.domain.Category;
import com.yuxuyao.domain.Dish;
import com.yuxuyao.domain.Setmeal;
import com.yuxuyao.service.CategoryService;
import com.yuxuyao.mapper.CategoryMapper;
import com.yuxuyao.service.DishService;
import com.yuxuyao.service.SetmealDishService;
import com.yuxuyao.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealDishService setmealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询当前分类是否关联了菜品，如果已经关联，，跑出一个业务异常
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        Long count = dishService.count(dishLambdaQueryWrapper);
        if (count>0){
            //已经关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，，跑出一个业务异常
        LambdaQueryWrapper<Setmeal> setMealServiceLambdaQueryWrapper = new LambdaQueryWrapper();
        setMealServiceLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count1 = setmealService.count();
        if (count1>0){
            //已经关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);


    }
}




