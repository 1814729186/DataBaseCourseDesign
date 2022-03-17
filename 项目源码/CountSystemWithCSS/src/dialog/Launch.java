package dialog;

import dataItem.StaffInfo;
import javafx.application.Application;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Launch extends Application {
    @Override
    public void start(Stage stage){
        //stage.show();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> textFileds = new ArrayList<>();
        ArrayList <String> choiceBoxs = new ArrayList<>();
        labels.add("test1");
        labels.add("test2");
        textFileds.add("field1");
        textFileds.add("field2");
        choiceBoxs.add("choiceBox1 choice1 choice2");
        choiceBoxs.add("choiceBox2 choice1 choice2");
        DialogItem dialogItem = new DialogItem(labels,textFileds,choiceBoxs,true);
        dialogItem.showAndWait();
    }
}
