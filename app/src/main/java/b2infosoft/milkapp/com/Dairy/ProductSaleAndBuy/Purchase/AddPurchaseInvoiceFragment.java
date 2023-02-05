package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Purchase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Adapter.InvoiceProductAdapter;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanAddProductItem;
import b2infosoft.milkapp.com.Model.BeanBrandtItem;
import b2infosoft.milkapp.com.Model.BeanPurchInvoiceProductItem;
import b2infosoft.milkapp.com.Model.BeanPurchaseItem;
import b2infosoft.milkapp.com.Model.BeanTransactionUserItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printProductInvoiceReceipt;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dateDDMMYY;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;


public class AddPurchaseInvoiceFragment extends Fragment implements View.OnClickListener, UpdateList {
    private static final String TAG = "Purchase>>>";

    Context mContext;
    Toolbar toolbar;

    TextView tvAddProd, tvDate, tvDiscount, tvSGST, tvCGST, tvIGST, tvSubTotal, tvInvoiceAmount, tvBalanceAmount;
    SearchableSpinner spinUser;
    SearchableSpinner spinGroup, spinItem, spinBrand, spinProduct;
    EditText ediInvoiceNo, ediWeight, ediTotalWeight, ediQty, ediPrice, ediTax, ediDiscount, ediTaxable, ediProdTotal;
    EditText ediCashDiscount, ediOtherCharges, ediCashAmount, ediDescription;

    CheckBox chkboxTax, chkboxDiscount;
    Button btnSubmit;

    String strTable = "purchase", selectLabel = "", formattedDate = "", strType = "add",
            editUserId = "", strSelectedId = "", strUserName = "", unic_customer = "",
            invoiceId = "", productId = "", editProductId = "", editProdGroupItemId = "", editBrandId = "", productName = "",
            groupId = "", prodGroupItemId = "", brandId = "", strInvoiceNo = "", strWeight = "", strDescription = "";
    int editQuantity = 0, quantity = 1, discountChecked = 0, taxChecked = 0, mYear = 0, mMonth = 0, mDay = 0;
    int position = 0, customerIdPos = 0, groupIdPos = 0, prodGroupItemPos = 0, brandIdPos = 0, productIdPos = 0;
    double weight = 1, netWeight = 0, ratePrice = 0, taxPrice = 0, itemDiscountPrice = 0,
            discount = 0, totalDiscount = 0, cashDiscount = 0, otherCharges = 0, subTotal = 0,
            texableAmount = 0, productAmount = 0, productTotalAmount = 0, totalTexableAmount = 0,
            totalTaxPrice = 0, tax = 0, sgst = 0, cgst = 0, igst = 0, invoiceAmount = 0, cashAmount = 0, balanceAmount = 0;

    JsonArray jsonArray;
    SessionManager sessionManager;
    ArrayList<BeanTransactionUserItem> beanUserItems = new ArrayList<>();
    ArrayList<BeanBrandtItem> groupItem = new ArrayList<>();
    ArrayList<BeanBrandtItem> podGroupItem = new ArrayList<>();
    ArrayList<BeanBrandtItem> brandItems = new ArrayList<>();
    ArrayList<BeanAddProductItem> beanAddProductItems = new ArrayList<>();
    ArrayList<BeanPurchInvoiceProductItem> invoiceProductList = new ArrayList<>();

