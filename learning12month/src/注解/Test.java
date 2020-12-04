package 注解;

import java.lang.reflect.Field;

public class Test {

    public static void getFruitInfo(String clas){
        try{
            Class<?> cls = Class.forName(clas);
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(FruitName.class) == true){
                    FruitName name = field.getAnnotation(FruitName.class);
                    System.out.println("Fruit Name:"+name.value());
                }
                if (field.isAnnotationPresent(FruitColor.class)){
                    FruitColor color = field.getAnnotation(FruitColor.class);
                    System.out.println("Fruit Color:"+color.fruitColor());
                }
                if (field.isAnnotationPresent(FruitProvider.class)){
                    FruitProvider provider = field.getAnnotation(FruitProvider.class);
                    System.out.println("Fruit FruitProvider:"+provider.id()+" Provider:"+provider.user()+" ProviderAddress:"+provider.address());
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getFruitInfo("注解.Apple");


    }

}
