package com.nickolas.mktbackend.request;

import com.nickolas.mktbackend.domain.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;

}
