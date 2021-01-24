package com.duoyi.mygrouping;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.awt.*;

public class OrderPartition extends Partitioner<OrderBean, Text> {
    // 分区规则:根据订单的ID实现分区

    /**
     * @param orderBean     K2
     * @param text          V2
     * @param i             ReduceTask个数
     * @return              分区的编号
     */
    @Override
    public int getPartition(OrderBean orderBean, Text text, int i) {
        return (orderBean.getOrderId().hashCode() & 2147483647) % i;
    }
}
