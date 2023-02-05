package b2infosoft.milkapp.com.BluetoothPrinter;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_MachineCode;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_MachineName;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_center_name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BonusYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FatYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RateYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SNFYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.NO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.SessionLang;
import static b2infosoft.milkapp.com.useful.MyApp.TAG;
import static b2infosoft.milkapp.com.useful.UtilityMethod.convertNumInGujrati;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.useful.UtilityMethod.toTitleCase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import b2infosoft.milkapp.com.Model.BeanPurchInvoiceProductItem;
import b2infosoft.milkapp.com.Model.BeanTransactionUserItem;
import b2infosoft.milkapp.com.Model.BeanUserTransaction;
import b2infosoft.milkapp.com.Model.BuyerCustomerDataListPojo;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.Model.ListPojo;
import b2infosoft.milkapp.com.Model.TenDaysMilkSellHistory;
import b2infosoft.milkapp.com.Model.TransectionListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

/**
 * Created by Choudhary on 30/01/2019.
 */
public class BluetoothClass {

    public static BluetoothDevice mDevice = null;
    public static OutputStream mOutputStream = null;
    public static InputStream mInputStream = null;
    public static BluetoothSocket mSocket = null;
    static Context mContext;
    private static String btname = " ";
    private static String machineType = " ";
    private static String machineName = " ";
    private static String machineHash = "#";
    private static BluetoothAdapter mBluetoothAdapter;
    private static boolean stopWorker = false;
    private static int readBufferPosition = 0;
    private static Button btn_connect;
    private static ImageView imgClosed;
    private static Dialog dialog;
    private static Integer titleFontSize = 20, messageFontSize = 18;

    // private static UUID uuId = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // 0- normal size text
    private static byte[] normatfontBt = new byte[]{0x1B, 0x21, 0x00};  // 0- normal size text
    private static byte[] boldfontBt = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
    private static byte[] mediumfontBt = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
    private static byte[] largefontBt = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
    private static byte[] extraLargefontBt = new byte[]{0x1B, 0x21, 0x05}; // 3- bold with large text
    private static byte[] doubleHeightAndWidthfontBt = new byte[]{0x1b, 0x21, 0x20}; // 3- bold with large text

