package com.duoyi.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * 四个泛型解释：
 * KEYIN:    K2类型
 * VAKUEIN:  V2类型
 * <p>
 * KEYOUT:   K3类型
 * VALUEOUT: V3类型
 */
public class WordCountReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

    /**
     * reduce方法作用： 将新的K2和V2转为 K3和V3，将K3和V3写入上下文中
     * @param key            新的k2
     * @param values         集合  新的v2
     * @param context        表示上下文对象
     *
     *    如何将新的k2和v2转为k3和v3
     */
    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
        long count = 0;
        //1. 遍历集合，将集合中的数字相加，得到v3
        for (LongWritable value : values) {
            count += value.get();
        }

        //2. 将k3和v3写入上下文中
        context.write(key,new LongWritable(count));
    }
}
