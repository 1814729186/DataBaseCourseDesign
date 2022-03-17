package dataItem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/*商品销售情况*/
public class GoodsSoldInfo {
    private final SimpleIntegerProperty good_sold_id;
    private final SimpleIntegerProperty sale_info_id;
    private final SimpleIntegerProperty good_id;
    private final SimpleStringProperty good_name;
    private final SimpleDoubleProperty good_num;

    public GoodsSoldInfo(int good_sold_id,int saleInfo_id, int good_id, String good_name, double good_num) {
        this.good_sold_id = new SimpleIntegerProperty(good_sold_id);
        this.sale_info_id = new SimpleIntegerProperty(saleInfo_id);
        this.good_id = new SimpleIntegerProperty(good_id);
        this.good_name = new SimpleStringProperty(good_name);
        this.good_num = new SimpleDoubleProperty(good_num);
    }

    public int getGood_sold_id() {
        return good_sold_id.get();
    }

    public SimpleIntegerProperty good_sold_idProperty() {
        return good_sold_id;
    }

    public int getSale_info_id() {
        return sale_info_id.get();
    }

    public SimpleIntegerProperty sale_info_idProperty() {
        return sale_info_id;
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
}
