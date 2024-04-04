package com.android.megainventory;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.android.csvtest", appContext.getPackageName());
    }
}
//
//package ATABLE;
//
//        import java.io.IOException;
//        import java.io.OutputStream;
//        import java.net.Socket;
//
//public class ZPL {
//
//    public static void main(String[] args) {
//
////        PRINT_ZPL("10.246.232.100", "Coca Cola Zero 2L", "177189   80354","05/07 01 011", "4860004445578", "5.40");
//
//        PRINT_ZPL("192.168.1.40", "Coca Cola ზერო 2ლ", "177189   80354","05/07 01 011", "4860004445578", "5.40");
//
//
//
////        String printerIpAddress = "192.168.1.34"; // Replace with your printer's IP
////        int printerPort = 9100; // Default ZPL port
////
////        try {
////            Socket printerSocket = new Socket(printerIpAddress, printerPort);
////            OutputStream outputStream = printerSocket.getOutputStream();
////
////                  String zplCommand = "^XA^PON\n" +
////                "^LH0,0\n" +
////                "^FO0,225^A0I,30,30^FD coca cola ç{Ú¡ÏßÀ{ ¯]Úåô 250Ú^FS\n" +
////                "^FO250,30^BQI,1,5^FDQA,12341234578444--2.0^FS\n" +
////                "^FO0,10^A0I,150,100^FD12.0^FS\n" +
////                "^XZ";
////
////
//////                String zplCommand = "^XA\n" +
//////"^LH100,150\n" +
//////"^CWT,E:TT0003M_.FNT\n" +
//////"^CFT,30,30\n" +
//////"^CI28\n" +
//////"^FT0,0^FH^FDTesting 1 2 3^FS\n" +
//////"^FT0,50^FH^FD_D0_94_D0_BE _D1_81_D0_B2_D0_B8_D0_B4_D0_B0_D0_BD_D0_B8_D1_8F^FS\n" +
////////                        e1 83 aa
//////"^FT0,100^B3^FDAAA001^FS\n" +
//////"^XZ";
////
////            // Convert the ZPL command to bytes and send it to the printer
////            byte[] zplBytes = zplCommand.getBytes("UTF-8");
////            outputStream.write(zplBytes);
////
////            // Flush and close the connection
////            outputStream.flush();
////            outputStream.close();
////            printerSocket.close();
////
////            System.out.println("ZPL command sent successfully.");
////        } catch (IOException e) {
////            e.printStackTrace();
////            System.err.println("Error sending ZPL command to the printer.");
////        }
//    }
//
//
//
//
//    public static void PRINT_ZPL(String IP, String Name1, String info,String info2, String Barcode, String Price) {
//        String printerIpAddress =IP;
//        int printerPort = 9100; // Default ZPL port
//
//        try {
//            Socket printerSocket = new Socket(printerIpAddress, printerPort);
//            OutputStream outputStream = printerSocket.getOutputStream();
//
//            //Sylfaen  //26785855.TTF  //SYL000.TTF
//
//                  String zplCommand = "^XA^PON\n" +
//                "^LH0,0\n" +
//                "^CI28\n" +
//                "^FO0,225^A@I,50,40,E:SYL000.TTF^FD "+Name1+"^FS\n" +
//                "^FO0,200^A@I,20,20,E:SYL000.TTF^FD"+info+"^FS\n" +
//                "^FO0,180^A@I,20,20,E:SYL000.TTF^FD"+info2+"^FS\n" +
//                "^FO290,30^BQI,1,5^FDQA,"+Barcode+"--"+Price+"^FS\n" +
//                "^FO0,0^A0I,150,100^FD"+Price+"^FS\n" +
//                "^FO290,0^A@I,20,20,^FD"+Barcode+"^FS\n" +
//                "^XZ";
//
//
//            String zplCommand = "^XA^PON\n" +
//                    "^LH0,0\n" +
//                    "^CI28\n" +
//                    "^FO0,225^A@I,50,40,E:26785855.TTF^FD "+Name1+"^FS\n" +
//                    "^FO0,200^A@I,20,20,E:26785855.TTF^FD"+info+"^FS\n" +
//                    "^FO0,180^A@I,20,20,E:26785855.TTF^FD"+info2+"^FS\n" +
//                    "^FO290,30^BQI,1,5^FDQA,"+Barcode+"--"+Price+"^FS\n" +
//                    "^FO0,0^A0I,150,100^FD"+Price+"^FS\n" +
//                    "^FO290,0^A@I,20,20,^FD"+Barcode+"^FS\n" +
//                    "^XZ";
//
//////Swiss 721 BT
////                      String zplCommand = "^XA^PON\n" +
////                "^LH0,0\n" +
////                "^CI28\n" +
////                "^FO0,225^A@I,50,40,E:TT0003M_.TTF^FD "+Name1+"^FS\n" +
////                "^FO0,200^A@I,20,20,E:TT0003M_.TTF^FD"+info+"^FS\n" +
////                "^FO0,180^A@I,20,20,E:TT0003M_.TTF^FD"+info2+"^FS\n" +
////                "^FO250,30^BQI,1,5^FDQA,"+Barcode+"--"+Price+"^FS\n" +
////                "^FO0,0^A0I,150,100^FD"+Price+"^FS\n" +
////                "^FO250,0^A@I,20,20,^FD"+Barcode+"^FS\n" +
////                "^XZ";
//
//
////                String zplCommand = "^XA\n" +
////"^LH100,150\n" +
////"^CWT,E:TT0003M_.FNT\n" +
////"^CFT,30,30\n" +
////"^CI28\n" +
////"^FT0,0^FH^FDTesting 1 2 3^FS\n" +
////"^FT0,50^FH^FD_D0_94_D0_BE _D1_81_D0_B2_D0_B8_D0_B4_D0_B0_D0_BD_D0_B8_D1_8F^FS\n" +
//////                        e1 83 aa
////"^FT0,100^B3^FDAAA001^FS\n" +
////"^XZ";
//
//            // Convert the ZPL command to bytes and send it to the printer
//            byte[] zplBytes = zplCommand.getBytes("UTF-8");
//            outputStream.write(zplBytes);
//
//            // Flush and close the connection
//            outputStream.flush();
//            outputStream.close();
//            printerSocket.close();
//
//            System.out.println("ZPL command sent successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error sending ZPL command to the printer.");
//            LIBES.MESS (AA.Attention, "Printing error \r\n"+e.getLocalizedMessage() );
//        }
//    }
//}