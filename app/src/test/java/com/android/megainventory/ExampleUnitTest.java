package com.android.megainventory;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }
}
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
//            String zplCommand = "^XA^PON\n" +
//                    "^LH0,0\n" +
//                    "^CI28\n" +
//                    "^FO0,225^A@I,50,40,E:SYL000.TTF^FD "+Name1+"^FS\n" +
//                    "^FO0,200^A@I,20,20,E:SYL000.TTF^FD"+info+"^FS\n" +
//                    "^FO0,180^A@I,20,20,E:SYL000.TTF^FD"+info2+"^FS\n" +
//                    "^FO290,30^BQI,1,5^FDQA,"+Barcode+"--"+Price+"^FS\n" +
//                    "^FO0,0^A0I,150,100^FD"+Price+"^FS\n" +
//                    "^FO290,0^A@I,20,20,^FD"+Barcode+"^FS\n" +
//                    "^XZ";

////Swiss 721 BT
//                      String zplCommand = "^XA^PON\n" +
//                "^LH0,0\n" +
//                "^CI28\n" +
//                "^FO0,225^A@I,50,40,E:TT0003M_.TTF^FD "+Name1+"^FS\n" +
//                "^FO0,200^A@I,20,20,E:TT0003M_.TTF^FD"+info+"^FS\n" +
//                "^FO0,180^A@I,20,20,E:TT0003M_.TTF^FD"+info2+"^FS\n" +
//                "^FO250,30^BQI,1,5^FDQA,"+Barcode+"--"+Price+"^FS\n" +
//                "^FO0,0^A0I,150,100^FD"+Price+"^FS\n" +
//                "^FO250,0^A@I,20,20,^FD"+Barcode+"^FS\n" +
//                "^XZ";


//                String zplCommand = "^XA\n" +
//"^LH100,150\n" +
//"^CWT,E:TT0003M_.FNT\n" +
//"^CFT,30,30\n" +
//"^CI28\n" +
//"^FT0,0^FH^FDTesting 1 2 3^FS\n" +
//"^FT0,50^FH^FD_D0_94_D0_BE _D1_81_D0_B2_D0_B8_D0_B4_D0_B0_D0_BD_D0_B8_D1_8F^FS\n" +
////                        e1 83 aa
//"^FT0,100^B3^FDAAA001^FS\n" +
//"^XZ";

            // Convert the ZPL command to bytes and send it to the printer
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
//
//
//
//
//
//
//
//    int[] rw=jTable_PRODUCTS.getSelectedRows();
//               for (int i = 0; i < rw.length; i++) {
//        String P_ID="";
//        try {         P_ID=jTable_PRODUCTS.getValueAt(rw[i], 0).toString();
//        String SQL=" select P_BARCODE,P_NAME_SALE,P_PRICE,  FORMAT(GETDATE(), 'MM/dd') AS CurrentDate ,P_GOODSGROUP,P_DAFAULTSUPPLIER,P_UUID from "+ATABLE.SHEMA+"PRODUCTS   where P_ID0='"+P_ID+"'" ;
//
//        ResultSet rs = ATABLE.SQL_ResultSet(SQL);
//
//        while (rs.next()) {
//
//
//        String  P_BARCODE=(rs.getString("P_BARCODE"));
//        String  P_NAME_SALE=(rs.getString("P_NAME_SALE"));
//        String  P_PRICE=(rs.getString("P_PRICE"));
//        String  CurrentDate=(rs.getString("CurrentDate"));
//        String  P_GOODSGROUP=(rs.getString("P_GOODSGROUP")).replace("---", "--").replace("--", "-").replace(" ", "");
//        String  P_DAFAULTSUPPLIER=(rs.getString("P_DAFAULTSUPPLIER")).replace(" ", "");
//        String  P_UUID=(rs.getString("P_UUID")).replace(" ", "");
//
//        ZPL.PRINT_ZPL(ATABLE.ZEBRA_printer, P_NAME_SALE, P_UUID+"  "+P_DAFAULTSUPPLIER, CurrentDate+"  "+P_GOODSGROUP, P_BARCODE, P_PRICE);
//        }
//        } catch (Exception e) {    }
//        }