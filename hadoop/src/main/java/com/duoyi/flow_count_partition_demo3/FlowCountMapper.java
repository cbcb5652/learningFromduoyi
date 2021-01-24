package com.duoyi.flow_count_partition_demo3;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowCountMapper extends Mapper<LongWritable, Text,Text, FlowBean> {

    /*
    将k1和v1 转为 k2和v2
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        //1. 拆分行文本数据，得到手机号  ---------->   K2
        String[] split = value.toString().split("\t");
        String phoneNum = split[1];

        //2. 创建FlowBean对象，并从行文本数据拆分出流量的四个字段，并将四个流量字段的值赋给FlowBean对象
        FlowBean flowBean = new FlowBean();
        flowBean.setUpFlow(Integer.parseInt(split[6]));
        flowBean.setDownFlow(Integer.parseInt(split[7]));
        flowBean.setUpCountFlow(Integer.parseInt(split[8]));
        flowBean.setDownCountFlow(Integer.parseInt(split[9]));

        // 将k2和v2写入上下文中
        context.write(new Text(phoneNum),flowBean);
    }
}
