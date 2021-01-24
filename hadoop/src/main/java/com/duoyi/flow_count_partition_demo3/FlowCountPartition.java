package com.duoyi.flow_count_partition_demo3;

import org.apache.hadoop.hdfs.tools.CacheAdmin;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class FlowCountPartition extends Partitioner<Text,FlowBean> {

    /*
    该方法用来指定分区的规则:
        135             开头的数据分到一个区
        136             开头的数据分到一个区
        137             开头的数据分到一个区
        其他分区

     参数:
        text:   K2      手机号
        flowBean:   V2
        i:      :   ReduceTask的个数
     */
    @Override
    public int getPartition(Text text, FlowBean flowBean, int i) {

        //1. 获取手机号
        String phoneNumber = text.toString();


        //2. 判断手机号以什么开头，返回对应的手机编号(0-3)
        if (phoneNumber.startsWith("135")){
            return 0;
        }else if (phoneNumber.startsWith("136")){
            return 1;
        }else if (phoneNumber.startsWith("137")){
            return 2;
        }

        return 3;
    }
}
