package b2infosoft.milkapp.com.customer_app.customer_actvities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.zxing.WriterException;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.SplashActivity;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.Profile_Item_adapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanProfile_Item;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.appglobal.Constant.user_group_id;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Address;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_UserGroupID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.generateQrCode;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


public class SellerProfileActivity extends Activity {

    Context mContext;
    Toolbar toolbar;
    TextView tvDairyApp, tvLogoutfromOther;

    Profile_Item_adapter adapter;
    ArrayList<BeanProfile_Item> mList;
    RecyclerView recyclerView;
    ImageView imgQRCode, imgEditProf;
    DatabaseHandler db;
    SessionManager sessionManager;
    String allUserGroupId = "", user_id = "", userToken = "", strName = "", strFname = "", strMobile = "",
            strAdhar = "", strAddress = "", strDairy = "", strCenter = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_customer_profile);
        user_group_id = "3";
        mContext = SellerProfileActivity.this;
        sessionManager = new SessionManager(mContext);
        db = DatabaseHandler.getDbHelper(mContext);
        toolbar = findViewById(R.id.toolbar);
        tvDairyApp = findViewById(R.id.tvDairyApp);
        imgEditProf = findViewById(R.id.imgEditProf);
        imgQRCode = findViewById(R.id.imgQRCode);
        tvLogoutfromOther = findViewById(R.id.tvLogoutfromOther);
        recyclerView = findViewById(R.id.recyclerView);

        toolbar.setTitle(R.string.Profile);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        initRecyclerView();


        if (allUserGroupId.contains("2")) {
            tvDairyApp.setVisibility(View.VISIBLE);
        }
        tvDairyApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.setValueSession(Key_UserGroupID, "2");
                Intent intent = new Intent(mContext, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);
            }
        });
        imgEditProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_location_address = "";
                str_location_city = "";
                str_location_state = "";
                str_location_postCode = "";
                str_location_Latitude = "";
                str_location_Longitude = "";
                Intent i = new Intent(mContext, EditSellerProfileActivity.class);
                startActivity(i);
            }
        });

        if (sessionManager.getValueSesion(SessionManager.KEY_QRCode).equalsIgnoreCase("No")) {
            tvLogoutfromOther.setVisibility(View.VISIBLE);
        }
        tvLogoutfromOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutFromAnotherDevice();
            }
        });
        generateQr();
        initRecyclerView();

    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mList = new ArrayList<>();
        user_id = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KEY_UserID));
        allUserGroupId = sessionManager.getValueSesion(SessionManager.Key_all_user_group_id);
        userToken = sessionManager.getValueSesion(SessionManager.user_token);
        strName = sessionManager.getValueSesion(SessionManager.KEY_Name);
        strMobile = sessionManager.getValueSesion(SessionManager.KEY_Mobile);
        strFname = sessionManager.getValueSesion(SessionManager.KEY_FatherName);
        strDairy = sessionManager.getValueSesion(SessionManager.KEY_dairy_name);
        strCenter = sessionManager.getValueSesion(SessionManager.KEY_center_name);
        strAddress = sessionManager.getValueSesion(KEY_Address);
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Name) + " ", ": " + strName));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Phone_Number) + " ", ": " + strMobile));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Father_Name) + " ", ": " + strFname));
        if (strDairy.length() > 0) {
            mList.add(new BeanProfile_Item(mContext.getString(R.string.Dairy) + " ", ": " + strDairy));
        }
        if (strCenter.length() > 0) {
            mList.add(new BeanProfile_Item(mContext.getString(R.string.Center_Name) + " ", ": " + strCenter));
        }
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Address) + " ", ": " + strAddress));
        adapter = new Profile_Item_adapter(mContext, mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void generateQr() {
        JsonObject qrCode = new JsonObject();
        qrCode.addProperty("user_group_id", user_group_id);
        qrCode.addProperty("user_id", user_id);
        qrCode.addProperty("user_token", userToken);

        Bitmap Bitmap = null;
        try {
            Bitmap = generateQrCode(qrCode.toString());
            imgQRCode.setImageBitmap(Bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void LogoutFromAnotherDevice() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...Updating", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        String token = jsonObject.getString("user-token");
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
                .addEncoded("user_id", user_id)
                .addEncoded("user-token", userToken).build();
        caller.addRequestBody(body);
        caller.execute(Constant.logoutFromQRCodeAPI);


    }

}
