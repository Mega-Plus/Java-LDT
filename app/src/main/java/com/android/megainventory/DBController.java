package com.android.megainventory;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBController extends SQLiteOpenHelper {
    private static final String LOGCAT = null;

    public static String tableName = "tblProducts";
    public static String tableAlternativeBarcodes = "tblBarcodes";
    public static String p_id = "ID";
    public static String code = "Code";
    public static String barcode = "Barcode";
    public static String name = "Name";
    public static String primeprice = "ერთეული";
    public static String price = "Price";
    public static String group = "ჯგუფი";
    public static String count = "Quantity";
    public static String anagweri = "Result";
    public static String difference = "Difference";

    String logtext;

    public DBController(Context applicationcontext) {

        super(applicationcontext, "mydb.db", null, 1); // creating DATABASE

        Log.d(LOGCAT, "Created");
        logtext = "";

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String query;
        String alternateBarCodes;

        alternateBarCodes = "CREATE TABLE IF NOT EXISTS " + tableAlternativeBarcodes + "( " + p_id + " TEXT, " +
                code + " TEXT, " + barcode + " TEXT)";

        query = "CREATE TABLE IF NOT EXISTS " + tableName + "( " + p_id + " TEXT," +
                " " + code + " TEXT, " + barcode + " TEXT, " + name + " TEXT, " + primeprice
                + " TEXT, " + price + " TEXT, " + group + " TEXT, " + count + " TEXT, " + anagweri
                + " TEXT, " + difference + " TEXT)";

        Log.e("create Query", query);
        Log.e("create Query", alternateBarCodes);

        database.execSQL(alternateBarCodes);
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        String query1;
        query = "DROP TABLE IF EXISTS " + tableName;
        query1 = "DROP TABLE IF EXISTS " + tableAlternativeBarcodes;
        database.execSQL(query);
        database.execSQL(query1);
        onCreate(database);
    }


    public ArrayList<HashMap<String, String>> getAllProducts(Boolean pidEnabled, Boolean codeChecked, Boolean barcodeChecked) {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Barcode != 'Barcode' AND Code != 'კოდი'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {
//Id, Company,Name,Price
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));
                map.put("j", cursor.getString(9));
                productList.add(map);
//                Log.e("dataofList", cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3));
            } while (cursor.moveToNext());
        }
        return productList;

    }

    public static int DOUBLETOINT(Double gadskvni){
        int re=0;
        String[] temp;
        String delimeter = "\\.";
        temp = Double.toString(gadskvni).split(delimeter);
        for(int i =0; i < temp.length ; i++)
        {
            re=Integer.parseInt(temp[0]);
        }
        return re;
    }

    public static String CHECKDIGIT_13(String IN){



        int JAMI=0;
        String [] BARTEMP = IN.split("");
        int TRIGER=1;
        for(int i =1; i < BARTEMP.length ; i++){
            int erteuli= Integer.parseInt(BARTEMP[i]);
            JAMI=JAMI+(erteuli*TRIGER);
            if(TRIGER==1){TRIGER=3;}else{TRIGER=1;}
        }
        Double NAVAR= Double.parseDouble(Integer.toString(JAMI))/10;
        int NAVAR2=DOUBLETOINT(NAVAR);
        NAVAR2=(NAVAR2*10)+10;
        int CHECK=NAVAR2-JAMI;
        if(CHECK==10){CHECK=0;}
        return Integer.toString(CHECK);
    }

    public static double  QUANT_FROM_BARCODE=0.0;
    public static double  PRICE_FROM_BARCODE=0.0;
    public ArrayList<HashMap<String, String>> filter(String filter, String time) {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
        QUANT_FROM_BARCODE=0.0;
        PRICE_FROM_BARCODE=0.0;
        if(filter.length()==13) {
            if (filter.startsWith("21")) {
                PRICE_FROM_BARCODE = Double.parseDouble(filter.substring(7, 12) )/100;
                filter=filter.substring(0, 7)+"00000"+  (CHECKDIGIT_13(filter.substring(0, 7)+"00000"));

                }else {
                if (filter.startsWith("99")) {  QUANT_FROM_BARCODE = Double.parseDouble(filter.substring(7, 12)) / 1000;
                    filter = Integer.toString(Integer.parseInt(filter.substring(2, 7)));}
                if (filter.startsWith("22")) {  QUANT_FROM_BARCODE = Double.parseDouble(filter.substring(7, 12));
                    filter = Integer.toString(Integer.parseInt(filter.substring(2, 7)));}

            }
        }

        String selectQuery = "SELECT * FROM " + tableName + " WHERE Barcode = '" + filter + "' OR Code like '%---" + filter + "---%'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("", null);
        try {
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

             try { if(PRICE_FROM_BARCODE>0){QUANT_FROM_BARCODE=PRICE_FROM_BARCODE/Double.parseDouble(cursor.getString(5));} }catch (Exception df){}

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));  // p_id
                    map.put("b", cursor.getString(1));  // code
                    map.put("c", cursor.getString(2));  // barcode
                    map.put("d", cursor.getString(3));  // name
                    map.put("e", cursor.getString(4));  // primeprice
                    map.put("f", cursor.getString(5));  // price
                    map.put("g", cursor.getString(6));  // group
                    map.put("h", cursor.getString(7));  // count
                    map.put("i", cursor.getString(8));  // anagweri
                    map.put("j", cursor.getString(9));  // difference
                    productList.add(map);

                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqLiteException) {
            selectQuery = "SELECT * FROM " + tableName + " WHERE " + name + " LIKE '%" + filter + "%'";
            database = this.getWritableDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));
                    map.put("b", cursor.getString(1));
                    map.put("c", cursor.getString(2));
                    map.put("d", cursor.getString(3));
                    map.put("e", cursor.getString(4));
                    map.put("f", cursor.getString(5));
                    map.put("g", cursor.getString(6));
                    map.put("h", cursor.getString(7));
                    map.put("i", cursor.getString(8));
                    map.put("j", cursor.getString(9));
                    productList.add(map);

                } while (cursor.moveToNext());
            }
        }


        if (productList.size() == 0) {
        }


        return productList;

    }

    public ArrayList<HashMap<String, String>> filterbyID(String filter) {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM " + tableName + " WHERE ID = " + filter;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("", null);
        try {
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));
                map.put("j", cursor.getString(9));
                productList.add(map);

            }
        } catch (SQLiteException sqLiteException) {
            selectQuery = "SELECT * FROM " + tableName + " WHERE " + name + " LIKE '%" + filter + "%'";
            database = this.getWritableDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));
                map.put("j", cursor.getString(9));
                productList.add(map);

            }
        }

        if (productList.size() == 0) {
            Log.e(TAG, "filterbyID: TEST");
        }


        return productList;

    }

    public ArrayList<HashMap<String, String>> AddCount(String filter, String time, Double amount) {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "UPDATE " + tableName + " SET Result = Result + " + amount +
                " WHERE Code = " + filter + " OR Barcode = " + filter;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("", null);
        Log.e("test", selectQuery);
        cursor = database.rawQuery(selectQuery, null);
        try {
            cursor = database.rawQuery(selectQuery, null);
            Log.e("te3st", cursor.toString());
            if (cursor.moveToFirst()) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));
                map.put("j", cursor.getString(9));
                productList.add(map);
            }
        } catch (SQLiteException sqLiteException) {
            selectQuery = "UPDATE " + tableName + " SET Quantity = Quantity + " + amount + " WHERE " + name + " LIKE '%" + filter + "%'";
            database = this.getWritableDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));
                map.put("j", cursor.getString(9));
                productList.add(map);

            }
        }

        return productList;
    }

    public boolean checkPrice(String productBarcode, String price) {
        //check if price is correct for product
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Barcode = '" + productBarcode + "' AND Price = '" + price + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("", null);
        try {
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                return true;
            }
        } catch (SQLiteException sqLiteException) {
            selectQuery = "SELECT * FROM " + tableName + " WHERE " + name + " LIKE '%" + productBarcode + "%' AND Price = '" + price + "'";
            database = this.getWritableDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<HashMap<String, String>> AddCountbyID(String filter, String time, Double amount) {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "UPDATE " + tableName + " SET Result = Result + " + amount +
                " WHERE ID = " + filter;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("", null);
        Log.e("test", selectQuery);
        cursor = database.rawQuery(selectQuery, null);
        try {
            cursor = database.rawQuery(selectQuery, null);
            Log.e("te3st", cursor.toString());
            if (cursor.moveToFirst()) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));
                map.put("j", cursor.getString(9));
                productList.add(map);

            }
        } catch (SQLiteException sqLiteException) {
            selectQuery = "UPDATE " + tableName + " SET Quantity = Quantity + " + amount + " WHERE " + name + " LIKE '%" + filter + "%'";
            database = this.getWritableDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("a", cursor.getString(0));
                map.put("b", cursor.getString(1));
                map.put("c", cursor.getString(2));
                map.put("d", cursor.getString(3));
                map.put("e", cursor.getString(4));
                map.put("f", cursor.getString(5));
                map.put("g", cursor.getString(6));
                map.put("h", cursor.getString(7));
                map.put("i", cursor.getString(8));
                map.put("j", cursor.getString(9));
                productList.add(map);

            }
        }

        return productList;
    }

    public ArrayList<HashMap<String, String>> CalculateDiff(String filter, String time, Double amount) {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "UPDATE " + tableName + " SET Difference  = Result -" +
                " (SELECT Quantity FROM " + tableName +
                " WHERE Code = " + filter + " OR Barcode = " + filter + " OR ID = " + filter + ")"
                + "WHERE Code = " + filter + " OR Barcode = " + filter + " OR ID = " + filter;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("", null);
        Log.e("test", selectQuery);


        cursor = database.rawQuery(selectQuery, null);
        try {
            cursor = database.rawQuery(selectQuery, null);
            Log.e("te3st", cursor.toString());
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));
                    map.put("b", cursor.getString(1));
                    map.put("c", cursor.getString(2));
                    map.put("d", cursor.getString(3));
                    map.put("e", cursor.getString(4));
                    map.put("f", cursor.getString(5));
                    map.put("g", cursor.getString(6));
                    map.put("h", cursor.getString(7));
                    map.put("i", cursor.getString(8));
                    map.put("j", cursor.getString(9));
                    productList.add(map);
