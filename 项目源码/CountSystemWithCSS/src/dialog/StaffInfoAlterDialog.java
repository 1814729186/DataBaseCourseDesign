package dialog;

import Window.LoginWindow;
import dataItem.ApplyInfo;
import dataItem.StaffInfo;
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

import java.sql.DriverManager;

/**
 * 员工信息修改对话框
 */
public class StaffInfoAlterDialog  {
    boolean apply = false; //是否点击确定按钮
    Stage stage = new Stage();
    public TextField staffNameField;
    public ChoiceBox<String> staffTypeChoiceBox;

    public StaffInfoAlterDialog(StaffInfo staff){
        stage.initStyle(StageStyle.TRANSPARENT);    //透明标题栏
        Parent root = null;
        try{
            root = FXMLLoader.load(LoginWindow.class.getResource("../resources/fxml/staffInfoAlterDialog.fxml"));
        }catch (Exception e){
            e.printStackTrace();
        }
        root.getStylesheets().add(LoginWindow.class.getResource("../resources/css/myDialog.css").toExternalForm());
        Button closeButton = (Button)root.lookup("#closeWindow");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        //stage.setResizable(false);
        //拖动事件
        VBox window = (VBox) root.lookup("#window");
        DragWindowHandler handler = new DragWindowHandler(stage);
        window.setOnMousePressed(handler);
        window.setOnMouseDragged(handler);
        Label messageBox = (Label)root.lookup("#messageBox");
        Button okButton = (Button)root.lookup("#okButton");

        okButton.setOnAction((event)->{

            stage.close();

        });
        //输入信息
        staffNameField = (TextField) root.lookup("#staffNameField");
        staffTypeChoiceBox = (ChoiceBox) root.lookup("#staffTypeChoiceBox");
        //初始信息
        staffNameField.setText(staff.getStaff_name());
        staffTypeChoiceBox.getItems().addAll("店长","售货员","仓库管理员");
        staffTypeChoiceBox.getSelectionModel().select(staff.getStaff_type());


        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);
    }
    public void show(){stage.show();}

}
