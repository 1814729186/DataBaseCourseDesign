package dataItem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SaveDiskInfo {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty staff_name;
    private final SimpleStringProperty staff_type;
    private final SimpleStringProperty amount;
    private final SimpleStringProperty login_in_time;
    private final SimpleStringProperty login_off_time;

    public SaveDiskInfo(int id, String staff_name,int staff_type, double amount, String login_in_time, String login_off_time) {
        this.id = new SimpleIntegerProperty(id);
        this.staff_name = new SimpleStringProperty(staff_name);
        String type = null;
        if(staff_type==-1) type = "超级管理员";
        else if(staff_type==0) type = "店长";
        else if(staff_type==1) type = "收银员";
        else type = "仓库管理员";
        this.staff_type = new SimpleStringProperty(type);
        if(staff_type==1) {this.amount = new SimpleStringProperty(String.valueOf(amount));}
        else {this.amount = new SimpleStringProperty("");}
        this.login_in_time = new SimpleStringProperty(login_in_time);
        this.login_off_time = new SimpleStringProperty(login_off_time);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getStaff_name() {
        return staff_name.get();
    }

    public SimpleStringProperty staff_nameProperty() {
        return staff_name;
    }

    public String getAmount() {
        return amount.get();
    }

    public SimpleStringProperty amountProperty() {
        return amount;
    }

    public String getLogin_in_time() {
        return login_in_time.get();
    }

    public SimpleStringProperty login_in_timeProperty() {
        return login_in_time;
    }

    public String getLogin_off_time() {
        return login_off_time.get();
    }

    public SimpleStringProperty login_off_timeProperty() {
        return login_off_time;
    }

    public String getStaff_type() {
        return staff_type.get();
    }

    public SimpleStringProperty staff_typeProperty() {
        return staff_type;
    }
}
