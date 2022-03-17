package dialog;

import Window.LoginWindow;
import handler.DragWindowHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 提示窗口，提示相关信息，并在点击确认之后关闭
 */
public class AlertDialog {
    Stage stage = new Stage();
    Label titleLabel = null;
    VBox dialogItemsPane = null;
    boolean result = false;
    Button okButton = null;
    /**
     * 构造函数
     * @param message 提示信息
     */
    public AlertDialog(String message){
        stage.initStyle(StageStyle.TRANSPARENT);    //透明标题栏
        Parent root = null;
        try{
            root = FXMLLoader.load(LoginWindow.class.getResource("../resources/fxml/alertDialog.fxml"));
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
        titleLabel = (Label)root.lookup("#titleLabel");
        dialogItemsPane = (VBox) root.lookup("#dialogItemsPane");   //获得相关信息
        messageBox.setText(message);
        okButton = (Button)root.lookup("#okButton");
        okButton.setOnAction((event)->{ result = true;stage.close(); });
        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);
    }
    public void show(){
        stage.setAlwaysOnTop(true);
        stage.showAndWait();
    }
    public void setAlwaysOnTop(boolean bool){
        stage.setAlwaysOnTop(bool);
    }
    public void setTitle(String title){
        titleLabel.setText(title);
    }
    public VBox getDialogItemsPane(){
        return dialogItemsPane;
    }
    /*获得访问结果*/
    public boolean getResult(){
        return result;
    }
    public Button getOkButton(){
        return okButton;
    }
    public void close(){
        stage.close();
    }
}
