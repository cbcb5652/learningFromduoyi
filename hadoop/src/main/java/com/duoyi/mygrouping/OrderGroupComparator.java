package com.duoyi.mygrouping;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 1. 继承WritableComparator
 * 2. 调用父类的有参构造
 * 3. 指定分组的规则(重写一个方法)
 */
public class OrderGroupComparator extends WritableComparator {

    public OrderGroupComparator() {
        super(OrderBean.class,true);
    }

    // 指定分组的规则
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        //3.1 对形参做强制类型转换
        OrderBean first = (OrderBean)a;
        OrderBean second = (OrderBean)b;

        //3.2 指定分组规则
        return first.getOrderId().compareTo(second.getOrderId());
    }
}
