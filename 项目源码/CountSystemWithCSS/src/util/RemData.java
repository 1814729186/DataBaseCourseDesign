package util;

import java.io.*;

/**
 * 记录信息
 *  序列化对象,储存信息
 */
public class RemData {
    public static String user;
    public static String password;


    /**
     * 加载文件到对象
     * @param file 加载文件到对象
     */
    public static void load(File file) {
        if(file == null)    return;
        try{
            readObject(new ObjectInputStream(new FileInputStream(file)));
        }catch(IOException |NullPointerException e){
            //e.printStackTrace();
        }
    }
    /**
     * 储存到文件
     * @param file 文件
     */
    public static void save(File file){
        try {
            writeObject(new ObjectOutputStream(new FileOutputStream(file)));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 序列化写入到文件
     * @param out 输出流
     */
    public static void writeObject(ObjectOutputStream out){
        try {
            out.writeObject(user);
            out.writeObject(password);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 序列化读入
     * @param in 输入流
     */
    public static void readObject(ObjectInputStream in){
        try{
            user = (String)in.readObject();
            password = (String)in.readObject();
        }catch (IOException|ClassNotFoundException e){
            //e.printStackTrace();
        }
    }

}
