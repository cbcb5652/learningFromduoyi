package com.duoyi.common_friends_step2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Arrays;

public class Step2Mapper extends Mapper<LongWritable, Text,Text,Text> {

    /**
     * K1           V1
     * 0        A-F-C-J-E-     b
     * ------------------------------------
     * K2           V2
     * A-C          B
     * A-E          B
     * A-F          B
     * C-E          B
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1: 拆分行文本数据，结果的第二部分可以得到v2
        String[] split = value.toString().split("\t");
        String friendStr = split[1];

        //2. 继续以'-' 为分割符拆分行文本数据第一部分，再得到数组
        String[] userArray = split[0].split("-");

        //3. 对数组做一个排序
        Arrays.sort(userArray);

        //4. 对数组中的元素进行两两组合，得到k2
        for (int i = 0; i < userArray.length - 1; i++) {
            for (int j = i+1; j < userArray.length; j++) {
                //5. 将k2和v2写入上下文中
                context.write(new Text(userArray[i] + "-" + userArray[j]),new Text(friendStr));
            }
        }

    }
}
