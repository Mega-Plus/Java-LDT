package com.android.megainventory;


import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.NetworkUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lilincpp.github.libezftp.EZFtpServer;
import com.lilincpp.github.libezftp.user.EZFtpUser;
import com.lilincpp.github.libezftp.user.EZFtpUserPermission;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class DescriptionActivity extends ListActivity {

    private static final int REQUEST_ENABLE_BT = 5;
    SendReceive sendReceive;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static int MY_PERMISSIONS_REQUEST_READ_STORAGE;
    TextView lbl;
    DBController controller;
    TextView ipHint;
    Button btnimport;
    ListView lv;
    Button btnSearch;
    Button btnBack;
    Button KeyboardButton;
    Button btnExport;
    Button buttonCameraScan;
    EditText filterCode;
    final Context context = this;
    ListAdapter adapter;
    Boolean pidChecked = true;
    Boolean codeChecked = true;
    Boolean barcodeChecked = true;
    Boolean scanningData = false;
    Double amountToAdd;
    inappkeyboard keyboard;
    CheckBox automatic;
    Boolean FTPActive = false;
    Button okButton;
    TextView customAmount;

    LinearLayout header;

    //bt
    BluetoothDevice[] btArray;
    BluetoothAdapter bluetoothAdapter;


    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECIEVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    ArrayList<HashMap<String, String>> myList;
    public static final int requestcode = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        checkPermission();


        controller = new DBController(this);
        lbl = findViewById(R.id.txtresulttext);
        btnimport = findViewById(R.id.btnupload);
        btnSearch = findViewById(R.id.btnsearch);
        btnExport = findViewById(R.id.btnExport);
        KeyboardButton = findViewById(R.id.btnKeyboard);
        buttonCameraScan = findViewById(R.id.buttonCameraScan);
        filterCode = findViewById(R.id.filter);
        btnBack = findViewById(R.id.btnback);
        lv = getListView();
        lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        customAmount = findViewById(R.id.customAmount);
        keyboard = findViewById(R.id.keyboard);
        keyboard.setVisibility(View.GONE);
        ipHint = findViewById(R.id.iphint);

        header = findViewById(R.id.header_bar);
        automatic = findViewById(R.id.automatic);
        okButton = findViewById(R.id.button_enter);
        inappkeyboard keyboard = findViewById(R.id.keyboard);
        customAmount.setRawInputType(InputType.TYPE_CLASS_TEXT);
        customAmount.setTextIsSelectable(false);

        ///////////////////////
        // FTP
        ///////////////////////
        if (!FTPActive) {
            //config
            EZFtpUser ftpUser = new EZFtpUser("mega", "1234", "/storage/emulated/0/", EZFtpUserPermission.WRITE);
            EZFtpServer ftpServer = new EZFtpServer.Builder()
                    .addUser(ftpUser)
                    .setListenPort(7890)
                    .create();
            try {
                if (ftpServer.isStopped()) {
                    ftpServer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                methodLogger("PORT ALREADY USED");
            }

            FTPActive = true;
        } else {
            Toast.makeText(context, R.string.ftp_already_started, Toast.LENGTH_SHORT).show();
        }

        String ipAddress = NetworkUtils.getIpAddressByWifi();
        String ipCut[] = ipAddress.split("\\.");
        ipHint.setText(ipCut[2] + "." + ipCut[3]);

        //service
        startForegroundService(new Intent(getApplicationContext(), MyService.class));

        ///////////////////////
        // FTP
        ///////////////////////

        InputConnection ic = customAmount.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);

        showSoftKeyboard(filterCode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lv.setFocusable(View.NOT_FOCUSABLE);
        }
        lv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                lv.clearFocus();
                filterCode.requestFocus();
            }
        });

        KeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) DescriptionActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(filterCode, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        //////////////////
        /// Bluetooth  /// btt
        //////////////////

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, R.string.device_bluetooth_unsupported, Toast.LENGTH_SHORT).show();

        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
        btArray = new BluetoothDevice[bt.size()];

        String[] addresses = new String[bt.size()];
        String[] names = new String[bt.size()];
        int index = 0;

        if (bt.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : bt) {
                btArray[index] = device;
                names[index] = device.getName();
                addresses[index] = device.getAddress();
                index++;
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, names);


        /////////////////////
        /// ^ Bluetooth ^ ///
        /////////////////////


        //////////////////
        ///ხელის სკანერი///
        //////////////////
        filterCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int yy = 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int yy = 0;

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().endsWith("\n")) {
                    String productBarcode = filterCode.getText().toString().replace("\n","");
                    if(productBarcode.contains("--")){
                        productBarcode = productBarcode.split("--")[0];
                    }
                    myList = controller.filter(productBarcode, getDateTime());
                    if (myList.size() != 0) {
                        //თუ იპოვა ერთი/მეტი პროდუქტი
                        if (myList.size() == 1) {
                            if(filterCode.getText().toString().replace("\n","").contains("--")){
                                //this label has price in it. check the price and show if incorrect.
                                String price = filterCode.getText().toString().replace("\n","").split("--")[1];
                                productBarcode = filterCode.getText().toString().replace("\n","").split("--")[0];

                                boolean isCorrectPrice = controller.checkPrice(productBarcode, price);

                                if(!isCorrectPrice){
                                    //ვერ იპოვა დასკანერებული პროდუქტი
                                    productIncorrectPrice(price);
                                }
                            }

                            if (automatic.isChecked()||DBController.QUANT_FROM_BARCODE>0) {
                                amountToAdd = Double.parseDouble("1");
                                if(DBController.QUANT_FROM_BARCODE>0){amountToAdd=DBController.QUANT_FROM_BARCODE;}
                                //ავტომატური თუ არის, მოუმატოს ერთი
//                                amountToAdd = 1.0;
                                ADD_RAOD(amountToAdd, productBarcode);
                                myList = controller.filter(productBarcode, getDateTime());
                                refreshLayout(true);
                                //გაანულოს ფილტრის ტექსტი
                                filterCode.setText("");
                            } else {
                                //ანუ ავტომატური გათიშულია
                                //აჩვენოს ციფრების კლავიატურა
                                showKeyboard(true);
                                //აჩვენოს გაფილტრული
                                myList = controller.filter(productBarcode, getDateTime());
                                refreshLayout(true);
                                //გამორთოს წვდომა ავტომატურზე და ფილტრის ველზე
                                automatic.setEnabled(false);
                                filterCode.setEnabled(false);
                            }

                        } else {
                            //myList.size > 1 იპოვა ბევრი
                            refreshLayout(true);
                            CHOICE_MULTIPLE();
                        }
                    } else {
                        //ვერ იპოვა დასკანერებული პროდუქტი
                        productNotFoundAlert(productBarcode);
                        filterCode.setText("");
                    }

                }
            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //შევამოწმოთ კოდური სიტყვა ბლუთუზისთვის
                if (filterCode.getText().toString().startsWith("SCANNER") && filterCode.getText().toString().endsWith("SCANNER")) {
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
                    materialAlertDialogBuilder.setTitle(R.string.select_bluetooth_device);
                    int startChecked = 0;
                    materialAlertDialogBuilder.setSingleChoiceItems(arrayAdapter, startChecked, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.e("SELECTED BT", arrayAdapter.getItem(i).toString());

                            ClientClass clientClass = new ClientClass(btArray[i], bluetoothAdapter);
                            clientClass.start();

                            Toast.makeText(context, R.string.select_bluetooth_connecting, Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();


                        }
                    });

                    materialAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // cancel
                        }
                    });
                    materialAlertDialogBuilder.show();
                    filterCode.setText("");
                } else if (filterCode.getText().length() == 0) {
                    RUN_PHOTO();
                } else {
                    //ბექგრაუნდში გაფილტროს სია
                    myList = controller.filter(filterCode.getText().toString(), getDateTime());
                    if (myList.size() > 1) {
                        //აჩვენოს რამოდენიმე არჩევანი
                        refreshLayout(true);
                        CHOICE_MULTIPLE();
                    } else {
                        if (myList.size() == 0) {
                            //ვერ იპოვა პროდუქტი
                            productNotFoundAlert(filterCode.getText().toString());
                        } else {
                            if (automatic.isChecked()||DBController.QUANT_FROM_BARCODE>0) {
                                amountToAdd = Double.parseDouble("1");
                                if(DBController.QUANT_FROM_BARCODE>0){amountToAdd=DBController.QUANT_FROM_BARCODE;}

                                refreshLayout(true);

                                View viewtoAdd = getViewByPosition(0, lv);
                                TextView pidview = viewtoAdd.findViewById(R.id.txtproductpid);
                                String pidString = pidview.getText().toString();
                                Log.e("napovnia view", pidString + "pidით");

                                controller.AddCountbyID(pidString, getDateTime(), amountToAdd);
                                controller.CalculateDiff(pidString, getDateTime(), amountToAdd);
                                //ამოიღოს ლოგი
                                controller.LogMaker(pidString, getDateTime(), amountToAdd);

                                //აჩვენოს მომატებული
                                myList = controller.filter(filterCode.getText().toString(), getDateTime());
                                refreshLayout(true);
                                //გაანულოს ფილტრის ტექსტი
                                filterCode.setText("");
                                return;
                            } else //მითითება{
                                //აჩვენოს ციფრების კლავიატურა
                                showKeyboard(true);
                            //აჩვენოს გაფილტრული
                            myList = controller.filter(filterCode.getText().toString(), getDateTime());
                            refreshLayout(true);
                            //გამორთოს წვდომა ავტომატურზე და ფილტრის ველზე
                            automatic.setEnabled(false);
                            filterCode.setEnabled(false);
                        }

                    }

                }


            }

        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OK_CLICK();
            }
        });

        btnimport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CSV_IMPORT_ACTION();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CSV_EXPORT();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SHOW_ITEM(view);
            }
        });

        LOAD_FULL_LIST();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void ADD_RAOD(Double raodenoba, String filtri) {
        amountToAdd = raodenoba;
        if (filtri.startsWith("21")) {  filtri=filtri.substring(0, 7)+"00000"+  (DBController.CHECKDIGIT_13(filtri.substring(0, 7)+"00000"));}
        controller.AddCount(filtri, getDateTime(), amountToAdd);
        controller.CalculateDiff(filtri, getDateTime(), amountToAdd);
        //ამოიღოს ლოგი
        controller.LogMaker(filtri, getDateTime(), amountToAdd);
    }

    public void RUN_PHOTO() {
        methodLogger("RUN_PHOTO");
        IntentIntegrator intentIntegrator = new IntentIntegrator(DescriptionActivity.this);
        scanningData = true;
        intentIntegrator.setPrompt(getString(R.string.camera_flash_volup_prompt));
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.initiateScan();
        return;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void OK_CLICK() {
        methodLogger("OK_CLICK");
        if (customAmount.getText().length() != 0) {
            //მოუმატოს რაოდენობა
            amountToAdd = Double.parseDouble(customAmount.getText().toString());

            View viewtoAdd = getViewByPosition(0, lv);
            TextView pidview = viewtoAdd.findViewById(R.id.txtproductpid);
            TextView barcodeview = viewtoAdd.findViewById(R.id.txtproductbarcode);
            String pidString = pidview.getText().toString();
            String barcodeviewString = barcodeview.getText().toString();
            Log.e("napovnia view", pidString + "pidით");

            controller.AddCountbyID(pidString, getDateTime(), amountToAdd);
            controller.CalculateDiff(pidString, getDateTime(), amountToAdd);
            //ამოიღოს ლოგი
            controller.LogMaker(pidString, getDateTime(), amountToAdd);

            //აჩვენოს გაფილტრული, უკვე მომატებული
            myList = controller.filter(barcodeviewString, getDateTime());
            refreshLayout(true);
            //ჩართოს კლავიატურა და წვდომა ფილტრზე და ავტომატურზე
            showKeyboard(false);
            //წაშალოს დარჩენილი ტექსტი ფილტრიდან და რაოდენობიდან
            customAmount.setText("");
            filterCode.setText("");
            //დააფოკუსოს ფილტრზე
            showSoftKeyboard(filterCode);
            //ხმა
            MediaPlayer mp = MediaPlayer.create(context, R.raw.beepsound);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                    mp = null;
                }
            });
            mp.start();
        }
    }

    public void CSV_EXPORT() {
        methodLogger("CSV_EXPORT");
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
        materialAlertDialogBuilder.setMessage(R.string.big_database_long_load_time);
        materialAlertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ამოიღოს მთლიანი ბაზა
                String fileName = getExportFileName("RESULT");
                controller.exportDB(getExportFileName("RESULT"));
                //აჩვენოს დიალოგი, თუ სად ამოიღო ბაზა
                MaterialAlertDialogBuilder materialAlertDialogBuilder1 = new MaterialAlertDialogBuilder(context);
                materialAlertDialogBuilder1.setMessage(getString(R.string.csvexport_yourlastfile) +
                        getString(R.string.csvexport_withname) + fileName + getString(R.string.csvexport_checkNameInDownloads));
                materialAlertDialogBuilder1.setPositiveButton(R.string.show, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    }
                });
                materialAlertDialogBuilder1.show();
            }
        });
        materialAlertDialogBuilder.setNegativeButton(R.string.dialogReturn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //უკან დაბრუნება
            }
        });
        materialAlertDialogBuilder.show();
    }

    public void LOAD_FULL_LIST() {
        methodLogger("LOAD_FULL_LIST");
        myList = controller.getAllProducts(pidChecked, codeChecked, barcodeChecked);
        if (myList.size() != 0) {
            ListView lv = getListView();
            ListAdapter adapter = new SimpleAdapter(DescriptionActivity.this, myList,
                    R.layout.lst_template, new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}, new int[]{
                    R.id.txtproductpid, R.id.txtproductcode, R.id.txtproductbarcode, R.id.txtproductname
                    , R.id.txtproductprimeprice, R.id.txtproductprice, R.id.txtproductgroup, R.id.txtproductcount, R.id.txtproductanagweri, R.id.txtproductdifference});
            setListAdapter(adapter);
            lbl.setText("");
            header.setVisibility(View.VISIBLE);
            automatic.setEnabled(true);

        }

    }

    public void SHOW_ITEM(View view) {
        methodLogger("SHOW_ITEM");
        TextView code = view.findViewById(R.id.txtproductbarcode);
        String filter = code.getText().toString();
        //გვაჩვენოს ციფრების კლავიატურა
        showKeyboard(true);
        //აჩვენოს გაფილტრული
        myList = controller.filter(filter, getDateTime());
        if (myList.size() == 1) {
            //kargia
        } else if (myList.size() > 1) {
            TextView pid = view.findViewById(R.id.txtproductpid);
            String pidfilter = pid.getText().toString();
            myList = controller.filterbyID(pidfilter);
        } else if (myList.size() == 0) {
            System.out.println(filter);
            Snackbar snack = Snackbar.make(findViewById(R.id.btnsearch), R.string.error_product_not_found, Snackbar.LENGTH_LONG);
            snack.setAnchorView(R.id.btnsearch).show();
        }
        refreshLayout(true);
        //მიანიჭოს ველს ფილტრი
        filterCode.setText(filter);
        //გამორთოს წვდომა ავტომატურზე და ფილტრის ველზე
        automatic.setEnabled(false);
        filterCode.setEnabled(false);
//        }
    }

    public void CSV_IMPORT_ACTION() {
        methodLogger("CSV_IMPORT_ACTION");
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
        alertDialogBuilder.setMessage(R.string.importChoice);
        alertDialogBuilder.setPositiveButton(R.string.csvimport_load_products, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                MaterialAlertDialogBuilder xalertDialogBuilder = new MaterialAlertDialogBuilder(context);
                xalertDialogBuilder.setMessage(R.string.csvimport_big_import_wait);
                xalertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new MaterialFilePicker()
                                .withActivity(DescriptionActivity.this)
                                .withCloseMenu(true)
                                .withRootPath("/storage/emulated/0/Download")
                                .withHiddenFiles(true)
                                .withFilter(Pattern.compile(".*\\.(csv)$"))
                                .withFilterDirectories(false)
                                .withTitle("File Manager")
                                .withRequestCode(requestcode)
                                .start();
                    }
                });
                xalertDialogBuilder.show();
            }
        });
        alertDialogBuilder.setNeutralButton(R.string.log_file_restore_data, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (myList.size() != 0 && !myList.isEmpty() && myList != null) {
                    new MaterialFilePicker()
                            .withActivity(DescriptionActivity.this)
                            .withCloseMenu(true)
                            .withHiddenFiles(true)
                            .withFilter(Pattern.compile(".*\\.(csv)$"))
                            .withFilterDirectories(false)
                            .withTitle("File Manager")
                            .withRequestCode(2)
                            .start();
                } else {
                    Snackbar snack = Snackbar.make(findViewById(R.id.btnsearch), R.string.error_product_not_found, Snackbar.LENGTH_LONG);
                    snack.setAnchorView(R.id.btnsearch).show();
                }
            }
        });
        alertDialogBuilder.show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null)
            return;

        if (scanningData == true) {
            //ვასკანერებთ კამერით
            super.onActivityResult(requestCode, resultCode, data);
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            //თუ იპოვა პროდუქტი
            if (intentResult.getContents() != null) {
                try {
                    //ბექგრაუნდში გაფლტვრა
                    myList = controller.filter(intentResult.getContents(), getDateTime());
                    if (myList.size() == 0) {
                        //ვერ იპოვა პროდუქტი
                        productNotFoundAlert(intentResult.getContents());
                        return;
                    } else {
                        //თუ იპოვა 1 ან მეტი
                        if (myList.size() == 1) {
                            if (automatic.isChecked()||DBController.QUANT_FROM_BARCODE>0) {
                                amountToAdd = Double.parseDouble("1");
                                if(DBController.QUANT_FROM_BARCODE>0){amountToAdd=DBController.QUANT_FROM_BARCODE;}
//                                amountToAdd = 1.0;
                                //მოუმატოს ერთი
                                ADD_RAOD(amountToAdd, intentResult.getContents());
                                myList = controller.filter(intentResult.getContents(), getDateTime());
                                //გადავიდეს პროდუქტზე -- გაფილტრულზე და მომატებულზე
                                refreshLayout(true);
                                //გაასუფთავოს ფილტრის ველი
                                filterCode.setText("");
                            } else {
                                //გამორთულია ავტომატური
                                //აჩვენოს ციფრების კლავიატურა
                                //გაუთიშოს წვდომა ფილტრის ველზე და ავტომატურის სვიჩზე
                                showKeyboard(true);
                                filterCode.setText(intentResult.getContents());
                                //აჩვენოს გაფილტრული სია
                                refreshLayout(true);
                            }

                        } else {
                            refreshLayout(true);
                            CHOICE_MULTIPLE();
                        }

                    }

                } catch (android.database.SQLException sqlException) {
                    Log.e("coudlntFind", "anything");
                    showKeyboard(false);
                    filterCode.setText("");
                }

            } else {
                Toast.makeText(getApplicationContext(), "OOPS", Toast.LENGTH_SHORT);
            }
            scanningData = false;
        } else {
            switch (requestCode) {

                case requestcode:
                    Log.e("matpicker", data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    String filepath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    File tempFile = new File(filepath);
                    String altBarcodeFilePath = (filepath.substring(0, filepath.length() - tempFile.getName().toString().length()) + "barcodes.csv");
                    String stringFileName = tempFile.getName();
                    String newFilePath = "";
                    String newAltBarcodeFilePath = "";

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        newFilePath = "/storage/emulated/0/Download/" + stringFileName;
                        newAltBarcodeFilePath = "/storage/emulated/0/Download/barcodes.csv";
                    } else {
                        newFilePath = filepath;
                        newAltBarcodeFilePath = altBarcodeFilePath;
                    }
                    if (filepath.contains("/root_path")) {
                        filepath = filepath.replace("/root_path", "");
                        altBarcodeFilePath = altBarcodeFilePath.replace("/root_path", "");
                    }


                    controller = new DBController(getApplicationContext());
                    SQLiteDatabase db = controller.getWritableDatabase();
                    db.execSQL("delete from " + DBController.tableName);
                    db.execSQL("delete from " + DBController.tableAlternativeBarcodes);

                    //MAIN DB
                    try {

                        if (resultCode == RESULT_OK) {
                            Log.e("RESULT CODE", "OK");

                            try {
                                FileReader file = new FileReader(newFilePath);
                                BufferedReader buffer = new BufferedReader(file);
                                ContentValues contentValues = new ContentValues();
                                String line = "";
                                db.beginTransaction();

                                while ((line = buffer.readLine()) != null) {

                                    Log.e("line", line);
                                    String[] str = line.split(",", 14);

                                    String Pid = str[0];
                                    String Code = str[1];
                                    String Barcode = str[2];
                                    String Name = str[3];
                                    String PrimePrice = str[4];
                                    String Price = str[5];
                                    String Group = str[6];
                                    String Count = str[7];
                                    String Anagweri = str[8];
                                    String Difference = str[9];

                                    contentValues.put(DBController.p_id, Pid);
                                    contentValues.put(DBController.code, Code);
                                    contentValues.put(DBController.barcode, Barcode);
                                    contentValues.put(DBController.name, Name);
                                    contentValues.put(DBController.primeprice, PrimePrice);
                                    contentValues.put(DBController.price, Price);
                                    contentValues.put(DBController.group, Group);
                                    contentValues.put(DBController.count, Count);
                                    contentValues.put(DBController.anagweri, Anagweri);
                                    contentValues.put(DBController.difference, Difference);
                                    db.insert(DBController.tableName, null, contentValues);

                                    lbl.setText(R.string.update_db_success);
                                    Log.e("Import", "Successfully Updated Database.");
                                }
                                //გადაარქვას ლოგს თუ არსებობს
                                RenameLogIfExists();

                                myList = controller.getAllProducts(pidChecked, codeChecked, barcodeChecked);
                                ListAdapter adapter = new SimpleAdapter(DescriptionActivity.this, myList,
                                        R.layout.lst_template, new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}, new int[]{
                                        R.id.txtproductpid, R.id.txtproductcode, R.id.txtproductbarcode, R.id.txtproductname
                                        , R.id.txtproductprimeprice, R.id.txtproductprice, R.id.txtproductgroup, R.id.txtproductcount, R.id.txtproductanagweri, R.id.txtproductdifference});
                                setListAdapter(adapter);
                                header.setVisibility(View.VISIBLE);
                                automatic.setEnabled(true);
                                db.setTransactionSuccessful();

                                db.endTransaction();

                            } catch (SQLException e) {
                                Log.e("SQLError", e.getMessage().toString());
                            } catch (IOException e) {
                                Log.e("IOException", e.getMessage().toString());

                            }
                        } else {
                            Log.e("RESULT CODE", "InValid");
                            if (db.inTransaction())

                                db.endTransaction();
                            Toast.makeText(DescriptionActivity.this, R.string.only_csv_files_allowed, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception ex) {
                        Log.e("Error", ex.getMessage().toString());
                        if (db.inTransaction())

                            db.endTransaction();

                        Toast.makeText(DescriptionActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                    }

                    //ALT DB
                    try {

                        if (resultCode == RESULT_OK) {
                            Log.e("RESULT CODE", "OK");

                            try {
                                FileReader file = new FileReader(newAltBarcodeFilePath);
                                BufferedReader buffer = new BufferedReader(file);
                                ContentValues contentValues = new ContentValues();
                                String line = "";
                                db.beginTransaction();

                                while ((line = buffer.readLine()) != null) {

                                    Log.e("line", line);
                                    String[] str = line.split(",");

                                    String Pid = str[0];
                                    String Code = str[1];
                                    String Barcode = str[2];

                                    contentValues.put(DBController.p_id, Pid);
                                    contentValues.put(DBController.code, Code);
                                    contentValues.put(DBController.barcode, Barcode);
                                    db.insert(DBController.tableAlternativeBarcodes, null, contentValues);

                                    lbl.setText(R.string.update_db_success);
                                    Log.e("Import", "Successfully Updated Database.");
                                }
                                db.setTransactionSuccessful();

                                db.endTransaction();

                            } catch (SQLException e) {
                                Log.e("SQLError", e.getMessage().toString());
                            } catch (IOException e) {
                                Log.e("IOException", e.getMessage().toString());

                            }
                        } else {
                            Log.e("RESULT CODE", "InValid");
                            if (db.inTransaction())

                                db.endTransaction();
                            Toast.makeText(DescriptionActivity.this, R.string.only_csv_files_allowed, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception ex) {
                        Log.e("Error", ex.getMessage().toString());
                        if (db.inTransaction())

                            db.endTransaction();

                        Toast.makeText(DescriptionActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                    }
                case 2:

                    filepath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    tempFile = new File(filepath);
                    stringFileName = tempFile.getName();
                    newFilePath = "";
//                    if (stringFileName != "LOG.csv" && stringFileName.startsWith("LOG")) {
                    if (stringFileName.startsWith("LOG")) {
                        Log.e("IMPORTDEBUG", "IMPORTING LOG");
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                            newFilePath = "/storage/emulated/0/Download/" + stringFileName;
                        } else {
                            newFilePath = filepath;
                        }
                        if (filepath.contains("/root_path"))
                            filepath = filepath.replace("/root_path", "");

                        controller = new DBController(getApplicationContext());
                        db = controller.getWritableDatabase();

                        if (resultCode == RESULT_OK) {
                            Log.e("RESULT CODE", "OK");

                            try {
                                FileReader file = new FileReader(filepath);
                                BufferedReader buffer = new BufferedReader(file);
                                ContentValues contentValues = new ContentValues();
                                String line = "";
                                db.beginTransaction();

                                while ((line = buffer.readLine()) != null) {

                                    String[] str = line.split(",", 3);

                                    String Pid = str[0];
                                    String time = str[1];
                                    String Count = str[2];
                                    RecoverLOG(Pid, Count, time);
                                    lbl.setText(R.string.log_upload_success);
                                }
                                db.setTransactionSuccessful();

                                db.endTransaction();

                            } catch (SQLException e) {
                                Log.e("SQLError", e.getMessage().toString());
                            } catch (IOException e) {
                                Log.e("IOException", e.getMessage().toString());

                            }
                        } else {
                            Log.e("RESULT CODE", "InValid");
                            if (db.inTransaction())

                                db.endTransaction();
                            Toast.makeText(DescriptionActivity.this, R.string.only_csv_files_allowed, Toast.LENGTH_LONG).show();

                        }

                        myList = controller.getAllProducts(pidChecked, codeChecked, barcodeChecked);

                        if (myList.size() != 0) {

                            ListView lv = getListView();

                            ListAdapter adapter = new SimpleAdapter(DescriptionActivity.this, myList,

                                    R.layout.lst_template, new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}, new int[]{
                                    R.id.txtproductpid, R.id.txtproductcode, R.id.txtproductbarcode, R.id.txtproductname
                                    , R.id.txtproductprimeprice, R.id.txtproductprice, R.id.txtproductgroup, R.id.txtproductcount, R.id.txtproductanagweri, R.id.txtproductdifference});

                            setListAdapter(adapter);
                            header.setVisibility(View.VISIBLE);
                            automatic.setEnabled(true);
                            lbl.setText(R.string.data_imported);


                        }

                    }

            }
        }


    }

    public void refreshLayout(Boolean filtered) {
        methodLogger("refreshLayout");
        if (myList.size() != 0) {
            ListView lv = getListView();
            if (filtered) {
                ListAdapter adapter = new SimpleAdapter(DescriptionActivity.this, myList,
                        R.layout.lst_template_filtered_big, new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}, new int[]{
                        R.id.txtproductpid, R.id.txtproductcode, R.id.txtproductbarcode, R.id.txtproductname
                        , R.id.txtproductprimeprice, R.id.txtproductprice, R.id.txtproductgroup, R.id.txtproductcount, R.id.txtproductanagweri, R.id.txtproductdifference});
                setListAdapter(adapter);
                header.setVisibility(View.GONE);
            } else {
                ListAdapter adapter = new SimpleAdapter(DescriptionActivity.this, myList,
                        R.layout.lst_template, new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}, new int[]{
                        R.id.txtproductpid, R.id.txtproductcode, R.id.txtproductbarcode, R.id.txtproductname
                        , R.id.txtproductprimeprice, R.id.txtproductprice, R.id.txtproductgroup, R.id.txtproductcount, R.id.txtproductanagweri, R.id.txtproductdifference});
                setListAdapter(adapter);
            }

        }
    }

    public void RecoverLOG(String filter, String Count, String time) {
        methodLogger("RecoverLOG");
        Log.e(filter, Count);
        Double DubCount;
        if (Count.contains(".")) {
            DubCount = Double.parseDouble(Count.substring(0, Count.indexOf(".")));
        } else {
            DubCount = Double.parseDouble(Count);
        }
        if (DubCount > 0) {
            myList = controller.RecoverFromLog(filter, DubCount, time);
            myList = controller.CalculateDiff(filter, "1", DubCount);
            //ამოიღოს ლოგი
            controller.LogMaker(filter, getDateTime(), DubCount);
        }


    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            } else {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_PRIVILEGED,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);

                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);

                }

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

        } else {
            checkPermission();
            Toast.makeText(DescriptionActivity.this, R.string.allow_save_permission, Toast.LENGTH_LONG).show();
        }
    }

    public void CHOICE_MULTIPLE() {
//        Toast toast = Toast.makeText(context, "აირჩიეთ ერთი პროდუქტი", Toast.LENGTH_LONG);
        Snackbar snack = Snackbar.make(findViewById(R.id.btnsearch), R.string.select_one_product, Snackbar.LENGTH_LONG);
        snack.setAnchorView(R.id.btnsearch).show();
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(customAmount.getWindowToken(), 0);
        filterCode.setText("");
    }

    public void productIncorrectPrice(String price){
        methodLogger("productIncorrectPrice");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.your_input_product_not_found);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.beepsound);
        showKeyboard(false);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp = null;
            }
        });
        mp.start();
        alertDialogBuilder.setMessage("ახალი ფასი არის "+price);
        alertDialogBuilder.setNeutralButton("ბეჭდვა და გაგრძელება", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialogBuilder.show();

    }

    public void productNotFoundAlert(String productBarcode) {
        methodLogger("productNotFoundAlert");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage(R.string.your_input_product_not_found);
        alertDialogBuilder.setMessage("Barcode Not Found"+" \n("+productBarcode+")"); //
        MediaPlayer mp = MediaPlayer.create(this, R.raw.beepsound);
        showKeyboard(false);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp = null;
            }
        });
        mp.start();
        alertDialogBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void onBackPressed() {
        if (header.getVisibility() != (View.VISIBLE)) {
            myList = controller.getAllProducts(pidChecked, codeChecked, barcodeChecked);
            ListAdapter adapter = new SimpleAdapter(DescriptionActivity.this, myList,

                    R.layout.lst_template, new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}, new int[]{
                    R.id.txtproductpid, R.id.txtproductcode, R.id.txtproductbarcode, R.id.txtproductname
                    , R.id.txtproductprimeprice, R.id.txtproductprice, R.id.txtproductgroup, R.id.txtproductcount, R.id.txtproductanagweri, R.id.txtproductdifference});
            setListAdapter(adapter);
            header.setVisibility(View.VISIBLE);
            showKeyboard(false);
            automatic.setEnabled(true);
            filterCode.setEnabled(true);
            filterCode.setText("");
        } else {
            myList = controller.getAllProducts(pidChecked, codeChecked, barcodeChecked);
            ListAdapter adapter = new SimpleAdapter(DescriptionActivity.this, myList,

                    R.layout.lst_template, new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}, new int[]{
                    R.id.txtproductpid, R.id.txtproductcode, R.id.txtproductbarcode, R.id.txtproductname
                    , R.id.txtproductprimeprice, R.id.txtproductprice, R.id.txtproductgroup, R.id.txtproductcount, R.id.txtproductanagweri, R.id.txtproductdifference});
            setListAdapter(adapter);
            header.setVisibility(View.VISIBLE);
            filterCode.setEnabled(true);
            showKeyboard(false);
            automatic.setEnabled(true);
            filterCode.setText("");
        }
    }

    public String getDateTime() {
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyyMMddhhmm").format(cDate);
        return fDate;
    }

    public String getExportFileName(String type) {
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy MM dd hh mm").format(cDate);
        String filename = type + " " + fDate + ".csv";
        return filename;
    }

    public void showKeyboard(Boolean show) {
        if (show) {
            keyboard.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(customAmount.getWindowToken(), 0);
            filterCode.setEnabled(false);
            automatic.setEnabled(false);
            btnSearch.setEnabled(false);
        } else {
            keyboard.setVisibility(View.GONE);
            filterCode.setEnabled(true);
            automatic.setEnabled(true);
            btnSearch.setEnabled(true);
        }
    }

    public void methodLogger(String method) {
        Log.e("METHOD", method);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isShowing = imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            if (!isShowing)
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            switch (message.what) {
                case STATE_CONNECTED:
                    Toast.makeText(context, R.string.bt_state_connected, Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTING:
                    Toast.makeText(context, R.string.bt_state_connecting, Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTION_FAILED:
                    Toast.makeText(context, R.string.bt_state_fail, Toast.LENGTH_SHORT).show();
                    break;
                case STATE_MESSAGE_RECIEVED:
                    byte[] readbuff = (byte[]) message.obj;
                    String tempMsg = new String(readbuff, 0, message.arg1);
                    Log.e("RECEIVED", tempMsg);
                    ScanWithBluetooth(tempMsg);
                    break;
            }

            return false;
        }
    });

    public void ScanWithBluetooth(String filtercode) {
        methodLogger("ScanWithBluetooth");
        methodLogger(filtercode);
//        filterCode.setText(filtercode);
        myList = controller.filter(filtercode, getDateTime());
        methodLogger(String.valueOf(myList.size()));
        if (myList.size() != 0) {
            //თუ იპოვა ერთი/მეტი პროდუქტი
            methodLogger(String.valueOf(myList.size()));
            methodLogger("1");
            if (myList.size() == 1) {
                methodLogger("2");
                if (automatic.isChecked()||DBController.QUANT_FROM_BARCODE>0) {
                    amountToAdd = Double.parseDouble("1");
                    if(DBController.QUANT_FROM_BARCODE>0){amountToAdd=DBController.QUANT_FROM_BARCODE;}
                    methodLogger("3");
                    //ავტომატური თუ არის, მოუმატოს ერთი
//                    amountToAdd = 1.0;
                    ADD_RAOD(amountToAdd, filtercode);
                    myList = controller.filter(filtercode, getDateTime());
                    refreshLayout(true);
                    //გაანულოს ფილტრის ტექსტი
                    filterCode.setText("");
                } else {
                    methodLogger("4");
                    //ანუ ავტომატური გათიშულია
                    //აჩვენოს ციფრების კლავიატურა
                    showKeyboard(true);
                    //აჩვენოს გაფილტრული
                    myList = controller.filter(filtercode, getDateTime());
                    refreshLayout(true);
                    //გამორთოს წვდომა ავტომატურზე და ფილტრის ველზე
                    automatic.setEnabled(false);
                    filterCode.setEnabled(false);
                }

            } else {
                //myList.size > 1 იპოვა ბევრი
                refreshLayout(true);
                methodLogger("5");
                CHOICE_MULTIPLE();
            }
        } else {
            //ვერ იპოვა დასკანერებული პროდუქტი
            productNotFoundAlert(filtercode);
            methodLogger("6");
            filterCode.setText("");
        }
    }

    public void RenameLogIfExists() {
        File file = new File("/storage/emulated/0/Download/LOG.csv");
        Log.e("logs", file.getAbsolutePath());
        if (file == null || !file.exists()) {
            Log.e("logs", "doesnt exist");
            //არ არსებობს
            return;
        } else {
            Log.e("logs", "!!exists");
            //ლოგი არსებობს, გადაარქვას სახელი
            File from = new File("/storage/emulated/0/Download/LOG.csv");
            File to = new File("/storage/emulated/0/Download/", getExportFileName("LOG"));
            from.renameTo(to);
//        }
        }

    }


    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1, BluetoothAdapter bluetoothAdapter) {
            device = device1;

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}