package Window;

import dataItem.GoodsInfo;
import dataItem.GoodsSoldInfo;
import dataItem.PurchaseOrder;
import dataItem.SaveDiskInfo;
import dialog.AlertDialog;
import handler.DragWindowHandler;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.w3c.dom.Text;
import util.Config;

import java.sql.DriverManager;

/**
 * 仓库管理员界面
 *      需求：     1. 工作窗口
 *                  1.1 进货单查询、修改、删除、添加
 *                  1.2 库存信息查询
 *                2. 信息窗口
 *                  2.1 本人打卡记录
 *                3. 设置窗口（TODO）
 *                  3.1 账号信息修改
 *                  3.2 账号密码修改
 *
 */

public class StoreWindow {
    Stage stage = new Stage();
    Stage superStage = null;    //上一级stage
    Parent root = null;
    //进货单
    private ObservableList<PurchaseOrder> purchaseOrderData = FXCollections.observableArrayList();
    //库存信息
    private ObservableList<GoodsInfo> goodsInfoData = FXCollections.observableArrayList();
    //盘存信息
    private ObservableList<SaveDiskInfo> saveDiskInfoData = FXCollections.observableArrayList();

    public StoreWindow(Stage superStage){
        this.superStage = superStage;
        this.superStage = superStage;
        try{
            root = FXMLLoader.load(StoreWindow.class.getResource("../resources/fxml/storeWindow.fxml"));
        }catch (Exception e){
            e.printStackTrace();
        }
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
        Scene scene = new Scene(root,1800,990);
        scene.getStylesheets().add(LoginWindow.class.getResource("/resources/css/window.css").toExternalForm());
        stage.setScene(scene);
        /*注销按钮*/
        Button quitButton = (Button)root.lookup("#quitButton");
        quitButton.setOnAction((param)->{
            //修改状态
            try{
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "select staff_login_off();";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();
                if(Config.result.next()){
                    if(Config.result.getInt(1)==0){
                        //失败
                        AlertDialog dialog = new AlertDialog("失败：下班打卡失败，请连联系管理员打卡下班。");
                        dialog.show();
                        return;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //关闭连接
                try{
                    Config.statement.close();
                    Config.connection.close();
                    Config.result.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            stage.close();
            superStage.show();
        });
        //下拉菜单设计
        /*下拉菜单容器*/
        AnchorPane workspaceAnchorPane = (AnchorPane)root.lookup("#workspaceAnchorPane");
        AnchorPane marketDataAnchorPane = (AnchorPane)root.lookup("#marketDataAnchorPane");
        AnchorPane informationAnchorPane = (AnchorPane)root.lookup("#informationAnchorPane");
        AnchorPane settingAnchorPane = (AnchorPane)root.lookup("#settingAnchorPane");
        /*设置非可见*/
        workspaceAnchorPane.setVisible(false);
        marketDataAnchorPane.setVisible(false);
        informationAnchorPane.setVisible(false);
        settingAnchorPane.setVisible(false);
        /*获得按钮信息*/
        Button workSpaceButton = (Button) root.lookup("#workSpaceButton");
        Button marketDataButton = (Button) root.lookup("#marketDataButton");
        Button informationButton = (Button) root.lookup("#informationButton");
        Button settingButton = (Button) root.lookup("#settingButton");
        marketDataButton.setVisible(false);
        settingButton.setVisible(false);
        /*按钮事件处理*/
        workSpaceButton.setOnAction((param)->{
            workspaceAnchorPane.setVisible(true);
            marketDataAnchorPane.setVisible(false);
            informationAnchorPane.setVisible(false);
            settingAnchorPane.setVisible(false);
            buttonSlide();
        });
        marketDataButton.setOnAction((param)->{
            workspaceAnchorPane.setVisible(false);
            marketDataAnchorPane.setVisible(true);
            informationAnchorPane.setVisible(false);
            settingAnchorPane.setVisible(false);
            buttonSlide();
        });
        informationButton.setOnAction((param)->{
            workspaceAnchorPane.setVisible(false);
            marketDataAnchorPane.setVisible(false);
            informationAnchorPane.setVisible(true);
            settingAnchorPane.setVisible(false);
            buttonSlide();
        });
        settingButton.setOnAction((param)->{
            workspaceAnchorPane.setVisible(false);
            marketDataAnchorPane.setVisible(false);
            informationAnchorPane.setVisible(false);
            settingAnchorPane.setVisible(true);
            buttonSlide();
        });
        /*更新按钮*/
        Button updateButton = (Button)root.lookup("#updateButton");
        updateButton.setOnAction((param)->{
            updatePurchaseOrderData();
            updateGoodsInfoData();
            updateSaveDiskInfoData();
        });
/*1 工作窗口*/
    /*1.1 进货单*/
        BorderPane purchaseOrderBorderPane = (BorderPane) root.lookup("#purchaseOrderBorderPane");
        TableView<PurchaseOrder> purchaseOrderTableView = (TableView)root.lookup("#purchaseOrderTableView");
        TableColumn[]purchaseOrderColumns = new TableColumn[9];
        purchaseOrderColumns[0] = new TableColumn("进货单号");
        purchaseOrderColumns[1] = new TableColumn("商品条码");
        purchaseOrderColumns[2] = new TableColumn("商品名");
        purchaseOrderColumns[3] = new TableColumn("数量");
        purchaseOrderColumns[4] = new TableColumn("计量单位");
        purchaseOrderColumns[5] = new TableColumn("仓库管理员");
        purchaseOrderColumns[6] = new TableColumn("时间");
        purchaseOrderColumns[7] = new TableColumn("单价");
        purchaseOrderColumns[8] = new TableColumn("总价");
        purchaseOrderTableView.getColumns().addAll(purchaseOrderColumns);
        purchaseOrderColumns[0].setCellValueFactory(new PropertyValueFactory<>("purchase_id"));
        purchaseOrderColumns[1].setCellValueFactory(new PropertyValueFactory<>("good_id"));
        purchaseOrderColumns[2].setCellValueFactory(new PropertyValueFactory<>("good_name"));
        purchaseOrderColumns[3].setCellValueFactory(new PropertyValueFactory<>("good_num"));
        purchaseOrderColumns[4].setCellValueFactory(new PropertyValueFactory<>("measure_unit"));
        purchaseOrderColumns[5].setCellValueFactory(new PropertyValueFactory<>("staff_name"));
        purchaseOrderColumns[6].setCellValueFactory(new PropertyValueFactory<>("purchase_time"));
        purchaseOrderColumns[7].setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
        purchaseOrderColumns[8].setCellValueFactory(new PropertyValueFactory<>("amount"));
        purchaseOrderTableView.setItems(purchaseOrderData);
        updatePurchaseOrderData();
        /*工作处理：添加进货单，删除进货单，修改进货单*/
        /*添加进货单*/
        TextField purchaseOrderAddField = (TextField) root.lookup("#purchaseOrderAddField");
        Button purchaseOrderAddButton = (Button) root.lookup("#purchaseOrderAddButton");
        purchaseOrderAddButton.setOnAction((param)->{
            //根据商品条码添加进货单，若库存中存在相关信息，则直接获得相关信息
            //若不存在商品，则需要补充相关信息，再添加到仓库中
            String good_idString = purchaseOrderAddField.getText();
            int good_id = -1;
            if(good_idString.equals("")) {System.out.println("return");return;}
            try{
                good_id = Integer.valueOf(good_idString);
            }catch (NumberFormatException e){
                AlertDialog dialog = new AlertDialog("条码格式不正确，请重新输入");
                dialog.setTitle("警告");
                dialog.show();
                purchaseOrderAddField.clear();
                return;
            }
            if(good_id <=0){
                AlertDialog dialog = new AlertDialog("条码格式不正确，请重新输入");
                dialog.setTitle("警告");
                dialog.show();
                purchaseOrderAddField.clear();
                return;
            }
            //在good_info表中查找相关信息
            try{
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "select * from goods_info where good_id="+good_id+";";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();
                if(!Config.result.next()){
                    //未找到相关信息--插入条目，更新相关信息,需要获得商品名称、商品数量、进货价格、计量单位、售价信息
                    //获得相关信息
                    AlertDialog dialog = new AlertDialog("库存中不存在该商品信息，请完成补充");
                    dialog.setTitle("补充商品信息");
                    dialog.getDialogItemsPane().getChildren().add(new Label("商品条码："+good_id));
                    dialog.getDialogItemsPane().setSpacing(5);
                    dialog.getDialogItemsPane().setPadding(new Insets(20));
                    TextField goodNameTextField = new TextField();
                    goodNameTextField.setPromptText("商品名称");
                    TextField goodNumTextField = new TextField();
                    goodNumTextField.setPromptText("商品数量");
                    TextField purchasePriceTextField = new TextField();
                    purchasePriceTextField.setPromptText("进货单价");
                    TextField measureUnitTextField = new TextField();
                    measureUnitTextField.setPromptText("计量单位");
                    TextField goodPriceTextField = new TextField();
                    goodPriceTextField.setPromptText("售价");
                    dialog.getDialogItemsPane().getChildren().addAll(goodNameTextField,goodNumTextField,purchasePriceTextField,measureUnitTextField,goodPriceTextField);
                    int finalGood_id1 = good_id;

                    dialog.getOkButton().setOnAction((param1)->{
                        //插入相关信息
                        String goodName = null;
                        double goodNum = 0;
                        double purchasePrice = 0;
                        String measureUnit = null;
                        double goodPrice = 0;
                        try{
                            goodName = goodNameTextField.getText();
                            measureUnit = measureUnitTextField.getText();
                            purchasePrice = Double.valueOf(purchasePriceTextField.getText());
                            goodPrice = Double.valueOf(goodPriceTextField.getText());
                            if(Config.contains(Config.MEASURETYPE1,measureUnit)){
                                //允许小数信息
                                goodNum = Double.valueOf(goodNumTextField.getText());
                            }else{
                                goodNum = Double.valueOf(goodNumTextField.getText());
                            }
                        }catch (NumberFormatException e){
                            AlertDialog dialog1 = new AlertDialog("信息格式不正确，查证后再输入");
                            dialog1.show();
                            return;
                        }
                        //完成信息插入
                        try{
                            String sql1 = "insert into purchase_order values(null,"+ finalGood_id1 +","+goodNum+",getUserId(),null,"+purchasePrice+");";
                            Config.statement = Config.connection.prepareStatement(sql1);
                            Config.statement.executeUpdate();
                            //更新库存表中相关信息
                            sql1 = "update goods_info set good_name=\'"+goodName+"\',good_price="+goodPrice+",measure_unit=\'"+measureUnit+"\' where good_id="+finalGood_id1+";";
                            Config.statement = Config.connection.prepareStatement(sql1);
                            Config.statement.executeUpdate();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        dialog.close();
                        updatePurchaseOrderData();
                    });
                    dialog.show();
                }else{
                    //已有条目信息，获取添加的数量即可
                    AlertDialog dialog = new AlertDialog("商品条码："+good_id+"\t商品名："+Config.result.getString(2));
                    dialog.getDialogItemsPane().getChildren().addAll(new Label("计量单位："+Config.result.getString(4)),
                            new Label("请输入进货数量与进货单价"));
                    dialog.getDialogItemsPane().setSpacing(5);
                    dialog.getDialogItemsPane().setPadding(new Insets(20));
                    TextField goodNumTextField = new TextField();
                    goodNumTextField.setPromptText("输入商品数量");
                    TextField purchasePriceTextField = new TextField();
                    purchasePriceTextField.setPromptText("商品进货单价");
                    dialog.getDialogItemsPane().getChildren().addAll(goodNumTextField,purchasePriceTextField);
                    //计量单位
                    String messureUnit = Config.result.getString(4);

                    int finalGood_id = good_id;
                    dialog.getOkButton().setOnAction((param1)->{
                        //获得单价和数量
                        double purchase_price = 0;
                        double good_num = 0;
                        try{
                            purchase_price = Double.valueOf(purchasePriceTextField.getText());
                            if(Config.contains(Config.MEASURETYPE1,messureUnit)){
                                //允许小数
                                good_num = Double.valueOf(goodNumTextField.getText());
                            }else{
                                good_num = Integer.valueOf(goodNumTextField.getText());
                            }
                        }catch (NumberFormatException e){
                            AlertDialog dialog1 = new AlertDialog("数量格式不正确，请重新输入");
                            dialog1.setTitle("警告");
                            dialog1.show();
                            return;
                        }
                        //插入相关信息到进货单即可
                        try{
                            String sql1 = "insert into purchase_order values(null,"+ finalGood_id +","+good_num+",getUserId(),null,"+purchase_price+");";
                            Config.statement = Config.connection.prepareStatement(sql1);
                            Config.statement.executeUpdate();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        dialog.close();
                        updatePurchaseOrderData();
                    });
                    dialog.show();
                }


            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //关闭连接
                try{
                    Config.result.close();
                    Config.statement.close();
                    Config.connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        //修改
        Button purchaseOrderAlterButton = (Button)root.lookup("#purchaseOrderAlterButton");
        purchaseOrderAlterButton.setOnAction((param)->{
            //修改按钮，弹出窗口，修改进货单信息 进货数量，进货单价等信息
            PurchaseOrder curItem = purchaseOrderTableView.getSelectionModel().getSelectedItem();
            if(curItem==null){
                AlertDialog dialog = new AlertDialog("请选中相关条目");
                dialog.setTitle("警告");
                dialog.show();
                return;
            }
            double remain_num = 0;
            //查询相关信息，获得仓库剩余量
            try{
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "select remain_num from goods_info where good_id="+curItem.getGood_id()+";";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();
                if(!Config.result.next()){
                    AlertDialog dialog = new AlertDialog("错误，数据库中未找到相关信息");
                }
                remain_num = Config.result.getDouble(1);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //关闭连接
                try{
                    Config.result.close();
                    Config.statement.close();
                    Config.connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //弹出窗口，获得修改信息
            AlertDialog dialog = new AlertDialog("修改进货单");
            //数量、进货单价
            Label message1 = new Label("商品条码："+curItem.getGood_id());
            Label message2 = new Label("商品名："+curItem.getGood_name());
            Label message3 = new Label("计量单位："+curItem.getMeasure_unit());
            HBox numHBox = new HBox();
            HBox priceHBox = new HBox();
            Label numLabel = new Label("数量");
            Label priceLabel = new Label("进货单价");
            TextField numTextField = new TextField();
            TextField priceTextField = new TextField();
            numHBox.getChildren().addAll(numLabel,numTextField);
            numHBox.setSpacing(5);
            priceHBox.getChildren().addAll(priceLabel,priceTextField);
            priceHBox.setSpacing(5);
            dialog.getDialogItemsPane().getChildren().addAll(message1,message2,message3,numHBox,priceHBox);
            dialog.getDialogItemsPane().setPadding(new Insets(20));
            //设置初值

            //如果没有小数信息，则不设置小数信息
            if(Config.contains(Config.MEASURETYPE1,curItem.getMeasure_unit())){
                numTextField.setText(String.valueOf(curItem.getGood_num()));
            }else{
                numTextField.setText(String.valueOf((int)(curItem.getGood_num())));
            }
            priceTextField.setText(String.valueOf(curItem.getPurchase_price()));
            double finalRemain_num = remain_num;
            dialog.getOkButton().setOnAction((param1)->{
                //修改信息
                double num = 0;
                double price = 0;
                try{
                    price = Double.valueOf(priceTextField.getText());
                    if(Config.contains(Config.MEASURETYPE1,curItem.getMeasure_unit())){
                        //允许小数
                        num = Double.valueOf(numTextField.getText());
                    }else{
                        num = Integer.valueOf(numTextField.getText());
                    }
                }catch (NumberFormatException e){
                    AlertDialog dialog1 = new AlertDialog("格式不正确，请查证后重新输入");
                    dialog1.show();
                    return;
                }
                //检查正确性
                double remainNumAfterAlter = finalRemain_num + (num - curItem.getGood_num());
                if(remainNumAfterAlter <0){
                    //不正确
                    AlertDialog dialog1 = new AlertDialog("库存信息不符合");
                    dialog1.show();
                    return;
                }
                //修改相关信息
                try{
                    String sql = "";
                    if(num==0.0){
                        //删除条目
                        sql = "delete from purchase_order where purchase_id="+curItem.getPurchase_id()+";";
                    }else{
                        sql = "update purchase_order set purchase_price="+price+",good_num="+num+" where purchase_id="+curItem.getPurchase_id()+";";
                    }
                    Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.executeUpdate();
                    sql = "update goods_info set remain_num="+remainNumAfterAlter+" where good_id="+curItem.getGood_id()+";";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.executeUpdate();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    //关闭连接
                    try{
                        Config.result.close();
                        Config.statement.close();
                        Config.connection.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                dialog.close();
                updatePurchaseOrderData();

            });
            dialog.show();

        });
        //删除按钮
        Button purchaseOrderDeleteButton = (Button) root.lookup("#purchaseOrderDeleteButton");
        purchaseOrderDeleteButton.setOnAction((param)->{
            //修改按钮，弹出窗口，修改进货单信息 进货数量，进货单价等信息
            PurchaseOrder curItem = purchaseOrderTableView.getSelectionModel().getSelectedItem();
            if(curItem==null){
                AlertDialog dialog = new AlertDialog("请选中相关条目");
                dialog.setTitle("警告");
                dialog.show();
                return;
            }
            double remain_num = 0;
            //查询相关信息，获得仓库剩余量
            try{
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "select remain_num from goods_info where good_id="+curItem.getGood_id()+";";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();
                if(!Config.result.next()){
                    AlertDialog dialog = new AlertDialog("错误，数据库中未找到相关信息");
                }
                remain_num = Config.result.getDouble(1);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //关闭连接
                try{
                    Config.result.close();
                    Config.statement.close();
                    Config.connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //剩余数量小于进货数量时，不允许删除
            if(remain_num < curItem.getGood_num()){
                AlertDialog dialog1 = new AlertDialog("库存信息不符合");
                dialog1.show();
                return;
            }
            double remainNumAfterAlter = remain_num - curItem.getGood_num();
            //删除条目
            try{
                String sql = "delete from purchase_order where purchase_id="+curItem.getPurchase_id()+";";
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                Config.statement = Config.connection.prepareStatement(sql);
                Config.statement.executeUpdate();
                sql = "update goods_info set remain_num="+remainNumAfterAlter+" where good_id="+curItem.getGood_id()+";";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.statement.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //关闭连接
                try{
                    Config.result.close();
                    Config.statement.close();
                    Config.connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            updatePurchaseOrderData();
        });



    /*1.2 库存信息*/
        BorderPane goodsInfoBorderPane = (BorderPane) root.lookup("#goodsInfoBorderPane");
        TableView<GoodsInfo> goodsInfoTableView = (TableView) root.lookup("#goodsInfoTableView");
        TableColumn[] goodsInfoTableColumns = new TableColumn[5];
        goodsInfoTableColumns[0] = new TableColumn("商品条码号");
        goodsInfoTableColumns[1] = new TableColumn("商品名");
        goodsInfoTableColumns[2] = new TableColumn("商品单价");
        goodsInfoTableColumns[3] = new TableColumn("计量单位");
        goodsInfoTableColumns[4] = new TableColumn("剩余数量");
        goodsInfoTableView.getColumns().addAll(goodsInfoTableColumns);
        goodsInfoTableColumns[0].setCellValueFactory(new PropertyValueFactory<>("good_id"));
        goodsInfoTableColumns[1].setCellValueFactory(new PropertyValueFactory<>("good_name"));
        goodsInfoTableColumns[2].setCellValueFactory(new PropertyValueFactory<>("good_price"));
        goodsInfoTableColumns[3].setCellValueFactory(new PropertyValueFactory<>("measure_unit"));
        goodsInfoTableColumns[4].setCellValueFactory(new PropertyValueFactory<>("remain_num"));
        goodsInfoTableView.setItems(goodsInfoData);
        updateGoodsInfoData();

    /*2.1 打卡记录*/
        BorderPane saveDiskInfoBorderPane = (BorderPane) root.lookup("#saveDiskInfoBorderPane");
        TableView<SaveDiskInfo> saveDiskInfoTableView = (TableView)root.lookup("#saveDiskInfoTableView");
        TableColumn[] saveDiskInfoTableColumns = new TableColumn[5];
        saveDiskInfoTableColumns[0] = new TableColumn("序号");
        saveDiskInfoTableColumns[1] = new TableColumn("员工姓名");
        saveDiskInfoTableColumns[2] = new TableColumn("员工职务");
        saveDiskInfoTableColumns[3] = new TableColumn("打卡上班时间");
        saveDiskInfoTableColumns[4] = new TableColumn("打卡下班时间");
        saveDiskInfoTableView.getColumns().addAll(saveDiskInfoTableColumns);
        saveDiskInfoTableColumns[0].setCellValueFactory(new PropertyValueFactory<>("id"));
        saveDiskInfoTableColumns[1].setCellValueFactory(new PropertyValueFactory<>("staff_name"));
        saveDiskInfoTableColumns[2].setCellValueFactory(new PropertyValueFactory<>("staff_type"));
        saveDiskInfoTableColumns[3].setCellValueFactory(new PropertyValueFactory<>("login_in_time"));
        saveDiskInfoTableColumns[4].setCellValueFactory(new PropertyValueFactory<>("login_off_time"));
        saveDiskInfoTableView.setItems(saveDiskInfoData);
        updateSaveDiskInfoData();


        /*信息控制*/
        //内部类，设置右侧界面全不可见
        class SetVisible{
            void setVisible(){
                //设置不可见
                purchaseOrderBorderPane.setVisible(false);
                goodsInfoBorderPane.setVisible(false);
                saveDiskInfoBorderPane.setVisible(false);
            }
        }
        new SetVisible().setVisible();
        //下拉菜单按钮
        Button listButton_purchaseOrder = (Button) root.lookup("#listButton_purchaseOrder");
        Button listButton_goodsInfo = (Button) root.lookup("#listButton_goodsInfo");
        Button listButton_saveDiskInfo = (Button) root.lookup("#listButton_saveDiskInfo");
        listButton_purchaseOrder.setOnAction((param)->{
            new SetVisible().setVisible();
            purchaseOrderBorderPane.setVisible(true);
            contentSlide();
        });
        listButton_goodsInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            goodsInfoBorderPane.setVisible(true);
            contentSlide();
        });
        listButton_saveDiskInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            saveDiskInfoBorderPane.setVisible(true);
            contentSlide();
        });



    }
    /*下拉菜单的向下滑动动画*/
    public void buttonSlide(){
        AnchorPane containButtonAnchorPane = (AnchorPane) root.lookup("#containButtonAnchorPane");
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(200),containButtonAnchorPane);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500),containButtonAnchorPane);
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition,fadeTransition);


        translateTransition.setInterpolator(Interpolator.EASE_BOTH);
        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(2);
        translateTransition.setFromY(0);
        translateTransition.setToY(30);

        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);

        parallelTransition.play();

    }
    /*信息栏的上下滑动动画*/
    public void contentSlide(){
        AnchorPane contentAnchorPane = (AnchorPane) root.lookup("#contentAnchorPane");
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(200),contentAnchorPane);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500),contentAnchorPane);
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(translateTransition,fadeTransition);


        translateTransition.setInterpolator(Interpolator.EASE_BOTH);
        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(2);
        translateTransition.setFromY(0);
        translateTransition.setToY(30);

        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);

        parallelTransition.play();

    }
    /*更新进货单信息*/
    void updatePurchaseOrderData(){
        purchaseOrderData.clear();
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select purchase_id,purchase_order.good_id,good_name,good_num,measure_unit,staff_name,purchase_time,purchase_price" +
                    " from purchase_order,staff_info,goods_info where purchase_order.staff_id=staff_info.staff_id and " +
                    "purchase_order.good_id=goods_info.good_id and staff_info.staff_name=getUser();";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            //创建信息
            while(Config.result.next()){
                purchaseOrderData.add(new PurchaseOrder(Config.result.getInt(1),Config.result.getInt(2),Config.result.getString(3),
                        Config.result.getDouble(4),Config.result.getString(5),Config.result.getString(6),Config.result.getString(7),
                        Config.result.getDouble(8)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭连接
            try{
                Config.result.close();
                Config.statement.close();
                Config.connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /*库存信息更新*/
    void updateGoodsInfoData(){
        goodsInfoData.clear();
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select * from goods_info;";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            //创建信息
            while(Config.result.next()){
                goodsInfoData.add(new GoodsInfo(Config.result.getInt(1),Config.result.getString(2),Config.result.getInt(3),
                        Config.result.getString(4),Config.result.getDouble(5)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭连接
            try{
                Config.result.close();
                Config.statement.close();
                Config.connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /*更新盘存信息*/
    void updateSaveDiskInfoData(){
        saveDiskInfoData.clear();
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select id,staff_name,staff_type,amount,login_in_time,login_off_time from save_disk_info,staff_info where save_disk_info.staff_id=staff_info.staff_id and staff_info.staff_name=getUser();";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            //创建信息
            while(Config.result.next()){
                saveDiskInfoData.add(new SaveDiskInfo(Config.result.getInt(1),Config.result.getString(2),Config.result.getInt(3),
                        Config.result.getInt(4), Config.result.getString(5),Config.result.getString(6)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭连接
            try{
                Config.result.close();
                Config.statement.close();
                Config.connection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void show() {
        stage.show();
    }
    public void close(){
        stage.close();

    }
}
