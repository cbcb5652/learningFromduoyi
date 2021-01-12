package com.duoyi.common_friends_step1;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class step1Mapper extends Mapper<LongWritable, Text,Text,Text> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        //1. 以冒号拆分行文本数据：冒号左边就是v2
        String[] split = value.toString().split(":");
        String userStr = split[0];

        //2. 将冒号右边的字符串以逗号拆分，每个成员就是k2
        String[] split1 = split[1].split(",");
        for (String s : split1) {
            //3. 将k2和v2写入上下文中
            context.write(new Text(s),new Text(userStr));
        }
    }
}
