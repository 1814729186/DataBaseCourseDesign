package dialog;

import Window.LoginWindow;
import dataItem.StaffInfo;
import handler.DragWindowHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

public class StaffInfoAlterDialog_dialog extends Dialog {
    public ChoiceBox<String> staffTypeChoiceBox;
    public TextField staffNameField;
    public StaffInfoAlterDialog_dialog(StaffInfo staff){
        this.initStyle(StageStyle.TRANSPARENT);    //透明标题栏
        Parent root = null;
        try{
            root = FXMLLoader.load(LoginWindow.class.getResource("../resources/fxml/staffInfoAlterDialog_dialog.fxml"));
        }catch (Exception e){
            e.printStackTrace();
        }
        root.getStylesheets().add(LoginWindow.class.getResource("../resources/css/myDialog.css").toExternalForm());
        Button closeButton = (Button)root.lookup("#closeWindow");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                StaffInfoAlterDialog_dialog.super.close();
            }
        });
        staffNameField = (TextField)root.lookup("#staffNameField");
        staffTypeChoiceBox = (ChoiceBox)root.lookup("#staffTypeChoiceBox");

        // 初值
        staffNameField.setText(staff.getStaff_name());
        staffTypeChoiceBox.getItems().addAll("店长","收银员","仓库管理员");
        staffTypeChoiceBox.getSelectionModel().select(staff.getStaff_type());

        this.getDialogPane().setContent(root);
        this.getDialogPane().getButtonTypes().addAll(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
    }


}
