package b2infosoft.milkapp.com.Dairy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.Model.BeanUserLoginAccount;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.LoginViaActivity;
import b2infosoft.milkapp.com.adapter.LoginUserAccountAdapter;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerDairyListActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerDeshBoardActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerUserGroupActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.InstallAPK;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Model.BeanUserLoginAccount.LoginWithMultipleAccount;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadPlantMilkEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadPlantSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.appglobal.Constant.AppUpdateServerUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.DairyName;
import static b2infosoft.milkapp.com.appglobal.Constant.user_group_id;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Address;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Adhar;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_FatherName;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_center_name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_Expire_Date;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RemainingDay;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_UserGroupID;
import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dateFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.generateQrCode;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hideKeyboardForFocusedView;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Choudha on 05-OCt-18.
 */

public class DairyUserProfileActivity extends AppCompatActivity implements View.OnClickListener, milkEntryUploadListner {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public ArrayList<BeanUserLoginAccount> loginUserAccountList = new ArrayList<>();
    TextView tvCustomerApp, tvDairyOwnweID, tvMobileNo, tvExpire, tvRemainingDay;
    EditText edName, edCenterName, edDairyName, edfathersname;
    Button btnEdit, btnAddnewAcount;
    LinearLayout btn_logoutOtherdevice;
    ImageView imgQRCode;
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title, tvUpdateApp;
    View layoutAccountlist;
    RecyclerView recyclerviewAccountlist;
    String allUserGroupId = "", qrcodeLogin = "", Name = "", CollectionCenterName = "", Dairy = "", FatherName = "", AdharNumber = "", StreetAddress;
    /*  DatabaseHandler databaseHandler;*/
    SessionManager sessionManager;
    LoginUserAccountAdapter loginUserAccountAdapter;
    DatabaseHandler dbHandler;
    String userExpireDate = "";
    int RemainingDay = 0;
    InstallAPK downloadAndInstall;
    View view;

