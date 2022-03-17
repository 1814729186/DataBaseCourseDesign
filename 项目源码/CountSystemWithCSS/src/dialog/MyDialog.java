package dialog;

import Window.LoginWindow;
import handler.DragWindowHandler;
import javafx.collections.FXCollections;
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

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 对话框封装:注册信息对话框
 */
public class MyDialog{
    Stage stage = new Stage();
    //储存需要返回的消息
    ArrayList<String> textFieldrResult = new ArrayList<>();
    ArrayList<String> buttonFieldResult = new ArrayList<>();
    public MyDialog()throws Exception{
        stage.initStyle(StageStyle.TRANSPARENT);    //透明标题栏
        Parent root = FXMLLoader.load(LoginWindow.class.getResource("../resources/fxml/loginDialog.fxml"));
        root.getStylesheets().addAll(LoginWindow.class.getResource("../resources/css/myDialog.css").toExternalForm());
        stage.setResizable(false);
        Button closeWindow = (Button)root.lookup("#closeWindow");
        closeWindow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //关闭当前窗口
                stage.close();
            }
        });
        //拖动事件
        VBox window = (VBox) root.lookup("#window");
        DragWindowHandler handler = new DragWindowHandler(stage);
        window.setOnMousePressed(handler);
        window.setOnMouseDragged(handler);
        //按钮事件及返回消息
        Button applyButton = (Button)root.lookup("#applyButton");
        ChoiceBox<String> typeChoiceBox = (ChoiceBox)root.lookup("#typeChoiceBox");
        TextField nameTextField = (TextField)root.lookup("#nameTextField");
        PasswordField passwordField = (PasswordField)root.lookup("#passwordField");
        PasswordField confirmPasswordField = (PasswordField)root.lookup("#confirmPasswordField");
        //添加选框信息
        typeChoiceBox.setItems(FXCollections.observableArrayList("售货员","仓库管理员"));

        applyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //向数据库插入相关信息
                String name = nameTextField.getText();
                String password = passwordField.getText();
                //账号不能为空
                if(name.equals("")||password.equals("")){
                    AlertDialog dialog = new AlertDialog("账号或密码不能为空");
                    dialog.show();
                    return ;
                }
                //账号、密码长度不能超过20
                if(name.length()>20||password.length()>20){
                    AlertDialog dialog = new AlertDialog("账号或密码长度不超过20字符");
                    dialog.show();
                    return ;
                }
                //查看账号格式，查看密码与确认密码是否相同
                if(!password.equals(confirmPasswordField.getText())){
                    //密码不相同，输出提示信息，并返回
                    AlertDialog dialog = new AlertDialog("请确定密码与确认密码一致");
                    dialog.show();
                    return ;
                }
                //获得信息，登录数据库，填入信息
                int type = typeChoiceBox.getSelectionModel().getSelectedIndex();
                if(type == -1){
                    AlertDialog dialog = new AlertDialog("请选择员工类型");
                    dialog.show();
                    return ;
                }
                //type 0 表示售货员，1 表示仓库管理员
                //使用申请用户登录数据库
                try{
                    Config.connection = DriverManager.getConnection(Config.url,Config.APPLYUSER,Config.APPLYPASS);
                    //将信息添加到表格中
                    //String sql = "INSERT INTO `online_sale_db`.`application_form` (`apply_name`, `apply_password`, `apply_type`) VALUES ('1', '1', '1');";
                    String sql = "insert into application_form(apply_name,apply_password,apply_type) values(\'"+name+"\',\'"+password+"\',\'"+(type+1)+"\');";
                    System.out.println("连接成功");
                    //执行语句，获得返回值
                    Config.statement = Config.connection.prepareStatement(sql);
                    int res = Config.statement.executeUpdate();
                    System.out.println(res);
                    //提示注册成功
                    stage.close();
                    AlertDialog dialog = new AlertDialog("申请成功，请等待店长确认并通过申请");
                    dialog.show();
                    Config.connection.close();
                    Config.statement.close();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);
        stage.show();
    }


}
