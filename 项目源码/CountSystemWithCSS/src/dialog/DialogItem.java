package dialog;

import Window.LoginWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * 通用的dialog模式信息，支持不同数量的label、TextField、ChoiceBox的创建
 */
public class DialogItem extends Dialog {

    public ArrayList<TextField> textFieldList = new ArrayList<>();
    public ArrayList<ChoiceBox> choiceBoxList = new ArrayList<>();
    private VBox items;
    Label titleLabel;
    //实例代码块
    {
        //初始化
        this.initStyle(StageStyle.TRANSPARENT);    //透明标题栏
        Parent root = null;
        try{
            root = FXMLLoader.load(DialogItem.class.getResource("../resources/fxml/dialogItem.fxml"));
        }catch (Exception e){
            e.printStackTrace();
        }
        root.getStylesheets().add(DialogItem.class.getResource("../resources/css/myDialog.css").toExternalForm());
        Button closeButton = (Button)root.lookup("#closeWindow");
        titleLabel = (Label)root.lookup("#titleLabel");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DialogItem.super.close();
            }
        });
        //容器
        items = (VBox)root.lookup("#items");
        items.setSpacing(10);
        /*Button*/
        this.getDialogPane().setContent(root);

    }
    /**
     * 构造函数
     * @param labels 标签信息，在dialog中垂直排列
     * @param fieldTips 输入框提示标签信息，在对话框中以标签-输入框为一组，组内横向排布，组间纵向排布，参数形式为
     * @param choiceBox 含有的选择框信息，在对话框中以标签-选择框为一组，组内横向排布，组间纵向排布，信息输入格式为ArrayList中的一个元素表示一组选框，字符串形式为" <选框标题> <空格> <选择项1> <空格> <选择项2> ……"
     * @param haveCancelButton 默认含有确认按钮，该参数为true时将添加取消按钮
     */
    public DialogItem(ArrayList<String> labels, ArrayList<String> fieldTips,ArrayList<String> choiceBox,boolean haveCancelButton){
        //创建相关信息，添加到容器中
        /*label*/
        for(String label:labels){
            Label labelItem = new Label(label);
            items.getChildren().add(labelItem);
            labelItem.setAlignment(Pos.CENTER);
        }
        /*输入框*/
        for(String fieldTip:fieldTips){
            HBox box = new HBox();
            Label label = new Label(fieldTip);
            TextField field = new TextField();
            box.getChildren().addAll(label,field);
            box.setSpacing(5);
            /*添加到访问列表*/
            items.getChildren().add(box);
            textFieldList.add(field);
        }
        /*选择框*/
        for(String choice:choiceBox){
            String[] strings = choice.split(" ");
            HBox box = new HBox();
            Label label = new Label(strings[0]);
            ChoiceBox<String> ch = new ChoiceBox<>();
            for(int i = 1;i < strings.length;i++){
                ch.getItems().add(strings[i]);
            }
            box.getChildren().addAll(label,ch);
            choiceBoxList.add(ch);
            items.getChildren().add(box);
        }
        /*Button*/
        this.getDialogPane().getButtonTypes().addAll(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        if(haveCancelButton) {this.getDialogPane().getButtonTypes().addAll(new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));}
    }

    /**
     * 无参构造器
     */
    public DialogItem(){

    }
    //获得容器
    public VBox getBox(){
        return items;   //添加组件
    }
    public void setTitleText(String title){
        this.titleLabel.setText(title);
    }
}
