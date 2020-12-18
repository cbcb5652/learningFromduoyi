package lambda.Express;

/**
 * 加上这个接口之后 只能是函数式接口
 */
@FunctionalInterface
interface Foo{
//    public void sayHello();
    public int add(int x,int y);

    default int div(int x,int y){
        System.out.println("div in --------");
        return x / y ;
    }

    public static int mv(int x,int y){
        return x * y;
    }

}

public class LambdaExpressDemo {
    public static void main(String[] args) {
        /*Foo foo = new Foo() {
            @Override
            public void sayHello() {
                System.out.println("==============Hello 2021");
            }
        };
        foo.sayHello();*/

      /*  Foo foo = ()->{System.out.println("Hello 2021");};
        foo.sayHello();*/

        Foo foo = (x,y) ->{
            System.out.println("add....");
            return x+y;
        };
        System.out.println(foo.add(22, 22));

        System.out.println(foo.div(10, 5));

        System.out.println(Foo.mv(3, 5));

    }
}
