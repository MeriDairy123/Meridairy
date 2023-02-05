package b2infosoft.milkapp.com.Dairy.Setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.getUserClrSetting;
import static b2infosoft.milkapp.com.appglobal.Constant.updateClrSettingAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBox;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class CLRSettingFragment extends Fragment {

    Context mContext;
    Button btnSubmit;
    boolean edit = false;
    String devideFac = "", multiplyFac = "", addFac = "";
    Toolbar toolbar;
    EditText ediDevideFac, ediMultiplyFac, ediAddFac;

    SessionManager sessionManager;
    Fragment fragment = null;

    View view;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crl_setting, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
        return view;
    }

    private void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.clrSetting));
        toolBarManage();
        ediDevideFac = view.findViewById(R.id.ediDevideFac);
        ediMultiplyFac = view.findViewById(R.id.ediMultiplyFac);
        ediAddFac = view.findViewById(R.id.ediAddFac);
        btnSubmit = view.findViewById(R.id.btnSubmit);


        setData();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit) {
                    devideFac = ediDevideFac.getText().toString().trim();
                    multiplyFac = ediMultiplyFac.getText().toString().trim();
                    addFac = ediAddFac.getText().toString().trim();


                    if (devideFac.length() != 0 || multiplyFac.length() != 0 || addFac.length() != 0) {
                        updateClrSetting();
                    } else {
                        showAlertBox(mContext, mContext.getString(R.string.Please_Enter_All_Field));
                    }
                } else {
                    edit = true;
                    setEditTextStatus(true, R.string.Submit);

                }
            }
        });


    }

    private void setData() {
        devideFac = nullCheckFunction(sessionManager.getValueSesion(SessionManager.Key_DevideFac));
        multiplyFac = nullCheckFunction(sessionManager.getValueSesion(SessionManager.Key_MultiFac));
        addFac = nullCheckFunction(sessionManager.getValueSesion(SessionManager.Key_AddFac));

        ediDevideFac.setText(devideFac);
        ediMultiplyFac.setText(multiplyFac);
        ediAddFac.setText(addFac);

        setEditTextStatus(false, R.string.Edit);
    }

    public void setEditTextStatus(boolean status, int id) {
        ediDevideFac.setEnabled(status);
        ediMultiplyFac.setEnabled(status);
        ediAddFac.setEnabled(status);
        if (status) {
            ediDevideFac.requestFocus();
        }
        btnSubmit.setText(mContext.getString(id));

    }

    private void updateClrSetting() {

        if (isNetworkAvaliable(mContext)) {
            devideFac = String.format("%.2f", Float.parseFloat(devideFac));
            multiplyFac = String.format("%.2f", Float.parseFloat(multiplyFac));
            addFac = String.format("%.2f", Float.parseFloat(addFac));

            @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            showToast(getActivity(), jsonObject.getString("user_status_message"));

                            sessionManager.setValueSession(SessionManager.Key_DevideFac, devideFac);
                            sessionManager.setValueSession(SessionManager.Key_MultiFac, multiplyFac);
                            sessionManager.setValueSession(SessionManager.Key_AddFac, addFac);
                            edit = false;
                            setEditTextStatus(false, R.string.Edit);
                        } else {
                            UtilityMethod.showAlertWithButton(getActivity(), jsonObject.getString("user_status_message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                    .addEncoded("devide_factor", devideFac)
                    .addEncoded("multiply_factor", multiplyFac)
                    .addEncoded("add_factor", addFac)
                    .build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(updateClrSettingAPI);
        } else {
            showToast(getActivity(), mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    public void toolBarManage() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.getString("from").equalsIgnoreCase("setting")) {
                toolbar.setVisibility(View.GONE);
            }
            toolbar.setNavigationIcon(R.drawable.back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();

                }
            });
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }
    }
    public static void geCLRSetting(Context mContext) {
        SessionManager session = new SessionManager(mContext);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        session.setValueSession(SessionManager.Key_DevideFac, jsonData.getString("devide_factor"));
                        session.setValueSession(SessionManager.Key_MultiFac, jsonData.getString("multiply_factor"));
                        session.setValueSession(SessionManager.Key_AddFac, jsonData.getString("add_factor"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", session.getValueSesion(SessionManager.KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(getUserClrSetting);


    }
}