    BeanPurchaseItem beanPurchaseItem;
    BeanPurchInvoiceProductItem beanInvoiceProdItem;
    InvoiceProductAdapter invoiceProductAdapter;
    RecyclerView recyclerView;
    Dialog dialog;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_add_purchase_new, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        invoiceProductList = new ArrayList<>();
        selectLabel = mContext.getString(R.string.select) + " ";
        toolbarManage();
        initView();
        return view;
    }

    private void initView() {
        spinUser = view.findViewById(R.id.spinUser);
        tvDate = view.findViewById(R.id.tvDate);
        tvAddProd = view.findViewById(R.id.tvAddProd);
        ediInvoiceNo = view.findViewById(R.id.ediInvoiceNo);
        recyclerView = view.findViewById(R.id.recyclerViewProduct);
        ediCashDiscount = view.findViewById(R.id.ediCashDiscount);
        ediOtherCharges = view.findViewById(R.id.ediOtherCharges);
        ediCashAmount = view.findViewById(R.id.ediCashAmount);
        ediDescription = view.findViewById(R.id.ediDescription);

        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvSGST = view.findViewById(R.id.tvSGST);
        tvCGST = view.findViewById(R.id.tvCGST);
        tvIGST = view.findViewById(R.id.tvIGST);
        tvSubTotal = view.findViewById(R.id.tvSubTotal);
        tvInvoiceAmount = view.findViewById(R.id.tvInvoiceAmount);
        tvBalanceAmount = view.findViewById(R.id.tvBalanceAmount);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        formattedDate = getSimpleDate();
        tvDate.setText(formattedDate);

        Bundle bundle = getArguments();
        toolbar.setTitle(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.Purchase) + " " + mContext.getString(R.string.invoice));
        tvAddProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProdGroupItemId = "";
                editProductId = "";
                groupId = "";
                editBrandId = "";
                addEditDialog("add");

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
                calculateInvoiceValue();
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
                calculateInvoiceValue();
            }
        });
        ediCashAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cashAmount = 0;
                if (editable.length() > 0) {
                    cashAmount = Double.parseDouble(editable.toString());
                }
                calculateInvoiceValue();
            }
        });
        strSelectedId = "";
        if (bundle != null) {
            strType = bundle.getString("type");
            toolbar.setTitle(mContext.getString(R.string.Edit) + " " + mContext.getString(R.string.Purchase) + " " + mContext.getString(R.string.invoice));
            Gson gson = new Gson();
            String json = sessionManager.getValueSesion("album");
            beanPurchaseItem = gson.fromJson(json, BeanPurchaseItem.class);
            invoiceProductList = beanPurchaseItem.getInvoiceProductItems();
            initRecyclerViewInvoiceProductList();
            editInvoice();

        }

        getUserList();
    }


    public void addEditDialog(String from) {


        editProductId = "";
        productId = "";
        editProductId = "";
        editProdGroupItemId = "";
        editBrandId = "";
        productName = "";
        groupId = "";
        prodGroupItemId = "";
        brandId = "";
        strInvoiceNo = "";
        strWeight = "";
        strDescription = "";
        quantity = 1;
        discountChecked = 0;
        taxChecked = 0;
        customerIdPos = 0;
        groupIdPos = 0;
        prodGroupItemPos = 0;
        brandIdPos = 0;
        productIdPos = 0;
        weight = 1;
        netWeight = 0;
        ratePrice = 0;
        taxPrice = 0;
        itemDiscountPrice = 0;
        discount = 0;
        texableAmount = 0;
        productAmount = 0;
        tax = 0;

        dialog = new Dialog(mContext, R.style.Theme_Design_BottomSheetDialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_add_purchase_product);
        Button btnSave;
        Toolbar toolbarDialog;
        toolbarDialog = dialog.findViewById(R.id.toolbar);
        spinGroup = dialog.findViewById(R.id.spinGroup);
        spinItem = dialog.findViewById(R.id.spinItem);
        spinBrand = dialog.findViewById(R.id.spinBrand);
        spinProduct = dialog.findViewById(R.id.spinProduct);
        ediWeight = dialog.findViewById(R.id.ediWeight);
        ediPrice = dialog.findViewById(R.id.ediPrice);
        ediTotalWeight = dialog.findViewById(R.id.ediTotalWeight);
        ediTax = dialog.findViewById(R.id.ediTax);
        ediQty = dialog.findViewById(R.id.ediQty);
        ediDiscount = dialog.findViewById(R.id.ediDiscount);
        chkboxTax = dialog.findViewById(R.id.chkboxTax);
        chkboxDiscount = dialog.findViewById(R.id.chkboxDiscount);
        ediTaxable = dialog.findViewById(R.id.ediTaxable);
        ediProdTotal = dialog.findViewById(R.id.ediProdTotal);
        btnSave = dialog.findViewById(R.id.btnProductSave);
        ediTotalWeight.setEnabled(false);
        ediTaxable.setEnabled(false);
        ediProdTotal.setEnabled(false);
        ediTax.setHint(mContext.getString(R.string.tax) + " (%)");

        initBrandAndProduct();

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
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strWeight = ediWeight.getText().toString();
                if (chkboxTax.isChecked()) {
                    taxChecked = 1;
                }
                if (chkboxDiscount.isChecked()) {
                    discountChecked = 1;
                }
                if (groupId.length() == 0) {
                    showAlertWithButton(mContext, selectLabel + mContext.getString(R.string.group));
                } else if (productId.length() == 0) {
                    showAlertWithButton(mContext, selectLabel + mContext.getString(R.string.Product));
                } else if (weight == 0 && quantity == 0) {
                    ediQty.requestFocus();
                    ediWeight.requestFocus();
                    showAlertWithButton(mContext, mContext.getString(R.string.Weight) + " Or " + mContext.getString(R.string.Weight));

                } else {
                    System.out.println("productAmount===>>>" + productAmount);
                    beanInvoiceProdItem = new BeanPurchInvoiceProductItem(productId, groupId, productName, prodGroupItemId, brandId,
                            quantity, taxChecked, discountChecked, weight, netWeight, ratePrice, discount, itemDiscountPrice,
                            tax, taxPrice, texableAmount, productAmount);

                    if (from.equals("edit")) {
                        invoiceProductList.remove(position);
                    }
                    invoiceProductList.add(beanInvoiceProdItem);
                    initRecyclerViewInvoiceProductList();
                    dialog.dismiss();

                }
            }
        });
        initBrandAndProduct();
        toolbarDialog.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        if (from.equals("edit")) {
            toolbarDialog.setTitle(mContext.getString(R.string.Edit) + " " + mContext.getString(R.string.Product));
            groupId = beanInvoiceProdItem.getCategory_id();
            editBrandId = beanInvoiceProdItem.getItem_brand();
            editProdGroupItemId = beanInvoiceProdItem.getProduct_group_item();
            editProductId = beanInvoiceProdItem.getProduct_id();
            getProductGroupOrBrand("group", Constant.getProductGroupListAPI);
            editProduct();
        } else {
            toolbarDialog.setTitle(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.Product));
            getProductGroupOrBrand("group", Constant.getProductGroupListAPI);
        }
        dialog.show();

    }

    private void editProduct() {
        groupId = beanInvoiceProdItem.getCategory_id();
        ediQty.setText(beanInvoiceProdItem.getQty() + "");

        weight = 1;
        editQuantity = beanInvoiceProdItem.getQty();
        System.out.println("editQuantity>>>>" + editQuantity);
        ediQty.setText(editQuantity + "");
        ediWeight.setText(weight + "");
        ediPrice.setText(beanInvoiceProdItem.getRate() + "");
        ratePrice = beanInvoiceProdItem.getRate();
        ediTax.setText(beanInvoiceProdItem.getTax() + "");
        tax = beanInvoiceProdItem.getTax();
        ediDiscount.setText(beanInvoiceProdItem.getItem_discount() + "");

        if (beanInvoiceProdItem.getTax_check() == 1) {
            chkboxTax.setChecked(true);
        } else {
            chkboxTax.setChecked(false);
        }
        if (beanInvoiceProdItem.getDiscount_check() == 1) {
            chkboxDiscount.setChecked(true);
        }


    }

    private void setProductData(BeanAddProductItem beanAddProductItem) {

        texableAmount = 0;
        productTotalAmount = 0;
        productAmount = 0;
        ratePrice = 0;
        weight = 1;
        netWeight = 1;
        quantity = 1;
        discount = 0;
        System.out.println("setProductData  editProductId>>>" + editProductId);
        System.out.println("item weight>>>" + beanAddProductItem.getItem_weight());

        if (nullCheckFunction(beanAddProductItem.getItem_weight()).length() > 0) {
            weight = Double.parseDouble(beanAddProductItem.getItem_weight());
        }
        ratePrice = beanAddProductItem.getPrice();
        tax = beanAddProductItem.getTax();
        if (weight > 0) {
            ratePrice = ratePrice / weight;
        }

        ediQty.setText("" + quantity);
        ediPrice.setText(new DecimalFormat("#.##").format(ratePrice));
        ediWeight.setText(new DecimalFormat("#.##").format(weight));
        ediTax.setText(new DecimalFormat("#.##").format(tax));
        ediDiscount.setText("" + discount);
        if (beanAddProductItem.getTax_check() == 1) {
            chkboxTax.setChecked(true);
            taxChecked = beanAddProductItem.getTax_check();
        }

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
                            spinGroup.setTitle(mContext.getString(R.string.group));
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
                        } else if (strType.equalsIgnoreCase("productGroupItem")) {
                            System.out.println("  strType>>>>" + strType + "  editProdGroupItemId>>>>" + editProdGroupItemId);
                            prodGroupItemPos = 0;
                            podGroupItem = new ArrayList<>();
                            strSelect = selectLabel + mContext.getString(R.string.Product) + " " + mContext.getString(R.string.group) + " " + mContext.getString(R.string.Item);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            spinItem.setTitle(mContext.getString(R.string.Product) + " " + mContext.getString(R.string.group) + " " + mContext.getString(R.string.Item));
                            listItem.add(strSelect);
                            podGroupItem.add(new BeanBrandtItem("", strSelect, ""));
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);
                                listItem.add(jobj.getString("item_group"));
                                podGroupItem.add(new BeanBrandtItem(jobj.getString("id"),
                                        jobj.getString("item_group"), jobj.getString("category_id"),
                                        jobj.getString("category_name"), "code"));
                                if (editProdGroupItemId.equalsIgnoreCase(jobj.getString("id"))) {
                                    prodGroupItemPos = i + 1;
                                }
                            }
                            initSpinItem(listItem);
                        } else if (strType.equalsIgnoreCase("brand")) {
                            brandIdPos = 0;
                            brandItems = new ArrayList<>();
                            strSelect = selectLabel + mContext.getString(R.string.brand);
                            System.out.println("brandId===" + brandId);
                            spinBrand.setTitle(mContext.getString(R.string.brand));
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

    private void initSpinGroup(ArrayList<String> groupListItem) {

        ArrayAdapter<String> spinAdapterGroup = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, groupListItem);
        spinAdapterGroup.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinGroup.setAdapter(spinAdapterGroup);
        spinGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    System.out.println(" Group  groupId>>>" + groupId + "+ Group  groupItem>>>" + groupItem.size());
                    groupId = groupItem.get(position).getId();
                    getProductGroupOrBrand("brand", Constant.getProductBrandListAPI);
                    getProductGroupOrBrand("productGroupItem", Constant.getProdItemGroupListAPI);

                } else {
                    groupId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (groupIdPos < spinAdapterGroup.getCount()) {
            spinGroup.setSelection(groupIdPos);
        }
    }

    public void initBrandAndProduct() {

        //Brand
        brandId = "";
        brandIdPos = 0;
        productIdPos = 0;
        prodGroupItemPos = 0;
        productId = "";
        brandItems = new ArrayList<>();
        ArrayList<String> listItem = new ArrayList<>();
        String strSelect = selectLabel + mContext.getString(R.string.brand);
        listItem.add(strSelect);
        brandItems.add(new BeanBrandtItem("", strSelect, ""));

        initSpinBrand(listItem);

        podGroupItem = new ArrayList<>();
        listItem = new ArrayList<>();
        strSelect = selectLabel + mContext.getString(R.string.group) + mContext.getString(R.string.Item);
        listItem.add(strSelect);
        podGroupItem.add(new BeanBrandtItem("", strSelect, ""));

        initSpinItem(listItem);


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

        ArrayAdapter<String> spinAdapterBrand = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapterBrand.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinBrand.setAdapter(spinAdapterBrand);
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

        if (brandIdPos < spinAdapterBrand.getCount()) {
            spinBrand.setSelection(brandIdPos);
        }
    }

    private void initSpinItem(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapterItem = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapterItem.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinItem.setAdapter(spinAdapterItem);
        spinItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prodGroupItemId = podGroupItem.get(position).getId();
                    getProductList(prodGroupItemId);
                } else {
                    prodGroupItemId = "";
                    if (groupId.length() > 0) {
                        getProductList(groupId);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prodGroupItemId = "";
                if (groupId.length() > 0) {
                    getProductList(groupId);
                }
            }
        });

        if (prodGroupItemPos < spinAdapterItem.getCount()) {
            spinItem.setSelection(prodGroupItemPos);
        }
    }

    private void initSpinProduct(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapterProd = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapterProd.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinProduct.setAdapter(spinAdapterProd);
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

        if (productIdPos < spinAdapterProd.getCount()) {
            spinProduct.setSelection(productIdPos);
        }

    }


    public void getProductList(String filterId) {
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
                        spinProduct.setTitle(mContext.getString(R.string.Product));
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
                .addEncoded("id", filterId)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getUserProductListAPI);

    }

    public void initRecyclerViewInvoiceProductList() {
        totalDiscount = 0;
        totalTaxPrice = 0;
        subTotal = 0;
        productTotalAmount = 0;
        totalTexableAmount = 0;
        invoiceProductAdapter = new InvoiceProductAdapter(mContext, invoiceProductList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(invoiceProductAdapter);
        jsonArray = new JsonArray();
        if (!invoiceProductList.isEmpty()) {
            for (int i = 0; i < invoiceProductList.size(); i++) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", invoiceProductList.get(i).getProduct_id());
                obj.addProperty("name", invoiceProductList.get(i).getName());
                obj.addProperty("category_id", invoiceProductList.get(i).getCategory_id());
                obj.addProperty("brand_id", invoiceProductList.get(i).getItem_brand());
                obj.addProperty("group_item", invoiceProductList.get(i).getProduct_group_item());
                obj.addProperty("qty", invoiceProductList.get(i).getQty());
                obj.addProperty("weight", invoiceProductList.get(i).getWeight());
                obj.addProperty("net_weight", invoiceProductList.get(i).getNet_weight());
                obj.addProperty("price_rate", invoiceProductList.get(i).getRate());
                obj.addProperty("tax", invoiceProductList.get(i).getTax());
                obj.addProperty("tax_check", invoiceProductList.get(i).getTax_check());
                obj.addProperty("tax_amt", invoiceProductList.get(i).getTaxPrice());
                obj.addProperty("discount", invoiceProductList.get(i).getItem_discount());
                obj.addProperty("discount_check", invoiceProductList.get(i).getDiscount_check());
                obj.addProperty("discount_amt", invoiceProductList.get(i).getDiscountPrice());
                obj.addProperty("taxable_amt", invoiceProductList.get(i).getTaxable_invo());
                obj.addProperty("gross", invoiceProductList.get(i).getGross());
                groupId = invoiceProductList.get(i).getCategory_id();

                jsonArray.add(obj);
                totalDiscount = totalDiscount + invoiceProductList.get(i).getDiscountPrice();
                totalTaxPrice = totalTaxPrice + invoiceProductList.get(i).getTaxPrice();

                totalTexableAmount = totalTexableAmount + invoiceProductList.get(i).getTaxable_invo();
                productTotalAmount = productTotalAmount + invoiceProductList.get(i).getGross();
                subTotal = totalTexableAmount;

            }

            System.out.println("product_array>>>>>>" + jsonArray.toString());

        }
        calculateInvoiceValue();

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

        itemDiscountPrice = 0;
        subTotal = 0;
        productAmount = 0;
        texableAmount = 0;
        System.out.println("quantity>>>>" + quantity);
        System.out.println("weight>>>>" + weight);
        if (quantity > 0 && weight > 0) {
            netWeight = quantity * weight;
        } else if (quantity > 0 && weight == 0) {
            netWeight = quantity;
        } else {
            netWeight = weight;
        }
        System.out.println("netWeight>>>>" + netWeight);
        if (ratePrice > 0 && netWeight > 0) {
            texableAmount = ratePrice * netWeight;
        }
        itemDiscountPrice = discount;
        if (texableAmount > 0 && discount > 0 && discountChecked == 1) {
            itemDiscountPrice = texableAmount * discount / 100;
        }
        itemDiscountPrice = itemDiscountPrice * quantity;
        texableAmount = texableAmount - itemDiscountPrice;
        if (tax > 0 && texableAmount > 0) {
            taxPrice = (texableAmount * tax) / 100;
        }
        if (!chkboxTax.isChecked()) {
            productAmount = texableAmount + taxPrice;
        } else {
            productAmount = texableAmount;
        }
        productAmount = productAmount + itemDiscountPrice;
        productAmount = Math.round(productAmount);
        System.out.println("taxPrice>>>" + taxPrice);
        System.out.println("itemDiscountPrice=>>>" + itemDiscountPrice);
        System.out.println("productAmount=>>>" + productAmount);

        String strNetWeight = new DecimalFormat("#.##").format(netWeight);
        String strTaxableAmount = new DecimalFormat("#.##").format(texableAmount);
        String strNetAmount = new DecimalFormat("#.##").format(productAmount);
        ediProdTotal.setText(Html.fromHtml(strNetAmount));
        ediTaxable.setText(Html.fromHtml(strTaxableAmount));
        ediTotalWeight.setText(Html.fromHtml(strNetWeight));

    }

    public void calculateInvoiceValue() {
        String strSGST = "", strCGST = "", strIGST = "", strDiscount = "", strSubTotal = "";
        invoiceAmount = 0;
        balanceAmount = 0;
        strDiscount = mContext.getString(R.string.discount) + " :" + new DecimalFormat("#.##").format(totalDiscount);
        cgst = totalTaxPrice / 2;
        sgst = cgst;
        invoiceAmount = totalTexableAmount;
        subTotal = totalTexableAmount + totalDiscount;
        double gross = totalTexableAmount + totalTaxPrice + otherCharges;
        balanceAmount = gross - cashDiscount;
        balanceAmount = balanceAmount - cashAmount;


        balanceAmount = Math.round(balanceAmount);
        System.out.println("invoiceAmount=>>>" + invoiceAmount);
        System.out.println("balanceAmount=>>>" + balanceAmount);

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
        tvBalanceAmount.setText(mContext.getString(R.string.balance) + mContext.getString(R.string.Amount) + ": " + new DecimalFormat("#.##").format(balanceAmount));


    }

    private void editInvoice() {
        invoiceId = beanPurchaseItem.getInvoice_id();
        editUserId = beanPurchaseItem.getCustomer_id();
        strSelectedId = beanPurchaseItem.getCustomer_id();
        ediInvoiceNo.setText(beanPurchaseItem.getInvoice_number());
        ediInvoiceNo.setEnabled(false);
        tvDate.setText(dateDDMMYY(beanPurchaseItem.getInvoice_date()));
        System.out.println("edit cashDiscount===" + beanPurchaseItem.getCash_discount());
        ediCashDiscount.setText(beanPurchaseItem.getCash_discount() + "");
        ediOtherCharges.setText(beanPurchaseItem.getOther_charg() + "");
        ediCashAmount.setText(beanPurchaseItem.getCash_div() + "");
        ediDescription.setText(beanPurchaseItem.getMsg_on_statement());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvDate:
                getDate();
                break;
            case R.id.btnSubmit:

                strInvoiceNo = ediInvoiceNo.getText().toString().trim();
                strDescription = ediDescription.getText().toString().trim();
                if (strSelectedId.length() == 0) {
                    showAlertWithButton(mContext, selectLabel + mContext.getString(R.string.Customer));
                } else if (invoiceProductList.isEmpty()) {
                    showAlertWithButton(mContext, mContext.getString(R.string.Add_Product));
                } else if (strInvoiceNo.length() == 0) {
                    showAlertWithButton(mContext, mContext.getString(R.string.invoice));
                } else {
                    addProductPurchase();
                }
        }
    }

    @Override
    public void onUpdateList(int pos, String from) {
        position = pos;
        if (from.equals("delete")) {
            System.out.println("delete>>>" + invoiceProductList);
            invoiceProductList.remove(pos);
            initRecyclerViewInvoiceProductList();
        } else if (from.equals("edit")) {
            beanInvoiceProdItem = invoiceProductList.get(position);
            addEditDialog(from);
        }
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

    public void getUserList() {
        beanUserItems = new ArrayList<>();
        customerIdPos = 0;

        ArrayList<String> listItem = new ArrayList<>();

        Gson gson = new Gson();

        String json = sessionManager.getValueSesion(SessionManager.KEY_ProdCustomerList);
        String jsonSpin = sessionManager.getValueSesion(SessionManager.KEY_ProCustomerSpinItem);
        beanUserItems.addAll(gson.fromJson(json, new TypeToken<ArrayList<BeanTransactionUserItem>>() {
        }.getType()));
        listItem.addAll(gson.fromJson(jsonSpin, new TypeToken<ArrayList<String>>() {
        }.getType()));

        if (editUserId.length() > 0) {
            for (int i = 0; i < beanUserItems.size(); i++) {
                if (editUserId.equalsIgnoreCase(beanUserItems.get(i).getId())) {

                    customerIdPos = i;
                }
            }
        }


        initSpinUser(listItem);

    }

    private void initSpinUser(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinUser.setAdapter(spinAdapter);
        spinUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    strSelectedId = beanUserItems.get(position).getId();
                    strUserName = beanUserItems.get(position).getName();
                } else {
                    strSelectedId = "";
                    editUserId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinUser.setSelection(customerIdPos);
    }

    public void addProductPurchase() {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        UtilityMethod.showAlertBox(mContext, jsonObject.getString("user_status_message"));

                        printProductInvoiceReceipt(mContext, strType, strInvoiceNo, formattedDate,
                                strSelectedId, strUserName, cashDiscount, otherCharges, invoiceAmount, invoiceProductList);
                        getActivity().onBackPressed();
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, jsonObject.getString("user_status_message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        System.out.println("strTable==>>>>" + strTable);
        System.out.println("strType==>>>>" + strType);
        System.out.println("groupId==>>>>" + groupId);
        System.out.println("strSelectedId==>>>>" + strSelectedId);
        System.out.println("strInvoiceNo==>>>>" + strInvoiceNo);
        System.out.println("prodjsonArray=>>>>" + jsonArray.toString());
        System.out.println("cashDiscount=>>>>" + cashDiscount);
        System.out.println("otherCharges=>>>>" + otherCharges);
        System.out.println("invoiceAmount=>>>>" + invoiceAmount);


        RequestBody body = new FormEncodingBuilder()
                .addEncoded("table", strTable)
                .addEncoded("type", strType)
                .addEncoded("invoice_id", invoiceId)
                .addEncoded("category_id", groupId)
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("invoice_date", formattedDate)
                .addEncoded("customer_id", strSelectedId)
                .addEncoded("invoice_number", strInvoiceNo)
                .addEncoded("product_array", jsonArray.toString())
                .addEncoded("sgst_inv", sgst + "")
                .addEncoded("cgst_inv", cgst + "")
                .addEncoded("igst_inv", igst + "")
                .addEncoded("cash_discount", cashDiscount + "")
                .addEncoded("subtotal", subTotal + "")
                .addEncoded("taxable_invo", totalTexableAmount + "")
                .addEncoded("other_charg", otherCharges + "")
                .addEncoded("net_amount", invoiceAmount + "")
                .addEncoded("balance_invo", balanceAmount + "")
                .addEncoded("msg_on_statement", strDescription + "")
                .build();


        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.addPurchaseInvoiceAPI);
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

}
