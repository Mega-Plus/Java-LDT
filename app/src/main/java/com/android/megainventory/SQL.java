package com.android.megainventory;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQL {


    public static String DB_HOST = "x192.168.1.5";
    private static   int DB_PORT = 1433;
    public static   String DB_NAME = "xFM2";
    public static   String USER = "xFM2";
    public static   String PASSWORD = "x#$%1018545246ERT";


    public static String mediator_QUERY = "";
    public static ResultSet mediator_SQL_ResultSet  = null;

    public static class SQL_ResultSet_Task extends AsyncTask<Void, Void, Connection> {



        @Override
        protected Connection doInBackground(Void... voids) {
            Connection conn = null;
            try {
                String driver = "net.sourceforge.jtds.jdbc.Driver";
                Class.forName(driver);
                String connString = "jdbc:jtds:sqlserver://192.168.1.5:1433;databaseName="+DB_NAME+";";
                conn = DriverManager.getConnection(connString, USER, PASSWORD);

                mediator_SQL_ResultSet = null;
                    Statement stmt = conn.createStatement();
                mediator_SQL_ResultSet = stmt.executeQuery(mediator_QUERY );

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    }



    public static ResultSet   SQL_ResultSet(String QUERY){
       int count_ResultSet=0;
        mediator_QUERY=QUERY;
        mediator_SQL_ResultSet  = null;
        new SQL_ResultSet_Task().execute();
        while(mediator_SQL_ResultSet == null  ){
            try {   Thread.sleep(100); count_ResultSet++;  }catch (Exception e){}
            if(count_ResultSet>2){ mediator_SQL_ResultSet= SQL_ResultSet(QUERY); } // Context context = null;
        }

       return mediator_SQL_ResultSet;
    }


    public static ResultSet   SQL_ResultSet_no_loop(String QUERY){
        int count_ResultSet=0;
        mediator_QUERY=QUERY;
        mediator_SQL_ResultSet  = null;
        new SQL_ResultSet_Task().execute();

        return mediator_SQL_ResultSet;
    }



    public static class SQL_Statement_Task extends AsyncTask<Void, Void, Connection> {



        @Override
        protected Connection doInBackground(Void... voids) {
            Connection conn = null;
            try {
                String driver = "net.sourceforge.jtds.jdbc.Driver";
                Class.forName(driver);
                String connString = "jdbc:jtds:sqlserver://192.168.1.5:1433;databaseName="+DB_NAME+";";
                conn = DriverManager.getConnection(connString, USER, PASSWORD);

                Statement stmt = conn.createStatement();
                stmt.executeUpdate(mediator_QUERY );
                mediator_QUERY="";

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }
    }


    public static String   SQL_Statement(String QUERY) {
        int count_Statement=0;
        String RST = "";
        if (!QUERY.equals("")) {
            try {
                mediator_QUERY = QUERY;
                new SQL_Statement_Task().execute();
                while(!mediator_QUERY.equals("")){
                    try {   Thread.sleep(200);  }catch (Exception e){}
                    if(count_Statement>2){  new SQL_Statement_Task().execute();}
                }
            } catch (Exception das) { }
        }
            return RST;

    }










}
