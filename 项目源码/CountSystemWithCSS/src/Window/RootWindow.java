package Window;

import dataItem.*;
import dialog.AlertDialog;
import dialog.StaffInfoAlterDialog;
import dialog.StaffInfoAlterDialog_dialog;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.Config;

import java.sql.DriverManager;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

/**
 * 店长窗口，
 *      基本菜单：1.员工信息
 *                  1.1 员工基本信息查询与修改
 *                  1.2 员工申请表
 *              2.超市数据
 *                  2.1 收银表单一览
 *                  2.2 进货账单一览
 *                  2.3 单一商品销售情况查询
 *                  2.4 会员卡信息一览
 *                      2.4.1 会员消费状况汇总
 *                  2.5 超市库存信息
 *              3.营收报表
 *                  3.1 年度/季度营收报表(TODO)
 *                  3.2 收银存盘数据一览
 *              4.数据库控制台（root账号全权限）
 */


public class RootWindow {
    Stage stage = new Stage();
    Stage superStage = null;    //上一级stage
    Parent root = null;
    //员工信息可观测列表
    private ObservableList<StaffInfo> staffData = FXCollections.observableArrayList();
    //申请表信息观测表
    private ObservableList<ApplyInfo> applyData = FXCollections.observableArrayList();
    //收银表单观测表
    private ObservableList<SaleInfo> saleInfoData = FXCollections.observableArrayList();
    //进货单
    private ObservableList<PurchaseOrder> purchaseOrderData = FXCollections.observableArrayList();
    //商品销售信息
    private ObservableList<GoodsSoldInfo> goodsSoldInfoData = FXCollections.observableArrayList();
    //会员卡信息
    private ObservableList<VipCardInfo> vipCardInfoData = FXCollections.observableArrayList();
    //库存信息
    private ObservableList<GoodsInfo> goodsInfoData = FXCollections.observableArrayList();
    //盘存信息
    private ObservableList<SaveDiskInfo> saveDiskInfoData = FXCollections.observableArrayList();
    //字体信息
    XYChart.Series series = null;
    public RootWindow(Stage superStage){
        this.superStage = superStage;
        try{
            root = FXMLLoader.load(LoginWindow.class.getResource("../resources/fxml/rootWindow.fxml"));
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
            //最小化
            stage.setIconified(true);
        });
        closeWindow.setOnAction(event -> {
            //退出按钮
            System.exit(0);
        });
        Scene scene = new Scene(root,1800,990);
        scene.getStylesheets().add(LoginWindow.class.getResource("/resources/css/window.css").toExternalForm());
        //加载
        stage.setScene(scene);

        //菜单栏按钮图标处理TODO
        //下拉菜单栏设计
        /*获得下拉菜单容器*/
        AnchorPane stuffAnchorPane = (AnchorPane) root.lookup("#stuffAnchorPane");
        AnchorPane dataAnchorPane = (AnchorPane) root.lookup("#dataAnchorPane");
        AnchorPane listAnchorPane = (AnchorPane) root.lookup("#listAnchorPane");
        AnchorPane cmdAnchorPane = (AnchorPane) root.lookup("#cmdAnchorPane");
        AnchorPane containButtonAnchorPane = (AnchorPane) root.lookup("#containButtonAnchorPane");
        //设置非可见
        stuffAnchorPane.setDisable(true);
        dataAnchorPane.setDisable(true);
        listAnchorPane.setDisable(true);
        cmdAnchorPane.setDisable(true);
        /*按钮信息*/
        Button stuffButton = (Button)root.lookup("#stuffButton");
        Button dataButton = (Button)root.lookup("#dataButton");
        Button listButton = (Button)root.lookup("#listButton");
        Button cmdButton = (Button)root.lookup("#cmdButton");
        /*员工按钮显示信息*/
        stuffButton.setOnAction((param)->{
            stuffAnchorPane.setDisable(false);
            stuffAnchorPane.setVisible(true);

            dataAnchorPane.setDisable(true);
            dataAnchorPane.setVisible(false);

            listAnchorPane.setDisable(true);
            listAnchorPane.setVisible(false);

            cmdAnchorPane.setDisable(true);
            cmdAnchorPane.setVisible(false);
            buttonSlide();
        });
        /*数据按钮*/
        dataButton.setOnAction((param)->{
            stuffAnchorPane.setDisable(true);
            stuffAnchorPane.setVisible(false);

            dataAnchorPane.setDisable(false);
            dataAnchorPane.setVisible(true);

            listAnchorPane.setDisable(true);
            listAnchorPane.setVisible(false);

            cmdAnchorPane.setDisable(true);
            cmdAnchorPane.setVisible(false);
            buttonSlide();
        });
        /*列表按钮*/
        listButton.setOnAction((param)->{
            stuffAnchorPane.setDisable(true);
            stuffAnchorPane.setVisible(false);

            dataAnchorPane.setDisable(true);
            dataAnchorPane.setVisible(false);

            listAnchorPane.setDisable(false);
            listAnchorPane.setVisible(true);

            cmdAnchorPane.setDisable(true);
            cmdAnchorPane.setVisible(false);
            buttonSlide();
        });
        /*控制台按钮*/
        cmdButton.setOnAction((param)->{
            stuffAnchorPane.setDisable(true);
            stuffAnchorPane.setVisible(false);

            dataAnchorPane.setDisable(true);
            dataAnchorPane.setVisible(false);

            listAnchorPane.setDisable(true);
            listAnchorPane.setVisible(false);

            cmdAnchorPane.setDisable(false);
            cmdAnchorPane.setVisible(true);
            buttonSlide();
        });
        /*头像显示*/
        ImageView headImage = (ImageView) root.lookup("#headImage");
        headImage.setImage(new Image("\\resources\\images\\staff.jpg"));
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

