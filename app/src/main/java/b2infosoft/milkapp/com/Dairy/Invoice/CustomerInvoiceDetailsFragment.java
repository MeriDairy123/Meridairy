package b2infosoft.milkapp.com.Dairy.Invoice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.BeanDairyCustomerInvoiceItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.Profile_Item_adapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanProfile_Item;

import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;


public class CustomerInvoiceDetailsFragment extends Fragment {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;

    RecyclerView recyclerView;

    Bundle bundle = null;
    View view;

    BeanDairyCustomerInvoiceItem invoiceItem;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_milk_invoice_details, container, false);
        mContext = getActivity();


        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView(view);

        return view;
    }

    private void initView(View view) {

        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);


        recyclerView = view.findViewById(R.id.recyclerView);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        bundle = getArguments();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String json = bundle.getString("album");
        invoiceItem = gson.fromJson(json, BeanDairyCustomerInvoiceItem.class);

        initRecyclerView();
    }

    private void initRecyclerView() {

        ArrayList<BeanProfile_Item> mList = new ArrayList<>();
        toolbar_title.setText(invoiceItem.getName() + mContext.getString(R.string.ViewInvoice));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Name) + " ", ": " + invoiceItem.getName()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Start_Date) + " ", ": " + invoiceItem.getStartDate()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.End_Date) + " ", ": " + invoiceItem.getEndDate()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.strType) + " ", ": " + invoiceItem.getType()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Weight) + " ", ": " + invoiceItem.getWeight()));
        mList.add(new BeanProfile_Item(mContext.getString(R.string.Amount) + " ", ": " + invoiceItem.getAmount()));

        Profile_Item_adapter adapter = new Profile_Item_adapter(mContext, mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }


}
