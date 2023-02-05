package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import b2infosoft.milkapp.com.Model.BeanPurchInvoiceProductItem;
import b2infosoft.milkapp.com.Model.BeanPurchaseItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;


public class InvoiceBuySaleDetailsFragment extends Fragment {
    private static final String TAG = "Purchase>>>";

    Context mContext;
    Toolbar toolbar;

    TextView tvDiscount, tvSGST, tvCGST, tvIGST, tvSubTotal, tvInvoiceAmount;

    EditText ediInvoiceNo, ediDate, ediCashDiscount, ediOtherCharges, ediDescription;


    double totalDiscount = 0, cashDiscount = 0, otherCharges = 0, subTotal = 0,
            productTotalAmount = 0, totalTexableAmount = 0,
            totalTaxPrice = 0, sgst = 0, cgst = 0, igst = 0, invoiceAmount = 0;

    SessionManager sessionManager;

    ArrayList<BeanPurchInvoiceProductItem> invoiceProductList = new ArrayList<>();

    BeanPurchaseItem beanPurchaseItem;

    ProductItemAdapter invoiceProductAdapter;
    RecyclerView recyclerView;


    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_purchase_sale_invoice_details, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        invoiceProductList = new ArrayList<>();

        toolbarManage();
        initView();
        return view;
    }

    private void initView() {

        ediDate = view.findViewById(R.id.ediDate);
        ediInvoiceNo = view.findViewById(R.id.ediInvoiceNo);
        recyclerView = view.findViewById(R.id.recyclerViewProduct);
        ediCashDiscount = view.findViewById(R.id.ediCashDiscount);
        ediOtherCharges = view.findViewById(R.id.ediOtherCharges);
        ediDescription = view.findViewById(R.id.ediDescription);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvSGST = view.findViewById(R.id.tvSGST);
        tvCGST = view.findViewById(R.id.tvCGST);
        tvIGST = view.findViewById(R.id.tvIGST);
        tvSubTotal = view.findViewById(R.id.tvSubTotal);
        tvInvoiceAmount = view.findViewById(R.id.tvInvoiceAmount);


        Bundle bundle = getArguments();

        if (bundle != null) {
            Gson gson = new Gson();
            String json = sessionManager.getValueSesion("album");
            beanPurchaseItem = gson.fromJson(json, BeanPurchaseItem.class);
            invoiceProductList = beanPurchaseItem.getInvoiceProductItems();
            toolbar.setTitle(beanPurchaseItem.getUser_name());
            initRecyclerViewInvoiceProductList();
            editInvoice();

        }

    }

    public void initRecyclerViewInvoiceProductList() {
        totalDiscount = 0;
        totalTaxPrice = 0;
        subTotal = 0;
        productTotalAmount = 0;
        totalTexableAmount = 0;
        System.out.println(" initRecycler==invoiceProductList.size()==>>>" + invoiceProductList.size());

        invoiceProductAdapter = new ProductItemAdapter(mContext, invoiceProductList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(invoiceProductAdapter);

        if (!invoiceProductList.isEmpty()) {
            for (int i = 0; i < invoiceProductList.size(); i++) {

                totalDiscount = totalDiscount + invoiceProductList.get(i).getDiscountPrice();
                totalTaxPrice = totalTaxPrice + invoiceProductList.get(i).getTaxPrice();
                totalTexableAmount = totalTexableAmount + invoiceProductList.get(i).getTaxable_invo();
                productTotalAmount = productTotalAmount + invoiceProductList.get(i).getGross();
                subTotal = totalTexableAmount;
            }


        }
        calculateInvoiceValue();

    }

    private void editInvoice() {
        ediInvoiceNo.setText(beanPurchaseItem.getInvoice_number());
        ediInvoiceNo.setEnabled(false);
        otherCharges = beanPurchaseItem.getOther_charg();
        cashDiscount = beanPurchaseItem.getCash_discount();
        ediDate.setText(beanPurchaseItem.getInvoice_date());
        ediCashDiscount.setText(beanPurchaseItem.getCash_discount() + "");
        ediOtherCharges.setText(beanPurchaseItem.getOther_charg() + "");
        ediDescription.setText(beanPurchaseItem.getMsg_on_statement());
    }


    public void calculateInvoiceValue() {
        String strSGST = "", strCGST = "", strIGST = "", strDiscount = "", strSubTotal = "";
        invoiceAmount = 0;

        strDiscount = mContext.getString(R.string.discount) + " :" + new DecimalFormat("#.##").format(totalDiscount);

        cgst = totalTaxPrice / 2;
        sgst = cgst;
        subTotal = totalTexableAmount + totalDiscount;
        double gross = totalTexableAmount + totalTaxPrice + otherCharges;
        invoiceAmount = gross - cashDiscount;


        invoiceAmount = Math.round(invoiceAmount);
        System.out.println("invoiceAmount=>>>" + invoiceAmount);

        strSGST = getColoredSpanned(mContext.getString(R.string.sgst) + "", "#1294F5") + "<br>" + new DecimalFormat("#.##").format(sgst);
        strCGST = getColoredSpanned(mContext.getString(R.string.cgst) + "", "#1294F5") + "<br>" + new DecimalFormat("#.##").format(cgst);
        strIGST = getColoredSpanned(mContext.getString(R.string.igst) + "", "#1294F5") + "<br>" + new DecimalFormat("#.##").format(igst);
        tvSGST.setText(Html.fromHtml(strSGST));
        tvCGST.setText(Html.fromHtml(strCGST));
        tvIGST.setText(Html.fromHtml(strIGST));


        strSubTotal = mContext.getString(R.string.subTotal) + " :" + new DecimalFormat("#.##").format(subTotal);

        tvDiscount.setText(Html.fromHtml(strDiscount));
        tvSubTotal.setText(Html.fromHtml(strSubTotal));
        tvInvoiceAmount.setText(mContext.getString(R.string.invoiceAmount) + ": " + new DecimalFormat("#.##").format(invoiceAmount));


    }


    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }


    private class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.MyViewHolder> {

        public Context mContext;
        public ArrayList<BeanPurchInvoiceProductItem> mList = new ArrayList<>();

        public ProductItemAdapter(Context mContext, ArrayList<BeanPurchInvoiceProductItem> item) {
            this.mContext = mContext;
            this.mList = item;
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

        public int getItemCount() {
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

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

                imgMoreDetail.setVisibility(View.GONE);
            }


        }
    }

}
