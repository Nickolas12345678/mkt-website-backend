package com.nickolas.mktbackend.request;

import com.nickolas.mktbackend.domain.DeliveryMethod;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateOrderRequest {
    private String deliveryAddress;
    private DeliveryMethod deliveryMethod;
}
