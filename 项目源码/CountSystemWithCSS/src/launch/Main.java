package launch;

import Window.LoginWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 程序入口
 */
public class Main extends Application {
    @Override
    public void start(Stage stage)throws Exception{
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.show();
    }
}
