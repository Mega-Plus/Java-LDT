package com.android.megainventory;

public class ListItem {

    public String P_BARCODE;
    public String P_NAME_SALE;
    public String P_PRICE;
    public String CurrentDate;
    public String P_GOODSGROUP;
    public String P_DEFAULTSUPPLIER;
    public String P_UUID;
    public int quantity;


    public ListItem(String P_BARCODE, String P_NAME_SALE, String P_PRICE, String P_GOODSGROUP, String P_DEFAULTSUPPLIER, String P_UUID, int quantity) {
        this.P_BARCODE = P_BARCODE;
        this.P_NAME_SALE = P_NAME_SALE;
        this.P_PRICE = P_PRICE;
//        this.CurrentDate = CurrentDate;
        this.P_GOODSGROUP = P_GOODSGROUP;
        this.P_DEFAULTSUPPLIER = P_DEFAULTSUPPLIER;
        this.P_UUID = P_UUID;
        this.quantity = quantity;
    }
}
