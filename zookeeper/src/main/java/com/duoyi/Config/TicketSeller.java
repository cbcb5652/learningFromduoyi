package com.duoyi.Config;

public class TicketSeller {

    private void sell(){
        System.out.println("售票开始");
        int sleepMillis = 5000;
        try{
            Thread.sleep(sleepMillis);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("售票结束");
    }

    private void sellTicketWithLock() throws Exception{
        // 使sell方法 锁住
        MyLock myLock = new MyLock();
        // 获取锁
        myLock.acquireLock();
        sell();
        // 释放锁
        myLock.releaseLock();
    }

    public static void main(String[] args) throws Exception {
        TicketSeller ticketSeller = new TicketSeller();
        for (int i = 0; i < 10; i++) {
            ticketSeller.sellTicketWithLock();
        }
    }
}