    private static byte[] alignCenter = new byte[]{0x1b, 0x61, 0x01};
    private static byte[] formatLeft = {0x1B, 'a', 0x00};
    private static byte[] formatSmall = {0x1, formatLeft[2]};
    private static byte[] formatRight = {0x1B, 'a', 0x02};
    public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, (byte) 255, 3};
    private static String strLangType = "en", strPrintMultiLang = "NO";
    private static String strDeshDotLine = "-------------------------------";
    private static String msg = "", strShift = "", FatSNF = "", Fat = "", SNF = "", CenterName = "", id = "", Name = "", QT = "", strAmount = "", reciptdate = "", title = "", credit = "", debit = "", remainBalance = "0";
    private static String labelId = "ID", labelSr = "Sr.", labelDate = "Date", labelType = "TYPE", labelCow = "Cow", labelBuffalo = "Buffalo", labelShift = "SHIFT", labelFat = "FAT", labelSNF = "SNF", labelCLR = "CLR", lableBonus = "Bonus", labelRate = "RATE", labelTotalRs = "TOTAL RS", labelQty = "Qty.", labelCenterName = "MERY DAIRY", labelCustomer = "Customer", labelBuyer = "Buyer", labelSeller = "Seller", labelSession = "", labelMorning = "Morning", labelEvening = "Evening", labelName = "NAME", labelMobileNo = "Mobile No", labelWeight = "WEIGHT", labelAmount = "AMOUNT", labelCashDiscount = "Cash Discount", labelOtherCharges = "Other Charges", labelTotalWeight = "TOTAL WEIGHT", labelTotalAmount = "TOTAL AMOUNT", labelLtr = "Ltr", labelPerLtr = "Per Ltr", labelTitle = "Title", labelCredit = "Credit", labelDebit = "Debit", labelTotalCredit = "Total Credit", labelTotalDebit = "Total Debit", labelRemainAmount = "Remain Amount", labelOpeningBalance = "Opening Balance", weekTotal = "WEEK TOTAL RS";
    private static double qt = 0, amount = 0, totalMilk = 0, TotalAmount = 0;


    static List<String> loadbtpname() {

        List<String> list = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName());
            }
        }
        return list;
    }

    public static boolean enableBluetooth(Context mContext) {

        try {
            BluetoothAdapter badapter = BluetoothAdapter.getDefaultAdapter();
            if (badapter != null) {
                badapter.enable();
                if (!badapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    ((Activity) mContext).startActivityForResult(enableBluetooth, 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void dialogBluetooth(Context Context) {
        mContext = Context;
        dialog = new BottomSheetDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_bluetooth);
        isBluetoothHeadsetConnected();
        final Spinner sp_pname;

        // set the custom dialog components - text, image and button
        btn_connect = dialog.findViewById(R.id.btn_connect);
        imgClosed = dialog.findViewById(R.id.imgClosed);
        sp_pname = dialog.findViewById(R.id.sp_pname);
        List<String> list = new ArrayList<String>(loadbtpname());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item, list);
        sp_pname.setAdapter(adapter);
        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_connect.getTag().toString().trim().equals("0")) {
                    if (sp_pname.getCount() > 0) {
                        btname = sp_pname.getSelectedItem().toString();
                        new ConnectBT().execute();
                    }
                } else {
                    try {
                        closeBT();
                        btn_connect.setTag("0");
                        btn_connect.setText(mContext.getString(R.string.connect));
                        toast("Printer DisConnected");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dialog.show();
    }

    public static void dialogMachineSetUp(Context Context) {
        mContext = Context;
        SessionManager sessionManager = new SessionManager(mContext);
        machineType = sessionManager.getValueSesion(KEY_MachineCode);


        dialog = new BottomSheetDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_bluetooth);
        dialog.setCanceledOnTouchOutside(true);
        isBluetoothHeadsetConnected();
        final Spinner sp_pname;

        // set the custom dialog components - text, image and button
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvSubtitle = dialog.findViewById(R.id.tvSubtitle);
        btn_connect = dialog.findViewById(R.id.btn_connect);
        imgClosed = dialog.findViewById(R.id.imgClosed);
        sp_pname = dialog.findViewById(R.id.sp_pname);
        tvTitle.setText(mContext.getResources().getString(R.string.machine_set_up));
        tvSubtitle.setText(mContext.getResources().getString(R.string.select_machine));
        List<String> list = new ArrayList<String>(Arrays.asList(mContext.getResources().getStringArray(R.array.machine_list)));
        List<Dashboard_item> machineList = new ArrayList<Dashboard_item>();
        machineList.add(new Dashboard_item("0", mContext.getResources().getString(R.string.select), "M CODE 00", 0, ""));
        machineList.add(new Dashboard_item("0", mContext.getResources().getString(R.string.lacto_scale), "M CODE 01", 0, ""));
        machineList.add(new Dashboard_item("0", mContext.getResources().getString(R.string.eko_milk), "M CODE 02", 0, ""));
        machineList.add(new Dashboard_item("0", mContext.getResources().getString(R.string.emt_serial), "M CODE 03", 0, ""));
        machineList.add(new Dashboard_item("0", mContext.getResources().getString(R.string.emt_parallel), "M CODE 04", 0, ""));
        machineList.add(new Dashboard_item("0", mContext.getResources().getString(R.string.eko_pro), "M CODE 05", 0, ""));
        machineList.add(new Dashboard_item("0", mContext.getResources().getString(R.string.lacten), "M CODE 06", 0, ""));
        machineList.add(new Dashboard_item("0", mContext.getResources().getString(R.string.master_classic), "M CODE 07", 0, ""));
        // list.add(new Dashboard_item("0",mContext.getResources().getString(R.string.eko_milk),"M CODE 08",0,""));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.machine_drop_down_item, list);
        sp_pname.setAdapter(adapter);
        int selectedPos = 0;
        machineName = sessionManager.getValueSesion(KEY_MachineName);
        for (int i = 0; i < machineList.size(); i++) {
            if (machineType.equalsIgnoreCase(machineList.get(i).getCount())) {
                selectedPos = i;
                break;
            }
        }

        sp_pname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    machineType = machineList.get(position).getCount();
                    machineName = adapter.getItem(position).toString();
                    printLog("machineName", machineName);

                } else {
                    machineType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_pname.setSelection(selectedPos);
        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (machineType.length() > 0) {
                    sessionManager.setValueSession(KEY_MachineCode, machineHash + machineType);
                    sessionManager.setValueSession(KEY_MachineName, machineName);
                    Log.d("TAG", "onClick3: " + machineName);
                    dialog.dismiss();

                } else {
                    showToast(mContext, mContext.getResources().getString(R.string.select));
                }

            }
        });

        dialog.show();
    }

    public static BluetoothDevice findBT(Context context) {


        mContext = context;
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                showToast(mContext, "No Bluetooth Adapter Available");
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String deviceBTMajorClass = getBTMajorDeviceClass(device.getBluetoothClass().getMajorDeviceClass());
                    System.out.println("printer" + "deviceBTMajorClass===" + deviceBTMajorClass);
                    // we got this name from the list of paired devices
                    System.out.println("BluetoothDevice Name===" + device.getName());
                    if (device.getName().equalsIgnoreCase(btname)) {

                        mDevice = device;
                        openBT();
                        break;


                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDevice;
    }

    public static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    public static OutputStream openBT() throws IOException {

        try {
            if (mDevice != null) {
                 String[] PERMISSIONS_LOCATIONN = {Manifest.permission.BLUETOOTH_CONNECT};
                int permission3 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT);
                if (permission3 != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_LOCATIONN, 1);
                }
                Log.d(TAG, "openBTasdasdac: ");
                System.out.println("openBT===" + "connected device===" + mDevice.getName());
                mSocket = mDevice.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
                mSocket.connect();
                mOutputStream = mSocket.getOutputStream();
                mInputStream = mSocket.getInputStream();
                beginListenForData();

            } else {
                Toast.makeText(mContext, "Bluetooth device is not Connected", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d(TAG, "openBT21165464: ");
            e.printStackTrace();
        }
        return mOutputStream;
    }

    private static void beginListenForData() {
        try {
            Log.d(TAG, "beginListenForDataasdcfasdfswsad: ");
            Thread workerThread;
            readBufferPosition = 0;
            byte delimiter = 10;
            stopWorker = false;

            byte[] readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {

                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {

                            int bytesAvailable = mInputStream.available();
                            if (bytesAvailable > 0) {
                                mInputStream = mSocket.getInputStream();
                                byte[] packetBytes = new byte[bytesAvailable];
                                mInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        System.out.println("sdfvbsdfbsdfgbsdfbsdfbsdfbe>>>" + data);
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public static void closeBT() throws IOException {
        try {
            stopWorker = true;
            if (mOutputStream != null) {
                mOutputStream.close();
                mInputStream.close();
                mSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetConnection() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (Exception e) {
            }
            mInputStream = null;
        }

        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (Exception e) {
            }
            mOutputStream = null;
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (Exception e) {
            }
            mSocket = null;
        }

    }

    private static String getBTMajorDeviceClass(int major) {
        switch (major) {
            case android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AUDIO_VIDEO";
            case android.bluetooth.BluetoothClass.Device.Major.COMPUTER:
                return "COMPUTER";

            case android.bluetooth.BluetoothClass.Device.Major.HEALTH:
                return "HEALTH";
            case android.bluetooth.BluetoothClass.Device.Major.IMAGING:
                return "IMAGING";
            case android.bluetooth.BluetoothClass.Device.Major.MISC:
                return "MISC";
            case android.bluetooth.BluetoothClass.Device.Major.NETWORKING:
                return "NETWORKING";
            case android.bluetooth.BluetoothClass.Device.Major.PERIPHERAL:
                return "PERIPHERAL";
            case android.bluetooth.BluetoothClass.Device.Major.PHONE:
                return "PHONE";
            case android.bluetooth.BluetoothClass.Device.Major.TOY:
                return "TOY";
            case android.bluetooth.BluetoothClass.Device.Major.UNCATEGORIZED:
                return "UNCATEGORIZED";
            case android.bluetooth.BluetoothClass.Device.Major.WEARABLE:
                return "AUDIO_VIDEO";
            default:
                return "unknown!";
        }
    }

    static void toast(String msg) {
        Toast toast2 = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        toast2.setGravity(Gravity.CENTER, 0, 0);
        toast2.show();
    }

    public static void LineFeed() {
        try {
            String msg = "     " + "\n" + "     ";
            msg = msg + "\n";
            mOutputStream.write(msg.getBytes(), 0, msg.getBytes().length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void printAddMilk_SingleEntryReciept(Context context, int milkTyp, int CustomerID, String selectedName, String shift, String actualFate, float snf, String rsPerKg, String weight, String totalBonus, String totalPayment, float totalWeekPayment, String msgg) {
        mContext = context;
        SessionManager sessionManager = new SessionManager(mContext);
        msg = "";
        String lableQuantity = mContext.getString(R.string.Quantity);
        strAmount = "";
        FatSNF = "";
        QT = "";
        Fat = "";
        SNF = "";
        totalMilk = 0;
        TotalAmount = 0;
        strShift = shift;
        setPrintlabelName(mContext);
        msg = "";
        if (mSocket != null && mOutputStream != null && mInputStream != null) {

            StringBuilder sb = new StringBuilder();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault()).format(new Date());
            sb.append(SelectedDate).append(" " + currentTime);
            sb.append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelId + " ").append(CustomerID).append("  " + selectedName).append("\n");
            sb.append(strDeshDotLine).append("\n");
            //SHIFT
            sb.append(labelShift + "   : ").append(strShift).append("\n");
            if (milkTyp == 1) {
                sb.append(labelType + "    : ").append(labelBuffalo).append("\n");
            } else if (milkTyp == 2) {
                sb.append(labelType + "    : ").append(labelCow).append("\n");
            }


            if (snf > 0) {

            }

            if (sessionManager.getValueSesion(Key_FatYES).matches(ONE)) {
                sb.append(labelFat + " %   : ").append(actualFate).append("\n");
            }
            if (sessionManager.getValueSesion(SessionManager.Key_SNFYES).matches(ONE)) {
                sb.append(labelSNF + "/" + labelCLR + " : ").append(snf).append("\n");
            }

            //WEIGHT
            sb.append(labelWeight + "/" + lableQuantity + "  : ").append(weight).append(" " + labelLtr).append("\n");

            if (sessionManager.getValueSesion(SessionManager.Key_RateYES).matches(ONE)) {
                sb.append(labelRate + "    : ").append(rsPerKg).append(" " + labelPerLtr).append("\n");
            }
            if (sessionManager.getValueSesion(Key_BonusYES).equalsIgnoreCase(ONE)) {
                sb.append(lableBonus + "   : ").append(" " + totalBonus).append("\n");
            }
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalRs + ": ").append(totalPayment).append(" /-").append("\n");
            sb.append(strDeshDotLine).append("\n");
            if (sessionManager.getValueSesion(SessionManager.Key_SendTotaLYES).matches(ONE)){
                sb.append(weekTotal + ": ").append(totalWeekPayment).append(" /-").append("\n");
            }

            sb.append(strDeshDotLine).append("\n");
            sb.append(msgg).append("\n").append("\n");
            msg = sb.toString();
            Log.d(TAG, "printAddMilk_SingleEntryReciept111 : " + msg);
            CommonPrintReciept(CenterName, msg);
        }
    }


    public static void printSaleMilk_SingleEntryReciept(Context context, int milkTyp, int CustomerID, String selectedName, String shift, String actualFate, float snf, String rsPerKg, String weight, String totalBonus, String totalPayment, float totalWeekPayment, String msgg) {
        mContext = context;
        SessionManager sessionManager = new SessionManager(mContext);
        msg = "";
        strAmount = "";
        FatSNF = "";
        QT = "";
        Fat = "";
        SNF = "";
        totalMilk = 0;
        TotalAmount = 0;
        strShift = shift;
        setPrintlabelName(mContext);


        if (mSocket != null && mOutputStream != null && mInputStream != null) {
            String lableQuantity = mContext.getString(R.string.Quantity);
            StringBuilder sb = new StringBuilder();
            String currentTime = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault()).format(new Date());
            sb.append(SelectedDate).append(" " + currentTime);
            sb.append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelId.toUpperCase() + " ").append(CustomerID).append("  " + selectedName.toUpperCase()).append("\n");
            sb.append(strDeshDotLine).append("\n");
            //SHIFT
            sb.append(labelShift.toUpperCase() + "   : ").append(strShift.toUpperCase()).append("\n");
            if (milkTyp == 1) {
                sb.append(labelType + "    : ").append(labelBuffalo.toUpperCase()).append("\n");
            } else if (milkTyp == 2) {
                sb.append(labelType + "    : ").append(labelCow.toUpperCase()).append("\n");
            }
            if (sessionManager.getValueSesion(Key_FatYES).equalsIgnoreCase(ONE)) {
                sb.append(labelFat + " %   : ").append(actualFate).append("\n");
            }

            if (snf > 0) {

            }

            if (sessionManager.getValueSesion(Key_SNFYES).equalsIgnoreCase(ONE)) {
                sb.append(labelSNF + "/" + labelCLR + " : ").append(snf).append("\n");
            }

            //WEIGHT
            sb.append(labelWeight + "/" + lableQuantity + "  : ").append(weight).append(" " + labelLtr).append("\n");
            if (sessionManager.getValueSesion(Key_RateYES).equalsIgnoreCase(ONE)) {
                sb.append(labelRate + "    : ").append(rsPerKg).append(" " + labelPerLtr).append("\n");
            }

            if (sessionManager.getValueSesion(Key_BonusYES).equalsIgnoreCase(ONE)) {
                sb.append(lableBonus + "    : ").append(" " + totalBonus).append("\n");
            }
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalRs + ": ").append(totalPayment).append(" /-").append("\n");
            sb.append(strDeshDotLine).append("\n");

            if (sessionManager.getValueSesion(SessionManager.Key_SendTotaLYES).matches(ONE)){
                sb.append(weekTotal + ": ").append(totalWeekPayment).append(" /-").append("\n");
            }


            sb.append(strDeshDotLine).append("\n");
            sb.append(msgg).append("\n").append("\n");
            msg = sb.toString();
            CommonPrintReciept(CenterName, msg);

        }
    }


    public static void printSaleMilk_OneDayReciept(Context context, List<CustomerSaleMilkEntryList> mList) {
        mContext = context;
        msg = "";
        strAmount = "";
        FatSNF = "";
        QT = "";
        Fat = "";
        SNF = "";
        totalMilk = 0;
        TotalAmount = 0;
        setPrintlabelName(mContext);
        StringBuilder sb = new StringBuilder();
        String actualFate = "";

        if (mSocket != null && mSocket.isConnected() && mOutputStream != null && mInputStream != null) {
            sb.append("\n");
            List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelDate + " : ").append(SelectedDate).append("\n");
            sb.append(labelShift + " : ").append(strShift).append("\n");
            sb.append(strDeshDotLine).append("\n");
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelId + "  ").append(labelFat + "  ").append(labelWeight + "  ").append(labelAmount).append("\n").append("\n");

            for (int i = 0; i < mList.size(); i++) {
                //------------------FAT------------------
                id = mList.get(i).unic_customer + " ";

                actualFate = mList.get(i).fat;
                if (actualFate.length() > 0 && !actualFate.contains(".")) {
                    float fat = 0;
                    fat = Float.parseFloat(actualFate);
                    actualFate = String.valueOf(fat / 10);
                } else if (actualFate.length() == 0) {
                    actualFate = mList.get(i).per_kg_price;
                } else {
                    actualFate = mList.get(i).fat;
                }

                FatSNF = actualFate + " ";

                //-----------------WEIGHT-------------------
                QT = mList.get(i).total_milk;
                QT = weightFun(QT);


                strAmount = mList.get(i).total_price;

                printdataList.add(new Reciept_Item(id, "", FatSNF, "", QT, strAmount, "", ""));
                strBody.append(id + " ").append(FatSNF + " ").append(QT + " ").append(strAmount).append("\n");

                if (!mList.get(i).total_milk.equals("") && !mList.get(i).total_milk.equals("-")) {
                    totalMilk = totalMilk + Double.parseDouble(mList.get(i).total_milk);
                }

                if (!mList.get(i).total_price.equals("") && !mList.get(i).total_price.equals("-")) {
                    TotalAmount = TotalAmount + Double.parseDouble(mList.get(i).total_price);
                }

            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();

                t.addColumn(labelId, Reciept_Item::getId);
                t.addColumn(labelFat.toUpperCase() + "/" + labelSNF, Reciept_Item::getFAT);
                t.addColumn(labelWeight, Reciept_Item::getQT);
                t.addColumn(labelAmount, Reciept_Item::getAmount);
                String strTabledata = t.createString(printdataList);
                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }

            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalWeight + ":").append(String.format("%.2f", totalMilk)).append(" " + labelLtr).append("\n");
            sb.append(labelTotalAmount + ":").append(String.format("%.2f", TotalAmount)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n").append("\n");
            msg = sb.toString();
            CommonPrintReciept(CenterName, msg);
        }
    }

    private static String weightFun(String qt) {

        if (qt.length() == 1) {
            qt = qt + "    ";
        } else if (qt.length() == 2) {
            qt = qt + "    ";
        } else if (qt.length() == 3) {
            qt = qt + "  ";
        } else {
            qt = qt + " ";
        }
        return qt;
    }


    public static void printAddMilk_OneDayReciept(Context context, List<CustomerEntryListPojo> mList) {
        mContext = context;
        msg = "";
        strAmount = "";
        FatSNF = "";
        QT = "";
        Fat = "";
        SNF = "";
        totalMilk = 0;
        TotalAmount = 0;
        setPrintlabelName(mContext);
        String actualFate = "";


        List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();

        if (mSocket != null && mSocket.isConnected() && mOutputStream != null && mInputStream != null) {

            StringBuilder sb = new StringBuilder();
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelDate + " : ").append(SelectedDate).append("\n");
            sb.append(labelShift + " : ").append(strShift.toUpperCase()).append("\n");
            sb.append(strDeshDotLine).append("\n").append("\n");

            StringBuilder strBody = new StringBuilder();
            strBody.append(labelId + " ").append(labelFat + " ").append(labelWeight + "  ").append(labelAmount).append("\n").append("\n");


            for (int i = 0; i < mList.size(); i++) {
                //------------------FAT------------------
                id = mList.get(i).unic_customer + " ";

                actualFate = mList.get(i).fat;
                if (actualFate.length() > 0 && !actualFate.contains(".")) {
                    float fat = 0;
                    fat = Float.parseFloat(actualFate);
                    actualFate = String.valueOf(fat / 10);
                } else if (actualFate.length() == 0) {
                    actualFate = mList.get(i).per_kg_price;
                } else {
                    actualFate = mList.get(i).fat;
                }
                String actualSNf = mList.get(i).snf;
                if (actualSNf.length() > 0 && !actualSNf.contains(".")) {
                    Float snf = Float.parseFloat(actualSNf);
                    actualSNf = String.valueOf(snf / 10);
                } else if (actualSNf.length() == 0) {
                    actualSNf = "0.0";
                } else {
                    actualSNf = mList.get(i).snf;
                }

                FatSNF = actualFate + "-" + actualSNf + " ";

                //-----------------WEIGHT-------------------
                QT = mList.get(i).total_milk;
                QT = weightFun(QT);


                strAmount = mList.get(i).total_price;
                printdataList.add(new Reciept_Item(id, "", FatSNF, "", QT, strAmount, "", ""));
                strBody.append(id).append(FatSNF + " ").append(QT + " ").append(strAmount).append("\n");


                if (!mList.get(i).total_milk.equals("") && !mList.get(i).total_milk.equals("-")) {
                    totalMilk = totalMilk + Double.parseDouble(mList.get(i).total_milk);
                }
                if (!mList.get(i).total_price.equals("") && !mList.get(i).total_price.equals("-")) {
                    TotalAmount = TotalAmount + Double.parseDouble(mList.get(i).total_price);
                }
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();
                t.addColumn(labelId.toUpperCase(), Reciept_Item::getId);
                t.addColumn(labelFat.toUpperCase() + "/" + labelSNF, Reciept_Item::getFAT);
                t.addColumn(labelWeight.toUpperCase(), Reciept_Item::getQT);
                t.addColumn(labelAmount.toUpperCase(), Reciept_Item::getAmount);
                String strTabledata = t.createString(printdataList);

                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }

            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalWeight + ": ").append(String.format("%.2f", totalMilk)).append(" " + labelLtr).append("\n");
            sb.append(labelTotalAmount + ": ").append(String.format("%.2f", TotalAmount)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n");

            msg = sb.toString();
            CommonPrintReciept(CenterName, msg);
        }
    }


    public static void printCustomer(Context context, boolean isBuyer, List<Reciept_Item> userList) {
        mContext = context;
        setPrintlabelName(mContext);

        if (mSocket != null && mOutputStream != null && mInputStream != null) {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            String customer = labelSeller;
            if (isBuyer) {
                customer = labelBuyer;
            }


            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(currentDateTimeString).append("\n");
            sb.append(strDeshDotLine).append("\n");

            sb.append(labelCustomer).append(" :" + customer).append("\n");
            sb.append(strDeshDotLine).append("\n");
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelId + "  ").append(labelName + "     ").append(labelMobileNo).append("\n").append("\n");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();
                t.addColumn(labelId + " ", Reciept_Item::getDate);
                t.addColumn(labelName + " ", Reciept_Item::getTitle);
                t.addColumn(labelMobileNo + " ", Reciept_Item::getCredit);


                String strTabledata = t.createString(userList);
                sb.append(strTabledata);
            } else {
                for (int i = 0; i < userList.size(); i++) {
                    String id = userList.get(i).getDate();
                    String name = userList.get(i).getTitle();
                    String mobile = userList.get(i).getCredit();
                    if (id.length() == 1) {
                        id = id + " ";
                    }

                    strBody.append(id + " ").append(name + " ").append(mobile).append("\n");

                }
                sb.append(strBody.toString());
            }
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n").append("\n");

            msg = sb.toString();
            CommonPrintReciept(CenterName, msg);
        }
    }


    public static void PrintTenDaysReciept(Context context, String user_name, String customerId, List<TenDaysMilkSellHistory> mList) {
        mContext = context;
        setPrintlabelName(mContext);
        List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();
        if (mSocket != null && mOutputStream != null && mInputStream != null) {
            msg = "";
            Fat = "";
            SNF = "";
            QT = "";
            strAmount = "";
            totalMilk = 0;
            TotalAmount = 0;
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(currentDateTimeString).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelId + " ").append(customerId).append("  " + user_name).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(mList.get(0).from_date).append("  To  " + mList.get(0).to_date).append("\n");
            sb.append(strDeshDotLine).append("\n");
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelDate + "  ").append(labelFat + "/" + labelSNF + "   ").append(labelWeight).append("   " + labelAmount).append("\n").append("\n");

            for (int i = 0; i < mList.size(); i++) {
                String date = "";
                if (mList.get(i).shift.equals("morning")) {
                    date = mList.get(i).for_date.substring(0, 2) + " - " + mList.get(i).shift.substring(0, 1).toUpperCase();
                } else {
                    date = mList.get(i).for_date.substring(0, 2) + " - " + mList.get(i).shift.substring(0, 1).toUpperCase();
                }
                //------------------FAT------------------
                if (mList.get(i).fat.equalsIgnoreCase("-")) {
                    Fat = "0.0";
                } else {
                    Fat = mList.get(i).fat;

                }
                //-----------------SNF-------------------
                if (mList.get(i).snf.equalsIgnoreCase("-")) {
                    SNF = "0.0";
                } else {
                    SNF = mList.get(i).snf;
                }
                FatSNF = Fat + "-" + SNF;
                //-----------------WEIGHT-------------------
                if (mList.get(i).entry_total_milk.equalsIgnoreCase("-")) {
                    QT = "   -  ";
                } else {


                    qt = Double.parseDouble(mList.get(i).entry_total_milk);
                    QT = String.format("%.2f", qt);
                }
                //-----------------AMOUNT-------------------
                if (mList.get(i).total_price.equalsIgnoreCase("-")) {
                    strAmount = "   -";
                } else {
                    strAmount = mList.get(i).total_price;
                }

                if (!mList.get(i).entry_total_milk.equals("") && !mList.get(i).entry_total_milk.equals("-")) {
                    totalMilk = totalMilk + Double.parseDouble(mList.get(i).entry_total_milk);
                }

                if (!mList.get(i).total_price.equals("") && !mList.get(i).total_price.equals("-")) {
                    TotalAmount = TotalAmount + Double.parseDouble(mList.get(i).total_price);
                }
                strBody.append(date + " ").append(FatSNF + " ").append(QT + " ").append(strAmount).append("\n");

                printdataList.add(new Reciept_Item(" ", date + "", " " + FatSNF, "", QT, strAmount, "", ""));

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();
                t.addColumn(labelDate + " ", Reciept_Item::getDate);
                t.addColumn(labelFat + "/" + labelSNF + " ", Reciept_Item::getFAT);
                t.addColumn(labelWeight + " ", Reciept_Item::getQT);
                t.addColumn(labelAmount, Reciept_Item::getAmount);

                String strTabledata = t.createString(printdataList);
                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }

            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalWeight + ": ").append(String.format("%.2f", totalMilk)).append(" " + labelLtr).append("\n");
            sb.append(labelTotalAmount + ": ").append(String.format("%.2f", TotalAmount)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n").append("\n").append("\n");

            msg = sb.toString();
            CommonPrintReciept(CenterName, msg);
        }
    }

    public static void PrintTenDaysWithTransReciept(Context context, String user_name, String customerId, String openingBalance, List<TenDaysMilkSellHistory> mList, List<BeanUserTransaction> transactions) {
        mContext = context;
        setPrintlabelName(mContext);
        List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();

        Fat = "";
        SNF = "";
        QT = "";
        strAmount = "";
        totalMilk = 0;
        TotalAmount = 0;
        System.out.println("mSocket------->>>>>" + mSocket);

        if (mSocket != null && mOutputStream != null && mInputStream != null) {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            System.out.println("currentDateTimeString------->>>>>" + currentDateTimeString);
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(currentDateTimeString).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelId + " ").append(customerId).append("  " + user_name).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(mList.get(0).from_date).append("  To  " + mList.get(0).to_date).append("\n");
            sb.append(strDeshDotLine).append("\n");
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelDate + "  ").append(labelFat + "/" + labelSNF + "   ").append(labelWeight).append("   " + labelAmount).append("\n").append("\n");

            for (int i = 0; i < mList.size(); i++) {
                String date = "";
                if (mList.get(i).shift.equals("morning")) {
                    date = mList.get(i).for_date.substring(0, 2) + " - " + mList.get(i).shift.substring(0, 1).toUpperCase();
                } else {
                    date = mList.get(i).for_date.substring(0, 2) + " - " + mList.get(i).shift.substring(0, 1).toUpperCase();
                }
                //------------------FAT------------------
                if (mList.get(i).fat.equalsIgnoreCase("-")) {
                    Fat = "0.0";
                } else {
                    Fat = mList.get(i).fat;

                }
                //-----------------SNF-------------------
                if (mList.get(i).snf.equalsIgnoreCase("-")) {
                    SNF = "0.0";
                } else {
                    SNF = mList.get(i).snf;
                }
                FatSNF = Fat + "-" + SNF;
                //-----------------WEIGHT-------------------
                if (mList.get(i).entry_total_milk.equalsIgnoreCase("-")) {
                    QT = "   -  ";
                } else {


                    qt = Double.parseDouble(mList.get(i).entry_total_milk);
                    QT = String.format("%.2f", qt);
                }
                //-----------------AMOUNT-------------------
                if (mList.get(i).total_price.equalsIgnoreCase("-")) {
                    strAmount = "   -";
                } else {
                    strAmount = mList.get(i).total_price;
                }

                if (!mList.get(i).entry_total_milk.equals("") && !mList.get(i).entry_total_milk.equals("-")) {
                    totalMilk = totalMilk + Double.parseDouble(mList.get(i).entry_total_milk);
                }

                if (!mList.get(i).total_price.equals("") && !mList.get(i).total_price.equals("-")) {
                    TotalAmount = TotalAmount + Double.parseDouble(mList.get(i).total_price);
                }
                strBody.append(date + " ").append(FatSNF + " ").append(QT + " ").append(strAmount).append("\n");

                printdataList.add(new Reciept_Item(" ", date + "", " " + FatSNF, "", QT, strAmount, "", ""));

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();
                t.addColumn(labelDate + " ", Reciept_Item::getDate);
                t.addColumn(labelFat + "/" + labelSNF + " ", Reciept_Item::getFAT);
                t.addColumn(labelWeight + " ", Reciept_Item::getQT);
                t.addColumn(labelAmount, Reciept_Item::getAmount);

                String strTabledata = t.createString(printdataList);
                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }

            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalWeight + ": ").append(String.format("%.2f", totalMilk)).append(" " + labelLtr).append("\n");
            //sb.append(labelOpeningBalance + ": ").append(openingBalance).append("\n");
            sb.append(labelTotalAmount + ": ").append(String.format("%.2f", TotalAmount)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            if (!transactions.isEmpty()) {
                sb.append("   *** Transaction ***").append("\n").append("\n");
                String transa = printTrans(transactions);

                sb.append(transa);
            }
            sb.append("WWW.MERIDAIRY.IN").append("\n");
            msg = sb.toString();
            System.out.println("msg------->>>>>" + msg);
            CommonPrintReciept(CenterName, msg);
        }
    }


    private static String printTrans(List<BeanUserTransaction> transactionList) {
        StringBuilder sb = new StringBuilder();
        StringBuilder strBody = new StringBuilder();
        List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();
        double totalCredit = 0, totalDebit = 0, remain = 0;
        strBody.append(labelDate + "  ").append(labelTitle + "  ").append(labelCredit + "  ").append(labelDebit).append("\n").append("\n");

        for (int i = 0; i < transactionList.size(); i++) {
            reciptdate = transactionList.get(i).getTransaction_date().substring(0, 2) + " ";
            title = transactionList.get(i).getDescription();
            String tmpTitle = transactionList.get(i).getDescription();

            if (tmpTitle.length() == 4) {
                title = title + "  ";
            } else if (tmpTitle.length() == 3) {
                title = title + "   ";
            } else if (tmpTitle.length() == 2) {
                title = title + "    ";
            } else if (tmpTitle.length() == 0 || tmpTitle.length() == 1) {
                title = title + "     ";
            } else {
                title = title.substring(0, 5) + " ";
            }
            totalCredit = totalCredit + transactionList.get(i).getCr();
            totalDebit = totalDebit + transactionList.get(i).getDr();
            //-----------------credit--------debit-----------
            if (transactionList.get(i).getCr() > 0) {
                credit = "" + transactionList.get(i).getCr();
                debit = " ---";
            } else {
                credit = " --- ";
                debit = "" + transactionList.get(i).getDr();
            }
            if (credit.length() == 3) {
                credit = credit + "    ";
            } else if (credit.length() == 4) {
                credit = credit + "   ";
            } else if (credit.length() == 5) {
                credit = credit + "  ";
            } else {
                credit = credit + " ";
            }
            strBody.append(reciptdate + " ").append(title + " ").append(credit + " ").append(debit).append("\n");
            printdataList.add(new Reciept_Item(reciptdate, title, credit, debit));
        }


        TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            t.addColumn(labelDate, Reciept_Item::getDate);
            t.addColumn(labelTitle + " ", Reciept_Item::getTitle);
            t.addColumn(labelCredit + " ", Reciept_Item::getCredit);
            t.addColumn(labelDebit, Reciept_Item::getDebit);
            String strTabledata = "";

            strTabledata = t.createString(printdataList);

            sb.append(strTabledata);
        } else {
            sb.append(strBody.toString());
        }


        sb.append(strDeshDotLine).append("\n");
        sb.append(labelTotalCredit + " :").append(String.format("%.2f", totalCredit)).append("\n");
        sb.append(labelTotalDebit + " :").append(String.format("%.2f", totalDebit)).append("\n");
        sb.append(labelRemainAmount + " :").append(String.format("%.2f", remain)).append("\n");
        sb.append(strDeshDotLine).append("\n");
        msg = sb.toString();
        return msg;
    }


    public static void printBhugtanRegister(Context context, List<BeanTransactionUserItem> mList) {
        mContext = context;
        setPrintlabelName(mContext);

        String strRegisterType = labelSeller + " " + labelCustomer;
        if (mList.get(0).getUserGroupId().equals("4")) {
            strRegisterType = labelBuyer;
        }
        msg = "";
        String customerId = "";

        credit = "";
        debit = "";
        double totalCr = 0, totalDr = 0, balance = 0;


        if (mSocket != null && mOutputStream != null && mInputStream != null) {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(currentDateTimeString).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(strRegisterType).append("\n");
            sb.append(strDeshDotLine).append("\n");
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelId + "  ").append(labelCredit + "  ").append(labelDebit + "  ").append(labelRemainAmount.substring(0, 4)).append("\n").append("\n");

            for (int i = 0; i < mList.size(); i++) {
                customerId = mList.get(i).getUnic_customer() + " ";

                if (mList.get(i).getTotal_cr() > 0) {
                    credit = String.format("%.2f", mList.get(i).getTotal_cr());
                } else {
                    credit = " --- ";
                }
                if (mList.get(i).getTotal_cr() > 0) {
                    debit = String.format("%.2f", mList.get(i).getTotal_dr());
                } else {
                    debit = "---";
                }
                remainBalance = String.format("%.2f", mList.get(i).getBalance());
                totalCr = totalCr + mList.get(i).getTotal_cr();
                totalDr = totalDr + mList.get(i).getTotal_dr();


                if (credit.length() == 3) {
                    credit = credit + "  ";
                } else if (credit.length() == 4) {
                    credit = credit + " ";
                } else {
                    credit = credit + " ";
                }
                if (debit.length() == 3) {
                    debit = debit + "  ";
                } else if (debit.length() == 4) {
                    debit = debit + " ";
                }
                strBody.append(customerId + " ").append(credit + " ").append(debit + " ").append(remainBalance).append("\n");
                printdataList.add(new Reciept_Item(customerId, title, credit, debit, remainBalance));
            }


            TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                t.addColumn(labelId, Reciept_Item::getDate);
                t.addColumn(labelCredit + "", Reciept_Item::getCredit);
                t.addColumn(labelDebit, Reciept_Item::getDebit);
                t.addColumn(labelRemainAmount.substring(0, 4), Reciept_Item::getRemainAmount);
                String strTabledata = "";

                strTabledata = t.createString(printdataList);

                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }
            balance = totalDr - totalCr;

            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalCredit + " :").append(String.format("%.2f", totalCr)).append("\n");
            sb.append(labelTotalDebit + " :").append(String.format("%.2f", totalDr)).append("\n");
            sb.append(labelRemainAmount + " :").append(String.format("%.2f", balance)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n");

            msg = sb.toString();

            CommonPrintReciept(CenterName, msg);


        }

    }

    public static void PrintBhugtanAllUserData(Context context, List<TenDaysMilkSellHistory> mList) {
        mContext = context;
        setPrintlabelName(mContext);
        totalMilk = 0;
        TotalAmount = 0;
        List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();

        if (mSocket != null && mOutputStream != null && mInputStream != null) {

            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(currentDateTimeString).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(mList.get(0).from_date).append("  To  " + mList.get(0).to_date).append("\n");
            sb.append(strDeshDotLine).append("\n");

            StringBuilder strBody = new StringBuilder();
            strBody.append(labelId + "  ").append(labelWeight + "      ").append(labelAmount).append("\n").append("\n");
            for (int i = 0; i < mList.size(); i++) {

                //------------------FAT------------------
                id = mList.get(i).unic_customer;
                if (id.length() == 1) {
                    id = id + "   ";
                } else if (id.length() == 2) {
                    id = id + "  ";
                } else if (id.length() == 3) {
                    id = id + " ";
                }

                //-----------------WEIGHT-------------------
                qt = Double.parseDouble(mList.get(i).total_milk);
                QT = String.format("%.2f", qt);
                if (QT.length() == 1) {
                    QT = QT + "        ";
                } else if (QT.length() == 2) {
                    QT = QT + "       ";
                } else if (QT.length() == 3) {
                    QT = QT + "       ";
                } else if (QT.length() == 4) {
                    QT = QT + "      ";
                } else if (QT.length() == 5) {
                    QT = QT + "     ";
                } else if (QT.length() == 6) {
                    QT = QT + "    ";
                } else {
                    QT = QT + "  ";
                }
                //-----------------AMOUNT-------------------
                if (!mList.get(i).grnd_total_amt.trim().startsWith("0.00")) {
                    amount = Double.parseDouble(mList.get(i).grnd_total_amt);
                    strAmount = String.format("%.2f", amount);
                }
                if (!mList.get(i).grnd_total_amt.trim().startsWith("0.00")) {
                    strBody.append(id + "").append(QT + "  ").append(strAmount).append("\n");
                    id = mList.get(i).unic_customer;
                    printdataList.add(new Reciept_Item(id, QT, strAmount));
                }
                if (!mList.get(i).total_milk.trim().equals("") && !mList.get(i).total_milk.trim().equals("-")) {
                    totalMilk = totalMilk + Double.parseDouble(mList.get(i).total_milk);
                }
                if (!mList.get(i).grnd_total_amt.trim().equals("") && !mList.get(i).grnd_total_amt.trim().equals("-")) {
                    TotalAmount = TotalAmount + Double.parseDouble(mList.get(i).grnd_total_amt);
                }
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();
                t.addColumn(labelId + " ", Reciept_Item::getId);
                t.addColumn(labelWeight + " ", Reciept_Item::getQT);
                t.addColumn(labelAmount, Reciept_Item::getAmount);
                String strTabledata = t.createString(printdataList);
                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }

            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalWeight + ": ").append(String.format("%.3f", totalMilk)).append("\n");
            sb.append(labelTotalAmount + ": ").append(String.format("%.2f", TotalAmount)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n").append("\n");
            msg = sb.toString();
            CommonPrintReciept(CenterName, msg);
        }
    }


    public static void PrintMonthReciept(Context context, String user_name, String customerId, String fromDate, String toDate, List<BuyerCustomerDataListPojo> mList, String grandTtotal) {
        mContext = context;
        setPrintlabelName(mContext);
        String ShiftMorning = "", ShiftEvening = "", strAmount = "";
        float TotalAmount = 0;
        List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();
        if (mDevice != null || mSocket != null && mOutputStream != null && mInputStream != null) {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            StringBuilder sb = new StringBuilder();
            sb.append(currentDateTimeString);
            sb.append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelId + " ").append(customerId).append("  " + user_name).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(fromDate).append("  To  " + toDate).append("\n");
            sb.append(strDeshDotLine).append("\n");
            if (strPrintMultiLang.equals(NO)) {
                labelDate = labelDate.toUpperCase() + " ";
                labelMorning = " M ";
                labelEvening = " E ";
                labelAmount = labelAmount.toUpperCase().toUpperCase();
            } else {
                labelDate = labelDate.toUpperCase() + " ";
                labelMorning = labelMorning + " ";
                labelEvening = labelEvening + " ";
                labelAmount = labelAmount.toUpperCase().toUpperCase();
            }
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelDate + " ").append(labelMorning + " ").append(labelEvening + " ").append(labelAmount).append("\n").append("\n");
            ArrayList<ListPojo> morningList = new ArrayList<>();
            ArrayList<ListPojo> eveningList = new ArrayList<>();

            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).session.equals("Morning")) {
                    morningList.add(new ListPojo(mList.get(i).for_date, mList.get(i).session, mList.get(i).entry_total_milk, mList.get(i).entry_total_price));
                } else if (mList.get(i).session.equals("Evening")) {
                    eveningList.add(new ListPojo(mList.get(i).for_date, mList.get(i).session, mList.get(i).entry_total_milk, mList.get(i).entry_total_price));
                }
            }
            int maxSize = 0;
            int morningListSize = morningList.size();
            int eveningListSize = eveningList.size();

            if (morningListSize >= eveningListSize) {
                maxSize = morningListSize;
            } else {
                maxSize = eveningListSize;
            }
            System.out.println("morningListSize>>>" + morningListSize);
            System.out.println("eveningListSize>>>" + eveningListSize);


            for (int k = 0; k < maxSize; k++) {
                double total = 0;
                String strDate = "";

                // Morning Size
                if (k < morningListSize) {
                    strDate = morningList.get(k).date + "  ";
                    if (!morningList.get(k).price.equals("-") && !morningList.get(k).price.equals("")) {
                        total = total + Double.parseDouble(morningList.get(k).price);
                        if (morningList.get(k).milk.length() < 3) {
                            ShiftMorning = " " + morningList.get(k).milk + " ";
                        } else {
                            ShiftMorning = " " + morningList.get(k).milk + " ";
                        }
                    } else {
                        ShiftMorning = "  " + morningList.get(k).milk + "  ";
                    }
                } else {
                    ShiftMorning = "  " + "-" + "  ";
                }
                // Evening List data
                if (k < eveningListSize) {
                    strDate = eveningList.get(k).date + "  ";
                    if (!eveningList.get(k).price.equals("-") && !eveningList.get(k).price.equals("")) {
                        total = total + Double.parseDouble(eveningList.get(k).price);
                        if (eveningList.get(k).milk.length() < 3) {
                            ShiftEvening = " " + eveningList.get(k).milk + " ";
                        } else {
                            ShiftEvening = " " + eveningList.get(k).milk + " ";
                        }
                    } else {
                        ShiftEvening = "  " + eveningList.get(k).milk + " ";
                    }
                } else {
                    ShiftEvening = " " + "-" + "  ";
                }
                strAmount = String.format("%.2f", total);

                strBody.append(strDate).append(ShiftMorning + "  ").append(ShiftEvening + "  ").append(strAmount).append("\n");
                printdataList.add(new Reciept_Item("", strDate, "", "", "", strAmount, ShiftMorning, ShiftEvening));


            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();
                t.addColumn(labelDate, Reciept_Item::getDate);
                t.addColumn(labelMorning, Reciept_Item::getShiftMorning);
                t.addColumn(labelEvening, Reciept_Item::getShiftEvening);
                t.addColumn(labelAmount, Reciept_Item::getAmount);

                String strTabledata = t.createString(printdataList);
                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }

            TotalAmount = Float.parseFloat(grandTtotal);
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalRs + " : ").append(String.format("%.2f", TotalAmount)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n");

            msg = sb.toString();
            CommonPrintReciept(CenterName, msg);
        }
    }


    public static void PrintTransactionReciept(Context context, ArrayList<TransectionListPojo> transactionList, String startDate, String endDate, String user_name, String customerId, double totalCredit, double totalDebit, double remain) {
        mContext = context;
        setPrintlabelName(mContext);
        msg = "";
        reciptdate = "";
        title = "";
        credit = "";
        debit = "";
        if (mSocket != null && mOutputStream != null && mInputStream != null) {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(currentDateTimeString).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelId + " ").append(customerId).append("  " + user_name).append("\n");
            sb.append(startDate).append("  To  " + endDate).append("\n");
            sb.append(strDeshDotLine).append("\n");
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelDate + "  ").append(labelTitle + "  ").append(labelCredit + "  ").append(labelDebit).append("\n").append("\n");

            for (int i = 0; i < transactionList.size(); i++) {
                reciptdate = transactionList.get(i).entry_date.substring(0, 2) + " ";
                title = transactionList.get(i).products_name;
                if (title.length() == 4) {
                    title = transactionList.get(i).products_name + "  ";
                } else if (title.length() == 3) {
                    title = transactionList.get(i).products_name + "   ";
                } else if (title.length() == 2) {
                    title = transactionList.get(i).products_name + "    ";
                } else if (title.length() == 0 || title.length() == 1) {
                    title = transactionList.get(i).products_name + "     ";
                } else {
                    title = transactionList.get(i).products_name.substring(0, 5) + " ";
                }

                //-----------------credit--------debit-----------
                if (transactionList.get(i).type.equals("credit") || transactionList.get(i).type.equals("receive")) {
                    credit = transactionList.get(i).total_price;
                    debit = " ---";
                } else {
                    credit = " --- ";
                    debit = transactionList.get(i).total_price;
                }
                if (credit.length() == 3) {
                    credit = credit + "    ";
                } else if (credit.length() == 4) {
                    credit = credit + "   ";
                } else if (credit.length() == 5) {
                    credit = credit + "  ";
                } else {
                    credit = credit + " ";
                }
                strBody.append(reciptdate + " ").append(title + " ").append(credit + " ").append(debit).append("\n");
                printdataList.add(new Reciept_Item(reciptdate, title, credit, debit));
            }


            TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                t.addColumn(labelDate, Reciept_Item::getDate);
                t.addColumn(labelTitle + " ", Reciept_Item::getTitle);
                t.addColumn(labelCredit + " ", Reciept_Item::getCredit);
                t.addColumn(labelDebit, Reciept_Item::getDebit);
                String strTabledata = "";

                strTabledata = t.createString(printdataList);

                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }


            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalCredit + " :").append(String.format("%.2f", totalCredit)).append("\n");
            sb.append(labelTotalDebit + " :").append(String.format("%.2f", totalDebit)).append("\n");
            sb.append(labelRemainAmount + " :").append(String.format("%.2f", remain)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n");

            msg = sb.toString();

            CommonPrintReciept(CenterName, msg);


        }
    }

    public static void PrintUserTransactionReceipt(Context context, String billTitle, ArrayList<BeanUserTransaction> transactionList, String startDate, String endDate, String user_name, String customerId, double totalCredit, double totalDebit, double remain) {
        mContext = context;
        setPrintlabelName(mContext);
        msg = "";
        reciptdate = "";
        title = "";
        credit = "";
        debit = "";

        if (mSocket != null && mOutputStream != null && mInputStream != null) {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();
            StringBuilder sb = new StringBuilder();
            sb.append("*****" + " " + billTitle).append(" *****\n").append("\n");
            sb.append(currentDateTimeString).append("\n");
            sb.append(strDeshDotLine).append("\n");
            if (!billTitle.equalsIgnoreCase("My Transactions")) {
                sb.append(labelId + " ").append(customerId).append("  " + user_name).append("\n");
            }

            sb.append(startDate).append("  To  " + endDate).append("\n");
            sb.append(strDeshDotLine).append("\n");
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelDate + "  ").append(labelTitle + "  ").append(labelCredit + "  ").append(labelDebit).append("\n").append("\n");

            for (int i = 0; i < transactionList.size(); i++) {
                reciptdate = transactionList.get(i).getTransaction_date().substring(0, 2) + " ";
                title = transactionList.get(i).getVoucher_type();
                String tmpTitle = transactionList.get(i).getVoucher_type();
                if (billTitle.equalsIgnoreCase("My Transactions")) {
                    tmpTitle = transactionList.get(i).getParty_name();
                    title = transactionList.get(i).getParty_name();
                }
                if (tmpTitle.length() == 4) {
                    title = title + "  ";
                } else if (tmpTitle.length() == 3) {
                    title = title + "   ";
                } else if (tmpTitle.length() == 2) {
                    title = title + "    ";
                } else if (tmpTitle.length() == 0 || tmpTitle.length() == 1) {
                    title = title + "     ";
                } else {
                    title = title.substring(0, 5) + " ";
                }

                //-----------------credit--------debit-----------
                if (transactionList.get(i).getCr() > 0) {
                    credit = "" + transactionList.get(i).getCr();
                    debit = " ---";
                } else {
                    credit = " --- ";
                    debit = "" + transactionList.get(i).getDr();
                }
                if (credit.length() == 3) {
                    credit = credit + "    ";
                } else if (credit.length() == 4) {
                    credit = credit + "   ";
                } else if (credit.length() == 5) {
                    credit = credit + "  ";
                } else {
                    credit = credit + " ";
                }
                strBody.append(reciptdate + " ").append(title + " ").append(credit + " ").append(debit).append("\n");
                printdataList.add(new Reciept_Item(reciptdate, title, credit, debit));
            }


            TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                t.addColumn(labelDate, Reciept_Item::getDate);
                t.addColumn(labelTitle + " ", Reciept_Item::getTitle);
                t.addColumn(labelCredit + " ", Reciept_Item::getCredit);
                t.addColumn(labelDebit, Reciept_Item::getDebit);
                String strTabledata = "";

                strTabledata = t.createString(printdataList);

                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }


            sb.append(strDeshDotLine).append("\n");
            sb.append(labelTotalCredit + " :").append(String.format("%.2f", totalCredit)).append("\n");
            sb.append(labelTotalDebit + " :").append(String.format("%.2f", totalDebit)).append("\n");
            sb.append(labelRemainAmount + " :").append(String.format("%.2f", remain)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n");

            msg = sb.toString();

            CommonPrintReciept(CenterName, msg);


        }
    }


    public static void printProductInvoiceReceipt(Context context, String strType, String invoiceNo, String date, String customerId, String user_name, double cashDiscount, double otherCharges, double invoiceAmount, List<BeanPurchInvoiceProductItem> mList) {
        mContext = context;
        setPrintlabelName(mContext);
        List<Reciept_Item> printdataList = new ArrayList<Reciept_Item>();
        msg = "";
        Fat = "";
        SNF = "";
        QT = "";
        strAmount = "";
        totalMilk = 0;
        TotalAmount = 0;
        if (mSocket != null && mOutputStream != null && mInputStream != null) {

            StringBuilder sb = new StringBuilder();
            strType = toTitleCase(strType);
            sb.append("*****" + " " + strType).append(" Invoice *****").append("\n").append("\n");
            if (invoiceNo.length() > 0) {
                invoiceNo = "#" + invoiceNo + "  ";
            }
            sb.append(invoiceNo).append(date).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append(labelId + " ").append(customerId).append("  " + user_name).append("\n");

            sb.append(strDeshDotLine).append("\n");
            StringBuilder strBody = new StringBuilder();
            strBody.append(labelSr + "  ").append(labelName + "  ").append(labelQty).append("   " + labelAmount).append("\n").append("\n");

            for (int i = 0; i < mList.size(); i++) {
                String prodName = "";
                prodName = mList.get(i).getName();
                QT = String.valueOf(mList.get(i).getQty());
                strAmount = String.valueOf(mList.get(i).getGross());
                if (prodName.length() > 5) {
                    prodName = prodName.substring(0, 4);
                }

                strBody.append(i + 1 + " ").append(prodName + " ").append(QT + " ").append(strAmount).append("\n");
                printdataList.add(new Reciept_Item(" ", i + 1 + "", " " + prodName, "", QT, strAmount, "", ""));

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TableStringBuilder<Reciept_Item> t = new TableStringBuilder<Reciept_Item>();
                t.addColumn(labelSr + " ", Reciept_Item::getDate);
                t.addColumn(" " + labelName + "  ", Reciept_Item::getFAT);
                t.addColumn(" " + labelQty + " ", Reciept_Item::getQT);
                t.addColumn(" " + labelAmount, Reciept_Item::getAmount);

                String strTabledata = t.createString(printdataList);
                sb.append(strTabledata);
            } else {
                sb.append(strBody.toString());
            }

            sb.append(strDeshDotLine).append("\n");
            sb.append(labelCashDiscount + ": ").append(String.format("%.2f", cashDiscount)).append("\n");
            sb.append(labelOtherCharges + ": ").append(String.format("%.2f", otherCharges)).append("\n");
            sb.append(labelTotalAmount + ": ").append(String.format("%.2f", invoiceAmount)).append("\n");
            sb.append(strDeshDotLine).append("\n");
            sb.append("WWW.MERIDAIRY.IN").append("\n").append("\n");

            msg = sb.toString();
            CommonPrintReciept(CenterName, msg);
        }
    }


    public static void CommonPrintReciept(String centerName, String msgBody) {
       /* SessionManager sessionManager = new SessionManager(mContext);
        String strPrintCommand="";
        if (sessionManager.getBooleanValue(KEY_MachineAuto)){
            centerName="PRINT   "+centerName;
            msgBody=msgBody+"\n\r#";
        }
*/
        Log.d("TAG", "CommonPrintReceipt: " + msgBody);

        //new ConnectBT().execute();

//        if (isBluetoothHeadsetConnected()) {
//            if (mDevice == null || mSocket == null || mOutputStream == null) {
//                dialogBluetooth(mContext);
//            }
//        }



        if (strPrintMultiLang.equalsIgnoreCase(NO)) {
            if (isBluetoothHeadsetConnected()) {
                if (mSocket != null && mOutputStream != null && mInputStream != null) {
                    try {
                        mOutputStream = mSocket.getOutputStream();
                        mOutputStream.write(alignCenter);
                        mOutputStream.write(boldfontBt);
                        mOutputStream.write(centerName.getBytes(), 0, centerName.getBytes().length);
                        LineFeed();
                        mOutputStream.write(formatLeft);
                        mOutputStream.write(msgBody.getBytes(), 0, msgBody.getBytes().length);
                        LineFeed();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    resetConnection();
                    dialogBluetooth(mContext);
                }
            }
        } else {
            Bitmap bitmap = null;
            if (strLangType.equalsIgnoreCase("gu")) {
                msg = convertNumInGujrati(msg);
            }
            bitmap = createBitmapFromLayoutWithText(mContext, CenterName, msg);
            printBitmap(bitmap);
        }

    }

    private static void setPrintlabelName(Context mContext) {
        SessionManager sessionManager = new SessionManager(mContext);
        if (sessionManager.getValueSesion(SessionManager.Key_PrintAllLang).length() == 0) {
            sessionManager.setValueSession(SessionManager.Key_PrintAllLang, NO);
        }

        strLangType = nullCheckFunction(sessionManager.getValueSesion(SessionLang));
        strPrintMultiLang = nullCheckFunction(sessionManager.getValueSesion(SessionManager.Key_PrintAllLang));
        CenterName = nullCheckFunction(sessionManager.getValueSesion(KEY_center_name));
        if (CenterName.length() == 0) {
            CenterName = "Meri Dairy";
        }
        CenterName = CenterName.toUpperCase();

        String multiLangDotLine = "--------------------------------------------";
        String englishLangLangDotLine = "-------------------------------";
        if (strPrintMultiLang.equalsIgnoreCase(NO)) {
            strDeshDotLine = englishLangLangDotLine;
        } else {
            strDeshDotLine = multiLangDotLine;
        }

        if (strPrintMultiLang.equalsIgnoreCase(NO)) {
            labelId = "ID";
            labelSr = "Sr.";
            labelDate = "DATE";
            labelMorning = "MORNING";
            labelEvening = "EVENING";
            labelShift = "SHIFT";
            labelType = "TYPE";
            labelFat = "FAT";
            labelSNF = "SNF";
            labelCenterName = "MERY DAIRY";
            labelMorning = "MORNING";
            labelEvening = "EVENING";
            labelSession = "";
            labelCow = "COW";
            labelBuffalo = "Buffalo";
            labelName = "NAME";
            labelWeight = "WEIGHT";
            labelQty = "Qty.";
            labelAmount = "AMOUNT";
            labelCLR = "CLR";
            lableBonus = "Bonus";
            labelRate = "RATE";
            labelTotalRs = "TOTAL RS";
            weekTotal = "WEEK TOTAL RS";
            labelCashDiscount = "Cash Discount";
            labelOtherCharges = "Other Charges";
            labelTotalWeight = "TOTAL WEIGHT";
            labelTotalAmount = "TOTAL AMOUNT";
            labelLtr = "Ltr";
            labelPerLtr = "Per Ltr";
            labelTitle = "Title";
            labelCredit = "Credit";
            labelDebit = "Debit";
            labelTotalCredit = "Total Credit";
            labelTotalDebit = "Total Debit";
            labelRemainAmount = "Remain Amount";
        } else {
            labelId = mContext.getString(R.string.ID);
            labelSr = mContext.getString(R.string.Sr);
            labelDate = mContext.getString(R.string.Date).toUpperCase();
            labelMorning = mContext.getString(R.string.MORNING);
            labelEvening = mContext.getString(R.string.Evening);
            labelType = mContext.getString(R.string.strType);
            labelFat = mContext.getString(R.string.Fat);
            labelCow = mContext.getString(R.string.Cow);
            labelBuffalo = mContext.getString(R.string.Buff);
            labelShift = mContext.getString(R.string.Shift);

            labelSNF = mContext.getString(R.string.SNF);
            labelCLR = mContext.getString(R.string.CLR);
            lableBonus = mContext.getString(R.string.Bonus);
            labelRate = mContext.getString(R.string.Rate);
            labelTotalRs = mContext.getString(R.string.Total_Rs);
            weekTotal = mContext.getString(R.string.Week_Total_Rs);
            labelQty = mContext.getString(R.string.Quantity);
            labelSession = mContext.getString(R.string.MORNING);
            labelName = mContext.getString(R.string.Name);
            labelMobileNo = mContext.getString(R.string.Phone_Number);
            labelCustomer = mContext.getString(R.string.Customer);
            labelSeller = mContext.getString(R.string.Seller);
            labelBuyer = mContext.getString(R.string.Buyer);
            labelWeight = mContext.getString(R.string.Weight);
            labelAmount = mContext.getString(R.string.Amount);
            labelCashDiscount = mContext.getString(R.string.cashDiscount);
            labelOtherCharges = mContext.getString(R.string.otherCharges);
            labelTotalWeight = mContext.getString(R.string.Total_Weight);
            labelTotalAmount = mContext.getString(R.string.Total_Amount);
            labelLtr = mContext.getString(R.string.Ltr);
            labelPerLtr = mContext.getString(R.string.Rs_Per_ltr);
            labelTitle = mContext.getString(R.string.TITLE);
            labelCredit = mContext.getString(R.string.Cradit);
            labelDebit = mContext.getString(R.string.DEBIT);
            labelTotalCredit = mContext.getString(R.string.Total_Credit);
            labelTotalDebit = mContext.getString(R.string.Total_Debit);
            labelRemainAmount = mContext.getString(R.string.REMAIN_AMOUNT);
        }
        if (strSession.equalsIgnoreCase("morning") || strSession.equalsIgnoreCase("m")) {
            if (strPrintMultiLang.equalsIgnoreCase(NO)) {
                strShift = "MORNING";//"MORNING";
            } else {
                strShift = mContext.getString(R.string.MORNING);//"MORNING";
            }

        } else if (strSession.equalsIgnoreCase("evening") || strSession.equalsIgnoreCase("e")) {
            if (strPrintMultiLang.equalsIgnoreCase(NO)) {
                strShift = "EVENING";//"Evening";
            } else {
                strShift = mContext.getString(R.string.Evening);//"Evening";
            }
        }
    }


    private static boolean printBitmap(Bitmap bitmap) {

        if (mSocket != null) {
            if (mSocket.isConnected() && mOutputStream != null && mInputStream != null) {

                bitmap = BitmapUtil.decodeSampledBitmapFromBitmap(bitmap, 384);
                try {
                    byte[] pixels = BitmapUtil.convert(bitmap);
                    byte xH = (byte) ((((bitmap.getWidth() - 1) / 8) + 1) / AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
                    byte yL = (byte) (bitmap.getHeight() % AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
                    byte yH = (byte) (bitmap.getHeight() / AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
                    byte xL = (byte) ((((bitmap.getWidth() - 1) / 8) + 1) % AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
                    byte[] command = new byte[]{(byte) 29, (byte) 118, (byte) 48, (byte) 0, xL, xH, yL, yH};
                    mOutputStream.write(formatLeft);
                    mOutputStream.write(largefontBt);
                    mOutputStream.write(command);
                    mOutputStream.write(pixels);
                    LineFeed();

                    toast(mContext.getString(R.string.Printing) + mContext.getString(R.string.Success));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                toast(mContext.getString(R.string.Printing) + mContext.getString(R.string.failed));
            }
        }
        return true;
    }

    @SuppressLint("WrongConstant")
    static Bitmap createBitmapFromLayoutWithText(final Context mContext, String title, String msgBody) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bitmap bitmap = null;
        SessionManager sessionManager = new SessionManager(mContext);
        //Inflate the layout into a view and configure it the way you like
        RelativeLayout view = new RelativeLayout(mContext);
        mInflater.inflate(R.layout.layout_reciept_print, view, true);
        TextView tvReciptTitle, tvReciptBody;

        tvReciptTitle = view.findViewById(R.id.tvReciptTitle);
        tvReciptBody = view.findViewById(R.id.tvReciptBody);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvReciptBody.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);

        } else {
            tvReciptBody.setGravity(Gravity.CENTER);

        }

      /*  if (sessionManager.getIntValueSesion(KEY_ReceiptTitleFontSize) == 0) {
            sessionManager.setIntValueSession(KEY_ReceiptTitleFontSize,titleFontSize);
        } else {
            titleFontSize = sessionManager.getIntValueSesion(KEY_ReceiptTitleFontSize);
        }
        if (sessionManager.getIntValueSesion(KEY_ReceiptMessageFontSize) == 0) {
            sessionManager.setIntValueSession(KEY_ReceiptMessageFontSize,messageFontSize);
        } else {
            messageFontSize = sessionManager.getIntValueSesion(KEY_ReceiptMessageFontSize);
        }*/

        tvReciptTitle.setText(title);
        tvReciptBody.setText(msgBody);
        // tvReciptTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,titleFontSize);
        // tvReciptBody.setTextSize(TypedValue.COMPLEX_UNIT_SP,messageFontSize);
        tvReciptTitle.buildDrawingCache();
        tvReciptBody.buildDrawingCache();

        view.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        //Pre-measure the view so that height and width don't remain null.
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //Assign a size and position to the view and all of its descendants
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create the bitmap
        bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //Create a canvas with the specified bitmap to draw into
        Canvas c = new Canvas(bitmap);

        //Render this view (and all of its children) to the given Canvas
        view.draw(c);
        return bitmap;
    }

    static class ConnectBT extends AsyncTask<Void, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setCancelable(false);
            pDialog.setMessage(mContext.getString(R.string.bluetooth_connection) + "...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            findBT(mContext);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (mSocket != null) {
                if (mSocket.isConnected()) {
                    btn_connect.setTag("1");
                    btn_connect.setText("disconnect");
                    dialog.dismiss();
                } else {
                    toast(mContext.getString(R.string.Printer_Not_Connect));
                }
            } else {
                toast(mContext.getString(R.string.Printer_Not_Connect));
            }
        }
    }

}