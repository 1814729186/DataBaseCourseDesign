package dataItem;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 申请表信息表项
 */
public class ApplyInfo {
    private final SimpleIntegerProperty apply_id;
    private final SimpleStringProperty apply_name;
    private final SimpleStringProperty apply_type;
    private final SimpleStringProperty apply_time;
    private final  String password;
    public ApplyInfo(int apply_id, String apply_name, int apply_type, String apply_time,String password) {
        this.apply_id = new SimpleIntegerProperty(apply_id);
        this.apply_name = new SimpleStringProperty(apply_name);
        String type = null;
        if(apply_type==1) type = "收银员";
        else type = "仓库管理员";
        this.apply_type = new SimpleStringProperty(type);
        this.apply_time = new SimpleStringProperty(apply_time);
        this.password = password;
    }

    public int getApply_id() {
        return apply_id.get();
    }

    public SimpleIntegerProperty apply_idProperty() {
        return apply_id;
    }

    public String getApply_name() {
        return apply_name.get();
    }

    public SimpleStringProperty apply_nameProperty() {
        return apply_name;
    }

    public String getApply_type() {
        return apply_type.get();
    }

    public SimpleStringProperty apply_typeProperty() {
        return apply_type;
    }

    public String getApply_time() {
        return apply_time.get();
    }

    public SimpleStringProperty apply_timeProperty() {
        return apply_time;
    }

    public String getPassword() {
        return password;
    }
}
