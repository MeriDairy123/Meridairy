package b2infosoft.milkapp.com.Shopping.Adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.ProductOnClickListner;
import b2infosoft.milkapp.com.Model.BeanCartItem;
import b2infosoft.milkapp.com.Model.BeanProductItem;
import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.decimalFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;


/**
 * Created by Choudhary on 11-02-19.
 */

public class ProductList_ItemAdapter extends RecyclerView.Adapter<ProductList_ItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanProductItem> mList;  // for loading main list
    public ArrayList<BeanCartItem> mCartList;
    ProductOnClickListner productOnClickListner;
    DatabaseHandler databaseHandler;
    int minQnt = 0;
    Integer productValue = 0;
    double totalPrice = 0;
    double price = 0;
    private ArrayList<BeanProductItem> arraylist = null;  // for loading  filter data


    public ProductList_ItemAdapter(Context mContext, ArrayList<BeanProductItem> mlist, ProductOnClickListner listner) {
        this.mContext = mContext;
        this.mList = mlist;
        this.productOnClickListner = listner;
        this.arraylist = new ArrayList<BeanProductItem>();
        this.arraylist.addAll(mlist);
        mCartList = new ArrayList<>();
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        mCartList = databaseHandler.getCartList();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shop_product_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final BeanProductItem productItem = mList.get(position);
        minQnt = 0;
        productValue = 0;
        price = 0;
        totalPrice = 0;
        String url = BaseImageUrl + productItem.thumb;
        Glide.with(mContext).load(url)
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader)).into(holder.imgProduct);
        holder.tvProductName.setText(" " + productItem.title);
        holder.tvProductDesc.setText(" " + Html.fromHtml(productItem.description));

        String strPrice = getColoredSpanned(mContext.getString(R.string.rsSymbol) + "  ", "#FF5E57") + decimalFormat(productItem.price);
        holder.tvPrice.setText(Html.fromHtml(strPrice));


        //...................... Cart value...........//
        if (!mCartList.isEmpty()) {
            for (int i = 0; i < mCartList.size(); i++) {
                if (productItem.id == mCartList.get(i).id) {
                    productValue = mCartList.get(i).itemQnt;
                }
            }

        } else {
            productValue = 0;
        }
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

                System.out.println("totalPrice====" + totalPrice);
                if (productValue == 0) {
                    holder.tvProductValue.setText("");
                    holder.imgMinus.setVisibility(View.GONE);
                } else {
                    holder.tvProductValue.setText(Integer.toString(productValue));
                    holder.imgMinus.setVisibility(View.VISIBLE);
                }

                productOnClickListner.onCartUpdate(position);

                totalPrice = 0;
            }
        });
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("===imgMinus===position====" + position);
                minQnt = productItem.min_qty;
                price = productItem.price;
                productValue = Integer.parseInt(holder.tvProductValue.getText().toString().trim());

                productValue = productValue - minQnt;

                if (productValue > 0) {
                    totalPrice = productValue * price;
                    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
                    databaseHandler.updateCart(productItem.id, productValue, totalPrice);
                    holder.tvProductValue.setText(Integer.toString(productValue));

                } else {
                    productValue = 0;

                    holder.tvProductValue.setText("");
                    holder.imgMinus.setVisibility(View.GONE);
                    databaseHandler.deleteCartItem(productItem.id);

                }

                System.out.println("===imgMinus===totalPrice====" + totalPrice);
                totalPrice = 0;
                productOnClickListner.onCartUpdate(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvProductName, tvProductDesc,
                tvPrice;
        public ImageView imgProduct, imgAdd, imgMinus, imgNext;
        public TextView tvProductValue;
        View layoutproduct, layoutAddButton;

        public MyViewHolder(View view) {
            super(view);
            layoutproduct = view.findViewById(R.id.layoutproduct);
            layoutAddButton = view.findViewById(R.id.layoutAddButton);
            imgNext = view.findViewById(R.id.imgNext);
            tvProductName = view.findViewById(R.id.tvProductName);
            imgProduct = view.findViewById(R.id.imgProduct);

            imgAdd = view.findViewById(R.id.imgAdd);
            imgMinus = view.findViewById(R.id.imgMinus);
            tvProductValue = view.findViewById(R.id.tvProductValue);
            tvProductDesc = view.findViewById(R.id.tvProductDesc);
            tvPrice = view.findViewById(R.id.tvPrice);

            imgProduct.setOnClickListener(this);
            layoutproduct.setOnClickListener(this);
            imgNext.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgProduct:
                    productOnClickListner.onProductAdapterClick(mList.get(getAdapterPosition()));
                    break;
                case R.id.imgNext:
                    productOnClickListner.onProductAdapterClick(mList.get(getAdapterPosition()));
                    break;
                case R.id.layoutproduct:
                    productOnClickListner.onProductAdapterClick(mList.get(getAdapterPosition()));
                    break;
            }
        }
    }

}
