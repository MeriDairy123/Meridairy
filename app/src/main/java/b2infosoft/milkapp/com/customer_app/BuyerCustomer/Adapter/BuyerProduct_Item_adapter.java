package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.Interface.BuyerProductOnClickListner;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanBuyerCartItem;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanCustomerProductItem;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;


/**
 * Created by Choudhary on 18/07/2019
 */

public class BuyerProduct_Item_adapter extends RecyclerView.Adapter<BuyerProduct_Item_adapter.MyViewHolder> {

    String productid = "";
    int productQnt = 0;
    Integer productValue = 0;
    Integer cartProductPosition = 0;
    double totalPrice = 0;
    double price = 0;
    DatabaseHandler databaseHandler;
    BuyerProductOnClickListner onClickListner;
    BeanCustomerProductItem album = null;
    private Context mContext;
    private List<BeanCustomerProductItem> albumList;
    private List<BeanBuyerCartItem> cartList;

    public BuyerProduct_Item_adapter(Context context, List<BeanCustomerProductItem> albumList, BuyerProductOnClickListner listner) {
        this.mContext = context;
        this.onClickListner = listner;
        this.albumList = albumList;
        //cartList=new ArrayList<>();
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        cartList = databaseHandler.getBuyerCartList();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_product_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        album = albumList.get(position);
        productid = "";
        productQnt = 0;
        productValue = 0;
        cartProductPosition = 0;
        totalPrice = 0;
        String productName = album.getProduct_name() + " - " + getColoredSpanned(album.getWeight(), "#40C4FF");

        holder.tvName.setText(Html.fromHtml(productName));
        String strprice = mContext.getString(R.string.Rupee_symbol) + " " + album.getPrice();
        holder.tvWeight.setText(Html.fromHtml(album.getWeight()));
        holder.tvPrice.setText(Html.fromHtml(strprice));

        if (!cartList.isEmpty()) {
            for (int i = 0; i < cartList.size(); i++) {

                if (album.id.equalsIgnoreCase(cartList.get(i).id)) {
                    productValue = cartList.get(i).itemQnt;
                    System.out.println("id match==" + productValue);
                }
            }

        } else {
            productValue = 0;
        }
        System.out.println("productValue==onCreate==" + productValue);
        holder.tvProductValue.setText(Integer.toString(productValue));
        if (album.getQty() > 0) {
            holder.layoutButton.setVisibility(View.VISIBLE);
            holder.tvoutOfStock.setVisibility(View.GONE);
        } else {
            holder.layoutButton.setVisibility(View.GONE);
            holder.tvoutOfStock.setVisibility(View.VISIBLE);
        }
        String url = BaseImageUrl + album.getImage();
        RequestOptions requestOption = new RequestOptions()
                .placeholder(R.color.color_light_white).centerCrop();
        Glide.with(mContext)
                .load(url)
                .thumbnail(Glide.with(mContext)
                        .load(R.drawable.preloader).apply(requestOption))
                .apply(requestOption)
                .into(holder.imageView);

        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album = albumList.get(position);
                cartProductPosition = position;
                productid = album.getId();
                productQnt = album.getQty();

                price = album.getPrice();

                productValue = Integer.parseInt(holder.tvProductValue.getText().toString().trim());

                System.out.println("===imgAdd===productQnt====" + productQnt);
                System.out.println("===imgAdd===productValue====" + productValue);

                if (productValue == 0 && productQnt > 0) {
                    productValue = 0;
                    productValue = productValue + 1;
                    totalPrice = productValue * price;
                    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));

                    addToCart(album, totalPrice, productValue);
                } else if (productQnt > 0) {
                    productValue = productValue + 1;
                    totalPrice = productValue * price;
                    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));

                    databaseHandler.updateBuyerCart(album.id, productValue, productQnt, totalPrice);
                }


                if (productValue >= productQnt) {
                    //  showToast(mContext, mContext.getString(R.string.product_OutOf_Stock));
                }

                holder.tvProductValue.setText(Integer.toString(productValue));
                onClickListner.onCartUpdate(position);
                System.out.println("===imgAdd===totalPrice====" + totalPrice);
                totalPrice = 0;
            }
        });
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album = albumList.get(position);
                cartProductPosition = position;
                productValue = Integer.parseInt(holder.tvProductValue.getText().toString().trim());

                if (productValue > 0) {
                    System.out.println("===imgMinus===position====" + cartProductPosition);
                    productid = album.getId();

                    productQnt = album.getQty();
                    price = album.getPrice();
                    productValue = productValue - 1;
                    totalPrice = productValue * price;
                    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));
                    holder.tvProductValue.setText(Integer.toString(productValue));

                    System.out.println("===imgMinus===productValue====" + productValue);


                    if (productValue == 0) {
                        System.out.println("===imgMinus===delete item====" + productValue);

                        databaseHandler.deleteBuyerCartItem(productid);
                    } else {
                        databaseHandler.updateBuyerCart(productid, productValue, productQnt, totalPrice);

                    }
                } else {
                    productValue = 0;
                    holder.tvProductValue.setText("0");
                }
                onClickListner.onCartUpdate(position);

                totalPrice = 0;
            }
        });

    }

    private void addToCart(BeanCustomerProductItem productItem, double totalPrice, Integer productValue) {


        databaseHandler.addBuyerCart(productItem.getId(), productItem.getProduct_name(),
                productItem.getWeight(), productItem.getImage(),
                productItem.getPrice(), totalPrice, productItem.getQty(), productValue);

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvWeight, tvPrice, tvProductValue, tvoutOfStock;
        View layoutButton;
        ImageView imageView, imgAdd, imgMinus;


        public MyViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.imageView);
            tvName = view.findViewById(R.id.tvName);
            tvProductValue = view.findViewById(R.id.tvValue);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvPrice = view.findViewById(R.id.tvPrice);
            layoutButton = view.findViewById(R.id.layoutButton);
            imgAdd = view.findViewById(R.id.imgAdd);
            imgMinus = view.findViewById(R.id.imgMinus);
            tvoutOfStock = view.findViewById(R.id.tvoutOfStock);

        }

    }


}
