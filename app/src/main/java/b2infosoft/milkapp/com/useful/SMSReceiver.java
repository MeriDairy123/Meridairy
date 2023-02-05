package b2infosoft.milkapp.com.useful;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import b2infosoft.milkapp.com.Interface.SmsListener;

import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class SMSReceiver extends BroadcastReceiver {


    private static SmsListener mListener;
    private static String oldMessage = "";
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public void setOTPListener(SmsListener otpListener) {
        this.mListener = otpListener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:

                    //This is the full message
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    System.out.println("message otp===>>>>" + message);
                    message = message.replace("9772196777", "");
                    message = message.replaceAll("[^\\d.]", "");

                    if (!oldMessage.equalsIgnoreCase(message)) {
                        oldMessage = message;
                        mListener.messageReceived(message);
                    }

                    break;
                case CommonStatusCodes.TIMEOUT:

                    break;

                case CommonStatusCodes.API_NOT_CONNECTED:
                    showToast(context, "API not connected");


                    break;

                case CommonStatusCodes.NETWORK_ERROR:
                    showToast(context, "API not connectedNetwork issue");


                    break;

                case CommonStatusCodes.ERROR:


                    break;

            }
        }
    }

    /**
     *
     */
    public interface OTPReceiveListener {

        void onOTPReceived(String otp);

        void onOTPTimeOut();

        void onOTPReceivedError(String error);
    }
}