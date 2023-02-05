package b2infosoft.milkapp.com.Notification;

/**
 * Created by Choudhary on 17-Jan-19.
 */

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.text.Html;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import b2infosoft.milkapp.com.Dairy.MainActivity;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.DeliveryBoy.DeliveryBoyMainActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.SplashActivity;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerDeshBoardActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.Database.DatabaseHandler.getDbHelper;
import static b2infosoft.milkapp.com.Model.BeanDairySnfFatChart.updateDairySNF_FATChart;
import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_DESCRIPTION;
import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_NAME;
import static b2infosoft.milkapp.com.appglobal.Constant.OnChat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_NotifType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Notification;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KeyBuyFatType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KeyBuyMilkRateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_AutoFatStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuffMinFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyBuffFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyCowFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyMilkBonusRate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerMilkWeekStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfFAT;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfFont;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfRate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfSnf;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfclr;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_CowMaxFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleBuffaloFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleCowFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleFateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleMilkBonusRate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleRateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerMilkWeekStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfFAT;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfFont;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfRate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfSnf;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfclr;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_UserGroupID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getFloatValuFromInputText;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


public class MyFirebaseMsgService extends FirebaseMessagingService {

    public static String FIREBASE_REQ_ACCEPT = "firebase_notifction_meridairy";
    Map<String, String> data;
    DatabaseHandler db;
    String title = "", message = "", type = "", image = "";
    JSONObject json;
    int notif_count = 0;