        /*更新按钮*/
        Button updateButton = (Button)root.lookup("#updateButton");
        updateButton.setOnAction((param)->{
            updateApplyInfoData();
            updateStaffInfoData();
            updateSaleInfoData();
            updatePurchaseOrderData();
            updateGoodsSoldInfoData();
            updateVipCardInfoData();
            updateGoodsInfoData();
            updateSaveDiskInfoData();
        });

        //右侧信息栏pane
/*1 员工信息 */

    /*员工基本信息窗口*/
        BorderPane staffInfoBorderPane = (BorderPane)root.lookup("#staffInfoBorderPane");
        //员工账号管理表格
        TableView<StaffInfo> staffInfoTableView =(TableView)root.lookup("#staffInfoTableView");
        TableColumn []staffInfoColumns = new TableColumn[5];
        staffInfoColumns[0] = new TableColumn("编号");
        staffInfoColumns[1] = new TableColumn("姓名");
        staffInfoColumns[2] = new TableColumn("职务");
        staffInfoColumns[3] = new TableColumn("状态");
        staffInfoColumns[4] = new TableColumn("上次登录时间");
        staffInfoTableView.getColumns().addAll(staffInfoColumns);
        staffInfoColumns[0].setCellValueFactory(new PropertyValueFactory<>("staff_id"));
        staffInfoColumns[1].setCellValueFactory(new PropertyValueFactory<>("staff_name"));
        staffInfoColumns[2].setCellValueFactory(new PropertyValueFactory<>("staff_type"));
        staffInfoColumns[3].setCellValueFactory(new PropertyValueFactory<>("staff_state"));
        staffInfoColumns[4].setCellValueFactory(new PropertyValueFactory<>("last_login_time"));
        staffInfoTableView.setItems(staffData);
        staffInfoTableView.setPadding(new Insets(20));
        //更新数据列表
        updateStaffInfoData();

