package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Purchase;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import b2infosoft.milkapp.com.Dairy.Customer.Fragment.AllCustomerListFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.BeanAddProductItem;
import b2infosoft.milkapp.com.Model.BeanBrandtItem;
import b2infosoft.milkapp.com.Model.BeanPurchaseItemOld;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dateDDMMYY;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;


public class AddPurchaseInvoiceSingleProdFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Product>>>";

    Context mContext;
    Toolbar toolbar;
    AutoCompleteTextView tvAutoCustID;
    TextView tvDate, tvCfathername, tvDiscount, tvSGST, tvCGST, tvIGST, tvSubTotal, tvInvoiceAmount;
    Spinner spinGroup, spinBrand, spinProduct;
    EditText ediInvoiceNo, ediWeight, ediTotalWeight, ediQty, ediPrice, ediTax, ediDiscount, ediCashDiscount, ediOtherCharges,
            ediDescription;

    CheckBox chkboxTax, chkboxDiscount;
    Button btnSubmit;
    String strTable = "purchase", selectLabel = "", formattedDate = "", strType = "add", sonOf = " / ", selectedName = "",
            selectedFatherName = "", selectedPhone_number = "", strSelectedId = "", unic_customer = "",
            invoiceId = "", productId = "", editProductId = "", editBrandId = "", productName = "", groupId = "", brandId = "", strInvoiceNo = "", strWeight = "", strDescription = "";
    int quantity = 1, discountChecked = 0, taxChecked = 0, mYear = 0, mMonth = 0, mDay = 0;
    int groupIdPos = 0, brandIdPos = 0, productIdPos = 0;
    double weight = 1, netWeight = 0, ratePrice = 0, taxPrice = 0, itemDiscount = 0,
            discount = 0, totalDiscount = 0, cashDiscount = 0, otherCharges = 0, subTotal = 0, texableAmount = 0,
            tax = 0, sgst = 0, cgst = 0, igst = 0, invoiceAmount = 0;

    SessionManager sessionManager;
    ArrayList<CustomerListPojo> CustomerList = new ArrayList<>();
    ArrayList<BeanBrandtItem> groupItem = new ArrayList<>();
    ArrayList<BeanBrandtItem> brandItems = new ArrayList<>();
    ArrayList<BeanAddProductItem> beanAddProductItems = new ArrayList<>();
    BeanPurchaseItemOld beanPurchaseItemOld;
    View view;
    private DatabaseHandler databaseHandler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_add_purchase, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        selectLabel = mContext.getString(R.string.select) + " ";
        toolbarManage();
        initView();
        return view;
    }

    private void initView() {
        tvAutoCustID = view.findViewById(R.id.tvAutoCustID);
        tvCfathername = view.findViewById(R.id.tvCfathername);
        spinGroup = view.findViewById(R.id.spinGroup);
        spinBrand = view.findViewById(R.id.spinBrand);
        spinProduct = view.findViewById(R.id.spinProduct);
        ediInvoiceNo = view.findViewById(R.id.ediInvoiceNo);
        ediTotalWeight = view.findViewById(R.id.ediTotalWeight);
        ediWeight = view.findViewById(R.id.ediWeight);
        ediPrice = view.findViewById(R.id.ediPrice);

        ediTax = view.findViewById(R.id.ediTax);

        ediQty = view.findViewById(R.id.ediQty);
        ediDiscount = view.findViewById(R.id.ediDiscount);
        ediCashDiscount = view.findViewById(R.id.ediCashDiscount);
        ediOtherCharges = view.findViewById(R.id.ediOtherCharges);
        ediDescription = view.findViewById(R.id.ediDescription);
        tvDate = view.findViewById(R.id.tvDate);

        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvSGST = view.findViewById(R.id.tvSGST);
        tvCGST = view.findViewById(R.id.tvCGST);
        tvIGST = view.findViewById(R.id.tvIGST);
        tvSubTotal = view.findViewById(R.id.tvSubTotal);
        tvInvoiceAmount = view.findViewById(R.id.tvInvoiceAmount);

        chkboxTax = view.findViewById(R.id.chkboxTax);
        chkboxDiscount = view.findViewById(R.id.chkboxDiscount);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        ediTax.setHint(mContext.getString(R.string.tax) + " (%)");

        tvDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        formattedDate = getSimpleDate();
        tvDate.setText(formattedDate);

        initBrandAndProduct();

        getCustomerList();
        Bundle bundle = getArguments();
        toolbar.setTitle(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.Purchase) + " " + mContext.getString(R.string.invoice));


        tvCfathername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.FromWhere = "product";
                Fragment fragment = new AllCustomerListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("FromWhere", "product");
                bundle.putString("user_group_id", "3");
                fragment.setArguments(bundle);
                goNextFragmentReplace(mContext, fragment);
            }
        });


        chkboxTax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                taxChecked = 0;
                if (b) {
                    taxChecked = 1;
                } else {
                    if (ratePrice > 0 && tax > 0) {
                        ratePrice = Math.round((ratePrice * (100 + tax)) / 100);
                        ediPrice.setText("" + ratePrice);
                    }
                }

                ediTax.setText(tax + "");
            }
        });
        chkboxDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                discountChecked = 0;
                if (b) {
                    discountChecked = 1;
                }

                ediDiscount.setText(discount + "");
            }
        });

        ediTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                tax = 0;
                if (editable.length() > 0) {
                    tax = Double.parseDouble(editable.toString());
                }

                calculateTax();
            }
        });


        ediQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                quantity = 0;
                if (editable.length() > 0) {
                    quantity = Integer.parseInt(editable.toString());
                }
                calculateSubTotalAndDiscount();
            }
        });
        ediPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ratePrice = 0;
                if (editable.length() > 0) {
                    ratePrice = Double.parseDouble(editable.toString());
                }
                calculateSubTotalAndDiscount();
            }
        });
        ediWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                weight = 0;
                if (editable.length() > 0) {
                    weight = Double.parseDouble(editable.toString());

                }
                calculateSubTotalAndDiscount();
            }
        });
        ediDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                discount = 0;
                if (editable.length() > 0) {
                    discount = Double.parseDouble(editable.toString());
                }
                calculateSubTotalAndDiscount();
            }
        });
        ediCashDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cashDiscount = 0;
                if (editable.length() > 0) {
                    cashDiscount = Double.parseDouble(editable.toString());
                }
                calculateSubTotalAndDiscount();
            }
        });
        ediOtherCharges.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                otherCharges = 0;
                if (editable.length() > 0) {
                    otherCharges = Double.parseDouble(editable.toString());
                }
                calculateSubTotalAndDiscount();
            }
        });

        if (bundle != null) {
            String from = bundle.getString("FromWhere");
            if (!from.equalsIgnoreCase("edit")) {
                tvAutoCustID.setText(bundle.getString("unic_customer"));
                tvCfathername.setText(bundle.getString("CustomerName") + sonOf + bundle.getString("CustomerFatherName"));
            } else {
                strType = bundle.getString("type");
                toolbar.setTitle(mContext.getString(R.string.Edit) + " " + mContext.getString(R.string.Purchase) + " " + mContext.getString(R.string.invoice));
                Gson gson = new Gson();
                String json = sessionManager.getValueSesion("album");
                beanPurchaseItemOld = gson.fromJson(json, BeanPurchaseItemOld.class);
                editProduct();
            }
        }

        getProductGroupOrBrand("group", Constant.getProductGroupListAPI);
    }

    private void editProduct() {
        groupId = beanPurchaseItemOld.getCategory_id();
        editBrandId = beanPurchaseItemOld.getItem_brand();
        editProductId = beanPurchaseItemOld.getProduct_id();

        invoiceId = beanPurchaseItemOld.getInvoice_id();
        tvAutoCustID.setText(beanPurchaseItemOld.getCustomer_id());
        ediInvoiceNo.setText(beanPurchaseItemOld.getInvoice_number());
        tvAutoCustID.setEnabled(false);
        tvCfathername.setEnabled(false);
        ediInvoiceNo.setEnabled(false);

        tvDate.setText(dateDDMMYY(beanPurchaseItemOld.getInvoice_date()));

        ediQty.setText(beanPurchaseItemOld.getQty() + "");
        ediWeight.setText("" + beanPurchaseItemOld.getWeight());
        quantity = 1;
        weight = 1;
        if (beanPurchaseItemOld.getTax_check() == 1) {
            chkboxTax.setChecked(true);
        }
        if (beanPurchaseItemOld.getQty() > 0) {
            quantity = beanPurchaseItemOld.getQty();
        }
        if (beanPurchaseItemOld.getWeight() > 0) {
            weight = beanPurchaseItemOld.getWeight();
        }
        ediQty.setText(quantity + "");
        ediWeight.setText(weight + "");
        ediPrice.setText(beanPurchaseItemOld.getRate() + "");
        ratePrice = beanPurchaseItemOld.getRate();

        ediTax.setText(beanPurchaseItemOld.getTax() + "");
        tax = beanPurchaseItemOld.getTax();

        ediDiscount.setText(beanPurchaseItemOld.getItem_discount() + "");
        ediCashDiscount.setText(beanPurchaseItemOld.getCash_discount() + "");
        ediOtherCharges.setText(beanPurchaseItemOld.getOther_charg() + "");

        ediDescription.setText(beanPurchaseItemOld.getMsg_on_statement());


        if (beanPurchaseItemOld.getDiscount_check() == 1) {
            chkboxDiscount.setChecked(true);
        }

    }

    private void calculateTax() {

        subTotal = 0;

        if (ediPrice.getText().length() > 0) {
            ratePrice = Double.parseDouble(ediPrice.getText().toString());
        }
        if (chkboxTax.isChecked()) {

            if (tax > 0 && ratePrice > 0 && netWeight > 0) {

                taxPrice = ratePrice / (tax + 100) * tax;
                ratePrice = ratePrice - taxPrice;
            }
        }
        ediPrice.setText(new DecimalFormat("#.##").format(ratePrice));
    }

    private void calculateSubTotalAndDiscount() {

        subTotal = 0;
        texableAmount = 0;
        System.out.println("quantity>>>>" + quantity);
        System.out.println("weight>>>>" + weight);
        if (quantity > 0 && weight > 0) {
            netWeight = quantity * weight;
        } else {
            netWeight = weight;
        }
        System.out.println("netWeight>>>>" + netWeight);
        if (ratePrice > 0 && netWeight > 0) {
            subTotal = ratePrice * netWeight;
        }
        itemDiscount = discount;
        if (subTotal > 0 && discount > 0 && discountChecked == 1) {
            itemDiscount = subTotal * discount / 100;
        }
        texableAmount = subTotal - itemDiscount;
        if (tax > 0 && texableAmount > 0) {
            taxPrice = (texableAmount * tax) / 100;
        }
        cgst = taxPrice / 2;
        sgst = cgst;

        texableAmount = texableAmount + taxPrice;

        System.out.println("taxPrice>>>" + taxPrice);
        System.out.println("itemDiscount=>>>" + itemDiscount);
        System.out.println("cashDiscount==>>>" + cashDiscount);
        System.out.println("otherCharges=>>>" + otherCharges);

        System.out.println("texableAmount=>>>" + texableAmount);

        totalDiscount = cashDiscount + otherCharges;
        System.out.println("totalDiscount=>>>" + totalDiscount);

        invoiceAmount = texableAmount - totalDiscount;
        invoiceAmount = Math.round(invoiceAmount);
        System.out.println("invoiceAmount=>>>" + invoiceAmount);
        String strSGST = "", strCGST = "", strIGST = "", strNetWeight = "", strDiscount = "", strSubTotal = "";

        strSGST = getColoredSpanned(mContext.getString(R.string.sgst) + "", "#1294F5") + "<br>" + new DecimalFormat("#.##").format(sgst);
        strCGST = getColoredSpanned(mContext.getString(R.string.cgst) + "", "#1294F5") + "<br>" + new DecimalFormat("#.##").format(cgst);
        strIGST = getColoredSpanned(mContext.getString(R.string.igst) + "", "#1294F5") + "<br>" + new DecimalFormat("#.##").format(igst);
        tvSGST.setText(Html.fromHtml(strSGST));
        tvCGST.setText(Html.fromHtml(strCGST));
        tvIGST.setText(Html.fromHtml(strIGST));

        strNetWeight = new DecimalFormat("#.##").format(netWeight);
        strDiscount = mContext.getString(R.string.discount) + " :" + new DecimalFormat("#.##").format(itemDiscount);
        strSubTotal = mContext.getString(R.string.subTotal) + " :" + new DecimalFormat("#.##").format(subTotal);

        ediTotalWeight.setText(Html.fromHtml(strNetWeight));
        tvDiscount.setText(Html.fromHtml(strDiscount));
        tvSubTotal.setText(Html.fromHtml(strSubTotal));
        tvInvoiceAmount.setText(mContext.getString(R.string.invoiceAmount) + ": " + new DecimalFormat("#.##").format(invoiceAmount));


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

    public void getProductGroupOrBrand(String strType, String strListUrl) {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {
                try {

                    ArrayList<String> listItem = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    String strSelect = "";
                    if (jsonObject.getString("status").equals("success")) {
                        if (strType.equalsIgnoreCase("group")) {
                            groupIdPos = 0;
                            groupItem = new ArrayList<>();
                            strSelect = selectLabel + mContext.getString(R.string.group);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            listItem.add(strSelect);
                            groupItem.add(new BeanBrandtItem("", strSelect, ""));
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);
                                listItem.add(jobj.getString("category_name"));
                                groupItem.add(new BeanBrandtItem(jobj.getString("id"),
                                        jobj.getString("category_name"), jobj.getString("code")));

                                if (groupId.equalsIgnoreCase(jobj.getString("id"))) {
                                    groupIdPos = i + 1;
                                }
                            }
                            initSpinGroup(listItem);


                        } else if (strType.equalsIgnoreCase("brand")) {
                            brandIdPos = 0;
                            brandItems = new ArrayList<>();
                            strSelect = selectLabel + mContext.getString(R.string.brand);
                            System.out.println("brandId===" + brandId);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            listItem.add(strSelect);
                            brandItems.add(new BeanBrandtItem("", strSelect, ""));
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);
                                listItem.add(jobj.getString("name"));
                                brandItems.add(new BeanBrandtItem(jobj.getString("id"),
                                        jobj.getString("name"), ""));
                                if (editBrandId.equalsIgnoreCase(jobj.getString("id"))) {
                                    brandIdPos = i + 1;
                                }
                            }

                            initSpinBrand(listItem);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        System.out.println("groupId==" + groupId);
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("category_id", groupId)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(strListUrl);

    }

    private void initSpinGroup(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinGroup.setAdapter(spinAdapter);
        spinGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    groupId = groupItem.get(position).id;
                    getProductGroupOrBrand("brand", Constant.getProductBrandListAPI);
                    getProductList();
                } else {
                    groupId = "";
                    //   initBrandAndProduct();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinGroup.setSelection(groupIdPos);
    }

    public void initBrandAndProduct() {

        //Brand
        brandId = "";
        brandIdPos = 0;
        productIdPos = 0;
        productId = "";
        brandItems = new ArrayList<>();
        ArrayList<String> listItem = new ArrayList<>();
        String strSelect = selectLabel + mContext.getString(R.string.brand);
        listItem.add(strSelect);
        brandItems.add(new BeanBrandtItem("", strSelect, ""));
        initSpinBrand(listItem);

        //product
        listItem = new ArrayList<>();
        beanAddProductItems = new ArrayList<>();
        strSelect = selectLabel + mContext.getString(R.string.Product);

        listItem.add(strSelect);
        brandItems.add(new BeanBrandtItem("", strSelect, ""));

        beanAddProductItems.add(new BeanAddProductItem("", strSelect, "", "",
                "", "", "", "", "", "", "", "",
                "", 0, 0, 0, 0, 0, 0, 0, 0, 0, "0"));

        initSpinProduct(listItem);
    }

    private void initSpinBrand(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinBrand.setAdapter(spinAdapter);
        spinBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    brandId = brandItems.get(position).id;
                } else {
                    brandId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        System.out.println("brandIdPos>>>>" + brandIdPos);

        spinBrand.setSelection(brandIdPos);
    }

    private void initSpinProduct(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinProduct.setAdapter(spinAdapter);
        spinProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    productId = beanAddProductItems.get(position).getId();
                    productName = beanAddProductItems.get(position).getName();
                    setProductData(beanAddProductItems.get(position));

                } else {
                    productId = "";
                    productName = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        System.out.println("productIdPos>>>>" + productIdPos);

        spinProduct.setSelection(productIdPos);
    }

    private void setProductData(BeanAddProductItem beanAddProductItem) {

        if (editProductId.length() == 0) {
            System.out.println("editProductId>>>" + editProductId);
            weight = 1;
            if (nullCheckFunction(beanAddProductItem.getItem_weight()).length() > 0) {
                weight = Double.parseDouble(beanAddProductItem.getItem_weight());
            }
            ratePrice = beanAddProductItem.getPrice();
            tax = beanAddProductItem.getTax();
            ratePrice = ratePrice / weight;
            quantity = 1;
            discount = 0;
            cashDiscount = 0;
            otherCharges = 0;
            ediQty.setText("" + quantity);
            ediWeight.setText(new DecimalFormat("#.##").format(weight));
            ediTax.setText(new DecimalFormat("#.##").format(tax));
            ediPrice.setText(new DecimalFormat("#.##").format(ratePrice));
            ediDiscount.setText(new DecimalFormat("#.##").format(discount));
            ediCashDiscount.setText(new DecimalFormat("#.##").format(cashDiscount));
            ediOtherCharges.setText(new DecimalFormat("#.##").format(otherCharges));

            if (beanAddProductItem.getTax_check() == 1) {
                chkboxTax.setChecked(true);
                taxChecked = beanAddProductItem.getTax_check();
            }
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvDate:
                getDate();
                break;
            case R.id.btnSubmit:
                strInvoiceNo = ediInvoiceNo.getText().toString().trim();
                strWeight = ediWeight.getText().toString().trim();
                strDescription = ediDescription.getText().toString().trim();
                if (chkboxTax.isChecked()) {
                    taxChecked = 1;
                }
                if (chkboxDiscount.isChecked()) {
                    discountChecked = 1;
                }

                if (strSelectedId.length() == 0) {
                    showAlertWithButton(mContext, selectLabel + mContext.getString(R.string.Customer));
                }
                if (groupId.length() == 0) {
                    showAlertWithButton(mContext, selectLabel + mContext.getString(R.string.group));
                } else if (productId.length() == 0) {
                    showAlertWithButton(mContext, selectLabel + mContext.getString(R.string.Product));
                } else if (strInvoiceNo.length() == 0) {
                    showAlertWithButton(mContext, mContext.getString(R.string.invoice));
                } else if (strWeight.length() == 0) {
                    ediInvoiceNo.clearFocus();
                    ediWeight.requestFocus();
                    showAlertWithButton(mContext, mContext.getString(R.string.Weight));
                } else {
                    addProductPurchase();
                }
        }
    }


    public void addProductPurchase() {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        UtilityMethod.showAlertBox(mContext, getString(R.string.Product_Added_Successfully));
                        getActivity().onBackPressed();
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, jsonObject.getString("user_status_message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        System.out.println("groupId===" + groupId);
        System.out.println("ratePrice===" + ratePrice);
        System.out.println("subTotal===" + subTotal);
        System.out.println("taxChecked===" + taxChecked);
        System.out.println("discountChecked===" + discountChecked);
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("table", strTable)
                .addEncoded("type", strType)
                .addEncoded("invoice_id", invoiceId)
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("category_id", groupId)
                .addEncoded("invoice_date", formattedDate)
                .addEncoded("customer_id", strSelectedId)
                .addEncoded("item_brand", brandId)
                .addEncoded("product_id", productId)
                .addEncoded("item_name", productName)
                .addEncoded("invoice_number", strInvoiceNo)
                .addEncoded("weight", strWeight)
                .addEncoded("net_weight", "" + netWeight)
                .addEncoded("item_discount", discount + "")
                .addEncoded("cash_discount", cashDiscount + "")
                .addEncoded("discount_check", discountChecked + "")
                .addEncoded("discount_invo", itemDiscount + "")
                .addEncoded("rate", ratePrice + "")
                .addEncoded("tax_rate", tax + "").addEncoded("tax_check", taxChecked + "")
                .addEncoded("qty", quantity + "").addEncoded("gross", subTotal + "")
                .addEncoded("taxable_invo", subTotal + "").addEncoded("sgst_inv", sgst + "")
                .addEncoded("cgst_inv", cgst + "").addEncoded("igst_inv", igst + "")
                .addEncoded("other_charg", otherCharges + "").addEncoded("net_amount", invoiceAmount + "")
                .addEncoded("msg_on_statement", strDescription + "")
                .build();


        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.addPurchaseInvoiceAPI);
    }

    public void getCustomerList() {

        CustomerList = databaseHandler.getCustomerList();
        System.out.println("customerList>>>" + CustomerList.size());
        setCustomerList(CustomerList);

    }

    public void setCustomerList(final ArrayList<CustomerListPojo> mList) {
        if (mList.isEmpty()) {
            FromWhere = "AddEntry";
            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Add_Customer));

        } else {

            tvAutoCustID.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable actid) {
                    System.out.println("actid====" + actid);
                    tvCfathername.setText("");
                    strSelectedId = "";
                    if (tvAutoCustID.getText().toString().trim().length() == 0) {
                        tvCfathername.setText("");
                        strSelectedId = "";
                    } else {
                        for (int i = 0; i < mList.size(); i++) {
                            if (actid.toString().trim().equals(mList.get(i).unic_customer)) {
                                strSelectedId = mList.get(i).id;
                                selectedName = mList.get(i).name;
                                selectedFatherName = mList.get(i).father_name;
                                selectedPhone_number = mList.get(i).phone_number;
                                unic_customer = mList.get(i).unic_customer;
                                System.out.println("strSelectedId>>>>" + unic_customer + " " + strSelectedId + " " + selectedFatherName + " Name==" + selectedName);
                                tvCfathername.setText(mList.get(i).name + sonOf + mList.get(i).father_name);


                            }
                        }
                    }
                }
            });
        }
    }

    public void getProductList() {
        beanAddProductItems = new ArrayList<>();
        productIdPos = 0;
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<String> listItem = new ArrayList<>();
                    if (jsonObject.getString("status").equals("success")) {

                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        listItem.add(selectLabel + mContext.getString(R.string.Product));
                        beanAddProductItems.add(new BeanAddProductItem("",
                                selectLabel + mContext.getString(R.string.Product), "",
                                "", "", "", "", "", "",
                                "", "", "", "", 0, 0,
                                0, 0, 0, 0, 0, 0, 0,
                                "0"));

                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            listItem.add(jobj.getString("name"));

                            beanAddProductItems.add(new BeanAddProductItem(jobj.getString("id"),
                                    jobj.getString("name"), nullCheckFunction(jobj.getString("item_weight")),
                                    jobj.getString("discription_product"), jobj.getString("category"),
                                    jobj.getString("category_name"), jobj.getString("item_brand"),
                                    jobj.getString("brand_name"), jobj.getString("item_unit"), jobj.getString("unit_name"),
                                    jobj.getString("item_code"), jobj.getString("images"),
                                    jobj.getString("created_at"), jobj.getInt("low_stock_alert"),
                                    jobj.getInt("initial_quantity"), jobj.getInt("weightc"),
                                    jobj.getInt("tax_check"), jobj.getDouble("opening_rate"),
                                    jobj.getDouble("opening_amt"), jobj.getDouble("price"),
                                    jobj.getDouble("sales_price"), jobj.getDouble("tax"), jobj.getString("plan_status")));

                            if (editProductId.equalsIgnoreCase(jobj.getString("id"))) {
                                productIdPos = i + 1;
                            }
                        }

                        initSpinProduct(listItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("id", groupId)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getUserProductListAPI);

    }


    public void getDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        ArrayList<String> monthList = getMonthList();

                        String month = "";
                        for (int i = 0; i < monthList.size(); i++) {
                            if (monthOfYear == i) {
                                month = monthList.get(i);
                            }
                        }
                        String day = "";
                        //  month = checkDigit(monthOfYear + 1);
                        System.out.println("Month==>>" + month);
                        day = checkDigit(dayOfMonth);
                        tvDate.setText(day + "-" + month + "-" + year);
                        formattedDate = day + "-" + month + "-" + year;
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
