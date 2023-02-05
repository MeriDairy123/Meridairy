package b2infosoft.milkapp.com.Dairy.SendNotification;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.notificationMessage;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SendSmsSetting;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.sendMessageTOAnotherApp;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlert;

/**
 * Created by u on 24-Apr-20.
 */

public class AddMessageNotification extends
        Fragment implements View.OnKeyListener, FragmentBackPressListener {

    Context mContext;

    Toolbar toolbar;
    EditText ediDesc;
    TextView tvDescWord;
    Button btnSubmit;
    SessionManager sessionManager;

    String screenFrom = "";
    View view;

    String updatedList = "", strMessage = "", strMsgSource = "";
    ArrayList<CustomerListPojo> beanSelectedUser = new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_mesage_notification, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        strMsgSource = sessionManager.getValueSesion(Key_SendSmsSetting);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        Gson gson = new Gson();
        String json = sessionManager.getValueSesion("album");
        beanSelectedUser = gson.fromJson(json, new TypeToken<List<CustomerListPojo>>() {
        }.getType());
        Bundle bundle = getArguments();
        updatedList = bundle.getString("userList");
        initView();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);

        return view;
    }


    private void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        ediDesc = view.findViewById(R.id.ediDesc);
        tvDescWord = view.findViewById(R.id.tvDescWord);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        toolbar.setTitle(mContext.getString(R.string.addNewMessage));
        screenFrom = FromWhere;

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnFragmentBackPressListener();
            }
        });

        ediDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvDescWord.setText("" + editable.length() + "/500");

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strMessage = ediDesc.getText().toString().trim();
                if (strMessage.length() == 0) {
                    showAlert(mContext.getString(R.string.message), mContext);
                } else {
                    if (strMsgSource.equalsIgnoreCase(ONE)) {
                        sendMeessageFromSim();
                    }
                    sendMessage();
                }

            }
        });

    }

    private void sendMeessageFromSim() {

        for (int i = 0; i < beanSelectedUser.size(); i++) {
            sendMessageTOAnotherApp(mContext, beanSelectedUser.get(i).getPhone_number(), strMessage);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                OnFragmentBackPressListener();
                return true;
        }

        return false;
    }

    @Override
    public void OnFragmentBackPressListener() {

        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    public void sendMessage() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Send_Message),
                true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        getActivity().onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .addEncoded("user_name[]", updatedList)
                .addEncoded("message", strMessage)
                .build();
        caller.addRequestBody(body);
        caller.execute(notificationMessage);
    }

}