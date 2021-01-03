package jvm;

public class Native {

    private native void start();

    public static void main(String[] args) {

        Thread thread = new Thread();
        thread.start();
    }

}
