package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.BeanAddProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.Profile_Item_adapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanProfile_Item;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;

public class ViewProductDetails extends Fragment {
    private static final String TAG = "Product>>>";

    Context mContext;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Profile_Item_adapter adapter;
    ArrayList<BeanProfile_Item> mList;
    ImageView imgProduct;

    TextView tvGroupName, tvBrandName, tvUnitName, tvWeight, tvPrice, tvSalePrice, tvTax, tvLowStockQuantity, tvOpeningQuantity,
            tvOpeningRate, tvOpeningAmount, tvDescription;

    String groupName = "", brandName = "", unitName = "",
            strWeight = "", strSalePrice = "", strDescription = "",
            lowStockQuantity = "", openingQuantity = "",
            openingRate = "", openingAmount = "", price = "", tax = "";


    SessionManager sessionManager;

    View view;
    BeanAddProductItem beanAddProductItem;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_buy_sale_details, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.Product));

        toolbarManage();
        initView();
        return view;
    }

    private void initView() {

        imgProduct = view.findViewById(R.id.imgProduct);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvGroupName = view.findViewById(R.id.tvGroupName);
        tvBrandName = view.findViewById(R.id.tvBrandName);
        tvUnitName = view.findViewById(R.id.tvUnitName);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvSalePrice = view.findViewById(R.id.tvSalePrice);
        tvTax = view.findViewById(R.id.tvTax);
        tvLowStockQuantity = view.findViewById(R.id.tvLowStock);
        tvOpeningQuantity = view.findViewById(R.id.tvOpeningQty);
        tvOpeningRate = view.findViewById(R.id.tvOpeningRate);
        tvOpeningAmount = view.findViewById(R.id.tvOpeningAmount);
        tvDescription = view.findViewById(R.id.tvDescription);

        Gson gson = new Gson();
        String json = sessionManager.getValueSesion("beanAddProductItem");
        beanAddProductItem = gson.fromJson(json, BeanAddProductItem.class);
        toolbar.setTitle(beanAddProductItem.getName());
        initRecyclerView();
        viewProductDetails();
    }

    private void viewProductDetails() {


        String url = BaseImageUrl + beanAddProductItem.getImages();
        System.out.println("===url=img" + url);

        Glide.with(mContext).load(url)
                .placeholder(R.drawable.ic_preloader)
                .error(R.drawable.app_icon).into(imgProduct);

        price = mContext.getString(R.string.Rupee_symbol) + " " + beanAddProductItem.getPrice();
        strSalePrice = mContext.getString(R.string.salePrice) + " : " + mContext.getString(R.string.Rupee_symbol) + " " + beanAddProductItem.getPrice();

        groupName = mContext.getString(R.string.group) + " : " + nullCheckFunction(beanAddProductItem.getGroup_name());
        brandName = mContext.getString(R.string.brand) + " : " + nullCheckFunction(beanAddProductItem.getBrand_name());
        unitName = mContext.getString(R.string.unit) + " : " + nullCheckFunction(beanAddProductItem.getUnit_name());

        strWeight = mContext.getString(R.string.Weight) + " : " + beanAddProductItem.getItem_weight();
        tax = mContext.getString(R.string.tax) + " : " + beanAddProductItem.getTax();

        lowStockQuantity = mContext.getString(R.string.lowStockQuantity) + " : " + beanAddProductItem.getLow_stock_alert();
        openingQuantity = mContext.getString(R.string.openingQuantity) + " : " + beanAddProductItem.getInitial_quantity();
        openingRate = mContext.getString(R.string.openingRate) + " : " + beanAddProductItem.getOpening_rate();
        openingAmount = mContext.getString(R.string.OpeningAmount) + " : " + beanAddProductItem.getOpening_amt();
        strDescription = mContext.getString(R.string.Description) + " : " + nullCheckFunction(beanAddProductItem.getDiscription_product());

        tvPrice.setText(price);
        tvSalePrice.setText(strSalePrice);

        tvGroupName.setText(groupName);
        tvBrandName.setText(brandName);
        tvUnitName.setText(unitName);
        tvWeight.setText(strWeight);
        tvTax.setText(tax);


        tvLowStockQuantity.setText(lowStockQuantity);
        tvOpeningQuantity.setText(openingQuantity);
        tvOpeningRate.setText(openingRate);
        tvOpeningAmount.setText(openingAmount);
        tvDescription.setText(strDescription);

    }

    private void initRecyclerView() {
        mList = new ArrayList<>();
        mList.add(new BeanProfile_Item(mContext.getString(R.string.salePrice) + " ", ": " + beanAddProductItem.getPrice()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.group) + " ", ": " + nullCheckFunction(beanAddProductItem.getGroup_name())));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.brand) + " ", ": " + nullCheckFunction(beanAddProductItem.getBrand_name())));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.unit) + " ", ": " + nullCheckFunction(beanAddProductItem.getUnit_name())));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Weight) + " ", ": " + beanAddProductItem.getItem_weight()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.tax) + " ", ": " + beanAddProductItem.getTax()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.lowStockQuantity) + " ", ": " + beanAddProductItem.getLow_stock_alert()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.openingQuantity) + " ", ": " + beanAddProductItem.getInitial_quantity()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.openingRate) + " ", ": " + beanAddProductItem.getOpening_rate()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.OpeningAmount) + " ", ": " + beanAddProductItem.getOpening_amt()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Description) + " ", ": " + beanAddProductItem.getDiscription_product()));


        adapter = new Profile_Item_adapter(mContext, mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }


}
