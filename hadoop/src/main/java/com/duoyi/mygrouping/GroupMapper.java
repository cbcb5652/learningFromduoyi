package com.duoyi.mygrouping;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class GroupMapper extends Mapper<LongWritable, Text,OrderBean,Text> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1. 拆分行文本数据,得到订单的id，订单的金额
        String[] split = value.toString().split("\t");

        //2. 封装orderBean，得到k2
        OrderBean orderBean = new OrderBean();
        orderBean.setOrderId(split[0]);
        orderBean.setPrice(Double.valueOf(split[2]));

        //3. 将k2和v2写入上下文中
        context.write(orderBean,value);
    }
}
