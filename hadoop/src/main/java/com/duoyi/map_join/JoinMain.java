package com.duoyi.map_join;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.net.URI;

public class JoinMain extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {

        //1.获取job对象
        Job job = Job.getInstance(super.getConf(), "map_join_job");

        //2.设置job对象（将小表放在分布式缓存中）
        //将小表放在分布式缓存中
//        DistributedCache.addCacheFile(new URI("hdfs://node01:8020/cache_file/product.txt"), super.getConf());
        job.addCacheFile(new URI("hdfs://node01:8020/cache_file/product.txt"));

        //第一步：设置输入类和输入的路径
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path("file:///D:\\input\\map_join_input"));
        //第二步： 设置mapper类和数据类型
        job.setMapperClass(MapJoinMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //第三 四 五 六 七

        //第八步：设置输出类和输出路径
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path("file:///D:\\out\\map_join-out"));

        //3.等待任务结束
        boolean b = job.waitForCompletion(true);

        return b ? 1 : 0;
    }

    public static void main(String[] args) throws Exception {
        // 启动job任务
        Configuration configuration = new Configuration();
        int run = ToolRunner.run(configuration, new JoinMain(), args);
        System.exit(run);
    }
}
