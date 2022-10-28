package com.yuxuyao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.yuxuyao.domain.OrderDetail;
import com.yuxuyao.domain.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuxuyao.utils.R;

import java.util.List;

/**
 *
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 通过订单id查询订单明细，得到一个订单明细的集合
     * @param orderId
     * @return
     */
    List<OrderDetail> getOrderDetailListByOrderId(Long orderId);

    Page getPage(int page, int pageSize);
}


