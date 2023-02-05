package b2infosoft.milkapp.com.Shopping;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.BeanCartItem;
import b2infosoft.milkapp.com.Model.BeanProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.TouchImageView;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getProductDetailsAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.decimalFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class ProductDescriptionFagment extends Fragment {

    public TextView tvProductName, tvMRP,
            tvProductValue, tvAmount, tvDescription;
    public ArrayList<BeanCartItem> mCartList;
    Context mContext;
    SessionManager sessionManager;
    DatabaseHandler databaseHandler;
    Toolbar toolbar;
    TextView toolbar_title;
    String labelinCart = " | item | Rs ";
    TextView tvCart, tvCartItem, tvViewCart;
    View layoutHeaderCart, layoutCart;

    int minQnt = 0;
    Integer productValue = 0;
    double totalPrice = 0;
    double price = 0;
    ViewPager mPager;
    CirclePageIndicator indicator;
    ImageView imgAdd, imgMinus;
    int productId = 0;
    Bundle bundle;
    BeanProductItem productItem;
    View view;
    private ArrayList<String> imagesArray;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_description, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        bundle = getArguments();
        productId = Integer.parseInt(bundle.getString("product_id", String.valueOf(0)));

        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
        return view;
    }


    private void initView() {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.Product));
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        layoutHeaderCart = view.findViewById(R.id.layoutHeaderCart);
        tvCart = view.findViewById(R.id.tvCart);
        layoutCart = view.findViewById(R.id.layoutCart);
        mPager = view.findViewById(R.id.viewPagerImage);
        indicator = view.findViewById(R.id.indicator);
        imgMinus = view.findViewById(R.id.imgMinus);
        imgAdd = view.findViewById(R.id.imgAddProduct);
        tvCartItem = view.findViewById(R.id.tvCartItem);
        tvViewCart = view.findViewById(R.id.tvViewCart);

        tvProductName = view.findViewById(R.id.tvProductName);
        tvMRP = view.findViewById(R.id.tvMRP);
        tvProductValue = view.findViewById(R.id.tvProductValue);
        tvAmount = view.findViewById(R.id.tvAmount);
        tvDescription = view.findViewById(R.id.tvDescription);
        mCartList = new ArrayList<>();
        getProductDetails(mContext);
    }


    private void productView() {

        imagesArray = new ArrayList<>();
        imagesArray.add(BaseImageUrl + productItem.image);
        productId = productItem.id;
        tvProductName.setText(" " + productItem.title);
        String strMrp = getColoredSpanned("MRP  ", "#FF5E57") + decimalFormat(productItem.mrp);
        String strDescription = getColoredSpanned("Description :- ", "#000000") + productItem.description;


        tvMRP.setText(Html.fromHtml(strMrp));


        tvDescription.setText(Html.fromHtml(strDescription));
        tvDescription.setMovementMethod(new ScrollingMovementMethod());
        //tvAmount.setText(Double.toString(productItem.price));
        tvAmount.setText(decimalFormat(productItem.price));

        mCartList = databaseHandler.getCartList();

        if (!mCartList.isEmpty()) {
            layoutCart.setVisibility(View.VISIBLE);

            for (int i = 0; i < mCartList.size(); i++) {
                if (productItem.id == mCartList.get(i).id) {
                    productValue = mCartList.get(i).itemQnt;
                }
            }

        } else {
            productValue = 0;
            layoutCart.setVisibility(View.GONE);

        }
        if (productValue == 0) {
            tvProductValue.setText("");
            imgMinus.setVisibility(View.GONE);
        } else {
            tvProductValue.setText(Integer.toString(productValue));
            imgMinus.setVisibility(View.VISIBLE);
        }
        updateProductCart();

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                minQnt = productItem.min_qty;
                price = productItem.price;
                productValue = Integer.parseInt(tvProductValue.getText().toString().trim());

                productValue = productValue - minQnt;

                if (productValue > 0) {
                    totalPrice = productValue * price;
                    totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));

                    System.out.println("===imgMinus===totalPrice====" + totalPrice);
                    databaseHandler.updateCart(productItem.id, productValue, totalPrice);
                    tvProductValue.setText(Integer.toString(productValue));

                } else {
                    productValue = 0;

                    tvProductValue.setText("");
                    imgMinus.setVisibility(View.GONE);
                    databaseHandler.deleteCartItem(productItem.id);

                }

                System.out.println("===imgMinus===totalPrice====" + totalPrice);
                totalPrice = 0;
                updateProductCart();
            }
        });
        layoutHeaderCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCart();
            }
        });
        tvViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCart();
            }
        });
        imageSlide();

    }


    private void imageSlide() {
        mPager.setAdapter(new ProductImage_Adapter(mContext, imagesArray));
        indicator.setViewPager(mPager);
        float density = getResources().getDisplayMetrics().density;
        //Set circle indicator radius
        indicator.setRadius(5 * density);


    }


    private void updateProductCart() {

        mCartList = new ArrayList<>();
        mCartList = databaseHandler.getCartList();

        if (!mCartList.isEmpty()) {
            layoutHeaderCart.setVisibility(View.VISIBLE);
            layoutCart.setVisibility(View.VISIBLE);
            int item = mCartList.size();
            double totalAmount = 0;
            for (int i = 0; i < mCartList.size(); i++) {
                totalAmount = totalAmount + mCartList.get(i).totalPrice;
            }
            tvCart.setText(Integer.toString(item));
            System.out.println("totalAmount=dashboard====" + totalAmount);
            tvCartItem.setText(item + labelinCart + totalAmount);
        } else {

            layoutHeaderCart.setVisibility(View.GONE);
            layoutCart.setVisibility(View.GONE);

        }
    }

    private void viewCart() {

        goNextFragmentWithBackStack(mContext, new CartFragment());
    }

    public void getProductDetails(final Context mContext) {

        if (isNetworkAvaliable(mContext)) {

            final ArrayList<BeanProductItem> beanProductItems = new ArrayList<BeanProductItem>();

            NetworkTask caller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            imagesArray = new ArrayList<>();
                            JSONObject objectData = jsonObject.getJSONObject("data");
                            //   BaseImageUrl = jsonObject.getString("path");

                            beanProductItems.add(new BeanProductItem(objectData.getInt("id"),
                                    objectData.getInt("min_qty"), objectData.getInt("qty"),
                                    objectData.getDouble("price"), objectData.getDouble("mrp"),
                                    objectData.getDouble("gst"),
                                    objectData.getString("title"), objectData.getString("type"),
                                    objectData.getString("image"), objectData.getString("thumb"),
                                    objectData.getString("status"), objectData.getString("description")));

                            JSONArray product_images = objectData.getJSONArray("product_images");
                            Log.d("===product_images======", product_images.toString());
                            for (int i = 0; i < product_images.length(); i++) {
                                //  JSONObject object = product_images.getString(i);
                                imagesArray.add(BaseImageUrl + product_images.getString(i));
                            }
                            if (!beanProductItems.isEmpty()) {
                                productItem = beanProductItems.get(0);
                                productView();
                            }
                            if (!imagesArray.isEmpty()) {
                                imageSlide();
                            }


                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            caller.execute(getProductDetailsAPI + productId);

        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }


    public void addProduct() {
        minQnt = productItem.min_qty;
        price = productItem.price;

        if (tvProductValue.getText().toString().length() == 0) {
            imgMinus.setVisibility(View.VISIBLE);

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
            productValue = Integer.parseInt(tvProductValue.getText().toString().trim());
            productValue = productValue + minQnt;
            totalPrice = productValue * price;
            totalPrice = Double.parseDouble(String.format("%.2f", totalPrice));

            databaseHandler.updateCart(productItem.id, productValue, totalPrice);
        }
        if (productValue == 0) {
            tvProductValue.setText("");
            imgMinus.setVisibility(View.GONE);
        } else {
            tvProductValue.setText(Integer.toString(productValue));
            imgMinus.setVisibility(View.VISIBLE);
        }

        updateProductCart();

        totalPrice = 0;
    }

    class ProductImage_Adapter extends PagerAdapter {

        private ArrayList<String> mList;
        private LayoutInflater inflater;
        private Context mContext;

        public ProductImage_Adapter(Context context, ArrayList<String> mList) {
            this.mContext = context;
            this.mList = mList;
            inflater = LayoutInflater.from(context);
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.view_pager_product_item, view, false);

            assert imageLayout != null;
            TouchImageView imageView = imageLayout.findViewById(R.id.imageView);
            String url = mList.get(position);
            Glide.with(mContext).load(url)
                    .placeholder(R.drawable.ic_preloader)

                    .error(R.color.color_light_white).into(imageView);
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
