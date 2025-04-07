package com.nickolas.mktbackend.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateOrderRequest {
    private String deliveryAddress;
}
