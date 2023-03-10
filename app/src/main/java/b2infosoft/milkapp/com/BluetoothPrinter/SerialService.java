package b2infosoft.milkapp.com.BluetoothPrinter;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.useful.MyApp.TAG;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;

import b2infosoft.milkapp.com.Interface.SerialListener;

/**
 * create notification and queue serial data while activity is not in the foreground
 * use listener chain: SerialSocket -> SerialService -> UI fragment
 */
public class SerialService extends Service implements SerialListener {

    public class SerialBinder extends Binder {
        public SerialService getService() {
            return SerialService.this;
        }
    }
    DecimalFormat format = new DecimalFormat("#.#");

    private enum QueueType {Connect, ConnectError, Read, IoError}

    private static class QueueItem {
        QueueType type;
        String data;
        Exception e;

        QueueItem(QueueType type, String data, Exception e) {
            this.type = type;
            this.data = data;
            this.e = e;
        }
    }

    private final Handler mainLooper;
    private final IBinder binder;
    private final Queue<QueueItem> queue1, queue2;

    private SerialSocket socket;
    private SerialListener listener;
    private boolean connected;
    Context mContext;

    /**
     * Lifecylce
     */

    public SerialService() {
        mainLooper = new Handler(Looper.getMainLooper());
        binder = new SerialBinder();
        queue1 = new LinkedList<>();
        queue2 = new LinkedList<>();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void connect(SerialSocket socket) throws IOException {
        socket.connect(this);
        this.socket = socket;
        connected = true;
    }

    public  void reConnect(Context context) {
        try {

            SerialSocket socket = new SerialSocket(context, mDevice, mSocket);
            connect(socket);
            listener.onSerialConnect();

        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    public void disconnect() {
        connected = false; // ignore data,errors while disconnecting
        cancelNotification();
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }

    public void write(String command) throws IOException {
        if (socket != null) {
            socket.write(command);
        }else {

        }
    }

    public void getMachineData(Context context) throws IOException {
        Log.d(TAG, "getMachineData: ");
        if (socket != null) {
            Log.d(TAG, "getMachineData1: ");
            socket.getDataFromMachine();
        }else{
            disconnect();
            reConnect(context);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (socket != null) {
                        socket.getDataFromMachine();
                    }else{
                        reConnect(context);
                    }
                }
            },1200);
        }

    }

    public void sendCommand(String command) throws IOException {
        if (socket != null) {
            socket.sendCommand(command);
            mSocket=socket.socket;
        }
    }

    public void setTareMachine() throws IOException {
        if (socket != null) {
            socket.setTareMachine();

        }
    }

