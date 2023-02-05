package b2infosoft.milkapp.com.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.Model.BeanUserLoginAccount;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.Model.BeanUserLoginAccount.LoginWithMultipleAccount;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadPlantMilkEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadPlantSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.user_token;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Choudhary on 12-01-19.
 */

public class LoginUserAccountAdapter extends RecyclerView.Adapter<LoginUserAccountAdapter.MyViewHolder> implements milkEntryUploadListner {

    public Context mContext;
    public ArrayList<BeanUserLoginAccount> mList = new ArrayList<>();
    SessionManager sessionManager;
    String user_id = "";
    String user_group_id = "";
    String userToken = "";
    String mainUserToken = "";
    Dialog dialog = null;
    DatabaseHandler dbHandler;
    int position = 0;

    public LoginUserAccountAdapter(Context mContext, ArrayList<BeanUserLoginAccount> beanUserLoginAccounts) {
        this.mContext = mContext;
        this.mList = beanUserLoginAccounts;
        sessionManager = new SessionManager(mContext);
        mainUserToken = sessionManager.getValueSesion(user_token);
        dbHandler = DatabaseHandler.getDbHelper(mContext);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_account_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvName.setText(" " + mList.get(position).name);
        holder.tvMobile.setText("" + mList.get(position).phone_number);


        if (mList.get(position).user_tokens.equalsIgnoreCase(mainUserToken)) {
            holder.imgTick.setVisibility(View.VISIBLE);
        } else {
            holder.imgTick.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void openDialog() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_switch_user);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvLogin, tvDelete;

        tvLogin = dialog.findViewById(R.id.tvLogin);
        tvDelete = dialog.findViewById(R.id.tvDelete);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (userToken.equalsIgnoreCase(mainUserToken)) {
                    showToast(mContext, "This user is already login");
                } else {
                    if (isNetworkAvaliable(mContext)) {
                        uploadEntry();
                    } else {
                        showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
                    }
                }

            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (userToken.equalsIgnoreCase(mainUserToken)) {
                    showAlertWithButton(mContext, "Please first select login another account");

                } else {
                    dbHandler.deleteUserLoginId(user_id);
                    mList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        dialog.show();


    }

    @Override
    public void onMilkEntryUploaded(String from) {

    }

    private void uploadEntry() {

        ArrayList<CustomerEntryListPojo> CustomerEntryList;
        CustomerEntryList = dbHandler.getMilkBuyEntryRecordsOffline();
        boolean islogout = true;

        if (CustomerEntryList.size() != 0) {
            islogout = false;
            uploadEntryToServer(mContext, "NavigationBase", this);

        }
        if (dbHandler.getSaleMilkEntryOfflineEntry().size() != 0) {
            uploadSaleMilkEntryToServer(mContext, "NavigationBase", this);
            islogout = false;
        }
        if (dbHandler.getPlantSaleMilkEntryRecords().size() != 0) {
            uploadPlantSaleMilkEntryToServer(mContext, "NavigationBase");
            islogout = false;
        }

        if (dbHandler.getPlantEntryRecords().size() != 0) {
            islogout = false;
            uploadPlantMilkEntryToServer(mContext, "NavigationBase");
        }

        if (islogout) {
            sessionManager.setValueSession(user_token, userToken);
            LoginWithMultipleAccount(mContext, user_id, user_group_id);
        } else {
            showToast(mContext, mContext.getString(R.string.UploadingOnline));
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName, tvMobile;
        public ImageView imgTick;


        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvMobile = view.findViewById(R.id.tvMobile);
            imgTick = view.findViewById(R.id.imgTick);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            user_id = mList.get(getLayoutPosition()).user_id;
            user_group_id = mList.get(getLayoutPosition()).user_group_id;
            userToken = mList.get(getLayoutPosition()).user_tokens;
            position = getLayoutPosition();
            openDialog();

        }
    }
}