        TextField staffInfoSearchField = (TextField) root.lookup("#staffInfoSearchField");
        staffInfoSearchField.setPromptText("请输入查询信息");
        ChoiceBox<String> staffInfoSearchChoiceBox = (ChoiceBox) root.lookup("#staffInfoSearchChoiceBox");
        staffInfoSearchChoiceBox.getItems().addAll("通过编号查询","通过账号名查询");
        staffInfoSearchChoiceBox.getSelectionModel().select(0);
        Button staffInfoSearchButton = (Button) root.lookup("#staffInfoSearchButton");
        Button staffInfoAlterButton = (Button)root.lookup("#staffInfoAlterButton");
        Button staffInfoDismissalButton = (Button)root.lookup("#staffInfoDismissalButton");
        //查询按钮响应事件
        staffInfoSearchButton.setOnAction((param)->{
            //获得查询信息
            String input = staffInfoSearchField.getText();
            int id = -1;
            StaffInfo res = null;
            if(input.equals("")){
                AlertDialog dialog = new AlertDialog("请输入查询信息");
                dialog.show();
                return;
            }
            boolean byNo = false;
            if(staffInfoSearchChoiceBox.getSelectionModel().getSelectedIndex()==0) byNo=true;
            if(byNo){
                //通过编号查询
                try{
                    id = Integer.valueOf(input);
                }catch(NumberFormatException e){
                    //格式不正确
                    AlertDialog dialog = new AlertDialog("输入格式不正确");
                    dialog.show();
                    return;
                }
            }
            for(StaffInfo staff:staffData){
                //查询数据信息
                if((byNo&&staff.getStaff_id()==id)||(!byNo&&staff.getStaff_name().equals(input))){
                    staffInfoTableView.getSelectionModel().select(staff);
                    return;
                }
            }
            //未找到
            AlertDialog dialog = new AlertDialog("未找到条目");
            dialog.show();
        });
        //修改事件响应
        staffInfoAlterButton.setOnAction((param)->{
            StaffInfo staff = staffInfoTableView.getSelectionModel().getSelectedItem();
            //未选中
            if(staff==null){
                AlertDialog dialog = new AlertDialog("提示：请选中修改项");
                dialog.show();
                return;
            }
            if(staff.getStaff_type().equals("超级管理员")){
                AlertDialog dialog = new AlertDialog("不能修改超级管理员账号");
                dialog.show();
                return;
            }
            StaffInfoAlterDialog_dialog dialog = new StaffInfoAlterDialog_dialog(staff);
            Optional<ButtonType> result = dialog.showAndWait();
            if(result.isEmpty()||!(result.get().getButtonData()== ButtonBar.ButtonData.OK_DONE)) return; //取消修改

            String newName = dialog.staffNameField.getText();
            int newType = dialog.staffTypeChoiceBox.getSelectionModel().getSelectedIndex();
            if(newName.equals("")||newType<0||newType>2){
                AlertDialog dialog1 = new AlertDialog("修改项不能为空");
                dialog1.show();
                return;
            }
            //获得原职位信息
            String oldTypeStr = staff.getStaff_type();
            int oldType = 0;
            if(oldTypeStr.equals("店长")) oldType = 0;
            else if(oldTypeStr.equals("收银员")) oldType = 1;
            else if(oldTypeStr.equals("仓库管理员")) oldType = 2;
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "select updateInfo_staff_info("+staff.getStaff_id()+",\'"+newName+"\',"+newType+");";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();//执行
                Config.result.next();
                boolean res = Config.result.getBoolean(1);
                if(!res){ //修改失败
                    AlertDialog alertDialog = new AlertDialog("失败：账号名重复，请查证后再修改相关信息。");
                    alertDialog.show();
                    return;
                }else{
                    //修改权限信息
                    String role = "";
                    if(oldType==0){
                        role = "ManagerRole";
                    }else if(oldType == 1){
                        role = "CashierRole";
                    }else{
                        role = "StoreRole";
                    }
                    sql = "revoke "+role+" from "+staff.getStaff_name()+";";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.execute();//执行
                    if(newType==0){
                        role = "ManagerRole";
                    }else if(newType == 1){
                        role = "CashierRole";
                    }else{
                        role = "StoreRole";
                    }
                    sql = "grant "+role+" to "+staff.getStaff_name()+";";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.execute();//执行
                    sql = "set default role "+role+" to "+staff.getStaff_name()+";";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.execute();//执行
                    AlertDialog alertDialog = new AlertDialog("成功：修改成功。\nstaff_name:"+staff.getStaff_name()+"->"+newName+"\nstaff_type:"+staff.getStaff_type()+"->"+newType);
                    alertDialog.show();
                }
            }catch(Exception e){
                e.printStackTrace();
                //格式错误
                AlertDialog alertDialog = new AlertDialog("错误：修改错误，请检查格式信息");
                alertDialog.show();
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

            updateStaffInfoData();
        });

