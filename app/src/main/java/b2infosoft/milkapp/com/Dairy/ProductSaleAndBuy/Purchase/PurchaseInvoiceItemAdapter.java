package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import b2infosoft.milkapp.com.Model.BeanPurchaseItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printProductInvoiceReceipt;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Choudhary on 15-Jan-20.
 */
public class PurchaseInvoiceItemAdapter extends RecyclerView.Adapter<PurchaseInvoiceItemAdapter.MyViewHolder> {


    private static final int MENU_VIEW_ITEM = Menu.FIRST;
    private static final int MENU_PRINT_ITEM = Menu.FIRST + 1;
    private static final int MENU_EDIT_ITEM = Menu.FIRST + 2;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 3;
    public Context mContext;
    public ArrayList<BeanPurchaseItem> mList = new ArrayList<>();
    int MENU_VIEW = 0;
    int MENU_PRINT = 1;
    int MENU_EDIT = 2;
    int MENU_DELETE = 3;
    SessionManager sessionManager;

    UpdateList clickInAdapter;


    String strTable = "purchase";

    public PurchaseInvoiceItemAdapter(Context mContext, ArrayList<BeanPurchaseItem> items, UpdateList onClickInAdapter) {
        this.mContext = mContext;
        this.mList = items;
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

        String invoNo = mContext.getString(R.string.invoice) + "  : #" + mList.get(position).getInvoice_number();
        String price = mContext.getString(R.string.Rupee_symbol) + " " + mList.get(position).getBalance_invo();
        String qty = mContext.getString(R.string.Date) + " : " + mList.get(position).getInvoice_date();

        holder.tvItem.setText("" + mList.get(position).getUser_name());
        holder.tvProductWeight.setText(invoNo);
        holder.tvProductQty.setText(qty);
        holder.tvPrice.setText(price);

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
        popup.getMenu().add(MENU_VIEW, MENU_VIEW_ITEM, 0, mContext.getString(R.string.MORE_DETAILS));
        popup.getMenu().add(MENU_PRINT, MENU_PRINT_ITEM, 1, mContext.getString(R.string.Print));
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 2, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 3, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_VIEW_ITEM:
                        clickInAdapter.onUpdateList(position, "view");
                        break;
                    case MENU_PRINT_ITEM:
                        if (isBluetoothHeadsetConnected()) {
                            if (mDevice == null || mSocket == null || mOutputStream == null) {
                                dialogBluetooth(mContext);
                            } else {

                            }
                            BeanPurchaseItem album = mList.get(position);
                            printProductInvoiceReceipt(mContext, strTable, album.getInvoice_number(),
                                    album.getInvoice_date(), album.getUnicCustomer(), album.getUser_name(),
                                    album.getCash_discount(), album.getOther_charg(), album.getNet_amount(), album.getInvoiceProductItems());


                        } else {
                            showAlertWithTitle(mContext.getString(R.string.PleaseON_Bluetooth_of_device), mContext);
                            enableBluetooth();
                            dialogBluetooth(mContext);
                        }


                        break;
                    case MENU_EDIT_ITEM:

                        clickInAdapter.onUpdateList(position, "edit");
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

    public boolean enableBluetooth() {
        try {
            BluetoothAdapter badapter = BluetoothAdapter.getDefaultAdapter();
            if (badapter != null) {
                badapter.enable();
                if (!badapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    ((Activity) mContext).startActivityForResult(enableBluetooth, 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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

                        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                            @Override
                            public void handleResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String staus = jsonObject.getString("status");
                                    if (staus.equals("success")) {
                                        clickInAdapter.onUpdateList(position, "delete");
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
                                .addEncoded("invoice_id", mList.get(position).getInvoice_id())
                                .addEncoded("customer_id", mList.get(position).getCustomer_id())
                                .addEncoded("type", "delete")
                                .addEncoded("table", strTable)
                                .build();
                        serviceCaller.addRequestBody(body);
                        serviceCaller.execute(Constant.addPurchaseInvoiceAPI);
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
            imgProduct.setVisibility(View.GONE);
        }

    }
}

