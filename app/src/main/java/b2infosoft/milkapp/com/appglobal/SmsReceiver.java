package b2infosoft.milkapp.com.appglobal;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import b2infosoft.milkapp.com.Interface.SmsListener;

import static b2infosoft.milkapp.com.appglobal.Constant.MeridairySuppportMob;

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        SmsMessage[] smsm = null;
        String sms_str = "", number = "";
        if (bundle != null) {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            smsm = new SmsMessage[pdus.length];
            System.out.println("smsm=====" + smsm.toString());
            for (int i = 0; i < smsm.length; i++) {
                smsm[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sms_str += "\r\nMessage: ";
                sms_str += smsm[i].getMessageBody();
                sms_str += "\r\n";

                if (sms_str.contains(MeridairySuppportMob)) {
                    number = sms_str.replaceAll(MeridairySuppportMob, "");
                    number = number.replaceAll("[^0-9]", "");
                    String Sender = smsm[i].getOriginatingAddress();
                    System.out.println("Sender==>>>" + Sender);
                    System.out.println("ReciveMessage==>>>" + sms_str);
                    mListener.messageReceived(number);
                }
            }
        }
    }
}
