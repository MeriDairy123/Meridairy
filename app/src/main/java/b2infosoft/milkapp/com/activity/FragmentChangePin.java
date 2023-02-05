package b2infosoft.milkapp.com.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.MainActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;

import static b2infosoft.milkapp.com.ShareAds_Animal.Animal_AdsActivity.mainCategoryList;
import static b2infosoft.milkapp.com.appglobal.Constant.BuyerFirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Mobile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;

/**
 * Created by Microsoft on 11-Sep-17.
 */

public class FragmentChangePin extends Fragment implements View.OnClickListener {

    EditText etenterpin, etnewpin, etreenterpin;
    Button btnsubPin, btnforgotPin;
    SessionManager sessionManager;
    TextInputLayout et_enterurpin;
    View view;
    Context mContext;
    String pinNumber = "";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.frag_enterpin, container, false);
        mContext = getActivity();
        etenterpin = mRootView.findViewById(R.id.etenterpin);
        etnewpin = mRootView.findViewById(R.id.etnewpin);
        etreenterpin = mRootView.findViewById(R.id.etreenterpin);
        btnsubPin = mRootView.findViewById(R.id.btnsubPin);
        btnforgotPin = mRootView.findViewById(R.id.btnforgotPin);
        view = mRootView.findViewById(R.id.view);
        sessionManager = new SessionManager(getActivity());
        et_enterurpin = mRootView.findViewById(R.id.et_enterurpin);
        tempMobileNumber = "";
        BuyerFirstTime = "";
        mainCategoryList = new ArrayList<>();
        pinNumber = nullCheckFunction(sessionManager.getValueSesion(SessionManager.PinNumber));

        if (pinNumber.length() > 0) {
            et_enterurpin.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            etenterpin.setVisibility(View.VISIBLE);
            btnforgotPin.setVisibility(View.VISIBLE);
        } else {
            et_enterurpin.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            etenterpin.setVisibility(View.GONE);
            btnforgotPin.setVisibility(View.GONE);
        }

        btnsubPin.setOnClickListener(this);
        btnforgotPin.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnsubPin:
                if (pinNumber.length() > 0) {
                    if (!etenterpin.getText().toString().trim().equals("") && !etnewpin.getText().toString().trim().equals("") && !etreenterpin.getText().toString().trim().equals("")) {
                        if (etenterpin.getText().toString().equals(pinNumber)) {
                            if (etnewpin.getText().toString().equals(etreenterpin.getText().toString())) {
                                if (etnewpin.getText().toString().trim().length() == 4 && etreenterpin.getText().toString().trim().length() == 4) {
                                    sessionManager.setValueSession(SessionManager.PinNumber, etnewpin.getText().toString().trim());
                                    UtilityMethod.showAlertBoxwithIntent(mContext, getString(R.string.PIN_Changed_Successfully), MainActivity.class);
                                } else {
                                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.PIN_Must_be_4_Digit));

                                }

                            } else {
                                UtilityMethod.showAlertWithButton(mContext, getString(R.string.Your_Old_Password_is_Not_Matching));

                            }
                        } else {
                            UtilityMethod.showAlertWithButton(mContext, getString(R.string.Your_Current_PIN_is_not_match));

                        }

                    } else {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Field_Can_be_empty));
                    }
                } else {

                    if (etnewpin.getText().toString().trim().equals(etreenterpin.getText().toString().trim())) {

                        if (etnewpin.getText().toString().trim().length() == 4 && etreenterpin.getText().toString().trim().length() == 4) {
                            sessionManager.setValueSession(SessionManager.PinNumber, etnewpin.getText().toString().trim());
                            UtilityMethod.showAlertBoxwithIntent(mContext, getString(R.string.PIN_Saved_Successfully), MainActivity.class);
                        } else {
                            UtilityMethod.showAlertWithButton(mContext, getString(R.string.PIN_Must_be_4_Digit));

                        }

                    } else {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Your_Old_Password_is_Not_Matching));

                    }

                }
                break;
            case R.id.btnforgotPin:


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                alertDialogBuilder
                        .setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Logout))
                        .setCancelable(true)
                        .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                tempMobileNumber = sessionManager.getValueSesion(KEY_Mobile);
                                sessionManager.logoutUser();
                                UtilityMethod.goNextClass(mContext, LoginActivity.class);
                            }
                        })
                        .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                break;

        }
    }

}
