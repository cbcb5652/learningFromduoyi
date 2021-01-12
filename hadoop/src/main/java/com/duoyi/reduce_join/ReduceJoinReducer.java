package com.duoyi.reduce_join;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class ReduceJoinReducer extends Reducer<Text,Text,Text,Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        //1. 遍历集合，获取V3(first + second)

        String first = "";
        String second = "";
        for (Text value : values) {
            // 商品信息
            if (value.toString().startsWith("p")){
                first = value.toString();
            }else {
                second += "||" + value.toString();
            }
        }

        //2. 将K3和V3写入上下文中
        context.write(key,new Text(first + "\t" + second));

    }
}
