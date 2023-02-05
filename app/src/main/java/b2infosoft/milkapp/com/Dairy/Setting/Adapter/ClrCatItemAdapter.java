package b2infosoft.milkapp.com.Dairy.Setting.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanCatChartItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by B2infosoft on 15/10/2019.
 */

public class ClrCatItemAdapter extends RecyclerView.Adapter<ClrCatItemAdapter.MyViewHolder> {


    private static final int MENU_EDIT_ITEM = Menu.FIRST;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 1;

    public Context mContext;
    public ArrayList<BeanCatChartItem> mList;

    UpdateList refreshList;


    SessionManager sessionManager;
    int itemPosition = 0;

    int MENU_EDIT = 1;
    int MENU_DELETE = 2;
    String strId = "", strName = "", strCatName = "", strCatId = "";

    public ClrCatItemAdapter(Context mContext, ArrayList<BeanCatChartItem> clrCatItems, UpdateList refreshEntryList) {
        this.mContext = mContext;
        this.mList = clrCatItems;
        this.refreshList = refreshEntryList;
        sessionManager = new SessionManager(mContext);

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.clr_category_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        BeanCatChartItem album = mList.get(position);

        int srNo = position + 1;
        holder.setIsRecyclable(false);
        holder.tvId.setText(" " + srNo + ".");
        holder.tvName.setText(album.name);
        holder.tvCatName.setText(album.category_Name);

    }

    private void showPopup(View view, int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 0, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 1, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case MENU_EDIT_ITEM:
                        refreshList.onUpdateList(position, "edit");
                        break;
                    case MENU_DELETE_ITEM:
                        deleteItem(position);
                        break;

                    default:
                        break;

                }
                return false;

            }
        });

        popup.show();
    }

    private void deleteItem(int pos) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Delete_Entry))
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (isNetworkAvaliable(mContext)) {
                            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Delete) + "...", true) {
                                @Override
                                public void handleResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String staus = jsonObject.getString("status");
                                        if (staus.equalsIgnoreCase("success")) {
                                            showToast(mContext, jsonObject.getString("user_status_message"));

                                            mList.remove(pos);
                                            notifyItemRemoved(pos);

                                            notifyDataSetChanged();
                                            refreshList.onUpdateList(pos, "delete");

                                        } else {
                                            showToast(mContext, jsonObject.getString("user_status_message"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            RequestBody body = new FormEncodingBuilder()
                                    .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                                    .addEncoded("categorychart_id", strId)
                                    .build();
                            serviceCaller.addRequestBody(body);

                            serviceCaller.execute(Constant.deleteCategoryChartAPI);
                        }
                    }
                })
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName, tvId, tvCatName;

        View viewPayment;
        ImageView imgMoreDetail;


        public MyViewHolder(View view) {
            super(view);
            viewPayment = view.findViewById(R.id.viewPayment);
            tvId = view.findViewById(R.id.tvId);
            tvName = view.findViewById(R.id.tvName);
            tvCatName = view.findViewById(R.id.tvCategory);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            imgMoreDetail.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            itemPosition = getAdapterPosition();
            strId = mList.get(getAdapterPosition()).id;
            strName = mList.get(getAdapterPosition()).name;
            strCatId = mList.get(getAdapterPosition()).category_id;
            strCatName = mList.get(getAdapterPosition()).category_Name;

            switch (v.getId()) {
                case R.id.imgMoreDetail:

                    showPopup(v, itemPosition);
                    break;

                default:
                    break;
            }
        }
    }
}




