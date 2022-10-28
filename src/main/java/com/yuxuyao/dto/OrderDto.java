package com.yuxuyao.dto;

import com.yuxuyao.domain.OrderDetail;
import com.yuxuyao.domain.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author yuxuyao
 * @date 2022/10/21 - 17:52
 */
@Data
public class OrderDto extends Orders {

    private List<OrderDetail> orderDetails;
}