    Context mContext;
    LocalBroadcastManager broadcaster;
    SessionManager sessionManager;
    String userGroupId = "";
    private String TAG = " Shoukin Jaat== FCMService==";
    private int numMessages = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {
            mContext = this;
            sessionManager = new SessionManager(this);
            db = getDbHelper(this);
            userGroupId = nullCheckFunction(sessionManager.getValueSesion(Key_UserGroupID));
            json = new JSONObject();
            if (isAppIsInBackground(mContext)) {
                System.out.println(TAG + "app background== " + true);
            }
            if (remoteMessage.getNotification() != null) {

                data = new HashMap<String, String>();
                title = remoteMessage.getNotification().getTitle();
                message = nullCheckFunction(remoteMessage.getNotification().getBody());
                if (OnChat.equals("") || OnChat.equals("false")) {
                    type = "message";
                }

                createNotification(message, title, type);
                System.out.println(TAG + "title== " + title);
                System.out.println(TAG + "message== " + message);
            } else if (remoteMessage.getData().size() > 0) {
                System.out.println(TAG + "web =Data= " + remoteMessage.getData());
                title = remoteMessage.getData().get("title");
                message = nullCheckFunction(remoteMessage.getData().get("description"));
                type = nullCheckFunction(remoteMessage.getData().get("type"));
                if (type.equals("web_buy_milk") ||
                        type.equals("web_sale_milk") || type.equals("buy_milk_delete_entry") ||
                        type.equals("sale_milk_delete_entry") || type.equals("banner")) {
                    if (type.equalsIgnoreCase("web_buy_milk")) {
                        JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                        int isEntry = db.getEntryId("milk_buy_entry", objectData.getString("id"));
                        if (isEntry == 0) {
                            db.addBothEntryFromFirebase("milk_buy_entry", objectData);
                        } else {
                            db.updateBothEntryFromWeb("milk_buy_entry", isEntry, objectData);
                        }
                    } else if (type.equalsIgnoreCase("web_sale_milk")) {
                        JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                        int isEntry = db.getEntryId("milk_sale_entry", objectData.getString("id"));
                        if (isEntry == 0) {
                            db.addBothEntryFromFirebase("milk_sale_entry", objectData);
                        } else {
                            db.updateBothEntryFromWeb("milk_sale_entry", isEntry, objectData);
                        }
                    } else if (type.equalsIgnoreCase("buy_milk_delete_entry")) {
                        db.deleteOnLineMilkEntry("buy", nullCheckFunction(remoteMessage.getData().get("data")));
                    } else if (type.equalsIgnoreCase("sale_milk_delete_entry")) {
                        db.deleteOnLineMilkEntry("sale", nullCheckFunction(remoteMessage.getData().get("data")));

                    }
                } else {
                    createNotification(message, title, type);
                }

                if (type.equalsIgnoreCase("buymilksetting")) {
                    JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                    sessionManager.setValueSession(KeyBuyMilkRateType, objectData.getString("entry_type"));
                    sessionManager.setValueSession(KeyBuyFatType, objectData.getString("fat_type_buy"));
                    sessionManager.setFloatValueSession(Key_BuyFatPrice, Float.parseFloat(objectData.getString("milk_price")));
                    sessionManager.setFloatValueSession(Key_BuyCowFatPrice, Float.parseFloat(objectData.getString("milk_price_cow")));
                    sessionManager.setFloatValueSession(Key_BuyBuffFatPrice, Float.parseFloat(objectData.getString("milk_price_buffalo")));
                } else if (type.equalsIgnoreCase("salemilksetting")) {
                    JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                    sessionManager.setValueSession(Key_SaleRateType, objectData.getString("entry_type"));
                    sessionManager.setValueSession(Key_SaleFateType, objectData.getString("fat_type_sale"));
                    sessionManager.setFloatValueSession(Key_SaleFatPrice, Float.parseFloat(objectData.getString("milk_price")));
                    sessionManager.setFloatValueSession(Key_SaleCowFatPrice, Float.parseFloat(objectData.getString("milk_price_sale_cow")));
                    sessionManager.setFloatValueSession(Key_SaleBuffaloFatPrice, Float.parseFloat(objectData.getString("milk_price_sale_buffalo")));
                } else if (type.equalsIgnoreCase("salebonus")) {
                    JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                    sessionManager.setFloatValueSession(Key_SaleMilkBonusRate, (float) objectData.getDouble("sale_milk_bonus"));
                } else if (type.equalsIgnoreCase("buy_chart")) {
                    updateDairySNF_FATChart(mContext, "buy", false);
                } else if (type.equalsIgnoreCase("sale_chart")) {
                    updateDairySNF_FATChart(mContext, "sale", false);
                } else if (type.equalsIgnoreCase("buybonus")) {
                    JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                    sessionManager.setFloatValueSession(Key_BuyMilkBonusRate, (float) objectData.getDouble("bonus"));
                } else if (type.equalsIgnoreCase("buyscreen")) {
                    JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                    sessionManager.setValueSession(Key_BuyMilkScreen, objectData.getString("screen_type"));
                } else if (type.equalsIgnoreCase("salescreen")) {
                    JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                    sessionManager.setValueSession(Key_SaleMilkScreen, objectData.getString("screen_type"));
                } else if (type.equalsIgnoreCase("auto_fat")) {
                    JSONObject objectData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                    sessionManager.setValueSession(Key_AutoFatStatus, objectData.getString("status"));
                    sessionManager.setFloatValueSession(Key_CowMaxFat, getFloatValuFromInputText(objectData.getString("cow")));
                    sessionManager.setFloatValueSession(Key_BuffMinFat, getFloatValuFromInputText(objectData.getString("buffalo")));
                } else if (type.equalsIgnoreCase("bhugtan_setting")) {
                    JSONObject jsonData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                    sessionManager.setIntValueSession(Key_SellerPdfSnf, jsonData.getInt("seller_snf"));
                    sessionManager.setIntValueSession(Key_SellerPdfFAT, jsonData.getInt("seller_fat"));
                    sessionManager.setIntValueSession(Key_SellerPdfclr, jsonData.getInt("seller_clr"));
                    sessionManager.setIntValueSession(Key_SellerPdfRate, jsonData.getInt("seller_rate"));
                    sessionManager.setValueSession(Key_SellerPdfFont, jsonData.getString("seller_font_size"));
                    sessionManager.setValueSession(Key_SellerMilkWeekStatus, jsonData.getString("seller_week_status"));

                    sessionManager.setIntValueSession(Key_BuyerPdfSnf, jsonData.getInt("buyer_snf"));
                    sessionManager.setIntValueSession(Key_BuyerPdfFAT, jsonData.getInt("buyer_fat"));
                    sessionManager.setIntValueSession(Key_BuyerPdfclr, jsonData.getInt("buyer_clr"));
                    sessionManager.setIntValueSession(Key_BuyerPdfRate, jsonData.getInt("buyer_rate"));
                    sessionManager.setValueSession(Key_BuyerPdfFont, jsonData.getString("buyer_font_size"));
                    sessionManager.setValueSession(Key_BuyerMilkWeekStatus, jsonData.getString("buyer_week_status"));
                }
                System.out.println(TAG + "web ====type======== " + type);
            }
        } catch (Exception e) {
            System.out.println(TAG + "===Exception: =error==" + e.getMessage());
        }
        broadcaster = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(FIREBASE_REQ_ACCEPT);
        broadcaster.sendBroadcast(intent);
    }

    private void createNotification(String messageBody, String title, String type) {
        int notificationID = new Random().nextInt(3000);
        System.out.println(TAG + "===Notification type=" + type);
        try {

            if (title == "") {
                title = mContext.getString(R.string.app_name);
            }
            Intent intent = new Intent(mContext, SplashActivity.class);
            userGroupId = sessionManager.getValueSesion(Key_UserGroupID);
            sessionManager.setValueSession(KEY_NotifType, type);
            if (type.equalsIgnoreCase("logout")) {
                sessionManager.logoutUser();
                db.deleteDatabase(this);
                sessionManager.setValueSession(KEY_NotifType, type);
            } else {
                db.addNotification(title, type, messageBody);
            }

            if (userGroupId.equals("2")) {
                intent = new Intent(mContext, MainActivity.class);
            }
            if (userGroupId.equals("3")) {
                intent = new Intent(mContext, CustomerDeshBoardActivity.class);
            } else if (userGroupId.equals("4")) {
                intent = new Intent(mContext, CustomerBuyerMainActivity.class);

            } else if (userGroupId.equals("5")) {
                intent = new Intent(mContext, DeliveryBoyMainActivity.class);

            }

            intent.putExtra("messageBody", messageBody);
            intent.putExtra("messageTitle", title);
            intent.putExtra("type", type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent resultIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(mContext,
                            mContext.getResources().getString(R.string.notification_channel_id))
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle(Html.fromHtml(title))
                            .setContentText(Html.fromHtml(messageBody))
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(resultIntent);
            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(notificationManager);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setColor(getResources().getColor(R.color.color_bg));
            }
            assert notificationManager != null;
            notificationManager.notify(notificationID, notificationBuilder.build());
            notificationCount();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void notificationCount() {
        notif_count = sessionManager.getIntValueSesion(KEY_Notification);
        ++notif_count;
        sessionManager.setIntValueSession(KEY_Notification, notif_count);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager) {

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(mContext.getString(R.string.notification_channel_id), FCMCHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(FCMCHANNEL_DESCRIPTION);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    @SuppressLint("NewApi")
    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = Objects.requireNonNull(am).getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = Objects.requireNonNull(am).getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (Objects.requireNonNull(componentInfo).getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }


        }
        return isInBackground;
    }
}