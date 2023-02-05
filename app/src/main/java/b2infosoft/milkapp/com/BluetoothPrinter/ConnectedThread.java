package b2infosoft.milkapp.com.BluetoothPrinter;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.BLUETOOTH_SPP;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import b2infosoft.milkapp.com.Interface.BluetoothReceiveListener;

public class ConnectedThread extends Thread implements Runnable {
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    Handler bluetoothIn;
    Thread workerThread;

    private static final int MESSAGE_READ = 0;
    private static final int MESSAGE_WRITE = 1;
    private static final int CONNECTING = 2;
    private static final int CONNECTED = 3;
    private static final int NO_SOCKET_FOUND = 4;
    Context mContext;
    BluetoothReceiveListener listener;
    public BluetoothSocket socket;
    int delimiter = 10;
    public static final String Machine_DATA_TARE = "#TARE";
    public static final String Machine_DATA = "#DATA3";
    final static String newline_crlf = "\r\n";
    final static String newline_lf = "\n";
    public String printMessages = "";
    public String printCommand = "PRINT  ";
    private String location = "JAIPUR";
    private String DATE_TIME = "20-Sept-2021  12:15:02 PM";
    private String USER_NAME = "ID 27 Shoukin Jat";
    private String SHIFT = "SHIFT     : MORNING";
    private String FAT = "FAT %    : 9.3";
    private String WEIGHT = "WEIGHT     : 100.000 Ltr";
    private String RATE = "RATE     : 55.50 Per Ltr";
    private String TOTAL = "5550.00 /-";
    private String WEBSITE = "WWW.MERIDAIRY.IN";
    int readBufferPosition;
    int b = 0;
    boolean stopWorker = false;
    StringBuilder msgSb = new StringBuilder();

    public void printValue() {
        printMessages = printCommand + location +
                "..................." +
                DATE_TIME + USER_NAME +
                "....................." +
                SHIFT +
                FAT +
                WEIGHT +
                RATE +
                "......................" +
                TOTAL +
                "......................" +
                WEBSITE + "/r/n#";
        printLog("printMessages", printMessages);
    }

    //creation of the connect thread
    byte[] readBuffer = new byte[1024];

    public ConnectedThread(Context context, BluetoothSocket mSocket,
                           BluetoothReceiveListener listener) {
        mContext = context;

        this.listener = listener;
        this.socket = mSocket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        readBuffer = new byte[1024];


        try {
            //Create I/O streams for connection
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            beginListenForData();

        } catch (IOException e) {
            showToast(mContext, "error received data " + e.getMessage());
            printLog("error received data ", e.getMessage());
        }


    }


    @Override
    public void run() {

        connectSocket();
        byte[] buffer = new byte[256];
        int bytes;
        printLog("run method", buffer + "");
        System.out.println("run method==>>" + buffer + "");
        msgSb = new StringBuilder();
        int b = 0;


        // Keep looping to listen for received messages
        while (true) {
            try {
                int bytesAvailable = mmInStream.available();
                System.out.println("bytesAvailable Data Machine == " + bytesAvailable);
                bytes = socket.getInputStream().read(buffer);
                byte[] data = Arrays.copyOf(buffer, bytes);
                //read bytes from input buffer
                String msg = new String(data);
                if (msg.length() > 0) {
                    printLog("text", msg);
                    msg = msg.replace(newline_crlf, newline_lf);
                    msgSb.append(msg);
                }


                System.out.println("msgSb== " + msgSb);
                System.out.println("b == " + b);

                if (msg.contains("}")) {
                    String receivedData = msgSb.toString();
                    System.out.println("received Data Machine== " + receivedData);
                    printLog("receivedData", receivedData);
                    readMachineData(receivedData);
                    msgSb = new StringBuilder();
                    b = 0;

                } else {
                    b++;
                }

            } catch (IOException e) {
                showToast(mContext, e.getMessage());
                finishScreen();
                break;
            }
        }

    }