    public static void onScanResult(Result result, Context mContext) {
        try {
            String user_id = "", user_tokens = "", user_group_id = "";
            String scanOutput = result.getText();
            JSONObject obj = new JSONObject(scanOutput);

            user_group_id = obj.getString("user_group_id");
            user_id = obj.getString("user_id");
            user_tokens = obj.getString("user_token");
            if (isConnected()) {
                LoginWithMultipleAccount(mContext, user_id, user_group_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_owner_profile);
        mContext = DairyUserProfileActivity.this;
        sessionManager = new SessionManager(mContext);
        dbHandler = DatabaseHandler.getDbHelper(mContext);
        allUserGroupId = sessionManager.getValueSesion(SessionManager.Key_all_user_group_id);
        qrcodeLogin = sessionManager.getValueSesion(SessionManager.KEY_QRCode);
        loginUserAccountList = new ArrayList<>();
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        initView();

    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        tvUpdateApp = toolbar.findViewById(R.id.tvUpdateApp);
        tvUpdateApp.setVisibility(View.VISIBLE);
        tvUpdateApp.setText(mContext.getString(R.string.installSmsApp));
        toolbar_title.setText(getString(R.string.Profile));
        imgQRCode = findViewById(R.id.imgQRCode);
        tvCustomerApp = findViewById(R.id.tvCustomerApp);
        tvDairyOwnweID = findViewById(R.id.tvCustomerID);
        edName = findViewById(R.id.edName);
        edCenterName = findViewById(R.id.edCenterName);
        edDairyName = findViewById(R.id.edDairyName);
        edfathersname = findViewById(R.id.edfathersname);
        tvMobileNo = findViewById(R.id.tvMobileNo);
        tvExpire = findViewById(R.id.tvExpire);
        tvRemainingDay = findViewById(R.id.tvRemainingDay);
        btnEdit = findViewById(R.id.btnEdit);
        btnAddnewAcount = findViewById(R.id.btnAddnewAcount);
        btn_logoutOtherdevice = findViewById(R.id.btn_logoutOtherdevice);
        layoutAccountlist = findViewById(R.id.layoutAccountlist);
        recyclerviewAccountlist = findViewById(R.id.recyclerviewAccountlist);
        viewDisable();
        tvDairyOwnweID.setText(sessionManager.getValueSesion(SessionManager.KEY_UserID));
        edName.setText(sessionManager.getValueSesion(SessionManager.KEY_Name));
        edDairyName.setText(sessionManager.getValueSesion(SessionManager.KEY_dairy_name));
        edCenterName.setText(sessionManager.getValueSesion(SessionManager.KEY_center_name));
        edfathersname.setText(sessionManager.getValueSesion(SessionManager.KEY_FatherName));
        tvMobileNo.setText(sessionManager.getValueSesion(SessionManager.KEY_Mobile));
        tvUpdateApp.setOnClickListener(this);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (qrcodeLogin != null && qrcodeLogin.equalsIgnoreCase("No")) {
            btn_logoutOtherdevice.setVisibility(View.VISIBLE);
        }
        if (allUserGroupId.contains("3") || allUserGroupId.contains("4")) {
            tvCustomerApp.setVisibility(View.VISIBLE);
        }
        tvCustomerApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allUserGroupId.contains("3") && allUserGroupId.contains("4")) {

                    moveDeshboard(mContext, CustomerUserGroupActivity.class);
                } else if (allUserGroupId.contains("3")) {
                    sessionManager.setValueSession(Key_UserGroupID, "3");
                    moveDeshboard(mContext, CustomerDeshBoardActivity.class);

                } else if (allUserGroupId.contains("4")) {
                    sessionManager.setValueSession(Key_UserGroupID, "4");
                    moveDeshboard(mContext, CustomerBuyerDairyListActivity.class);
                }
            }
        });
        btnAddnewAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewAccount();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEdit.getText().toString().equalsIgnoreCase(mContext.getString(R.string.Edit))) {
                    viewEnable();
                } else {
                    Name = edName.getText().toString();
                    CollectionCenterName = edCenterName.getText().toString();
                    Dairy = edDairyName.getText().toString().trim();
                    StreetAddress = sessionManager.getValueSesion(KEY_Address);
                    FatherName = edfathersname.getText().toString();
                    if (Name.equals("") && CollectionCenterName.isEmpty() && Dairy.isEmpty()
                            && FatherName.isEmpty()) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Field_Can_be_empty));
                    } else if (Name.isEmpty()) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Owner_Name));
                        edName.requestFocus();
                    } else if (CollectionCenterName.isEmpty()) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Collection_Center_Name));
                        edCenterName.requestFocus();
                    } else if (Dairy.isEmpty()) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Dairy_Name));
                        edDairyName.requestFocus();
                    } else if (FatherName.isEmpty()) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Father_Name));
                        edfathersname.requestFocus();
                    } else {
                        updateRegistrationDetail();
                    }
                }


            }
        });
        btn_logoutOtherdevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutFromAnotherDevice();
            }
        });

        user_group_id = nullCheckFunction(sessionManager.getValueSesion(SessionManager.Key_UserGroupID));
        JsonObject qrCode = new JsonObject();
        qrCode.addProperty("user_group_id", user_group_id);
        qrCode.addProperty("user_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
        qrCode.addProperty("user_token", sessionManager.getValueSesion(SessionManager.user_token));

        Bitmap lBitmap1 = null;
        try {
            lBitmap1 = generateQrCode(qrCode.toString());
            imgQRCode.setImageBitmap(lBitmap1);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        hideKeyboardForFocusedView((Activity) mContext);

        loginUserAccountList = new ArrayList<>();

        loginUserAccountList = dbHandler.getLoginUserAccList();
        System.out.println("loginUserAccountList===" + loginUserAccountList.size());

        if (loginUserAccountList.size() > 1) {
            layoutAccountlist.setVisibility(View.VISIBLE);
            initRecyclerView();
        } else {
            layoutAccountlist.setVisibility(View.GONE);
        }
        userExpireDate = nullCheckFunction(sessionManager.getValueSesion(Key_Expire_Date));

        RemainingDay = sessionManager.getIntValueSesion(Key_RemainingDay);
        if (userExpireDate.length() > 0) {
            userExpireDate = dateFormat(userExpireDate);
        }
        tvExpire.setText(userExpireDate);
        if (RemainingDay >= 0) {
            tvRemainingDay.setText(String.valueOf(RemainingDay));
        } else {
            tvRemainingDay.setText(mContext.getString(R.string.no));

        }

    }

    public void moveDeshboard(Context context, Class className) {

        Intent intent = new Intent(context, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }

    private void initRecyclerView() {
        loginUserAccountAdapter = new LoginUserAccountAdapter(mContext, loginUserAccountList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerviewAccountlist.setLayoutManager(mLayoutManager);
        recyclerviewAccountlist.setAdapter(loginUserAccountAdapter);
    }

    private void viewEnable() {

        edName.setEnabled(true);
        edCenterName.setEnabled(true);
        edDairyName.setEnabled(true);
        edfathersname.setEnabled(true);
        btnEdit.setText(R.string.Save);
    }

    private void viewDisable() {

        edName.setEnabled(false);
        edCenterName.setEnabled(false);
        edDairyName.setEnabled(false);
        edfathersname.setEnabled(false);
        btnEdit.setText(R.string.Edit);
    }

    private void LogoutFromAnotherDevice() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...Updating", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        sessionManager.setValueSession(SessionManager.user_token, jsonObject.getString("user-token"));
                        UtilityMethod.showAlertBox(mContext, getString(R.string.logout_from_another_device));
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Updating_Failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("user_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("user-token", sessionManager.getValueSesion(SessionManager.user_token))
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.logoutFromQRCodeAPI);

    }

    public void updateRegistrationDetail() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext,
                "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        showToast(mContext, jsonObject.getString("user_status_message"));
                        DairyName = Dairy;
                        sessionManager.setValueSession(KEY_Name, Name);
                        sessionManager.setValueSession(KEY_center_name, CollectionCenterName);
                        sessionManager.setValueSession(KEY_dairy_name, Dairy);
                        sessionManager.setValueSession(KEY_FatherName, FatherName);
                        sessionManager.setValueSession(KEY_Adhar, AdharNumber);
                        sessionManager.setValueSession(KEY_Address, StreetAddress);
                        hideKeyboardForFocusedView((Activity) mContext);
                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("id", sessionManager.getValueSesion(KEY_UserID))
                .addEncoded("name", Name)
                .addEncoded("center_name", CollectionCenterName)
                .addEncoded("dairy_name", Dairy)
                .addEncoded("father_name", FatherName)
                .addEncoded("address", StreetAddress)
                .addEncoded("adhar", AdharNumber)
                .build();
        caller.addRequestBody(body);

        caller.execute(Constant.updateDairyProfile);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvUpdateApp:

                if (!hasPermissions(mContext, PERMISSIONS)) {
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
                }
                downloadAndInstall = new InstallAPK();
                downloadAndInstall.setContext(mContext);
                downloadAndInstall.execute(AppUpdateServerUrl);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void AddNewAccount() {

        ArrayList<CustomerEntryListPojo> CustomerEntryList;
        CustomerEntryList = dbHandler.getMilkBuyEntryRecordsOffline();
        boolean isAddNew = true;

        if (CustomerEntryList.size() != 0) {
            isAddNew = false;
            uploadEntryToServer(mContext, "NavigationBase", this);

        }
        if (dbHandler.getSaleMilkEntryOfflineEntry().size() != 0) {
            uploadSaleMilkEntryToServer(mContext, "NavigationBase", this);
            isAddNew = false;
        }
        if (dbHandler.getPlantSaleMilkEntryRecords().size() != 0) {
            uploadPlantSaleMilkEntryToServer(mContext, "NavigationBase");
            isAddNew = false;
        }

        if (dbHandler.getPlantEntryRecords().size() != 0) {
            isAddNew = false;
            uploadPlantMilkEntryToServer(mContext, "NavigationBase");
        }

        if (isAddNew) {
            Intent intent = new Intent(mContext, LoginViaActivity.class);
            intent.putExtra("from", "proifle");
            startActivity(intent);
        } else {
            showToast(mContext, mContext.getString(R.string.UploadingOnline));
        }

    }

    @Override
    public void onMilkEntryUploaded(String from) {

    }
}