package com.android.megainventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class ZebraPrinterService extends AsyncTask<Void, Void, Void> {


    protected Context context;

    protected String ip, name, info, info2, barcode, price;
    protected static final int printer_port = 9100;

    public ZebraPrinterService(Context context, String IP, String name, String info, String info2, String Barcode, String Price) {
        this.context = context;
        this.ip = IP;
        this.name = name;
        this.info = info;
        this.info2 = info2;
        this.barcode = Barcode;
        this.price = Price;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        try {
            Socket printerSocket = new Socket(this.ip, printer_port);
            OutputStream outputStream = printerSocket.getOutputStream();
            @SuppressLint("DefaultLocale") String zpl_command = String.format(
                    "^XA\n" +
                            "^PON\n" +
                            "^LH0,0\n" +
                            "^CI28\n" +
                            "^FO0,225^A@I,50,40,E:SYL000.TTF^FD %s^FS\n" +
                            "^FO0,200^A@I,20,20,E:SYL000.TTF^FD%s^FS\n" +
                            "^FO0,180^A@I,20,20,E:SYL000.TTF^FD%s2^FS\n" +
                            "^FO250,30^BQI,1,5^FDQA,%s--%s^FS\n" +
                            "^FO0,0^A0I,150,100^FD%s^FS\n" +
                            "^FO250,0^A@I,20,20,^FD%s^FS\n" +
                            "^XZ",
                    this.name, this.info, this.info2, this.barcode, this.price, this.price, this.barcode
            );

            byte[] zpl_bytes = zpl_command.getBytes("UTF-8");
            outputStream.write(zpl_bytes);
            outputStream.flush();
            outputStream.close();
            printerSocket.close();

        } catch (IOException e) {
            System.err.println("Error sending ZPL command to the printer.");
            LIBES.MESS(this.context, "Printing error \r\n", e.getLocalizedMessage());
            throw new RuntimeException(e);

        }

        return null;
    }
}
