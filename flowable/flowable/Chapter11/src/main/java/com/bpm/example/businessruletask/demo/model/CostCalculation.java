package com.bpm.example.businessruletask.demo.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class CostCalculation implements Serializable {
    //原价
    double originalTotalPrice;
    //折扣比例
    double discountRatio;
    //实际价格
    double actualTotalPrice;
}
