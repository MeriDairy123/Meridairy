package b2infosoft.milkapp.com.BluetoothPrinter;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.BLUETOOTH_SPP;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.useful.MyApp.TAG;
import static b2infosoft.milkapp.com.useful.UtilityMethod.INTENT_ACTION_DISCONNECT;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.firebase.client.authentication.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;

import b2infosoft.milkapp.com.Interface.BluetoothReceiveListener;
import b2infosoft.milkapp.com.Interface.SerialListener;

public class SerialSocket implements Runnable {

    public InputStream mmInStream;
    public OutputStream mmOutStream;
    private final Context mContext;
    SerialListener listener;
    BluetoothDevice device;
    public BluetoothSocket socket;
    public boolean connected;
    public static final String Machine_DATA_TARE = "#TARE";
    public static final String Machine_DATA = "#DATA3";
    final static String newline_crlf = "\r\n";
    final static String newline_lf = "\n";
    private static boolean pendingNewline = false;
    StringBuilder msgSb = new StringBuilder();
    private final BroadcastReceiver disconnectBroadcastReceiver;
    boolean isStartRead = false;

    // private static UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //public static final UUID BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public SerialSocket(Context context, BluetoothDevice device, BluetoothSocket mSocket) {

        this.mContext = context;
        this.socket = mSocket;
        this.device = device;

        disconnectBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (listener != null)
                    listener.onSerialIoError(new IOException("background disconnect"));
                disconnect(); // disconnect now, else would be queued until UI re-attached
            }
        };
    }

    public String getName() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "TODO";
        }
        return device.getName() != null ? device.getName() : device.getAddress();
    }


    /**
     * connect-success and most connect-errors are returned asynchronously to listener
     */

    void connect(SerialListener listener) throws IOException {
        this.listener = listener;
        mContext.registerReceiver(disconnectBroadcastReceiver, new IntentFilter(INTENT_ACTION_DISCONNECT));
        Executors.newSingleThreadExecutor().submit(this);
    }


    public void sendCommand(String command) {

        try {
            Thread.sleep(1000);
            write(command);

        } catch (InterruptedException e) {
            showToast(mContext, e.getMessage());
            e.printStackTrace();
            System.out.println("error 111 == sendCommand>>>>" + e.getStackTrace());
            finishScreen();
        }

    }

    void finishScreen() {
        ((Activity) mContext).onBackPressed();
    }

    public void getDataFromMachine() {
        try {
            if (socket.isConnected()) {
                Log.d(TAG, "getDataFromMachine2: ");
                Thread.sleep(100);
                write(Machine_DATA);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            printLog("error 45454445===>>>>>", e.getStackTrace().toString());
        }

    }

    public void setTareMachine() {

        write(Machine_DATA_TARE);
        msgSb = new StringBuilder();

//        try {
//           // mmOutStream.flush();
//            mmOutStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    //write method
    public void write(String input) {
        Log.d(TAG, "getDataFromMachine3: " + input);
        //msgSb=new StringBuilder();

        try {
            if (socket != null) {
                if (connected) {
                    msgSb = new StringBuilder();
                    byte[] msgBuffer = (input + newline_crlf).getBytes();           //converts entered String into bytes
                    mmOutStream = socket.getOutputStream();
                    mmOutStream.write(msgBuffer);
                    BluetoothClass.mOutputStream = mmOutStream;
                    Log.d(TAG, "write463235444: " + msgBuffer.toString());
                } else {
                    printLog("error socket45554 ==>>>> ", "not connected");

                }

            } else {
                printLog("error socket is56454544 ==>>>> ", "null");
            }
            //write bytes over BT connection via outstream
        } catch (IOException e) {
            //if you cannot write, close the application
            printLog("error write=454654646=>>>> ", e.getMessage());
            finishScreen();

        }
    }


    public void write(byte[] data) throws IOException {

        connected = socket.isConnected();

        if (connected) {
            mmOutStream = socket.getOutputStream();
            socket.getOutputStream().write(data);
        } else {
            throw new IOException("not connected");
        }

    }

    @Override
    public void run() {

        // connect & read
        try {
            printLog("socket ", "init");

            socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
            socket.connect();
            mSocket=socket;
            if(listener != null)
                listener.onSerialConnect();
        } catch (Exception e) {
            if(listener != null)
                listener.onSerialConnectError(e);
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
            return;
        }
        connected = true;
        try {
            byte[] buffer = new byte[1024];
            int len;
          //  msgSb = new StringBuilder();
            //noinspection InfiniteLoopStatement
            while (true) {
                len = socket.getInputStream().read(buffer);
                printLog("lensdfgasdgadfgadfbasdfgbasdfbasdfbasdfbfb",len+"");
                byte[] data = Arrays.copyOf(buffer, len);
                //msgSb = new StringBuilder();
                receive(data);
               // Thread.sleep(500);
                listener.onSerialRead(msgSb.toString());
               // listener.onReceiveMachineData("ee","6.5","2.5","5.4","9");
            }

        } catch (Exception e) {
            printLog("error ==>>>> ", e.getMessage());
            connected = false;


        }
    }
    public void receive(byte[] data) {

            String msg = new String(data);

        printLog("msg ==>>>> ", msg);
            if(  msg.length() > 0) {

                // special handling if CR and LF come in separate fragments

                /* msg = msg.replace( newline_crlf, newline_lf);
                msgSb.append(msg);
                if (msg.contains("}")){
                    String strData=msgSb.toString();
                    if (strData.startsWith("{")&&strData.endsWith("}")){
                        printLog("strData ==>>>> ", strData);
                        listener.onSerialRead(msgSb.toString());
                   // readMachineData(msgSb.toString());
                    }
                    msgSb=new StringBuilder();
                    isStartRead=false;
                }
*/

                // new machine
                msg = msg.replace( newline_crlf, newline_lf);
                if (pendingNewline && msg.charAt(0) == '\n') {
                    msg = msg.replace( newline_crlf, newline_lf);
                    msgSb.append(msg);
                    Log.d(TAG, "receive23r23424234: ");
                    listener.onSerialRead(msgSb.toString());
                    msgSb=new StringBuilder();
                }else {
                    msgSb.append(msg);
                }
                pendingNewline = msg.charAt(msg.length() - 1) == '\r';
            }


    }

    private void readMachineData(String readMessage) {
        //{"Weight":"  67.10","fat":"07.15","snf":"09.58","den":"35.22","water":"00.00"}

        try {
            JSONObject jsonObject = new JSONObject(readMessage.replace(" ",""));
            String fat = "", weight = "", snf = "", clr = "";
            if (jsonObject.has("fat")) {
                fat = jsonObject.getString("fat").replace(" ", "");
                float fatD=Math.round(Float.parseFloat(fat));
                fat= String.valueOf(fatD);
                printLog("fatD",fatD+"");
                printLog("fatstr",fat+"");

            }
            if (jsonObject.has("Weight")) {
                weight = jsonObject.getString("Weight").replace(" ", "");
            }
            if (jsonObject.has("snf")) {
                snf = jsonObject.getString("snf").replace(" ", "");
                float snfD=Math.round(Float.parseFloat(snf));
                snf= String.valueOf(snfD);
                printLog("snfD",snfD+"");
            }
            if (jsonObject.has("den")) {
                clr = jsonObject.getString("den");

            }
            if (jsonObject.has("clr")) {
                clr = jsonObject.getString("clr").replace(" ","");

            }
         //  listener.onReceiveMachineData(readMessage, fat, weight, snf, clr);
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(mContext, e.getMessage());
        }
    }

    void disconnect() {
        listener = null; // ignore remaining data and errors
        // connected = false; // run loop will reset connected
        if(socket != null) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
        try {
            mContext.unregisterReceiver(disconnectBroadcastReceiver);
        } catch (Exception ignored) {
        }
    }
}
