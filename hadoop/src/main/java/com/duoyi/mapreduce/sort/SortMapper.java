package com.duoyi.mapreduce.sort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class SortMapper extends Mapper<LongWritable, Text,SortBean, NullWritable> {

    /**
     * map方法将k1和v1转为k2和v2
     *
     * k1           v1
     * 0           a  3
     * 5           b  7
     * -----------------------
     * k2                            v2
     * SortBean(a  3)         nullWritable
     * SortBean(b  7)         nullWritable
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        //1. 将行文本数据（v1）拆分，并将数据封装到SortBean对象，就可以得到k2
        String[] split = value.toString().split("\t");
        SortBean sortBean = new SortBean();
        sortBean.setWord(split[0]);
        sortBean.setNum(Integer.parseInt(split[1]));

        //2. 将k2和v2写入上下文对象
        context.write(sortBean,NullWritable.get());
    }
}
