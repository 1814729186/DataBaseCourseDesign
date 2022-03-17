package Window;

import dataItem.*;
import dialog.AlertDialog;
import dialog.DialogItem;
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
import util.Config;

import java.sql.Array;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 售货员窗口
 *      基本需求：   1.工作窗口
 *                      1.1 收银 会员卡派发
 *                 2.超市数据信息
 *                      2.1 超市商品库存信息
 *                      2.2 会员卡信息查询
 *                 3.信息查询窗口
 *                      3.1 本人上班打卡历史记录
 *                      3.2 本人收银单信息查询
 *                      3.3 对接会员名单
 *                 4.设置窗口
 *                      4.1 账号信息修改
 *                      4.2 账号密码修改
 *                      4.3 头像设置（TODO）
 */
public class SalesWindow {
    Stage stage = new Stage();
    Stage superStage = null;
    Parent root = null;
    /**
     * 售货单表项信息记录，开始时置空，通过按钮进行添加
     */
    ObservableList<SaleOrderItem> saleOrderItemData = FXCollections.observableArrayList();
    //库存信息
    private ObservableList<GoodsInfo> goodsInfoData = FXCollections.observableArrayList();
    //会员卡信息
    private ObservableList<VipCardInfo> vipCardInfoData = FXCollections.observableArrayList();
    //盘存信息
    private ObservableList<SaveDiskInfo> saveDiskInfoData = FXCollections.observableArrayList();
    //收银表单观测表
    private ObservableList<SaleInfo> saleInfoData = FXCollections.observableArrayList();

