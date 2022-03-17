package dataItem;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 员工信息类，用于员工信息表格显示
 */
public class StaffInfo {
    private final SimpleIntegerProperty staff_id;
    private final SimpleStringProperty staff_name;
    private final SimpleStringProperty last_login_time;
    private final SimpleStringProperty staff_type;
    private final SimpleStringProperty staff_state;//员工状态

    public StaffInfo(int staff_id, String staff_name, String last_login_time, int staff_type, int staff_state) {
        this.staff_id = new SimpleIntegerProperty(staff_id);
        this.staff_name = new SimpleStringProperty(staff_name);
        this.last_login_time = new SimpleStringProperty(last_login_time);
        String type = "";
        if(staff_type==-1) type = "超级管理员";
        else if(staff_type==0) type = "店长";
        else if(staff_type==1) type = "收银员";
        else type = "仓库管理员";
        this.staff_type = new SimpleStringProperty(type);
        this.staff_state = new SimpleStringProperty(staff_state==0?"离线":"在线");
    }

    /*getter方法*/
    public int getStaff_id() {
        return staff_id.get();
    }

    public SimpleIntegerProperty staff_idProperty() {
        return staff_id;
    }

    public String getStaff_name() {
        return staff_name.get();
    }

    public SimpleStringProperty staff_nameProperty() {
        return staff_name;
    }

    public String getLast_login_time() {
        return last_login_time.get();
    }

    public SimpleStringProperty last_login_timeProperty() {
        return last_login_time;
    }

    public String getStaff_type() {
        return staff_type.get();
    }

    public SimpleStringProperty staff_typeProperty() {
        return staff_type;
    }

    public String getStaff_state() {
        return staff_state.get();
    }

    public SimpleStringProperty staff_stateProperty() {
        return staff_state;
    }
}
