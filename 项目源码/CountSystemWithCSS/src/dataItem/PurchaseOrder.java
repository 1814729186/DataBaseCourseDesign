package dataItem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 进货账单表项
 */
public class PurchaseOrder {
    private final SimpleIntegerProperty purchase_id;
    private final SimpleIntegerProperty good_id;
    private final SimpleStringProperty good_name;
    private final SimpleDoubleProperty good_num;
    private final SimpleStringProperty measure_unit;    //计量单位
    private final SimpleStringProperty staff_name;
    private final SimpleStringProperty purchase_time;
    private final SimpleDoubleProperty purchase_price;
    private final SimpleDoubleProperty amount;

    public PurchaseOrder(int purchase_id, int good_id, String good_name, double good_num,String measure_unit, String staff_name, String purchase_time, double purchase_price) {
        this.purchase_id = new SimpleIntegerProperty(purchase_id);
        this.good_id = new SimpleIntegerProperty(good_id);
        this.good_name = new SimpleStringProperty(good_name);
        this.good_num = new SimpleDoubleProperty(good_num);
        this.measure_unit = new SimpleStringProperty(measure_unit);
        this.staff_name = new SimpleStringProperty(staff_name);
        this.purchase_time = new SimpleStringProperty(purchase_time);
        this.purchase_price = new SimpleDoubleProperty(purchase_price);
        this.amount = new SimpleDoubleProperty(purchase_price*good_num);

    }

    public int getPurchase_id() {
        return purchase_id.get();
    }

    public SimpleIntegerProperty purchase_idProperty() {
        return purchase_id;
    }

    public String getMeasure_unit() {
        return measure_unit.get();
    }

    public SimpleStringProperty measure_unitProperty() {
        return measure_unit;
    }

    public int getGood_id() {
        return good_id.get();
    }

    public SimpleIntegerProperty good_idProperty() {
        return good_id;
    }

    public String getGood_name() {
        return good_name.get();
    }

    public SimpleStringProperty good_nameProperty() {
        return good_name;
    }

    public double getGood_num() {
        return good_num.get();
    }

    public SimpleDoubleProperty good_numProperty() {
        return good_num;
    }

    public String getStaff_name() {
        return staff_name.get();
    }

    public SimpleStringProperty staff_nameProperty() {
        return staff_name;
    }

    public String getPurchase_time() {
        return purchase_time.get();
    }

    public SimpleStringProperty purchase_timeProperty() {
        return purchase_time;
    }

    public double getPurchase_price() {
        return purchase_price.get();
    }

    public SimpleDoubleProperty purchase_priceProperty() {
        return purchase_price;
    }

    public double getAmount() {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }
}
