package com.duoyi.common_friends_step2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class Step2Reducer extends Reducer<Text,Text,Text,Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        //1. 原来k2就是k3
        //2. 将集合进行遍历，将集合中的元素拼接，得到v3
        StringBuffer stringBuffer = new StringBuffer();
        for (Text value : values) {
            stringBuffer.append(value.toString()).append("-");
        }
        //3. 将k3和v3写入上下文中
        context.write(key,new Text(stringBuffer.toString()));
    }
}
