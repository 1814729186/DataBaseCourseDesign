package dataItem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 收银单表项：
 *      商品条码
 *      商品名称
 *      商品单价
 *      计量单位
 *      商品数量
 *      剩余数量（不显示）
 *      价格小计
 */
public class SaleOrderItem {
    private final SimpleIntegerProperty good_id;
    private final SimpleStringProperty good_name;
    private final SimpleDoubleProperty good_price;
    private final SimpleStringProperty measure_unit;
    private final SimpleDoubleProperty good_num;
    private final SimpleDoubleProperty remain_num;  /*阶段小计，插入数据库时会检测相关剩余数量*/
    private final SimpleDoubleProperty good_amount;

    public SaleOrderItem(int good_id, String good_name, double good_price, String measure_unit,double good_num, double remain_num) {
        this.good_id = new SimpleIntegerProperty(good_id);
        this.good_name = new SimpleStringProperty(good_name);
        this.good_price = new SimpleDoubleProperty(good_price);
        this.measure_unit = new SimpleStringProperty(measure_unit);
        this.good_num = new SimpleDoubleProperty(good_num);
        this.remain_num = new SimpleDoubleProperty(remain_num);
        this.good_amount = new SimpleDoubleProperty(good_num*good_price);
    }

    public int getGood_id() {
        return good_id.get();
    }

    public SimpleIntegerProperty good_idProperty() {
        return good_id;
    }

    public void setGood_id(int good_id) {
        this.good_id.set(good_id);
    }

    public String getGood_name() {
        return good_name.get();
    }

    public SimpleStringProperty good_nameProperty() {
        return good_name;
    }

    public void setGood_name(String good_name) {
        this.good_name.set(good_name);
    }

    public double getGood_price() {
        return good_price.get();
    }

    public SimpleDoubleProperty good_priceProperty() {
        return good_price;
    }

    public void setGood_price(double good_price) {
        this.good_price.set(good_price);
    }

    public String getMeasure_unit() {
        return measure_unit.get();
    }

    public SimpleStringProperty measure_unitProperty() {
        return measure_unit;
    }

    public void setMeasure_unit(String measure_unit) {
        this.measure_unit.set(measure_unit);
    }

    public double getGood_num() {
        return good_num.get();
    }

    public SimpleDoubleProperty good_numProperty() {
        return good_num;
    }

    public void setGood_num(double good_num) {
        this.good_num.set(good_num);
    }

    public double getRemain_num() {
        return remain_num.get();
    }

    public SimpleDoubleProperty remain_numProperty() {
        return remain_num;
    }

    public void setRemain_num(double remain_num) {
        this.remain_num.set(remain_num);
    }

    public double getGood_amount() {
        return good_amount.get();
    }

    public SimpleDoubleProperty good_amountProperty() {
        return good_amount;
    }

    public void setGood_amount(double good_amount) {
        this.good_amount.set(good_amount);
    }
}
