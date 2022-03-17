package util;

import javafx.scene.text.Font;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Config {
    public static String url = "jdbc:mysql://localhost:3306/online_sale_db";
    public static String user;
    public static String password;

    public static int curStaffID;   //当前员工编号
    public static String curStaff;  //当前员工姓名
    public static String curStaffPass; //当前员工使用账号密码
    public static Connection connection;
    public static PreparedStatement statement;
    public static ResultSet result;
    public static final String configFilePath = "./config.dat";
    //店长用户
    public static final String ROOTUSER = "root";
    public static final String ROOTPASS = "root";
    //登录用户
    public static final String LOGINUSER = "LoginUser";
    public static final String LOGINPASS = "loginuser";
    //申请用户
    public static final String APPLYUSER = "ApplyUser";
    public static final String APPLYPASS = "applyuser";
    //收银员
    public static final String CASHIERUSER = "CashierUser";
    public static final String CASHIERPASS = "cashieruser";
    //仓库管理员
    public static final String STOREUSER = "StoreUser";
    public static final String STOREPASS = "storeuser";
    //字体
    //public static final Font = Font.loadFont("/resources/fonts/AaEluanShi-2.ttf",50);
    //计量单位与数量能否为小数的匹配
    //能为小数的计量单位
    public static final String[] MEASURETYPE1 = {
            "千克","斤","kg","Kg","KG","500g","500G","500克","盎司"
    };
    public static boolean contains(String[] list,String messure){
        for(String mes:list){
            if(mes.equals(messure)) return true;
        }
        return false;
    }
    //会员打折折扣信息
    public static final double DISCOUNT = 0.9;
    //满额办理会员卡
    public static final double AMOUNTFORGETVIPCARD = 1000;


}
