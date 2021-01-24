package jvm.Heap;

public class HeapSpaceInitial {
    public static void main(String[] args) {

        // 返回java虚拟机中的堆内存总量
        long initialMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        // 返回Java虚拟机试图使用的最大堆内存量
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;

        System.out.println("-Xms:"+initialMemory + "M");
        System.out.println("-Xmx:"+maxMemory + "M");

        try{
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
