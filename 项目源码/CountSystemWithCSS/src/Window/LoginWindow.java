package Window;

import dialog.AlertDialog;
import dialog.MyDialog;
import handler.DragWindowHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.Config;
import util.RemData;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginWindow {
    Stage stage = new Stage();
    public LoginWindow() throws Exception{
        Parent root = FXMLLoader.load(LoginWindow.class.getResource("../resources/fxml/login.fxml"));
        stage.initStyle(StageStyle.TRANSPARENT);    //透明标题栏
        //获得组件信息
        Button minWindow = (Button)root.lookup("#minWindow");
        Button closeWindow = (Button)root.lookup("#closeWindow");
        VBox window = (VBox)root.lookup("#window");
        stage.setResizable(false);
        //设置鼠标事件
        DragWindowHandler handler = new DragWindowHandler(stage);
        window.setOnMousePressed(handler);
        window.setOnMouseDragged(handler);
        //关闭、最小化按钮
        minWindow.setOnAction(event->{
            stage.setIconified(true);
        });
        closeWindow.setOnAction(event -> {
            System.exit(0);
        });
        //获得需要的组件信息
        TextField userTextField = (TextField)root.lookup("#userTextField");
        PasswordField passwordField = (PasswordField)root.lookup("#passwordField");
        CheckBox remCheckBox = (CheckBox)root.lookup("#remCheckBox");
        Button loginInButton = (Button)root.lookup("#loginInButton");
        Button loginUpButton = (Button)root.lookup("#loginUpButton");
        //获得账号信息
        //从文件中拷贝初数据并加载到选框中
        RemData.load(new File(Config.configFilePath));
        //将获得的信息填入输入框中
        userTextField.setText(RemData.user);
        passwordField.setText(RemData.password);
        //检查非空，设置checkBox选中状态
        if(userTextField.getText()!=null&&(!(userTextField.getText().equals("")))){
            remCheckBox.setSelected(true);
        }

        //事件响应
        loginInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //记录账号密码信息
                if(remCheckBox.isSelected()){
                    //存入相关信息
                    RemData.user = userTextField.getText();
                    RemData.password = passwordField.getText();
                    RemData.save(new File(Config.configFilePath));
                }
                //登录系统
                //获得账号密码信息
                Config.curStaff = userTextField.getText();
                Config.curStaffPass = passwordField.getText();
                //使用登录账号登录平台，获得员工类型，并初始化相关类进入相关界面
                int staff_type=-1;
                try {
                    //1,加载驱动
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("创建连接");
                    //2.创建连接
                    //此处按照实际的数据库名称和账号密码进行修改
                    //格式为jdbc:mysql://127.0.0.1:3306/数据库名称?useSSL=true&characterEncoding=utf-8&user=账号名&password=密码
                    Config.connection = DriverManager.getConnection(Config.url,Config.LOGINUSER,Config.LOGINPASS);
                    System.out.println("创建连接成功");
                    //查询表格信息，获得返回值
                    String sql = "select * from staff_info where staff_name = \'"+Config.curStaff+"\';";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.result = Config.statement.executeQuery();
                    if(!Config.result.next()){
                        //查询为空
                        AlertDialog dialog = new AlertDialog("error:请检查账号信息是否正确");
                        dialog.show();
                        return ;
                    }else if(!Config.result.getString(3).equals(Config.curStaffPass)){
                        //密码错误
                        AlertDialog dialog = new AlertDialog("warning:密码错误");
                        dialog.show();
                        return;
                    }else {
                        //登录成功
                        AlertDialog dialog = new AlertDialog("success:登录成功");
                        dialog.show();
                        //员工类型
                        staff_type = Config.result.getInt(5);
                        //记录账号id
                        Config.curStaffID = Config.result.getInt(1);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    AlertDialog dialog = new AlertDialog("error:缺少组件");
                    dialog.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    AlertDialog dialog = new AlertDialog("error:登陆失败");
                    dialog.show();
                }finally {
                    //7.关闭
                    if(Config.result!=null){
                        try {
                            Config.result.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if(Config.statement!=null) {
                        try {
                            Config.statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try{
                    //修改登录状态
                    Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                    //登录修改状态信息
                    String sql = "select staff_login_in();";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.result = Config.statement.executeQuery();
                    Config.result.next();
                    if(Config.result.getInt(1)==0){
                        //登录失败
                        AlertDialog dialog = new AlertDialog("失败：登陆失败");
                        dialog.show();
                        return;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(staff_type == 0||staff_type==-1){
                    //店长账号
                    stage.close();
                    RootWindow rootWindow = new RootWindow(stage);
                    rootWindow.show();
                }else if(staff_type == 1){
                    //售货员账号,尝试连接数据库
                    stage.close();
                    SalesWindow window = new SalesWindow(stage);
                    window.show();
                }else if(staff_type == 2){
                    //仓库管理员账号
                    stage.close();
                    StoreWindow window = new StoreWindow(stage);
                    window.show();
                }else{

                    System.out.println("error_stuff_type");
                }

            }
        });
        loginUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //注册账号信息,打开对话框
                try{
                    MyDialog dialog = new MyDialog();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        Scene scene = new Scene(root,960,540);
        scene.getStylesheets().addAll(LoginWindow.class.getResource("/resources/css/login.css").toExternalForm(),LoginWindow.class.getResource("/resources/css/window.css").toExternalForm());
        stage.setScene(scene);
    }


    public void show(){stage.show();}
    public void close(){stage.close();}


}