    public void attach(SerialListener listener) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread())
            throw new IllegalArgumentException("not in main thread");
        cancelNotification();
        // use synchronized() to prevent new items in queue2
        // new items will not be added to queue1 because mainLooper.post and attach() run in main thread
        synchronized (this) {
            this.listener = listener;
        }
        for (QueueItem item : queue1) {
            switch (item.type) {
                case Connect:
                    listener.onSerialConnect();
                    break;
                case ConnectError:
                    listener.onSerialConnectError(item.e);
                    break;
                case Read:
                    listener.onSerialRead(item.data);
                    break;
                case IoError:
                    listener.onSerialIoError(item.e);
                    break;
            }
        }
        for (QueueItem item : queue2) {
            switch (item.type) {
                case Connect:
                    listener.onSerialConnect();
                    break;
                case ConnectError:
                    listener.onSerialConnectError(item.e);
                    break;
                case Read:
                    listener.onSerialRead(item.data);
                    break;
                case IoError:
                    listener.onSerialIoError(item.e);
                    break;
            }
        }
        queue1.clear();
        queue2.clear();
    }

    public void detach() {
        if (connected)
            createNotification();
        // items already in event queue (posted before detach() to mainLooper) will end up in queue1
        // items occurring later, will be moved directly to queue2
        // detach() and mainLooper.post run in the main thread, so all items are caught
        listener = null;
    }

    private void createNotification() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(Constants.NOTIFICATION_CHANNEL, "Background service", NotificationManager.IMPORTANCE_LOW);
            nc.setShowBadge(false);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(nc);
        }
        Intent disconnectIntent = new Intent()
                .setAction(Constants.INTENT_ACTION_DISCONNECT);
        Intent restartIntent = new Intent()
                .setClassName(this, Constants.INTENT_CLASS_MAIN_ACTIVITY)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent disconnectPendingIntent = PendingIntent.getBroadcast(this, 1, disconnectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent restartPendingIntent = PendingIntent.getActivity(this, 1, restartIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(socket != null ? "Connected to "+socket.getName() : "Background Service")
                .setContentIntent(restartPendingIntent)
                .setOngoing(true)
                .addAction(new NotificationCompat.Action(R.drawable.ic_clear_white_24dp, "Disconnect", disconnectPendingIntent));
        // @drawable/ic_notification created with Android Studio -> New -> Image Asset using @color/colorPrimaryDark as background color
        // Android < API 21 does not support vectorDrawables in notifications, so both drawables used here, are created as .png instead of .xml
        Notification notification = builder.build();
        startForeground(Constants.NOTIFY_MANAGER_START_FOREGROUND_SERVICE, notification);*/
    }

    private void cancelNotification() {
        stopForeground(true);
    }

    /**
     * SerialListener
     */
    public void onSerialConnect() {
        if (connected) {
            synchronized (this) {
                if (listener != null) {
                    mainLooper.post(() -> {
                        if (listener != null) {
                            listener.onSerialConnect();
                        } else {
                            queue1.add(new QueueItem(QueueType.Connect, null, null));
                        }
                    });
                } else {
                    queue2.add(new QueueItem(QueueType.Connect, null, null));
                }
            }
        }
    }

    public void onSerialConnectError(Exception e) {
        if (connected) {
            synchronized (this) {
                if (listener != null) {
                    mainLooper.post(() -> {
                        if (listener != null) {
                            listener.onSerialConnectError(e);
                        } else {
                            queue1.add(new QueueItem(QueueType.ConnectError, null, e));
                            cancelNotification();
                            disconnect();
                        }
                    });
                } else {
                    queue2.add(new QueueItem(QueueType.ConnectError, null, e));
                    cancelNotification();
                    disconnect();
                }
            }
        }
    }

    public void onSerialRead(String data) {
        Log.d(TAG, "onSerialRead:asdfasdasdfasdfasdfasdvsdadvsvaf ");
        if (connected) {
            synchronized (this) {
                if (listener != null) {
                    mainLooper.post(() -> {
                        if (listener != null) {
                            listener.onSerialRead(data);
                            readMachineData(data);
                        } else {
                            queue1.add(new QueueItem(QueueType.Read, data, null));
                        }
                    });
                } else {
                    queue2.add(new QueueItem(QueueType.Read, data, null));
                }
            }
        }
    }

    public void onSerialIoError(Exception e) {
        if (connected) {
            synchronized (this) {
                if (listener != null) {
                    mainLooper.post(() -> {
                        if (listener != null) {
                            listener.onSerialIoError(e);
                        } else {
                            queue1.add(new QueueItem(QueueType.IoError, null, e));
                            cancelNotification();
                            disconnect();
                        }
                    });
                } else {
                    queue2.add(new QueueItem(QueueType.IoError, null, e));
                    cancelNotification();
                    disconnect();
                }
            }
        }
    }

    @Override
    public void onReceiveMachineData(String readMessage, String fat, String weight, String snf, String clr) {
        printLog("on Receive service", readMessage);
        listener.onReceiveMachineData(readMessage, fat, weight, snf, clr);
    }

    private void readMachineData(String readMessage) {
        //{"Weight":"  67.10","fat":"07.15","snf":"09.58","den":"35.22","water":"00.00"}
        readMessage = readMessage.replace(" ", "");
        readMessage = readMessage.replace("???", "");
        printLog("json", readMessage);

        try {
            JSONObject jsonObject = new JSONObject(readMessage.replace(" ", ""));
            String fat = "", weight = "", snf = "", clr = "";
            if (jsonObject.has("fat")) {
                fat = jsonObject.getString("fat").replace(" ", "");
                fat = format.format(Float.parseFloat(fat));
               /* float fatD = Math.round(Float.parseFloat(fat));
                fat = String.valueOf(fatD);*/

                printLog("fatstr", fat + "");

            }
            if (jsonObject.has("Weight")) {
                weight = jsonObject.getString("Weight").replace(" ", "");
            }
            if (jsonObject.has("snf")) {
                snf = jsonObject.getString("snf").replace(" ", "");
                snf = format.format(Float.parseFloat(snf));
               /* float snfD = Math.round(Float.parseFloat(snf));
                snf = String.valueOf(snfD);*/
                printLog("snfD", snf + "");
            }
            /*if (jsonObject.has("den")) {
                clr = jsonObject.getString("den");

            }*/
            if (jsonObject.has("clr")) {
                clr = jsonObject.getString("clr").replace(" ", "");

            }
            onReceiveMachineData(readMessage, fat, weight, snf, clr);
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(mContext, e.getMessage());
        }
    }

    public void showToast(Context context, String message) {
        /*((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }); */
       /* Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/

    }

}
