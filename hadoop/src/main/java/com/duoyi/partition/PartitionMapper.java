package com.duoyi.partition;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * K1:   行偏移量 LongWritable
 * V1:   行文本数据    Text
 *
 * K2:   行文本数据
 * V2:   NullWritable
 */
public class PartitionMapper extends Mapper<LongWritable, Text,Text, NullWritable> {


    // map方法将k1和v1 转为k2和v2
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //方式1：定义计数器
        Counter counter = context.getCounter("MR_COUNTER", "partition_counter");
        //每次执行该方法，则计数器变量的值加1
        counter.increment(1L);

        context.write(value,NullWritable.get());
    }
}
