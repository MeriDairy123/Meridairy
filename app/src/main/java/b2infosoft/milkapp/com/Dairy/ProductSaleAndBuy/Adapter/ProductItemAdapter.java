package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Adapter;

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

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanAddProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Microsoft on 24-Aug-17.
 */

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.MyViewHolder> {

    private static final int MENU_VIEW_ITEM = Menu.FIRST;
    private static final int MENU_EDIT_ITEM = Menu.FIRST + 1;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 2;
    public Context mContext;
    public ArrayList<BeanAddProductItem> mList = new ArrayList<>();
    SessionManager sessionManager;

    UpdateList clickInAdapter;
    int MENU_VIEW = 0;
    int MENU_EDIT = 1;
    int MENU_DELETE = 2;
    int itemPosition = 0;


    public ProductItemAdapter(Context mContext, ArrayList<BeanAddProductItem> transectionListPojoArrayList, UpdateList onClickInAdapter) {
        this.mContext = mContext;
        this.mList = transectionListPojoArrayList;
        sessionManager = new SessionManager(mContext);
        clickInAdapter = onClickInAdapter;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String weight = mContext.getString(R.string.Weight) + " : " + mList.get(position).getItem_weight();
        String price = mContext.getString(R.string.Rupee_symbol) + " " + mList.get(position).getPrice();
        String qty = mContext.getString(R.string.Quantity) + " " + mList.get(position).getInitial_quantity();

        holder.tvItem.setText("" + mList.get(position).getName());
        holder.tvProductWeight.setText(weight);
        holder.tvProductQty.setText(qty);
        holder.tvPrice.setText(price);


        String url = BaseImageUrl + mList.get(position).getImages();
        System.out.println("===url=img=====" + url);
        Glide.with(mContext).load(url)
                .error(R.drawable.app_icon)
                .into(holder.imgProduct);


        holder.imgMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemPosition = position;
                showPopup(view);
            }
        });


    }

    private void showPopup(View view) {
        @SuppressLint("ResourceType") ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_VIEW, MENU_VIEW_ITEM, 0, mContext.getString(R.string.MORE_DETAILS));
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 1, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 2, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_VIEW_ITEM:

                        clickInAdapter.onUpdateList(itemPosition, "details");
                        break;
                    case MENU_EDIT_ITEM:

                        clickInAdapter.onUpdateList(itemPosition, "edit");
                        break;

                    case MENU_DELETE_ITEM:
                        deleteItem();
                        break;

                    default:
                        break;

                }
                return false;

            }
        });

        popup.show();
    }

    private void deleteItem() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Delete_Entry))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                            @Override
                            public void handleResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String staus = jsonObject.getString("status");
                                    if (staus.equals("success")) {
                                        clickInAdapter.onUpdateList(itemPosition, "delete");
                                        showToast(mContext, mContext.getString(R.string.Product_Deleted_Successfully));

                                    } else {
                                        showToast(mContext, mContext.getString(R.string.Product_Deleting_Failed));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        RequestBody body = new FormEncodingBuilder()
                                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                                .addEncoded("product_id", mList.get(itemPosition).getId())
                                .addEncoded("category", mList.get(itemPosition).getItem_group())
                                .addEncoded("type", "delete")
                                .build();
                        serviceCaller.addRequestBody(body);
                        serviceCaller.execute(Constant.addUserProductAPI);
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
        public TextView tvItem, tvPrice, tvProductQty, tvProductWeight;
        ImageView imgMoreDetail, imgProduct;

        public MyViewHolder(View view) {
            super(view);
            tvItem = view.findViewById(R.id.tvItem);
            tvProductWeight = view.findViewById(R.id.tvProductWeight);
            tvProductQty = view.findViewById(R.id.tvProductQty);
            tvPrice = view.findViewById(R.id.tvPrice);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }

    }
}

