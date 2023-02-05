package b2infosoft.milkapp.com.Dairy.Product.Adapter;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.OnClickInAdapter;
import b2infosoft.milkapp.com.Interface.RefreshProductsList;
import b2infosoft.milkapp.com.Model.ProductListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;

/**
 * Created by Microsoft on 24-Aug-17.
 */

public class ProductListAdapter2 extends RecyclerView.Adapter<ProductListAdapter2.MyViewHolder> {

    private static final int MENU_EDIT_ITEM = Menu.FIRST;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 1;
    public Context mContext;
    public ArrayList<ProductListPojo> mList = new ArrayList<>();
    SessionManager sessionManager;
    RefreshProductsList refreshProductsList;
    OnClickInAdapter clickInAdapter;
    int MENU_EDIT = 0;
    int MENU_DELETE = 1;


    public ProductListAdapter2(Context mContext, ArrayList<ProductListPojo> transectionListPojoArrayList, RefreshProductsList listener, OnClickInAdapter onClickInAdapter) {
        this.mContext = mContext;
        this.mList = transectionListPojoArrayList;
        sessionManager = new SessionManager(mContext);
        refreshProductsList = listener;
        clickInAdapter = onClickInAdapter;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_product_list_row2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String weight = mContext.getString(R.string.Weight) + " : " + mList.get(position).product_weight;
        String price = mContext.getString(R.string.Rupee_symbol) + " " + mList.get(position).product_price;

        holder.tvItem.setText("" + mList.get(position).product_name);
        holder.tvProductWeight.setText(weight);
        holder.tvPrice.setText(price);
        String url = BaseImageUrl + mList.get(position).product_image;
        Glide.with(mContext).load(url)
                .error(R.drawable.app_icon)
                .into(holder.imgProduct);


        holder.imgMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, position);
            }
        });


    }

    private void showPopup(View view, int position) {
        @SuppressLint("ResourceType") ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 0, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 1, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_EDIT_ITEM:
                        clickInAdapter = clickInAdapter;
                        System.out.println("====img=====product===" + mList.get(position).product_image);
                        clickInAdapter.onClickEditInAdapter(mList.get(position).id, mList.get(position).product_name, mList.get(position).product_price, mList.get(position).product_weight, "", "", "", "", "", "", mList.get(position).product_image);
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

    private void deleteItem(int position) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Delete_Entry))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        refreshProductsList = refreshProductsList;
                        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                            @Override
                            public void handleResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String staus = jsonObject.getString("status");
                                    if (staus.equals("success")) {
                                        mList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, mList.size());
                                        refreshProductsList.refreshProductList(mList);
                                        Toast.makeText(mContext, mContext.getString(R.string.Product_Deleted_Successfully), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, mContext.getString(R.string.Product_Deleting_Failed), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        RequestBody body = new FormEncodingBuilder()
                                .addEncoded("product_id", mList.get(position).id).build();
                        caller.addRequestBody(body);
                        caller.execute(Constant.DeleteProduct);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvItem, tvPrice, tvTotalAmt, tvProductWeight;
        ImageView imgMoreDetail, imgProduct;


        public MyViewHolder(View view) {
            super(view);
            tvItem = view.findViewById(R.id.tvItem);
            tvProductWeight = view.findViewById(R.id.tvProductWeight);
            tvPrice = view.findViewById(R.id.tvPrice);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }

    }
}

