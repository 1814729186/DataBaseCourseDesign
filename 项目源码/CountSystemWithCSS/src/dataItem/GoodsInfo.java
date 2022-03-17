package dataItem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/*商品库存信息*/
public class GoodsInfo {
    private final SimpleIntegerProperty good_id;
    private final SimpleStringProperty good_name;
    private final SimpleDoubleProperty good_price;
    private final SimpleStringProperty measure_unit;
    private final SimpleDoubleProperty remain_num;

    public GoodsInfo(int good_id, String good_name, double good_price, String measure_unit, double remain_num) {
        this.good_id = new SimpleIntegerProperty(good_id);
        this.good_name = new SimpleStringProperty(good_name);
        this.good_price = new SimpleDoubleProperty(good_price);
        this.measure_unit = new SimpleStringProperty(measure_unit);
        this.remain_num = new SimpleDoubleProperty(remain_num);
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

    public double getGood_price() {
        return good_price.get();
    }

    public SimpleDoubleProperty good_priceProperty() {
        return good_price;
    }

    public String getMeasure_unit() {
        return measure_unit.get();
    }

    public SimpleStringProperty measure_unitProperty() {
        return measure_unit;
    }

    public double getRemain_num() {
        return remain_num.get();
    }

    public SimpleDoubleProperty remain_numProperty() {
        return remain_num;
    }
}
