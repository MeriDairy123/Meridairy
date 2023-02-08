package b2infosoft.milkapp.com.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import b2infosoft.milkapp.com.Dairy.MainActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import in.arjsna.passcodeview.PassCodeView;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_skip_ad;
import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

/**
 * Created by u on 9/6/2017.
 */

public class MainFragment extends Fragment implements View.OnClickListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Context mContext;
    Button btnChangePin, changePin;
    SessionManager sessionManager;
    private String PASSCODE = "";
    private PassCodeView passCodeView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        if (!hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }

        passCodeView = mRootView.findViewById(R.id.pass_code_view);
        TextView promptView = mRootView.findViewById(R.id.promptview);
        changePin = mRootView.findViewById(R.id.changePin);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        btnChangePin = mRootView.findViewById(R.id.btnChangePin);
        passCodeView.setKeyTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        passCodeView.setEmptyDrawable(R.drawable.pin_empty_dot);
        passCodeView.setFilledDrawable(R.drawable.pin_full_dot);
        btnChangePin.setOnClickListener(this);
        if (sessionManager.getValueSesion(SessionManager.PinNumber).length() == 0) {
            changePin.setVisibility(View.GONE);
            btnChangePin.setText(getString(R.string.SET_PIN));

        } else {
            changePin.setText(getString(R.string.CHANGE_PIN));
            btnChangePin.setVisibility(View.GONE);
            PASSCODE = sessionManager.getValueSesion(SessionManager.PinNumber);
            Log.d("PASSCODE", PASSCODE);

            changePin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new FragmentChangePin())
                            .commit();
                }
            });
        }
        bindEvents();
        return mRootView;
    }

    private void bindEvents() {
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                if (text.length() == 4) {
                    if (PASSCODE != null) {
                        if (text.equals(PASSCODE)) {
                            Log.d("Passcode", text);
                            //  Intent intent = new Intent(getActivity(), DeshboardActivity.class);
                            // startActivity(intent);
                            if (isConnected() && sessionManager.getValueSesion(Key_skip_ad).equals("1")) {
                                UtilityMethod.goNextClass(mContext, Banner_activity.class);

                            } else {
                                UtilityMethod.goNextClass(mContext, MainActivity.class);

                            }
                        } else {
                            passCodeView.setError(true);

                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangePin:
                if (passCodeView.getPassCodeText().length() != 0) {
                    if (sessionManager.getValueSesion(SessionManager.PinNumber).length() == 0) {
                        sessionManager.setValueSession(SessionManager.PinNumber, passCodeView.getPassCodeText().toString());
                        UtilityMethod.showAlertBoxwithIntent(mContext, getString(R.string.PIN_Set_Successfull), MainActivity.class);
                    } else {
                        sessionManager.setValueSession(SessionManager.PinNumber, passCodeView.getPassCodeText().toString());
                        UtilityMethod.showAlertBoxwithIntent(mContext, getString(R.string.PIN_Updated_Successfull), MainActivity.class);

                    }
                } else {
                    passCodeView.setError(true);
                }
                break;
        }
    }
}
