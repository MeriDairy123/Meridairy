package b2infosoft.milkapp.com.DeliveryBoy.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.LoginWithPasswordActivity;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.Profile_Item_adapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanProfile_Item;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;

import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Address;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Mobile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


public class fragmentDeliveryBoyProfile extends Fragment {

    Context mContext;

    View view;
    Profile_Item_adapter adapter;

    ArrayList<BeanProfile_Item> mList;

    RecyclerView recyclerView;

    TextView btn_logout;
    DatabaseHandler db;
    SessionManager sessionManager;
    String user_id = "", userToken = "", strName = "", strFname = "", strMobile = "", strAdhar = "", strAddress = "", strDairy = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_boy_profile, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        db = DatabaseHandler.getDbHelper(mContext);

        recyclerView = view.findViewById(R.id.recyclerView);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setVisibility(View.VISIBLE);


        mList = new ArrayList<>();

        user_id = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KEY_UserID));
        userToken = nullCheckFunction(sessionManager.getValueSesion(SessionManager.user_token));
        strName = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KEY_Name));
        strMobile = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KEY_Mobile));
        strFname = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KEY_FatherName));
        strDairy = sessionManager.getValueSesion(SessionManager.KEY_dairy_name);
        strAddress = nullCheckFunction(sessionManager.getValueSesion(KEY_Address));


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });

        //   generateQr();
        initRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mList = new ArrayList<>();
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Name) + " ", ": " + strName));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Phone_Number) + " ", ": " + strMobile));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Dairy) + " ", ": " + strDairy));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Address) + " ", ": " + strAddress));

        adapter = new Profile_Item_adapter(mContext, mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }


    public void Logout() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Logout))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                tempMobileNumber = sessionManager.getValueSesion(KEY_Mobile);
                                sessionManager.logoutUser();
                                UtilityMethod.goNextClass(mContext, LoginWithPasswordActivity.class);

                            }
                        })
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


}
