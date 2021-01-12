package com.duoyi.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 四个泛型解释
 *     KEYIN: K1的类型
 *     VALUEIN: V1的类型
 *
 *     KEYOUT: K2的类型
 *     VALUEOUT: V2的类型
 */
public class WordCountMapper extends Mapper<LongWritable, Text,Text,LongWritable> {

    // map方法就是将K1和V1 转换为K2和v2
    /**
     * 参数：
     *  key       :  k1     行偏移量
     *  value     :  b1     每一行的文本数据
     *  context   :  表示上下文对象
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        Text text = new Text();
        LongWritable longWritable = new LongWritable();

        //1. 将一行的文本数据进行拆分
        String[] split = value.toString().split(",");

        //2. 遍历数组，组装k2 和 v2
        for (String word : split) {
            //3. 将k2和v2写入上下文
            text.set(word);
            longWritable.set(1);
            context.write(text,longWritable);
        }
    }
}






























