package com.yuxuyao.service;

import com.yuxuyao.domain.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuxuyao.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 减少购物车菜品数量
     * @param shoppingCart
     * @return
     */
    ShoppingCart sub(@RequestBody ShoppingCart shoppingCart);

    /**
     * 添加购物车菜品数量
     * @param shoppingCart
     * @return
     */
    ShoppingCart add(@RequestBody ShoppingCart shoppingCart);
}
