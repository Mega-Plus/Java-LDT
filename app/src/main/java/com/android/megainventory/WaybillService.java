package com.android.megainventory;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class WaybillService {

    public static String RS_USER="xfms:405648893";
    public static String RS_PASS="xxxxxxx";//473822

    public interface OnTaskCompletedListener {
        void onTaskCompleted(String result);
    }

    public static void getWaybillGoodsList(String su, String sp, String waybillNumber, OnTaskCompletedListener listener) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return GET_XML(params[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                listener.onTaskCompleted(result);
            }
        }.execute(buildXmlData(su, sp, waybillNumber));
    }

    private static String buildXmlData(String su, String sp, String waybillNumber) {
        return      "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "  <soap:Body>"
                + "    <get_waybill_by_number xmlns=\"http://tempuri.org/\">"+
                "      <su>"+su+"</su>"+
                "      <sp>"+sp+"</sp>"+
                "      <waybill_number>"+waybillNumber+"</waybill_number>"
                + "    </get_waybill_by_number>"
                + "  </soap:Body>"
                + "</soap:Envelope>" +
                "";
    }

    private static String GET_XML(String xmldata) {
        String ret = "";
        HttpURLConnection con = null;
        try {
            URL url = new URL("https://services.rs.ge/WayBillService/WayBillService.asmx");
//
            if(MainActivity.PROXYPORT==0){ //No proxy
                con = (HttpURLConnection) url.openConnection();
            }else{ //with proxy
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(MainActivity.PROXYSERVER, MainActivity.PROXYPORT));
                con = (HttpURLConnection) url.openConnection(proxy);
            }


            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
            con.setRequestProperty("Content-Length", Integer.toString(xmldata.length()));

            try (OutputStream os = con.getOutputStream()) {
                byte[] postDataBytes = xmldata.getBytes("UTF-8");
                os.write(postDataBytes);
                os.flush();
            }

            int code = con.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        ret += line;
                    }
                }
            } else {
                Log.e("WaybillService", "HTTP error response: " + code + " " + con.getResponseMessage());
            }
        } catch (IOException e) {

            Log.e("WaybillService", "Error performing network operation: " + e.getMessage());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return ret;
    }








    public static String [] SHEMOSULI;

    public static  String FULL_AMOUNT="";
    public static  String SELLER_TIN="";

    public static void CALI_ZEDNADEBIS_DASHLA(String WB_N, String XMLL){
          FULL_AMOUNT=""; try{FULL_AMOUNT=XMLL.substring(XMLL.lastIndexOf("<FULL_AMOUNT>")+"FULL_AMOUNT".length()+2, XMLL.lastIndexOf("</FULL_AMOUNT>") );    } catch (Exception e) {  }
          SELLER_TIN=""; try{SELLER_TIN=XMLL.substring(XMLL.lastIndexOf("<SELLER_TIN>")+"SELLER_TIN".length()+2, XMLL.lastIndexOf("</SELLER_TIN>") );    } catch (Exception e) {  }
//        String SELLER_NAME=""; try{SELLER_NAME=XMLL.substring(XMLL.lastIndexOf("<SELLER_NAME>")+"SELLER_NAME".length()+2, XMLL.lastIndexOf("</SELLER_NAME>") );    } catch (Exception e) {  }



                StringBuffer buf =new StringBuffer("delete ["+SQL.DB_NAME+"].[dbo].[WB_GOODS] \n");
        SHEMOSULI =XMLL.replace("'", "").split("<GOODS>");

        for (int i = 1; i < SHEMOSULI.length; i++) {
            try {

                String DTA=SHEMOSULI[i];


                String ID=""; try{ID=DTA.substring(DTA.lastIndexOf("<ID>")+"ID".length()+2, DTA.lastIndexOf("</ID>") );    } catch (Exception e) {  }
                String W_NAME=""; try{W_NAME=DTA.substring(DTA.lastIndexOf("<W_NAME>")+"W_NAME".length()+2, DTA.lastIndexOf("</W_NAME>") );    } catch (Exception e) {  }
                String UNIT_ID=""; try{UNIT_ID=DTA.substring(DTA.lastIndexOf("<UNIT_ID>")+"UNIT_ID".length()+2, DTA.lastIndexOf("</UNIT_ID>") );    } catch (Exception e) {  }
//String UNIT_TXT=""; try{UNIT_TXT=DTA.substring(DTA.lastIndexOf("<UNIT_TXT>")+"UNIT_TXT".length()+2, DTA.lastIndexOf("</UNIT_TXT>") );    } catch (Exception e) {  }
                String QUANTITY=""; try{QUANTITY=DTA.substring(DTA.lastIndexOf("<QUANTITY>")+"QUANTITY".length()+2, DTA.lastIndexOf("</QUANTITY>") );    } catch (Exception e) {  }
                String PRICE="0"; try{PRICE=DTA.substring(DTA.lastIndexOf("<PRICE>")+"PRICE".length()+2, DTA.lastIndexOf("</PRICE>") );    } catch (Exception e) {  }
                String AMOUNT=""; try{AMOUNT=DTA.substring(DTA.lastIndexOf("<AMOUNT>")+"AMOUNT".length()+2, DTA.lastIndexOf("</AMOUNT>") );    } catch (Exception e) {  }
                String BAR_CODE=""; try{BAR_CODE=DTA.substring(DTA.lastIndexOf("<BAR_CODE>")+"BAR_CODE".length()+2, DTA.lastIndexOf("</BAR_CODE>") );  } catch (Exception e) {  }
                String A_ID=""; try{A_ID=DTA.substring(DTA.lastIndexOf("<A_ID>")+"A_ID".length()+2, DTA.lastIndexOf("</A_ID>") );    } catch (Exception e) {  }
                String VAT_TYPE=""; try{VAT_TYPE=DTA.substring(DTA.lastIndexOf("<VAT_TYPE>")+"VAT_TYPE".length()+2, DTA.lastIndexOf("</VAT_TYPE>") );    } catch (Exception e) {  }
                String STATUS=""; try{STATUS=DTA.substring(DTA.lastIndexOf("<STATUS>")+"STATUS".length()+2, DTA.lastIndexOf("</STATUS>") );    } catch (Exception e) {  }

                buf.append("INSERT INTO ["+SQL.DB_NAME+"].[dbo].[WB_GOODS]"
                        + "           ( [WB_N],SELLER_TIN,FULL_AMOUNT,[p_ID] ,[W_NAME] ,[UNIT_ID] ,[QUANTITY] ,[PRICE] ,[AMOUNT] ,[BAR_CODE] ,[A_ID] ,[VAT_TYPE] ,[STATUS])"
                        + "     VALUES"
                        + " ( '"+WB_N+"' ,'"+SELLER_TIN+"' ,'"+FULL_AMOUNT+"' ,'"+ID+"' ,LEFT(N'"+W_NAME+"',50) ,'"+UNIT_ID+"' ,'"+QUANTITY+"' ,'"+PRICE+"' ,'"+AMOUNT+"' ,LEFT('"+BAR_CODE+"',30) ,'"+A_ID+"' ,'"+VAT_TYPE+"' ,'"+STATUS+"')\n");

            } catch (Exception e) {  }

        }
        SQL.SQL_Statement(buf.toString());

    }












}
