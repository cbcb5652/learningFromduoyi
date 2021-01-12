package com.duoyi.mapreduce.sort;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SortBean implements WritableComparable<SortBean> {

    private String word;
    private int num;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return word + "\t" + num ;
    }

    // 实现比较器，指定排序规则

    /**
     *  规则：
     *      第一列： 按照字典序列进行排序
     *      第二列： 相同的时候，第二列（num）按照升序进行排序
     */
    @Override
    public int compareTo(SortBean o) {
        // 先对第一列排序：word排序
        int result = this.word.compareTo(o.getWord());
        // 如果第一列相同，则按照第二列进行排序
        if (result == 0){
            return this.num - o.getNum();
        }
        return result;
    }

    // 实现序列化
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(word);
        dataOutput.writeInt(num);
    }

    // 实现反序列化
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.word = dataInput.readUTF();
        this.num = dataInput.readInt();
    }
}