    void connectSocket() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            socket = mDevice.createRfcommSocketToServiceRecord(BLUETOOTH_SPP);
            socket.connect();
            if (listener != null) {
                //  listener.onSerialConnect();
            }

        } catch (Exception e) {
            if (listener != null)
                //  listener.onSerialConnectError(e);
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
            socket = null;
            return;
        }
    }


    void finishScreen() {
        ((Activity) mContext).onBackPressed();
    }

    public void sendCommand(String command) {


        try {
            Thread.sleep(1000);
            writeData(command);
        } catch (InterruptedException e) {
            showToast(mContext, e.getMessage());
            e.printStackTrace();
            System.out.println("error 111 == >>>>" + e.getStackTrace());
            finishScreen();
        }

    }


    public void showToast(Context context, String message) {
        /*((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }); */
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void getDataFromMachine() {
        try {
            Thread.sleep(1000);
            writeData(Machine_DATA);

        } catch (InterruptedException e) {
            e.printStackTrace();
            printLog("error ==>>>> ", e.getStackTrace().toString());
        }


    }

    public void setTareMachine() {
        writeData(Machine_DATA_TARE);
    }

    public void writeData(String command) {
        printLog("command===>>>", command);
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    byte[] msgBuffer = (command + newline_crlf).getBytes();
                    mmOutStream = socket.getOutputStream();
                    mmOutStream.write(msgBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(" Bluetooth socket" + "not connected");
            }
        } else {
            System.out.println("   socket" + "null");
        }

    }

    //write method
    public void write(String input) {
        printLog("write input ===>>>", input);
        input = input + newline_crlf;

        byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
        } catch (IOException e) {
            //if you cannot write, close the application
            showToast(mContext, "Connection Failure Check Machine");
            finishScreen();

        }
    }

    void beginListenForData() {
        try {
            mmOutStream = socket.getOutputStream();
            mmInStream = socket.getInputStream();
            stopWorker = false;
            byte[] readBuffer = new byte[1024];
            msgSb = new StringBuilder();

            printLog("msgSb init", msgSb.toString());
            //connectSocket();

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {


                            String receivedData = "";
                            if (socket != null) {
                                mmInStream = socket.getInputStream();
                                int bytesAvailable = mmInStream.available();
                                System.out.println("bytesAvailable Data Machine == " + bytesAvailable);
                                int length = mmInStream.read(readBuffer);
                                byte[] data = Arrays.copyOf(readBuffer, length);
                                String msg = new String(data);
                                printLog("begindata msg str ==", msg);
                                if (msg.length() > 0) {
                                    msg = msg.replace(newline_crlf, newline_lf);
                                    msgSb.append(msg);
                                }


                                System.out.println("msgSb== " + msgSb);
                                System.out.println("b == " + b);

                                if (b == delimiter) {
                                    receivedData = msgSb.toString();
                                    System.out.println("received Data Machine== " + receivedData);
                                    printLog("receivedData", receivedData);
                                    readMachineData(receivedData);

                                    msgSb = new StringBuilder();
                                    b = 0;

                                } else {
                                    b++;
                                }
                            }
                            /*if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        receivedData = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        System.out.println("received Data Machine == " + receivedData);
                                        showToast(mContext, "Machine Data      " + receivedData);
                                        readMachineData(receivedData);
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }*/

                        } catch (IOException ex) {
                            showToast(mContext, "error received data " + ex.getMessage());
                            stopWorker = true;
                            printLog("error receive data", ex.getMessage());
                        }
                    }
                }
            });

            workerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readMachineData(String readMessage) {
        //{"Weight":"  67.10","fat":"07.15","snf":"09.58","den":"35.22","water":"00.00"}

        try {
            JSONObject jsonObject = new JSONObject(readMessage);
            String fat = "", weight = "", snf = "", clr = "";
            if (jsonObject.has("fat")) {
                fat = jsonObject.getString("fat").replace(" ", "");
                float fatD = Math.round(Float.parseFloat(fat));
                fat = String.valueOf(fatD);
                printLog("fatD", fatD + "");
                printLog("fatstr", fat + "");

            }
            if (jsonObject.has("Weight")) {
                weight = jsonObject.getString("Weight").replace(" ", "");
            }
            if (jsonObject.has("snf")) {
                snf = jsonObject.getString("snf").replace(" ", "");
                float snfD = Math.round(Float.parseFloat(snf));
                snf = String.valueOf(snfD);
                printLog("snfD", snfD + "");
            }
            if (jsonObject.has("den")) {
                clr = jsonObject.getString("clr");

            }
            if (jsonObject.has("clr")) {
                clr = jsonObject.getString("clr").replace(" ", "");

            }
            listener.onReceiveMachineData(readMessage, fat, weight, snf, clr);
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(mContext, e.getMessage());
        }
    }
}
