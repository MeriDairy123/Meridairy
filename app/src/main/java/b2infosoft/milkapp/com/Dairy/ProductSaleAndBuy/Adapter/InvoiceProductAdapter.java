package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Adapter;

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

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanPurchInvoiceProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

/**
 * Created by B2infosoft on 11/01/2020.
 */

public class InvoiceProductAdapter extends RecyclerView.Adapter<InvoiceProductAdapter.MyViewHolder> {


    private static final int MENU_EDIT_ITEM = Menu.FIRST;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 1;

    public Context mContext;
    public ArrayList<BeanPurchInvoiceProductItem> mList = new ArrayList<>();

    UpdateList refreshList;

    SessionManager sessionManager;
    int itemPosition = 0;
    int MENU_EDIT = 1;
    int MENU_DELETE = 2;
    String strId = "", strName = "", strCatId = "";

    public InvoiceProductAdapter(Context mContext, ArrayList<BeanPurchInvoiceProductItem> item, UpdateList refreshEntryList) {
        this.mContext = mContext;
        this.mList = item;
        this.refreshList = refreshEntryList;
        sessionManager = new SessionManager(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.purchase_product_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        BeanPurchInvoiceProductItem album = mList.get(position);

        int srNo = position + 1;
        holder.setIsRecyclable(false);
        holder.tvId.setText(" " + srNo + ".");
        holder.tvName.setText(album.getName());
        holder.tvQty.setText("" + album.getQty());
        holder.tvTax.setText("" + album.getTax() + "%");
        holder.tvAmount.setText("" + album.getGross());

    }

    private void showPopup(View view, int pos) {
        System.out.println("==InvoiceProductAdapter=position===>>>" + pos);
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 0, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 1, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case MENU_EDIT_ITEM:
                        refreshList.onUpdateList(pos, "edit");
                        break;
                    case MENU_DELETE_ITEM:
                        deleteItem(pos);
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

                        refreshList.onUpdateList(pos, "delete");
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

        public TextView tvId, tvName, tvQty, tvTax, tvAmount;
        View viewPayment;
        ImageView imgMoreDetail;

        public MyViewHolder(View view) {
            super(view);
            viewPayment = view.findViewById(R.id.viewPayment);
            tvId = view.findViewById(R.id.tvSrNo);
            tvName = view.findViewById(R.id.tvName);
            tvQty = view.findViewById(R.id.tvQty);
            tvTax = view.findViewById(R.id.tvTax);
            tvAmount = view.findViewById(R.id.tvAmount);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            imgMoreDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemPosition = getAdapterPosition();
            strId = mList.get(getAdapterPosition()).getProduct_id();
            strCatId = mList.get(getAdapterPosition()).getCategory_id();
            strName = mList.get(getAdapterPosition()).getName();

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




