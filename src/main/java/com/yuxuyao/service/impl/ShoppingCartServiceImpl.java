package com.yuxuyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuxuyao.domain.ShoppingCart;
import com.yuxuyao.service.ShoppingCartService;
import com.yuxuyao.mapper.ShoppingCartMapper;
import com.yuxuyao.utils.BaseContext;
import com.yuxuyao.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 *
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{
    @Autowired
    private ShoppingCartService shoppingCartService;

    public ShoppingCart sub(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //减少的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            Integer number1 = shoppingCart1.getNumber();
            if (number1 == 1) {
                shoppingCartService.removeById(shoppingCart1);
            } else {
                shoppingCart1.setNumber(--number1);
                shoppingCartService.updateById(shoppingCart1);
            }
            return shoppingCart1;
        }else {
            //减少的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            ShoppingCart shoppingCart2 = shoppingCartService.getOne(queryWrapper);
            Integer number2 = shoppingCart2.getNumber();
            if (number2 == 1) {
                shoppingCartService.removeById(shoppingCart2);
            } else {
                shoppingCart2.setNumber(--number2);
                shoppingCartService.updateById(shoppingCart2);
            }
            return shoppingCart2;
        }

    }


    public ShoppingCart add(ShoppingCart shoppingCart) {
        //设置点餐用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询当前菜品或者套餐是否在购物车中已经存在则数量+1
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        if (shoppingCart1 != null) {
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCartService.updateById(shoppingCart1);
        } else {
            //不存在
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        }
        return shoppingCart1;
    }
}




