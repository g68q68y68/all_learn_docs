package com.bpm.example.servicetask.demo2.bean;

import java.io.Serializable;

public class CalculationBean implements Serializable{
    public double calculationAmount(double unitPrice, int quantity) {
        return unitPrice * quantity;
    }
}