        //解雇事件响应
        staffInfoDismissalButton.setOnAction((param)->{
            StaffInfo staff = staffInfoTableView.getSelectionModel().getSelectedItem();
            //未选中
            if(staff==null){
                AlertDialog dialog = new AlertDialog("提示：请选中解雇项");
                dialog.show();
                return;
            }
            if(staff.getStaff_type().equals("超级管理员")){
                AlertDialog dialog = new AlertDialog("不能解雇超级管理员账号");
                dialog.show();
                return;
            }
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                //修改状态为 -1 表示已经离职
                String sql = "update staff_info set staff_state=-1 where staff_id="+staff.getStaff_id()+";";
                Config.statement = Config.connection.prepareStatement(sql);
                int result = Config.statement.executeUpdate();//执行
                if(result==-1){ //修改失败
                    AlertDialog alertDialog = new AlertDialog("失败");
                    alertDialog.show();
                    return;
                }else{
                    AlertDialog alertDialog = new AlertDialog("成功：解雇成功！");
                    alertDialog.show();
                    //删除数据库存在的用户信息
                    sql = "drop user "+staff.getStaff_name()+";";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.execute();
                }
            }catch(Exception e){
                e.printStackTrace();
                //格式错误
                AlertDialog alertDialog = new AlertDialog("错误：解雇失败");
                alertDialog.show();
            }finally {
                //关闭数据库连接
                try{
                    Config.statement.close();
                    Config.connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            updateStaffInfoData();
        });


    /*申请表信息处理*/
        BorderPane applyInfoBorderPane = (BorderPane) root.lookup("#applyInfoBorderPane");
        //表格信息
        TableView<ApplyInfo> applyInfoTableView = (TableView)root.lookup("#applyInfoTableView");
        TableColumn[] applyColumns = new TableColumn[3];
        applyColumns[0] = new TableColumn("申请名");
        applyColumns[1] = new TableColumn("申请职位");
        applyColumns[2] = new TableColumn("申请时间");
        applyInfoTableView.getColumns().addAll(applyColumns);
        applyColumns[0].setCellValueFactory(new PropertyValueFactory<>("apply_name"));
        applyColumns[1].setCellValueFactory(new PropertyValueFactory<>("apply_type"));
        applyColumns[2].setCellValueFactory(new PropertyValueFactory<>("apply_time"));
        applyInfoTableView.setItems(applyData);
        updateApplyInfoData();
        //按钮响应事件处理
        Button applyTable_applyButton = (Button) root.lookup("#applyTable_applyButton");
        Button applyTable_rejectButton = (Button) root.lookup("#applyTable_rejectButton");
        //通过申请按钮
        applyTable_applyButton.setOnAction((param)->{
            //获得选中项
            ApplyInfo applyinfo = applyInfoTableView.getSelectionModel().getSelectedItem();
            if(applyinfo==null){
                AlertDialog dialog=new AlertDialog("请选中表项");
                dialog.show();
            }
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                //FUNCTION `apply_application_form`(applyId int,isApplied boolean)
                String sql = "select apply_application_form("+applyinfo.getApply_id()+",true);";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();//执行
                Config.result.next();
                if(!Config.result.getBoolean(1)){
                    //失败
                    AlertDialog dialog = new AlertDialog("失败:通过申请失败，请查看员工表已经存在当前账号");
                    dialog.show();
                }else{
                    AlertDialog dialog = new AlertDialog("成功:通过申请成功");
                    dialog.show();
                    //授予相关权限
                    String role = "";
                    if(applyinfo.getApply_type().equals("收银员")) { role = "CashierRole";}
                    else if(applyinfo.getApply_type().equals("仓库管理员")){role = "StoreRole";}
                    //创建用户
                    sql = "create user \'"+applyinfo.getApply_name()+ "\'@\'%\' IDENTIFIED BY \'"+applyinfo.getPassword()+"\';";
                    //执行
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.execute();
                    //授予角色权限
                    sql = "grant "+role+" to "+applyinfo.getApply_name()+"@\'%\';";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.execute();
                    //设置默认登录角色
                    sql = "set default role "+role+" to "+applyinfo.getApply_name()+"@\'%\';";
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.execute();
                }
            }catch(Exception e){
                e.printStackTrace();
                //格式错误
                AlertDialog alertDialog = new AlertDialog("错误：请检查是否与已有账号冲突");
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
            updateStaffInfoData();
            updateApplyInfoData();
        });
        //拒绝申请按钮响应事件
        applyTable_rejectButton.setOnAction((param)->{
            //获得选中项
            ApplyInfo applyinfo = applyInfoTableView.getSelectionModel().getSelectedItem();
            if(applyinfo==null){
                AlertDialog dialog=new AlertDialog("请选中表项");
                dialog.show();
            }
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                //FUNCTION `apply_application_form`(applyId int,isApplied boolean)
                String sql = "select apply_application_form("+applyinfo.getApply_id()+",false);";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();//执行
                Config.result.next();
                if(!Config.result.getBoolean(1)){
                    //失败
                    AlertDialog dialog = new AlertDialog("失败");
                    dialog.show();
                }else{
                    AlertDialog dialog = new AlertDialog("成功:已拒绝申请");
                    dialog.show();
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
            updateStaffInfoData();
            updateApplyInfoData();
        });

/*2 超市数据 */
    /*2.1 收银表单*/
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
        //收银表详情
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


    /*2.2 进货单 */
        BorderPane purchaseOrderBorderPane = (BorderPane)root.lookup("#purchaseOrderBorderPane");
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


    /*2.3 商品销售情况*/
        BorderPane goodsSoldInfoBorderPane = (BorderPane)root.lookup("#goodsSoldInfoBorderPane");
        TableView<GoodsSoldInfo> goodsSoldInfoTableView = (TableView) root.lookup("#goodsSoldInfoTableView");
        TableColumn[] goodsSoldInfoColumns = new TableColumn[5];
        goodsSoldInfoColumns[0] = new TableColumn("编号");
        goodsSoldInfoColumns[1] = new TableColumn("售货单号");
        goodsSoldInfoColumns[2] = new TableColumn("商品编号");
        goodsSoldInfoColumns[3] = new TableColumn("商品名");
        goodsSoldInfoColumns[4] = new TableColumn("销售数量");
        goodsSoldInfoTableView.getColumns().addAll(goodsSoldInfoColumns);
        goodsSoldInfoColumns[0].setCellValueFactory(new PropertyValueFactory<>("good_sold_id"));
        goodsSoldInfoColumns[1].setCellValueFactory(new PropertyValueFactory<>("sale_info_id"));
        goodsSoldInfoColumns[2].setCellValueFactory(new PropertyValueFactory<>("good_id"));
        goodsSoldInfoColumns[3].setCellValueFactory(new PropertyValueFactory<>("good_name"));
        goodsSoldInfoColumns[4].setCellValueFactory(new PropertyValueFactory<>("good_num"));
        goodsSoldInfoTableView.setItems(goodsSoldInfoData);
        updateGoodsSoldInfoData();

    /*2.4 会员信息*/
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

    /*2.5 超市库存信息*/
        BorderPane goodsInfoBorderPane = (BorderPane)root.lookup("#goodsInfoBorderPane");
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
        //修改按钮
        Button goods_info_alterButton = (Button) root.lookup("#goods_info_alterButton");
        goods_info_alterButton.setOnAction((param)->{
            GoodsInfo goodsInfo = goodsInfoTableView.getSelectionModel().getSelectedItem();
            if(goodsInfo==null){
                AlertDialog dialog = new AlertDialog("请选中商品");
                dialog.setTitle("警告");
                dialog.show();
                return;
            }
            AlertDialog  dialog = new AlertDialog("请输入修改后的信息");
            dialog.setTitle("修改商品信息");
            dialog.getDialogItemsPane().setPadding(new Insets(20));
            dialog.getDialogItemsPane().setSpacing(5);
            //商品名
            HBox nameHBox = new HBox();
            TextField nameField = new TextField();
            nameHBox.getChildren().addAll(new Label("名称"),nameField);
            nameField.setText(goodsInfo.getGood_name());
            //单价
            HBox priceHBox = new HBox();
            TextField priceField = new TextField();
            priceHBox.getChildren().addAll(new Label("单价"),priceField);
            priceField.setText(String.valueOf(goodsInfo.getGood_price()));
            //数量
            HBox numHBox = new HBox();
            TextField numField = new TextField();
            numHBox.getChildren().addAll(new Label("数量"),numField);
            numField.setText(String.valueOf(goodsInfo.getRemain_num()));
            //计量单位
            HBox measureHBox = new HBox();
            TextField measureField = new TextField();
            measureHBox.getChildren().addAll(new Label("单位"),measureField);
            measureField.setText(goodsInfo.getMeasure_unit());
            //添加到布局
            dialog.getDialogItemsPane().getChildren().addAll(nameHBox,priceHBox,measureHBox,numHBox);
            //显示信息
            dialog.getOkButton().setOnAction((param1)->{
                //获取相关信息
                String name = nameField.getText();
                String measure = measureField.getText();
                double num = 0;
                double price = 0;
                try{
                    price = Double.valueOf(priceField.getText());
                    if(Config.contains(Config.MEASURETYPE1,measure)){
                        num = Double.valueOf(numField.getText());
                    }else {
                        num = Integer.valueOf(numField.getText());
                    }
                }catch(NumberFormatException e){
                    AlertDialog dialog1 = new AlertDialog("请检查输入数据是否合法");
                    dialog1.show();
                    return;
                }
                //修改相关信息
                try{
                    String sql = "update goods_info set good_name=\'"+name+"\',good_price="+price+",measure_unit=\'"+measure+"\',remain_num="+num+" where good_id="+goodsInfo.getGood_id()+";";
                    //创建连接
                    Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                    Config.statement = Config.connection.prepareStatement(sql);
                    Config.statement.executeUpdate();
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
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                updateGoodsInfoData();
                dialog.close();
            });
            dialog.show();

        });
        TextField goods_info_searchField = (TextField) root.lookup("#goods_info_searchField");
        ChoiceBox<String> goods_info_searchChoiceBox = (ChoiceBox)root.lookup("#goods_info_searchChoiceBox");
        goods_info_searchChoiceBox.getItems().addAll("通过商品条码查询","通过商品名查询");
        goods_info_searchChoiceBox.setValue("通过商品条码查询");
        Button goods_info_searchButton = (Button) root.lookup("#goods_info_searchButton");
        goods_info_searchButton.setOnAction((param)->{
            //获得信息
            if(goods_info_searchChoiceBox.getSelectionModel().getSelectedIndex()==0){
                int good_num = -1;
                try{
                    good_num = Integer.valueOf(goods_info_searchField.getText());
                }catch (NumberFormatException e){
                    AlertDialog dialog = new AlertDialog("条码格式不正确，请重新输入");
                    dialog.setTitle("警告");
                    dialog.show();
                    return;
                }
                //删除队列中相关信息
                for(GoodsInfo curItem : goodsInfoData){
                    if(curItem.getGood_id() != good_num){
                        goodsInfoData.remove(curItem);
                    }
                }

            }else{
                String good_name = goods_info_searchField.getText();
                for(GoodsInfo curItem : goodsInfoData){
                    if(!curItem.getGood_name().equals(good_name)){
                        goodsInfoData.remove(curItem);
                    }
                }
            }

        });
        //删除按钮，按下后将相关商品数量置0
        Button goods_info_deleteButton = (Button) root.lookup("#goods_info_deleteButton");
        goods_info_deleteButton.setOnAction((param)->{
            //将数量修改为0
            GoodsInfo goodsInfo = goodsInfoTableView.getSelectionModel().getSelectedItem();
            if(goodsInfo==null){
                AlertDialog dialog = new AlertDialog("请选中表项");
                dialog.setTitle("提示");
                dialog.show();
                return;
            }
            try{
                String sql = "update goods_info set remain_num=0 where good_id="+goodsInfo.getGood_id()+";";
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                Config.statement = Config.connection.prepareStatement(sql);
                Config.statement.executeUpdate();
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                //关闭数据库连接
                try{
                    Config.statement.close();
                    Config.connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            updateGoodsInfoData();

        });

/*3 统计报表*/
    /*3.1 营收统计*/
        BorderPane statisticInfoBorderPane = (BorderPane)root.lookup("#statisticInfoBorderPane");

        /* 首页 1： 销售总额/利润总额 事件选取：最近一年 最近一月 最近一周 最近一天 */
        /* 附页 2： 最近30天销售额折线图 */
        AnchorPane lineAnchorPane = (AnchorPane) root.lookup("#lineAnchorPane");
        NumberAxis xAxis = new NumberAxis(-30,0,1);
        NumberAxis yAxis = new NumberAxis();

        LineChart<Integer,Double> amountChart = new LineChart(xAxis,yAxis);
        xAxis.setLabel("日期");
        yAxis.setLabel("金额");
        series = new XYChart.Series();
        series.setName("营业额曲线");
        amountChart.getData().add(series);
        lineAnchorPane.getChildren().add(amountChart);
        /* 附页 4： 商品销售数量情况统计饼状图、商品销售利润收入饼状图*/

        updateAmount();
    /*3.2 盘存信息*/
        BorderPane saveDiskInfoBorderPane = (BorderPane)root.lookup("#saveDiskInfoBorderPane");
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

/*4 数据库控制台*/
        BorderPane cmdBorderPane = (BorderPane)root.lookup("#cmdBorderPane");

        Button executeButton = (Button) root.lookup("#executeButton");
        TextArea codeTextArea = (TextArea) root.lookup("#codeTextArea");
        TextArea resultTextArea =(TextArea) root.lookup("#resultTextArea");

        executeButton.setOnAction((param)->{
            String sql = codeTextArea.getText();
            boolean isSelect = false;
            if(sql.contains("select"))isSelect=true;
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                Config.statement = Config.connection.prepareStatement(sql);
                if(isSelect){
                    Config.result = Config.statement.executeQuery();//执行
                    //输出相关信息
                    String res = "";
                    while(Config.result.next()){
                        for(int i = 1;i <= Config.result.getRow();i++)
                        while(true){
                            try {
                                res = Config.result.getString(i++);
                                resultTextArea.appendText(res + "\t");
                            }catch(Exception e){
                                break;
                            }
                        }
                        resultTextArea.appendText("\n");
                    }
                }else{
                    int res = Config.statement.executeUpdate();
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
                    if(isSelect) Config.result.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });


        /*信息控制*/
        //内部类，设置全不可见
        class SetVisible{
            void setVisible(){
                //设置不可见
                staffInfoBorderPane.setVisible(false);
                applyInfoBorderPane.setVisible(false);
                cmdBorderPane.setVisible(false);
                saleInfoBorderPane.setVisible(false);
                purchaseOrderBorderPane.setVisible(false);
                goodsSoldInfoBorderPane.setVisible(false);
                vipCardInfoBorderPane.setVisible(false);
                goodsInfoBorderPane.setVisible(false);
                statisticInfoBorderPane.setVisible(false);
                saveDiskInfoBorderPane.setVisible(false);
            }
        }
        new SetVisible().setVisible();

        //左侧下拉菜单按钮信息
        Button listButton_staffArrange = (Button)root.lookup("#listButton_staffArrange"); /* 1.1 员工账号管理*/
        Button listButton_applyInfo = (Button)root.lookup("#listButton_applyInfo");/* 1.2 员工申请表信息*/
        Button listButton_cmdButton = (Button)root.lookup("#listButton_cmdButton");/* 4.1 数据库控制台 */
        Button listButton_SaleInfo = (Button)root.lookup("#listButton_SaleInfo");/* 2.1 收银表单*/
        Button listButton_purchaseOrder = (Button)root.lookup("#listButton_purchaseOrder"); /*2.2 进货单 */
        Button listButton_goodsSoldInfo = (Button)root.lookup("#listButton_goodsSoldInfo");/*2.3 商品销售状况 */
        Button listButton_vipCardInfo = (Button)root.lookup("#listButton_vipCardInfo");/*2.4 会员卡信息*/
        Button listButton_goofdInfo = (Button)root.lookup("#listButton_goofdInfo"); /*2.5 库存信息*/
        Button listButton_statisticInfo = (Button) root.lookup("#listButton_statisticInfo");/*3.1 营收报表*/
        Button listButton_saveDiskInfo = (Button)root.lookup("#listButton_saveDiskInfo");/*3.2 盘存信息*/

        /*下拉按钮动作设置*/
        listButton_staffArrange.setOnAction((param)->{
            //设置不可见
            new SetVisible().setVisible();

            staffInfoBorderPane.setVisible(true);
            //动画播放
            contentSlide();

        });
        listButton_applyInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            applyInfoBorderPane.setVisible(true);
            //动画播放
            contentSlide();

        });
        listButton_cmdButton.setOnAction((param)->{
            //查询当前用户类型，若为超级管理员才可打开界面
            try{
                //创建连接
                Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
                String sql = "select staff_type from staff_info where staff_id=getUserId();";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();//执行
                Config.result.next();
                if(Config.result.getInt(1)!=-1){
                    AlertDialog dialog = new AlertDialog("非超级管理员不可使用数据库控制台");
                    dialog.show();
                    return;
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
            new SetVisible().setVisible();
            cmdBorderPane.setVisible(true);
            //动画播放
            contentSlide();

        });
        listButton_SaleInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            saleInfoBorderPane.setVisible(true);
            //动画播放
            contentSlide();
        });
        listButton_purchaseOrder.setOnAction((param)->{
            new SetVisible().setVisible();
            purchaseOrderBorderPane.setVisible(true);
            //动画播放
            contentSlide();
        });
        listButton_goodsSoldInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            goodsSoldInfoBorderPane.setVisible(true);
            //动画播放
            contentSlide();
        });
        listButton_vipCardInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            vipCardInfoBorderPane.setVisible(true);
            //动画播放
            contentSlide();
        });
        listButton_goofdInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            goodsInfoBorderPane.setVisible(true);
            //动画播放
            contentSlide();
        });
        listButton_statisticInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            statisticInfoBorderPane.setVisible(true);
            //动画播放
            contentSlide();
        });
        listButton_saveDiskInfo.setOnAction((param)->{
            new SetVisible().setVisible();
            saveDiskInfoBorderPane.setVisible(true);
            //动画播放
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
    /*更新员工信息*/
    void updateStaffInfoData(){
        staffData.clear();
        //连接数据库，查询员工信息
        try{
            //创建连接
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select * from staff_info;";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();//执行
            while(Config.result.next()){
                //查看状态，若状态为-1，则不生成此员工表单
                if(Config.result.getInt(6) == -1)
                    continue;
                staffData.add(new StaffInfo(Config.result.getInt(1),Config.result.getString(2),
                                Config.result.getString(4),Config.result.getInt(5),
                        Config.result.getInt(6)));
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
    /*更新申请信息*/
    void updateApplyInfoData(){
        applyData.clear();
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select * from application_form;";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            //创建信息
            while(Config.result.next()){
                applyData.add(new ApplyInfo(Config.result.getInt(1),Config.result.getString(2),
                                Config.result.getInt(4),Config.result.getString(5),Config.result.getString(3)));
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
            String sql = "select sale_id,staff_name,vip_id,time,amount,amount_after_discount from sale_info,staff_info where sale_info.staff_id=staff_info.staff_id;";
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
    /*更新进货单信息*/
    void updatePurchaseOrderData(){
        purchaseOrderData.clear();
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select purchase_id,purchase_order.good_id,good_name,good_num,measure_unit,staff_name,purchase_time,purchase_price" +
                    " from purchase_order,staff_info,goods_info where purchase_order.staff_id=staff_info.staff_id and " +
                    "purchase_order.good_id=goods_info.good_id;";
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
    /*更新商品销售信息*/
    void updateGoodsSoldInfoData(){
        goodsSoldInfoData.clear();
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select goods_sold_id,sale_info_id,goods_sold_info.good_id,good_name,good_num from goods_sold_info,goods_info where goods_sold_info.good_id=goods_info.good_id;";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            //创建信息
            while(Config.result.next()){
                goodsSoldInfoData.add(new GoodsSoldInfo(Config.result.getInt(1),Config.result.getInt(2),Config.result.getInt(3),
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
                //商品数量为0时不显示
                if(Config.result.getDouble(5)==0){
                    continue;
                }
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
            String sql = "select id,staff_name,staff_type,amount,login_in_time,login_off_time from save_disk_info,staff_info where save_disk_info.staff_id=staff_info.staff_id;";
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
    /*更新统计报表*/
    void updateAmount(){
        Label amountOfDayLabel = (Label) root.lookup("#amountOfDay");
        Label amountOfWeekLabel = (Label)root.lookup("#amountOfWeek");
        Label amountOfYearLabel = (Label) root.lookup("#amountOfYear");
        double amountDay = 0;
        double amountWeek = 0;
        double amountYear = 0;
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "select get_sale_amount_day(NOW());";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            if(Config.result.next()){
                amountDay = Config.result.getDouble(1);
            }
            sql = "select get_sale_amount_month(NOW());";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            if(Config.result.next()){
                amountWeek = Config.result.getDouble(1);
            }
            sql = "select get_sale_amount_year(NOW());";
            Config.statement = Config.connection.prepareStatement(sql);
            Config.result = Config.statement.executeQuery();
            if(Config.result.next()){
                amountYear = Config.result.getDouble(1);
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
        amountOfDayLabel.setText(String.valueOf(amountDay));
        amountOfWeekLabel.setText(String.valueOf(amountWeek));
        amountOfYearLabel.setText(String.valueOf(amountYear));
        //更新显示折线图
        series.getData().clear();
        //访问数据库，获得相关信息，访问每日营业额，填入到表中
        int day = 0;
        int index = 0;
        double []amount = new double[31];
        try{
            Config.connection = DriverManager.getConnection(Config.url,Config.curStaff,Config.curStaffPass);
            String sql = "";
            while(day<31) {
                sql = "select get_sale_amount_day(date_sub(NOW(),interval " + day + " day));";
                Config.statement = Config.connection.prepareStatement(sql);
                Config.result = Config.statement.executeQuery();
                //存入数据信息
                Config.result.next();
                amount[index] = Config.result.getDouble(1);
                day++;
                index++;
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
        //储存数据
        for(int i = 0;i < 31;i ++){
            series.getData().add(new XYChart.Data(-i,amount[i]));
        }
    }

    public void show() {
        stage.show();
    }
    public void close(){
        stage.close();
    }
}
