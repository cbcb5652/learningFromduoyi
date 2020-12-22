package jvm.test;

import java.io.FileNotFoundException;

public class CustomClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try{
            byte[] result = getClassFromCustomPath(name);
            if (result == null){
                throw new FileNotFoundException();
            }else {
                return defineClass(name,result,0,result.length);
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        throw new ClassNotFoundException(name);
    }

    private byte[] getClassFromCustomPath(String name){
        return  null;
    }

    public static void main(String[] args) {
        // 自定义路径中加载指定类：细节略
        // 如果指定路径的字节码文件进行了加密。则需要在此方法中进行解密操作
        CustomClassLoader customClassLoader = new CustomClassLoader();
        try{
            Class<?> clazz = Class.forName("One",true,customClassLoader);
            Object obj = clazz.newInstance();
            System.out.println(obj.getClass().getClassLoader());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
