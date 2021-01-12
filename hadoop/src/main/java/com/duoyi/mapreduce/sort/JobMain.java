package com.duoyi.mapreduce.sort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JobMain extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {

        //1. 创建job对象
        Job job = Job.getInstance(super.getConf(), "mapreduce_sort");

        //2. 配置job任务(八个步骤)
        //第一步： 设置输入类和输入的路径
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path("hdfs://node01:8020/input/sort_input"));
        //第二步： 设置mapper类和输入类型
        job.setMapperClass(SortMapper.class);
        job.setMapOutputKeyClass(SortBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        //第三步，四五六

        //第七步：设置Reducer类和类型
        job.setReducerClass(SortReducer.class);
        job.setOutputKeyClass(SortBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        //第八步： 设置输出类和输出的路径
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path("hdfs://node01:8020/sort_out"));

        //3. 等待任务结束
        boolean bl = job.waitForCompletion(true);

        return bl ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();

        // 启动job任务
        int run = ToolRunner.run(configuration, new JobMain(), args);
        System.exit(run);
    }

}
