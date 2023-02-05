package b2infosoft.milkapp.com.Interface;

import android.content.Context;

import com.google.zxing.Result;

/**
 * Created by Choudhary on 12/1/2019.
 */

public interface OnQrScanResult {
    void onScanResult(Result result, Context mContext);

}
