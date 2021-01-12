package com.duoyi.mygrouping;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OrderBean implements WritableComparable<OrderBean> {

    private String orderId;
    private Double price;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return orderId + "\t" + price;
    }

    // 指定排序规则
    @Override
    public int compareTo(OrderBean o) {
        // 先去比较订单ID，如果订单ID一致，则排序订单金额(降序)
        int i = this.orderId.compareTo(o.orderId);
        if (i == 0){
            i = this.price.compareTo(o.getPrice()) * -1;
        }
        return i;
    }

    // 序列化
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(orderId);
        dataOutput.writeDouble(price);
    }

    // 反序列化
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.orderId = dataInput.readUTF();
        this.price = dataInput.readDouble();
    }
}