    public SalesWindow(Stage superStage){
        this.superStage = superStage;
        try{
            root = FXMLLoader.load(LoginWindow.class.getResource("../resources/fxml/salesWindow.fxml"));
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
        /*注销按钮，返回登陆界面*/
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
        /*更新按钮*/
        Button updateButton = (Button)root.lookup("#updateButton");
        updateButton.setOnAction((param)->{
            updateGoodsInfoData();
            updateSaleInfoData();
            updateSaveDiskInfoData();
            updateVipCardInfoData();
        });
        /*用户信息显示 TODO*/

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
        settingButton.setVisible(false);
/*
 * 右侧窗口
 */

/*1. 工作窗口*/
        /**/
        /*列表显示，访问goods_info表，根据相关信息*/
        BorderPane workspaceBorderPane = (BorderPane)root.lookup("#workspaceBorderPane");
        TableView<SaleOrderItem> workspaceTableView = (TableView) root.lookup("#workspaceTableView");
        TableColumn[] workspaceColumns = new TableColumn[6];
        workspaceColumns[0] = new TableColumn("商品条码");
        workspaceColumns[1] = new TableColumn("商品名");
        workspaceColumns[2] = new TableColumn("单价");
        workspaceColumns[3] = new TableColumn("计量单位");
        workspaceColumns[4] = new TableColumn("销售数量");
        workspaceColumns[5] = new TableColumn("小计");
        workspaceTableView.getColumns().addAll(workspaceColumns);
        workspaceColumns[0].setCellValueFactory(new PropertyValueFactory<>("good_id"));
        workspaceColumns[1].setCellValueFactory(new PropertyValueFactory<>("good_name"));
        workspaceColumns[2].setCellValueFactory(new PropertyValueFactory<>("good_price"));
        workspaceColumns[3].setCellValueFactory(new PropertyValueFactory<>("measure_unit"));
        workspaceColumns[4].setCellValueFactory(new PropertyValueFactory<>("good_num"));
        workspaceColumns[5].setCellValueFactory(new PropertyValueFactory<>("good_amount"));
        workspaceTableView.setItems(saleOrderItemData);
        workspaceTableView.setPadding(new Insets(20));
        /*获得必要组件*/
        TextField workspaceAddField = (TextField)root.lookup("#workspaceAddField");
        TextField workspaceVipField = (TextField)root.lookup("#workspaceVipField");
        Button workspaceAddButton = (Button)root.lookup("#workspaceAddButton");
        Button workspaceAlterButton = (Button)root.lookup("#workspaceAlterButton");
        Button workspaceDeleteButton = (Button)root.lookup("#workspaceDeleteButton");
        Button workspaceCountButton = (Button)root.lookup("#workspaceCountButton");
        Button workspaceClearButton = (Button)root.lookup("#workspaceClearButton");
        Button workspaceVipDistributeButton = (Button)root.lookup("#workspaceVipDistributeButton");
        /*add按钮*/
        workspaceAddButton.setOnAction((param)->{
            String good_idString = workspaceAddField.getText();
            if(good_idString.equals(""))return; //为空时不响应
            //转换
            int good_id=0;
            try{
                good_id = Integer.valueOf(good_idString);
            }catch(NumberFormatException e){
                AlertDialog dialog = new AlertDialog("条码格式不正确");
                dialog.show();
                return;
            }
            //查询信息
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "select * from goods_info where good_id="+good_id+";";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();//执行
                if(!Config.result.next()){
                    //未找到商品信息
                    AlertDialog dialog = new AlertDialog("未查找到商品");
                    dialog.show();
                    return;
                };
                //弹出对话框，获取相关数量等信息
                String good_name = Config.result.getString(2);
                double good_price = Config.result.getDouble(3);
                String measure_unit = Config.result.getString(4);
                double remain_num = Config.result.getDouble(5);
                //对话框信息
                ArrayList<String> labels = new ArrayList<>();
                labels.add("商品信息添加");
                labels.add("商品条码:"+good_id+", 商品名称："+good_name);
                labels.add("计量单位:"+measure_unit+", 单价："+good_price);
                ArrayList<String> textFields = new ArrayList<>();
                textFields.add("销售数量");
                DialogItem dialogItem = new DialogItem(labels,textFields,new ArrayList<>(),true);
                //设置默认显示信息
                dialogItem.textFieldList.get(0).setText("1");    //默认数量1
                Optional<ButtonType> res = dialogItem.showAndWait();
                if(res.isEmpty()) return;
                if(res.get().getButtonData() == ButtonBar.ButtonData.OK_DONE){
                    //获得数量信息
                    double good_num = 0;
                    boolean allowFloat = false; //是否允许小数
                    allowFloat = Config.contains(Config.MEASURETYPE1,measure_unit);
                    try{
                        if(allowFloat){good_num = Double.valueOf(dialogItem.textFieldList.get(0).getText());}
                        else {good_num = Integer.valueOf(dialogItem.textFieldList.get(0).getText());}
                    }catch (NumberFormatException e){
                        AlertDialog dialog = new AlertDialog("数量格式不正确");
                        dialog.show();
                        return;
                    }
                    //检测数量信息是否符合需求
                    if(good_num == 0.0) return; //不添加
                    if(good_num < 0.0) {
                        //数量小于0，跳过
                        AlertDialog dialog = new AlertDialog("数量格式不正确");
                        dialog.show();
                        return;
                    }
                    //合并同类项
                    SaleOrderItem curItem = null;
                    for(SaleOrderItem item : saleOrderItemData){
                        if(item.getGood_id() == good_id){
                            curItem = item;
                            break;
                        }
                    }
                    if(curItem!=null){
                        //合并同类项
                        if(curItem.getGood_num() + good_num > curItem.getRemain_num()){
                            //库存不足
                            AlertDialog dialog = new AlertDialog("库存不足");
                            dialog.show();
                            return;
                        }else{
                            curItem.setGood_num(curItem.getGood_num() + good_num);
                            curItem.setGood_amount(curItem.getGood_num()*curItem.getGood_price());
                        }
                    }else{
                        //创建信息项目
                        if(good_num > remain_num){
                            AlertDialog dialog = new AlertDialog("库存不足");
                            dialog.show();
                            return;
                        }else{
                            curItem = new SaleOrderItem(good_id,good_name,good_price,measure_unit,good_num,remain_num);
                            //添加到队列
                            saleOrderItemData.add(curItem);
                        }
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
                //格式错误
                AlertDialog alertDialog = new AlertDialog("错误");
                alertDialog.show();
            }finally {
                //关闭数据库连接
                try{
                    Config.statement.close();
                    Config.connection.close();
                    Config.result.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
        /*修改按钮*/
        workspaceAlterButton.setOnAction((param)->{
            //获得选中项
            SaleOrderItem curItem = workspaceTableView.getSelectionModel().getSelectedItem();
            if(curItem==null){
                AlertDialog dialog = new AlertDialog("请选中条目");
                dialog.show();
                return;
            }
            //显示修改框
            ArrayList<String> labels = new ArrayList<>();
            labels.add("商品信息添加");
            labels.add("商品条码:"+curItem.getGood_id()+", 商品名称："+curItem.getGood_name());
            labels.add("计量单位:"+curItem.getMeasure_unit()+", 单价："+curItem.getGood_price());
            ArrayList<String> textFields = new ArrayList<>();
            textFields.add("销售数量");
            DialogItem dialogItem = new DialogItem(labels,textFields,new ArrayList<>(),true);
            //设置默认显示信息
            dialogItem.textFieldList.get(0).setText(String.valueOf(curItem.getGood_num()));
            Optional<ButtonType> res = dialogItem.showAndWait();
            if(res.isEmpty()) return;
            if(res.get().getButtonData() == ButtonBar.ButtonData.OK_DONE){
                //获得数量信息
                double good_num = 0;
                boolean allowFloat = false; //是否允许小数
                allowFloat = Config.contains(Config.MEASURETYPE1,curItem.getMeasure_unit());
                try{
                    if(allowFloat){good_num = Double.valueOf(dialogItem.textFieldList.get(0).getText());}
                    else {good_num = Integer.valueOf(dialogItem.textFieldList.get(0).getText());}
                }catch (NumberFormatException e){
                    AlertDialog dialog = new AlertDialog("数量格式不正确");
                    dialog.show();
                    return;
                }
                //检测数量信息是否符合需求
                if(good_num == 0.0) {
                    //数量改为0，从列表中删除该项
                    saleOrderItemData.remove(curItem);
                    return;
                }
                if(good_num < 0.0) {
                    //数量小于0，跳过
                    AlertDialog dialog = new AlertDialog("数量格式不正确");
                    dialog.show();
                    return;
                }
                //修改相关信息
                if(good_num > curItem.getRemain_num()){
                    AlertDialog dialog = new AlertDialog("库存不足");
                    dialog.show();
                    return;
                }
                curItem.setGood_num(good_num);
            }
        });
        //删除条目
        workspaceDeleteButton.setOnAction((param)->{
            SaleOrderItem curItem = workspaceTableView.getSelectionModel().getSelectedItem();
            if(curItem==null){
                AlertDialog dialog = new AlertDialog("请选中条目");
                dialog.show();
                return;
            }
            saleOrderItemData.remove(curItem);
        });
        /*清空购物车*/
        workspaceClearButton.setOnAction((param)->{
            saleOrderItemData.clear();
        });
        //结算按钮
        workspaceCountButton.setOnAction((param)->{
            if(saleOrderItemData.isEmpty()) return;
            //总价
            double amount = 0.0;
            for(SaleOrderItem item : saleOrderItemData){
                amount += item.getGood_amount();
            }
            double amount_after_discount = amount;
            //会员卡号
            int vip_id = -1;
            String vip_name = "";
            String vip_idString = workspaceVipField.getText();
            if(!vip_idString.equals("")){
                try{
                    vip_id = Integer.valueOf(vip_idString);
                }catch (NumberFormatException e){
                    AlertDialog dialog = new AlertDialog("格式不正确");
                    dialog.show();
                    return;
                }
                if(vip_id < 0){
                    AlertDialog dialog = new AlertDialog("格式不正确");
                    dialog.show();
                    return;
                }
                //在数据库中查询相关信息
                try{
                    //创建连接
                    Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                    String sql = "select * from vip_card_info where vip_id="+vip_id+";";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.result = Config.statement.executeQuery();//执行
                    if(!Config.result.next()){
                        AlertDialog dialog = new AlertDialog("会员信息不存在");
                        dialog.show();
                        return;
                    }else{
                        if(!Config.result.getBoolean(5)){
                            AlertDialog dialog = new AlertDialog("会员卡已过期，请重新办理");
                            dialog.show();
                            return;
                        }
                        vip_name = Config.result.getString(2);
                        amount_after_discount = amount * Config.DISCOUNT;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    //关闭数据库连接
                    try{
                        Config.result.close();
                        Config.statement.close();
                        Config.connection.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
            //显示收银支付信息
            DialogItem dialogItem = new DialogItem();
            dialogItem.getBox().getChildren().addAll(new Label("收银单确认"),new Label("消费总额："+amount+",折后总额："+amount_after_discount));
            if(vip_id!=-1){
                dialogItem.getBox().getChildren().add(new Label("会员卡："+vip_id+",会员姓名："+vip_name));
            }
            dialogItem.getDialogPane().getButtonTypes().addAll(new ButtonType("确认完成支付", ButtonBar.ButtonData.OK_DONE),new ButtonType("取消订单", ButtonBar.ButtonData.CANCEL_CLOSE));
            Optional<ButtonType> res = dialogItem.showAndWait();
            if(res.isEmpty()) return;
            if(res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE){
                //取消订单
                System.out.println("dddd");
            }else{
                System.out.println("dddd1");
                //完成订单信息
                int sale_id = 0;
                try{
                    //创建连接 FUNCTION `insert_sale_order`(vip_id int,amount double,amount_after_discount double) RETURNS int
                    Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                    String sql = "select insert_sale_order("+vip_id+","+amount+","+amount_after_discount+");";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.result = Config.statement.executeQuery();//执行
                    if(!Config.result.next()){
                        AlertDialog dialog = new AlertDialog("错误");
                        dialog.show();
                        return;
                    }else{
                        //获得收银单编号
                        sale_id = Config.result.getInt(1);
                        //将商品销售信息添加到数据库中
                        for(SaleOrderItem item : saleOrderItemData){
                            sql = "insert into goods_sold_info values("+null+","+sale_id+","+item.getGood_id()+","+item.getGood_num()+");";
                            Config.statement = Config.connection.prepareStatement(sql);
                            Config.statement.executeUpdate();
                        }
                    }
                    AlertDialog dialog = new AlertDialog("购买成功");
                    dialog.show();
                    saleOrderItemData.clear();

                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    //关闭数据库连接
                    try{
                        Config.result.close();
                        Config.statement.close();
                        Config.connection.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //销售金额超过一定数额
                if(vip_id==-1 && amount >= Config.AMOUNTFORGETVIPCARD){
                    AlertDialog alertDialog = new AlertDialog("消费超过"+Config.AMOUNTFORGETVIPCARD+" 是否免费办理会员卡？");

                    alertDialog.getOkButton().setOnAction((param1)->{
                        //alertDialog.close();
                        alertDialog.setAlwaysOnTop(false);
                        System.out.println("debug111");
                        ArrayList<String> message = new ArrayList<>();
                        int new_vip_id = -1;
                        try{
                            //创建连接
                            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                            String sql = "select distribute_vipcard(\'满额优惠\')";
                            Config.statement = Config.connection.prepareStatement(sql);
                            Config.result = Config.statement.executeQuery();//执行
                            Config.result.next();
                            new_vip_id = Config.result.getInt(1);

                        }catch(Exception e){
                            e.printStackTrace();
                        }finally {
                            //关闭数据库连接
                            try{
                                Config.result.close();
                                Config.statement.close();
                                Config.connection.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        //会员信息补充
                        message.clear();
                        message.add("会员卡编号："+new_vip_id);
                        message.add("请完善会员卡信息");
                        ArrayList<String> fieldTips = new ArrayList<>();
                        fieldTips.add("会员名 ");
                        DialogItem dialogItem1 = new DialogItem(message,fieldTips,new ArrayList<>(),false);
                        dialogItem1.setTitleText("会员信息补充");

                        Optional<ButtonType> res1 = dialogItem1.showAndWait();

                        //设置相关信息
                        String new_vip_name = dialogItem1.textFieldList.get(0).getText();
                        try{
                            //创建连接
                            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                            String sql = "update vip_card_info set vip_name=\'"+new_vip_name+"\' where vip_id="+new_vip_id+";";
                            Config.statement = Config.connection.prepareStatement(sql);
                            int result = Config.statement.executeUpdate();//执行
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally {
                            //关闭数据库连接
                            try{
                                Config.result.close();
                                Config.statement.close();
                                Config.connection.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        alertDialog.close();
                    });
                    alertDialog.show();
                }
            }


        });
        //会员卡派发按钮
        workspaceVipDistributeButton.setOnAction((param)->{
            //办理会员卡
            //调用函数，插入会员卡信息，获得会员卡编号，然后再进行会员卡信息得设置
            ArrayList<String> message = new ArrayList<>();
            message.add("正在申请会员卡");
            message.add("请完成支付 ￥50");

            DialogItem dialogItem = new DialogItem(message,new ArrayList<>(),new ArrayList<>(),true);
            Optional<ButtonType> res = dialogItem.showAndWait();
            if(res.isEmpty()) return;
            if(res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
            //会员卡申请
            int vip_id = 0;
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "select distribute_vipcard(\'办理\')";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();//执行
                Config.result.next();
                vip_id = Config.result.getInt(1);

            }catch(Exception e){
                e.printStackTrace();
            }finally {
                //关闭数据库连接
                try{
                    Config.result.close();
                    Config.statement.close();
                    Config.connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //会员信息补充
            message.clear();
            message.add("会员卡编号："+vip_id);
            message.add("请完善会员卡信息");
            ArrayList<String> fieldTips = new ArrayList<>();
            fieldTips.add("会员名 ");
            DialogItem dialogItem1 = new DialogItem(message,fieldTips,new ArrayList<>(),false);
            dialogItem1.setTitleText("会员信息补充");

            res = dialogItem1.showAndWait();
            //设置相关信息
            String vip_name = dialogItem1.textFieldList.get(0).getText();
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "update vip_card_info set vip_name=\'"+vip_name+"\' where vip_id="+vip_id+";";
                Config.statement = Config.connection.prepareStatement(sql);
                int result = Config.statement.executeUpdate();//执行
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                //关闭数据库连接
                try{
                    Config.result.close();
                    Config.statement.close();
                    Config.connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
/*2 超市数据*/
    /*2.1 超市库存信息 重用rootWindow相关代码*/
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
    /*2.2 会员卡信息*/
        BorderPane vipCardInfoBorderPane = (BorderPane)root.lookup("#vipCardInfoBorderPane");
        TableView<VipCardInfo> vipCardInfoTableView = (TableView) root.lookup("#vipCardInfoTableView");
        TableColumn []vipCardInfoColumns = new TableColumn[5];
        vipCardInfoColumns[0] = new TableColumn("会员编号");
        vipCardInfoColumns[1] = new TableColumn("会员姓名");
        vipCardInfoColumns[2] = new TableColumn("到期时间");
        vipCardInfoColumns[3] = new TableColumn("年度消费");
        vipCardInfoColumns[4] = new TableColumn("对接员工");
        vipCardInfoTableView.getColumns().addAll(vipCardInfoColumns);
        vipCardInfoColumns[0].setCellValueFactory(new PropertyValueFactory<>("vip_id"));
        vipCardInfoColumns[1].setCellValueFactory(new PropertyValueFactory<>("vip_name"));
        vipCardInfoColumns[2].setCellValueFactory(new PropertyValueFactory<>("end_time"));
        vipCardInfoColumns[3].setCellValueFactory(new PropertyValueFactory<>("amount_yearly"));
        vipCardInfoColumns[4].setCellValueFactory(new PropertyValueFactory<>("staff_name"));
        vipCardInfoTableView.setItems(vipCardInfoData);
        updateVipCardInfoData();
/*3 信息查询*/
    /*3.1 打卡记录查询*/
        BorderPane saveDiskInfoBorderPane = (BorderPane) root.lookup("#saveDiskInfoBorderPane");
        TableView<SaveDiskInfo> saveDiskInfoTableView = (TableView)root.lookup("#saveDiskInfoTableView");
        TableColumn[] saveDiskInfoTableColumns = new TableColumn[6];
        saveDiskInfoTableColumns[0] = new TableColumn("序号");
        saveDiskInfoTableColumns[1] = new TableColumn("员工姓名");
        saveDiskInfoTableColumns[2] = new TableColumn("员工职务");
        saveDiskInfoTableColumns[3] = new TableColumn("销售总额");
        saveDiskInfoTableColumns[4] = new TableColumn("打卡上班时间");
        saveDiskInfoTableColumns[5] = new TableColumn("打卡下班时间");
        saveDiskInfoTableView.getColumns().addAll(saveDiskInfoTableColumns);
        saveDiskInfoTableColumns[0].setCellValueFactory(new PropertyValueFactory<>("id"));
        saveDiskInfoTableColumns[1].setCellValueFactory(new PropertyValueFactory<>("staff_name"));
        saveDiskInfoTableColumns[2].setCellValueFactory(new PropertyValueFactory<>("staff_type"));
        saveDiskInfoTableColumns[3].setCellValueFactory(new PropertyValueFactory<>("amount"));
        saveDiskInfoTableColumns[4].setCellValueFactory(new PropertyValueFactory<>("login_in_time"));
        saveDiskInfoTableColumns[5].setCellValueFactory(new PropertyValueFactory<>("login_off_time"));
        saveDiskInfoTableView.setItems(saveDiskInfoData);
        updateSaveDiskInfoData();
    /*3.2 收银表单*/
        BorderPane saleInfoBorderPane = (BorderPane)root.lookup("#saleInfoBorderPane");
        TableView<SaleInfo> saleInfoTableView = (TableView) root.lookup("#saleInfoTableView");
        TableColumn[] saleInfoColumns = new TableColumn[6];
        saleInfoColumns[0] = new TableColumn("售货单号");
        saleInfoColumns[1] = new TableColumn("收银员");
        saleInfoColumns[2] = new TableColumn("会员卡号");
        saleInfoColumns[3] = new TableColumn("时间");
        saleInfoColumns[4] = new TableColumn("总价");
        saleInfoColumns[5] = new TableColumn("折后总价");
        saleInfoTableView.getColumns().addAll(saleInfoColumns);
        saleInfoColumns[0].setCellValueFactory(new PropertyValueFactory<>("sale_id"));
        saleInfoColumns[1].setCellValueFactory(new PropertyValueFactory<>("staff_name"));
        saleInfoColumns[2].setCellValueFactory(new PropertyValueFactory<>("vip_id"));
        saleInfoColumns[3].setCellValueFactory(new PropertyValueFactory<>("time"));
        saleInfoColumns[4].setCellValueFactory(new PropertyValueFactory<>("amount"));
        saleInfoColumns[5].setCellValueFactory(new PropertyValueFactory<>("amount_afterDiscount"));
        saleInfoTableView.setItems(saleInfoData);
        updateSaleInfoData(); //更新数据信息
        /*收银表单详情*/
        Button saleorderButton = (Button) root.lookup("#saleorderButton");
        saleorderButton.setOnAction((param)->{
            //查看收银单详情，收银单详情
            SaleInfo curItem = saleInfoTableView.getSelectionModel().getSelectedItem();
            int saleInfo_id = curItem.getSale_id();
            AlertDialog dialog = new AlertDialog("收银单单号："+curItem.getSale_id()+",会员卡号："+curItem.getVip_id());
            dialog.setTitle("收银单详情");
            dialog.getDialogItemsPane().setPadding(new Insets(20));
            dialog.getDialogItemsPane().getChildren().add(new Label("收银员："+curItem.getStaff_name()));
            dialog.getDialogItemsPane().getChildren().add(new Label("----------------------------------------"));
            dialog.getDialogItemsPane().getChildren().add(new Label("条码\t名称\t单价\t计量单位\t数量\t小计"));
            try{
                //创建连接
                double amount = 0;
                String sql = "select goods_info.good_id,good_name,good_price,measure_unit,good_num from goods_sold_info,goods_info where goods_sold_info.good_id = goods_info.good_id and sale_info_id="+saleInfo_id+";";
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();
                while(Config.result.next()){
                    amount = Config.result.getDouble(3) * Config.result.getDouble(5);
                    dialog.getDialogItemsPane().getChildren().add(new Label(Config.result.getString(1)+"\t"+
                            Config.result.getString(2)+"\t"+Config.result.getString(3)+"\t"+Config.result.getString(4)+"\t\t"+
                            Config.result.getString(5)+"\t"+amount));
                }
            }catch(Exception e){
                e.printStackTrace();
                //格式错误
                AlertDialog alertDialog = new AlertDialog("错误:格式错误，无法执行");
                alertDialog.show();
            }finally {
                //关闭数据库连接
                try{
                    Config.statement.close();
                    Config.connection.close();
                    Config.result.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            dialog.getDialogItemsPane().getChildren().add(new Label("----------------------------------------"));
            dialog.getDialogItemsPane().getChildren().add(new Label("销售总额："+curItem.getAmount()+"\t\t折后总额："+curItem.getAmount_afterDiscount()));
            dialog.show();
        });



        /*信息控制*/
        //内部类，设置右侧界面全不可见
        class SetVisible{
            void setVisible(){
                //设置不可见
                workspaceBorderPane.setVisible(false);
                goodsInfoBorderPane.setVisible(false);
                vipCardInfoBorderPane.setVisible(false);
                saveDiskInfoBorderPane.setVisible(false);
                saleInfoBorderPane.setVisible(false);
            }
        }
        new SetVisible().setVisible();
        //下拉菜单按钮
        Button listButton_workspace = (Button) root.lookup("#listButton_workspace");
        Button listButton_goodsInfo = (Button) root.lookup("#listButton_goodsInfo");
        Button listButton_vipCardInfo = (Button)root.lookup("#listButton_vipCardInfo");
        Button listButton_saveDiskInfo = (Button) root.lookup("#listButton_saveDiskInfo");
        Button listButton_saleInfo = (Button) root.lookup("#listButton_saleInfo");
        //下拉按钮响应事件
        listButton_workspace.setOnAction((param)->{
            new SetVisible().setVisible();
            workspaceBorderPane.setVisible(true);
            contentSlide();
        });
        listButton_goodsInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            goodsInfoBorderPane.setVisible(true);
            contentSlide();
        });
        listButton_vipCardInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            vipCardInfoBorderPane.setVisible(true);
            contentSlide();
        });
        listButton_saveDiskInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            saveDiskInfoBorderPane.setVisible(true);
            contentSlide();
        });
        listButton_saleInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            saleInfoBorderPane.setVisible(true);
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
    public void show() {
        stage.show();
    }
    public void close(){
        stage.close();
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
    /*更新会员卡信息*/
    void updateVipCardInfoData(){
        vipCardInfoData.clear();
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select vip_card_info.vip_id,vip_name,end_time,amount_yearly,staff_name from vip_card_info,vipcard_distribute,staff_info" +
                    " where vip_card_info.vip_id=vipcard_distribute.vip_id and vipcard_distribute.staff_id=staff_info.staff_id;";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            //创建信息
            while(Config.result.next()){
                vipCardInfoData.add(new VipCardInfo(Config.result.getInt(1),Config.result.getString(2),Config.result.getString(3),
                        Config.result.getDouble(4),Config.result.getString(5)));
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
    /*更新收银信息*/
    void updateSaleInfoData(){
        saleInfoData.clear();
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select sale_id,staff_name,vip_id,time,amount,amount_after_discount from sale_info,staff_info where sale_info.staff_id=staff_info.staff_id and staff_info.staff_name=getUser();";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            //创建信息
            while(Config.result.next()){
                saleInfoData.add(new SaleInfo(Config.result.getInt(1),Config.result.getString(2),
                        Config.result.getString(3),Config.result.getString(4),Config.result.getInt(5),
                        Config.result.getInt(6)));
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
}
