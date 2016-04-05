package com.cheese.jinwooklee.interfacedemo;

/**
 * Created by jinwooklee on 16-04-04.
 */
public class LayoutweightFloat {
    private float num = 0.0f;
    final private float max;
    final private float min;

    public LayoutweightFloat(float num, float max, float min){
        this.num = num;
        this.max = max;
        this.min = min;
    }

    public float getNum(){
        return this.num;
    }

    public void addNum(float num){
        WillMaxLimit(num);
    }

    public void NegNum(float num){
        WillMinLimit(num);
    }

    private void WillMaxLimit(float addingNum){
        if(this.num + addingNum >= this.max){
            this.num = this.max;
        }
        else{
            this.num = this.num + addingNum;
        }
    }

    private void WillMinLimit(float NegNum){
        if(this.num - NegNum <= this.min){
            this.num = this.min;
        }
        else {
            this.num = this.num - NegNum;
        }
    }
}
