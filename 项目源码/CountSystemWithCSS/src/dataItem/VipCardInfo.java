package dataItem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 会员信息
 */
public class VipCardInfo {
    private final SimpleIntegerProperty vip_id;
    private final SimpleStringProperty vip_name;
    private final SimpleStringProperty end_time;
    private final SimpleDoubleProperty amount_yearly;
    private final SimpleStringProperty staff_name;

    public VipCardInfo(int vip_id, String vip_name, String end_time, double amount_yearly, String staff_name) {
        this.vip_id = new SimpleIntegerProperty(vip_id);
        this.vip_name = new SimpleStringProperty(vip_name);
        this.end_time = new SimpleStringProperty(end_time);
        this.amount_yearly = new SimpleDoubleProperty(amount_yearly);
        this.staff_name = new SimpleStringProperty(staff_name);
    }

    public int getVip_id() {
        return vip_id.get();
    }

    public SimpleIntegerProperty vip_idProperty() {
        return vip_id;
    }

    public String getVip_name() {
        return vip_name.get();
    }

    public SimpleStringProperty vip_nameProperty() {
        return vip_name;
    }

    public String getEnd_time() {
        return end_time.get();
    }

    public SimpleStringProperty end_timeProperty() {
        return end_time;
    }

    public double getAmount_yearly() {
        return amount_yearly.get();
    }

    public SimpleDoubleProperty amount_yearlyProperty() {
        return amount_yearly;
    }

    public String getStaff_name() {
        return staff_name.get();
    }

    public SimpleStringProperty staff_nameProperty() {
        return staff_name;
    }
}
