package dataItem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 收银表单
 */
public class SaleInfo {
    private final SimpleIntegerProperty sale_id;
    private final SimpleStringProperty staff_name;
    private final SimpleStringProperty vip_id;
    private final SimpleStringProperty time;
    private final SimpleDoubleProperty amount;
    private final SimpleDoubleProperty amount_afterDiscount;

    public SaleInfo(int sale_id, String staff_name, String vip_id, String time, double amount, double amount_afterDiscount) {
        this.sale_id = new SimpleIntegerProperty(sale_id);
        this.staff_name = new SimpleStringProperty(staff_name);
        this.vip_id = new SimpleStringProperty(vip_id);
        this.time = new SimpleStringProperty(time);
        this.amount = new SimpleDoubleProperty(amount);
        this.amount_afterDiscount = new SimpleDoubleProperty(amount_afterDiscount);
    }

    public int getSale_id() {
        return sale_id.get();
    }

    public SimpleIntegerProperty sale_idProperty() {
        return sale_id;
    }

    public String getStaff_name() {
        return staff_name.get();
    }

    public SimpleStringProperty staff_nameProperty() {
        return staff_name;
    }

    public String getVip_id() {
        return vip_id.get();
    }

    public SimpleStringProperty vip_idProperty() {
        return vip_id;
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public double getAmount() {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }

    public double getAmount_afterDiscount() {
        return amount_afterDiscount.get();
    }

    public SimpleDoubleProperty amount_afterDiscountProperty() {
        return amount_afterDiscount;
    }
}

