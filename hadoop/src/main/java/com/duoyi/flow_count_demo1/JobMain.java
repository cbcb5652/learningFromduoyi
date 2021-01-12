package com.duoyi.flow_count_demo1;

import com.duoyi.mapreduce.WordCountMapper;
import com.duoyi.mapreduce.WordCountReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.net.URI;

public class JobMain extends Configured implements Tool {

    // 该方法用于指定一个job任务
    @Override
    public int run(String[] strings) throws Exception {
        //1. 创建一个job任务对象
        Job job = Job.getInstance(super.getConf(), "mapreduce_flowcount");
        // 如果打包运行出错则需要加该配置
        job.setJarByClass(JobMain.class);

        //2. 配置job任务对象（八个步骤）

        // 第一步：指定文件的读取方式和读取路径
        job.setInputFormatClass(TextInputFormat.class);
//        TextInputFormat.addInputPath(job,new Path("hdfs://node01:8020/wordcount"));
        TextInputFormat.addInputPath(job,new Path("file:///D:\\input\\flowcount_input"));


        // 第二步: 指定Map阶段的处理方式和数据类型
        job.setMapperClass(FlowCountMapper.class);
        // 设置Map阶段K2的类型
        job.setMapOutputKeyClass(Text.class);
        // 设置Map阶段的v2的类型
        job.setMapOutputValueClass(FlowBean.class);

        // 第三，四，五，六 采用默认的方式

        // 第七步: 指定Reduce阶段的处理方式和数据类型
        job.setReducerClass(FlowCountReducer.class);
        // 设置k3的类型
        job.setOutputKeyClass(Text.class);
        // 设置v3的类型
        job.setMapOutputValueClass(FlowBean.class);

        // 第八步： 设置输出类型
        job.setOutputFormatClass(TextOutputFormat.class);
        // 设置输出的路径


        TextOutputFormat.setOutputPath(job,new Path("file:///D:\\out\\flowcount_out"));


        // 等待任务结束
        boolean flag = job.waitForCompletion(true);
        return flag ? 0:1;
    }

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();

        // 启动job任务
        int run = ToolRunner.run(configuration, new JobMain(), args);
        System.exit(run);
    }

}
