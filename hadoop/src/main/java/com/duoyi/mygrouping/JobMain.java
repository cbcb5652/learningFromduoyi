package com.duoyi.mygrouping;

import com.duoyi.myoutputformat.MyOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JobMain extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        //1. 获取job对象
        Job job = Job.getInstance(super.getConf(), "mygroup_job");

        //2. 设置job任务
            // 第一步：设置输入类和输入的路径
            job.setInputFormatClass(TextInputFormat.class);
            TextInputFormat.addInputPath(job,new Path("file:///D:\\input\\mygroup_input"));

            // 第二步：设置mapper类和数据类型
            job.setMapperClass(GroupMapper.class);
            job.setMapOutputKeyClass(OrderBean.class);
            job.setMapOutputValueClass(Text.class);

            // 设置分区
        job.setPartitionerClass(OrderPartition.class);

            // 设置分组
        job.setGroupingComparatorClass(OrderGroupComparator.class);

            //设置reducer
        job.setReducerClass(GroupReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

            // 设置输出类和输出的路径
        job.setOutputFormatClass(TextOutputFormat.class);
        MyOutputFormat.setOutputPath(job,new Path("file:///D:\\out\\mygroup_output"));

        //3. 等待任务结束
        boolean b = job.waitForCompletion(true);

        return b ? 0 :1;
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        int run = ToolRunner.run(configuration, new JobMain(), args);
        System.exit(run);
    }
}