//                    if (logtext != null) {
//                        logtext += logConstructor(cursor.getString(0),
//                                cursor.getString(8), time);
//                    } else {
//                        logtext = logConstructor(cursor.getString(0),
//                                cursor.getString(8), time);
//                    }


                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqLiteException) {
            selectQuery = "UPDATE " + tableName + " SET Quantity = Quantity + " + amount + " WHERE " + name + " LIKE '%" + filter + "%'";
            database = this.getWritableDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));
                    map.put("b", cursor.getString(1));
                    map.put("c", cursor.getString(2));
                    map.put("d", cursor.getString(3));
                    map.put("e", cursor.getString(4));
                    map.put("f", cursor.getString(5));
                    map.put("g", cursor.getString(6));
                    map.put("h", cursor.getString(7));
                    map.put("i", cursor.getString(8));
                    map.put("j", cursor.getString(9));
                    productList.add(map);


                } while (cursor.moveToNext());
            }
        }

        return productList;
    }

    public ArrayList<HashMap<String, String>> RecoverFromLog(String filter, Double amount, String time) {

        ArrayList<HashMap<String, String>> productList;
        productList = new ArrayList<HashMap<String, String>>();
//        String selectQuery = "UPDATE " + tableName + " SET Quantity = " + amount + " WHERE Code = " + filter + " OR Barcode = " + filter + " OR ID = " + filter;
        String selectQuery = "UPDATE " + tableName + " SET Result = Result + " + amount +
                " WHERE ID = " + filter;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("", null);
        Log.e("test", selectQuery);
        try {
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));
                    map.put("b", cursor.getString(1));
                    map.put("c", cursor.getString(2));
                    map.put("d", cursor.getString(3));
                    map.put("e", cursor.getString(4));
                    map.put("f", cursor.getString(5));
                    map.put("g", cursor.getString(6));
                    map.put("h", cursor.getString(7));
                    map.put("i", cursor.getString(8));
                    map.put("j", cursor.getString(9));
                    logtext = logConstructor(cursor.getString(0),
                            amount.toString(), time);
                    productList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqLiteException) {
            selectQuery = "UPDATE " + tableName + " SET Result = Result + " + amount +
                    " WHERE " + name + " LIKE '%" + filter + "%'";
            database = this.getWritableDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));
                    map.put("b", cursor.getString(1));
                    map.put("c", cursor.getString(2));
                    map.put("d", cursor.getString(3));
                    map.put("e", cursor.getString(4));
                    map.put("f", cursor.getString(5));
                    map.put("g", cursor.getString(6));
                    map.put("h", cursor.getString(7));
                    map.put("i", cursor.getString(8));
                    map.put("j", cursor.getString(9));
                    logtext = logConstructor(cursor.getString(0),
                            amount.toString(), time);
                    productList.add(map);
                } while (cursor.moveToNext());
            }
        }

        return productList;

    }

    public void LogMaker(String filter, String time, Double amount) {
        String selectQuery = "SELECT * FROM " + tableName + " WHERE Code = " + filter + " OR Barcode = " + filter + " OR ID = " + filter;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("", null);
        try {
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));
                    logtext = logConstructor(cursor.getString(0),
                            amount.toString(), time);

                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqLiteException) {
            selectQuery = "SELECT * FROM " + tableName + " WHERE " + name + " LIKE '%" + filter + "%'";
            database = this.getWritableDatabase();
            cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("a", cursor.getString(0));
                    logtext = logConstructor(cursor.getString(0),
                            amount.toString(), time);
//                    if(logtext != null){
//                        logtext += logConstructor(cursor.getString(0),
//                                amount.toString(), time);
//                    }else{
//                        logtext = logConstructor(cursor.getString(0),
//                                amount.toString(), time);
//                    }

                } while (cursor.moveToNext());
            }
        }

        try {
            writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeToFile() throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "LOG.csv");
//        FileOutputStream stream = new FileOutputStream(file);
        FileOutputStream stream = new FileOutputStream(file, true);
        try {
            if (logtext.length() > 1 || logtext != null) {
                stream.write(logtext.getBytes());
            }

        } finally {
            stream.close();
        }
    }

    private String logConstructor(String pid, String count, String time) {
        return logtext = pid + "," + time + "," + count + "\n";
    }

    public void exportDB(String name) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT ID, Code, Barcode, Quantity, Result FROM " + tableName, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }


}
