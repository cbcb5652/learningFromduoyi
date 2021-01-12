package jvm.java1;

public class OperandStackTest {

    public void add(){
        // 第1类问题
        int i1 = 10;
        i1++;

        int i2 = 10;
        ++i2;

        //第2类问题
        int i3 = 10;
        int i4 = i3++;

        int i5 = 10;
        int i6 = ++i5;

        //第3类问题
        int i7 = 10;
        i7 = i7++;

        int i8 = 10;
        i8 = ++ i8;

        // 第4类问题
        int i9 = 10;
        int i10 = i9++ + ++i9;
    }

}
