package b2infosoft.milkapp.com.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.CartOnClickListner;
import b2infosoft.milkapp.com.Model.BeanCartItem;
import b2infosoft.milkapp.com.R;


/**
 * Created by Choudhary on 28-03-19.
 */

public class CartList_ItemAdapter extends RecyclerView.Adapter<CartList_ItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanCartItem> mList;

    CartOnClickListner cartOnClickListner;

    DatabaseHandler databaseHandler;
    int minQnt = 0;
    Integer productValue = 0;
    double totalPrice = 0;
    double price = 0;
    ProgressDialog progressDialog;
    BeanCartItem productItem;

    public CartList_ItemAdapter(Context mContext, ArrayList<BeanCartItem> mlist, CartOnClickListner listner) {
        this.mContext = mContext;
        this.mList = mlist;
        this.cartOnClickListner = listner;
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(mContext.getString(R.string.Please_Wait));
    }

    public void updateCartItemAdapter() {
        mList = databaseHandler.getCartList();
        notifyDataSetChanged();

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        productItem = mList.get(position);

        minQnt = 0;
        productValue = 0;
        price = 0;
        totalPrice = 0;
        productValue = productItem.itemQnt;

        holder.tvProductName.setText(" " + productItem.title);
        holder.tvAmount.setText(Double.toString(productItem.totalPrice));

        //...................... Cart value...........//
       /* if (!mCartList.isEmpty()) {
            for (int i = 0; i < mCartList.size(); i++) {
                if (productItem.id == mCartList.get(i).id) {
                    productValue = mCartList.get(i).itemQnt;
                }
            }

        } else {
            productValue = 0;
        }*/
        if (productValue == 0) {
            holder.tvProductValue.setText("");
            holder.imgMinus.setVisibility(View.GONE);
        } else {
            holder.tvProductValue.setText(Integer.toString(productValue));
            holder.imgMinus.setVisibility(View.VISIBLE);
        }

        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                productItem = mList.get(position);
                System.out.println("===imgAdd===position====" + position);
                minQnt = productItem.min_qty;
                price = productItem.price;
                if (holder.tvProductValue.getText().toString().length() == 0) {
                    holder.imgMinus.setVisibility(View.VISIBLE);

                    productValue = 0;
                    productValue = productValue + minQnt;
                    totalPrice = productValue * price;
                    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
                    databaseHandler.addCart(productItem.id, productItem.min_qty, productItem.qty, productValue,
                            productItem.mrp, productItem.price, productItem.gst, totalPrice, productItem.title,
                            productItem.type, productItem.image,
                            productItem.thumb, productItem.status,
                            productItem.description);
                } else {


                    productValue = Integer.parseInt(holder.tvProductValue.getText().toString().trim());
                    productValue = productValue + minQnt;
                    totalPrice = productValue * price;
                    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
                    databaseHandler.updateCart(productItem.id, productValue, totalPrice);
                }
                if (productValue == 0) {
                    holder.tvProductValue.setText("");
                    holder.imgMinus.setVisibility(View.GONE);
                } else {
                    holder.tvProductValue.setText(Integer.toString(productValue));
                    holder.imgMinus.setVisibility(View.VISIBLE);
                }
                holder.tvAmount.setText(Double.toString(totalPrice));
                cartOnClickListner.onCartUpdate(position);
                System.out.println("===imgAdd===totalPrice====" + totalPrice);
                totalPrice = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);
            }
        });
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                productItem = mList.get(position);
                System.out.println("===imgMinus===position====" + position);
                minQnt = productItem.min_qty;
                price = productItem.price;
                productValue = Integer.parseInt(holder.tvProductValue.getText().toString().trim());

                productValue = productValue - minQnt;

                if (productValue > 0) {
                    totalPrice = productValue * price;
                    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
                    holder.tvProductValue.setText(Integer.toString(productValue));
                    databaseHandler.updateCart(productItem.id, productValue, totalPrice);

                } else {
                    productValue = 0;
                    totalPrice = 0;
                    holder.tvProductValue.setText("");
                    holder.imgMinus.setVisibility(View.GONE);
                    databaseHandler.deleteCartItem(productItem.id);
                    cartOnClickListner.onCartUpdate(position);
                    mList.remove(position);

                    updateCartItemAdapter();
                }
                holder.tvAmount.setText(Double.toString(totalPrice));
                cartOnClickListner.onCartUpdate(position);
                totalPrice = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 500);

            }
        });


        if (position == mList.size() - 1) {
            cartOnClickListner.onCartUpdate(position);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvProductName,

        tvAmount;
        public ImageView imgAdd, imgMinus;
        public TextView tvProductValue;

        public MyViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tvProductName);
            imgAdd = view.findViewById(R.id.imgAdd);
            imgMinus = view.findViewById(R.id.imgMinus);
            tvProductValue = view.findViewById(R.id.tvProductValue);
            tvAmount = view.findViewById(R.id.tvAmount);
        }


    }

}
