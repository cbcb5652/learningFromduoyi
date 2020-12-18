package juc;

import java.util.concurrent.TimeUnit;

class Phone{

    public  synchronized void sendEmail() throws Exception{
        TimeUnit.SECONDS.sleep(4);
        System.out.println("--------sendEmail------------");
    }

    public static synchronized void sendSMS() throws Exception{
        System.out.println("--------sendSMS--------------");
    }

    public void hello(){
        System.out.println("--------hello-------------");
    }

}

/**
 * 题目： 多线程8锁
 * 1. 标准访问，请问先打印邮件还是短信？                   邮件   短信
 * 2. 邮件方法暂停4秒钟，请问先打印邮件还是短信               邮件    短信		 (同一个类里面的sychronized只能有一个线程访问，同一时刻)
 * 3. 新增一个普通方法hello() 请问先打印邮件还是Hello         hello    邮件    （hello 没有加sychronized）
 * 4. 两部手机，请问先打印邮件还是短信							不同的类，所以不影响   短信   邮件
 * 5. 两个静态同步方法，同一部手机，请问先打印邮件还是短信？			    邮件    短信   --->  和之前一样
 * 6. 两个静态同步方法，两部手机，请问先打印邮件还是短信？			静态同步方法后，两个手机内存中对应的都是同一个对象  邮件   短信 （锁的是手机模板）
 * 7. 1个普通同步方法，1个静态同步方法，1部手机，请问先打印邮件还是先打印短信？	       短信     邮件  （一个锁的是模板 一个锁的是实体对象）
 * 8. 1个普通同步方法，1个静态同步方法，2部手机，请问先打印邮件还是短信？				 短信     邮件  （同理，只是换了不同的对象）
 */
public class Lock8 {

    public static void main(String[] args) throws InterruptedException {
        Phone phone = new Phone();
        Phone phone1 = new Phone();

        new Thread(() ->{
            try{
                phone.sendEmail();
            }catch (Exception e){
                e.printStackTrace();
            }
        },"A").start();

        Thread.sleep(100);

        new Thread(() ->{
            try{
                phone1.sendSMS();
//                phone.hello();
            }catch (Exception e){
                e.printStackTrace();
            }
        },"B").start();

    }

}
