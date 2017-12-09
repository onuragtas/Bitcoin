package org.resoft.bitcoin.models;

import java.util.Date;

/**
 * Created by onuragtas on 10.12.2017.
 */

public class Data {
    double data;
    String date;

    public Data(double data, String date) {
        this.data = data;
        this.date = date;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
