package b2infosoft.milkapp.com.useful;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import b2infosoft.milkapp.com.R;

public class  SMSDeliverReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.d("arg1==", arg1.getExtras().toString());

        switch (getResultCode()) {

            case Activity.RESULT_OK:
                Toast.makeText(context, R.string.Message_Sent_Successfully, Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(context).sendBroadcast(arg1);
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(context, "sms not sent", Toast.LENGTH_SHORT).show();

                LocalBroadcastManager.getInstance(context).sendBroadcast(arg1);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                break;
        }
        Intent smsIntent = new Intent("milkmessage");
        smsIntent.putExtra("milkmessage", arg1);
        LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);

    }
}
