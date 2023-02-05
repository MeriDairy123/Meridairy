package b2infosoft.milkapp.com.Dairy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.Help_item;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.adapter.HelpItemAdapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

/**
 * Created by Official on 19-Jan-19.
 */

public class Help_activity extends Activity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Context mContext;
    Toolbar toolbar;

    RecyclerView recycler_view;
    SessionManager sessionManager;
    HelpItemAdapter help_itemAdapter;
    private ArrayList<Help_item> help_itemArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mContext = Help_activity.this;

        help_itemArrayList = new ArrayList<>();
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        //   LoadGoogleVideoAd();
        initView();
    }


    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        sessionManager = new SessionManager(mContext);

        recycler_view = findViewById(R.id.recycler_view);

        // How to use Meri dairy

        help_itemArrayList = new ArrayList<>();
        help_itemArrayList.add(new Help_item(getString(R.string.MeriDairy_Introduction), "https://www.youtube.com/watch?v=2E77oG_Dglo"));
        help_itemArrayList.add(new Help_item(getString(R.string.how_to_use_Meridairy), "https://www.youtube.com/watch?v=CUQUmFf7bOA"));
        help_itemArrayList.add(new Help_item(getString(R.string.Loginon_multiple_evices), "https://www.youtube.com/watch?v=jYy7T1n3ORg"));
        help_itemArrayList.add(new Help_item(getString(R.string.How_to_sale_milk_to_customeronMeri_Dairyapp), "https://www.youtube.com/watch?v=_XB72YLUC-M"));
        help_itemArrayList.add(new Help_item(getString(R.string.Howto_updateSNFandFATrate_in_MeriDairyapp), "https://www.youtube.com/watch?v=e46XVk0G8PA"));
        help_itemArrayList.add(new Help_item(getString(R.string.How_to_editCustomer), "https://www.youtube.com/watch?v=TgXlGifXs1I"));

        help_itemArrayList.add(new Help_item(getString(R.string.Howto_add_a_payment_with_signatureMERIDAIRY), "https://www.youtube.com/watch?v=YBn8gIWcaWg"));

        help_itemArrayList.add(new Help_item(getString(R.string.MERIDAIRY_milk_sowtware_mini_printer_printreciept_by_phone), "https://www.youtube.com/watch?v=iAGldlX5jks"));


        help_itemArrayList.add(new Help_item(getString(R.string.Payment_Register), "https://youtu.be/4hHbE5XxuQY"));
        help_itemArrayList.add(new Help_item(getString(R.string.milkBill), "https://youtu.be/Hcgg9ONhwEY"));
        help_itemArrayList.add(new Help_item(getString(R.string.khata), "https://youtu.be/8o9LT7jafSQ"));
        String product = mContext.getString(R.string.Product) + " ";
        help_itemArrayList.add(new Help_item(product + mContext.getString(R.string.group), "https://youtu.be/G8IgoO9Xjbc"));
        help_itemArrayList.add(new Help_item(product + mContext.getString(R.string.brand), "https://youtu.be/QinRcEVvuIA"));
        help_itemArrayList.add(new Help_item(product + mContext.getString(R.string.Item), "https://youtu.be/IQkoaOHn5iQ"));
        help_itemArrayList.add(new Help_item(product + mContext.getString(R.string.list), "https://youtu.be/_cpE3ggDHPQ"));
        help_itemArrayList.add(new Help_item(product + mContext.getString(R.string.Purchase), "https://youtu.be/JESvzv_oEZQ"));

        help_itemArrayList.add(new Help_item(product + mContext.getString(R.string.Purchase) + " " +
                (mContext.getString(R.string.strReturn)), "https://youtu.be/DTPa6unr3lA"));

        help_itemArrayList.add(new Help_item(product + mContext.getString(R.string.Sale), "https://youtu.be/7gliR9eYMAI"));
        help_itemArrayList.add(new Help_item(product + mContext.getString(R.string.Sale) + " " + (mContext.getString(R.string.strReturn)), "https://youtu.be/4IIAXgsUakI"));
        initRecyclerView();

    }

    private void initRecyclerView() {
        help_itemAdapter = new HelpItemAdapter(mContext, help_itemArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(help_itemAdapter);
    }

}