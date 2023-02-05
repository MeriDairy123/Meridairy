package b2infosoft.milkapp.com.Dairy.PaymentVoucher;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import b2infosoft.milkapp.com.Advertisement.ImagePickerAcitvity;
import b2infosoft.milkapp.com.Model.BeanBrandtItem;
import b2infosoft.milkapp.com.Model.BeanTransactionUserItem;
import b2infosoft.milkapp.com.Model.BeanVoucherItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_IMAGE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getCameraIntent;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getGalleryIntent;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showSettingsDialog;


public class AddPaymentVoucherFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Voucher>>>";

    Context mContext;
    Toolbar toolbar;

    TextView tvDate, tvBalanceAmt, tvUploadPhoto;
    Spinner spinPayment, spinUser;
    EditText ediReferenceNo, ediChequeNo, ediBankName, ediAccountNo, ediAmount, ediDescription;


    Button btnSubmit;
    String selectLabel = "", formattedDate = "", strUrl = "", strFrom = "Pay", strType = "add",
            dairyId = "", strSelectedId = "", strUnqCustomer = "", editUserId = "",
            invoiceId = "", strPaymentType = "", strRefNo = "", strBankName = "",
            strBankAcc = "", strChequeNo = "", strDescription = "";
    int mYear = 0, mMonth = 0, mDay = 0;
    int brandIdPos = 0, customerIdPos = 0;
    File file;
    String filePath = "";
    double amount = 0;
    SessionManager sessionManager;
    ArrayList<BeanBrandtItem> paymentItems = new ArrayList<>();
    ArrayList<BeanTransactionUserItem> beanUserItems = new ArrayList<>();
    BeanVoucherItem beanVoucherItem;

    View view, layoutBank, layoutCheque;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vouchar_pay, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);

        selectLabel = mContext.getString(R.string.select) + " ";
        toolbarManage();
        initView();
        return view;
    }

    private void initView() {
        tvDate = view.findViewById(R.id.tvDate);
        tvUploadPhoto = view.findViewById(R.id.tvUploadPhoto);
        tvBalanceAmt = view.findViewById(R.id.tvBalanceAmt);
        ediReferenceNo = view.findViewById(R.id.ediReferenceNo);
        spinUser = view.findViewById(R.id.spinUser);
        spinPayment = view.findViewById(R.id.spinPayment);

        ediChequeNo = view.findViewById(R.id.ediChequeNo);
        ediAccountNo = view.findViewById(R.id.ediAccountNo);
        ediBankName = view.findViewById(R.id.ediBankName);
        ediAmount = view.findViewById(R.id.ediAmount);
        ediDescription = view.findViewById(R.id.ediDescription);
        layoutBank = view.findViewById(R.id.layoutBank);
        layoutCheque = view.findViewById(R.id.layoutCheque);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvBalanceAmt.setVisibility(View.GONE);
        tvDate.setOnClickListener(this);
        tvUploadPhoto.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        formattedDate = getSimpleDate();
        tvDate.setText(formattedDate);

        Bundle bundle = getArguments();
        toolbar.setTitle(mContext.getString(R.string.Pay) + " " + mContext.getString(R.string.voucher) + " " + mContext.getString(R.string.invoice));
        if (bundle != null) {
            strType = bundle.getString("type");
            strFrom = bundle.getString("title");
            strUrl = bundle.getString("url");

            toolbar.setTitle(strFrom + " " + mContext.getString(R.string.voucher));
            if (strType.equalsIgnoreCase("add")) {
                toolbar.setTitle(mContext.getString(R.string.Add) + " " + strFrom + " " + mContext.getString(R.string.payment));

            } else {
                toolbar.setTitle(mContext.getString(R.string.Edit) + " " + strFrom + " " + mContext.getString(R.string.payment));

                Gson gson = new Gson();
                String json = sessionManager.getValueSesion("album");
                beanVoucherItem = gson.fromJson(json, BeanVoucherItem.class);
                setVoucher(beanVoucherItem);
            }
        }
        initSpinPayment();
        getUserList();
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


    private void initSpinPayment() {
        brandIdPos = 0;
        paymentItems = new ArrayList<>();
        ArrayList<String> listItem = new ArrayList<>();
        String strSelect = selectLabel + mContext.getString(R.string.paymentType);
        String cash = mContext.getString(R.string.cash);
        String bank = mContext.getString(R.string.bank);
        String cheque = mContext.getString(R.string.cheque);
        listItem.add(strSelect);
        listItem.add(cash);
        listItem.add(bank);
        listItem.add(cheque);

        paymentItems.add(new BeanBrandtItem("", strSelect, ""));
        paymentItems.add(new BeanBrandtItem("", cash, "Cash"));
        paymentItems.add(new BeanBrandtItem("", bank, "Bank"));
        paymentItems.add(new BeanBrandtItem("", cheque, "Cheque"));

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinPayment.setAdapter(spinAdapter);
        spinPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    strPaymentType = paymentItems.get(position).getCode();
                } else {
                    strPaymentType = "";
                }
                checkPayType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkPayType();

        spinPayment.setSelection(brandIdPos);
    }

    private void checkPayType() {
        System.out.println("payIdPos>>>>" + brandIdPos);
        System.out.println("strPaymentType>>>>" + strPaymentType);
        if (strPaymentType.equalsIgnoreCase("Cash")) {
            brandIdPos = 1;
            layoutBank.setVisibility(View.GONE);
            layoutCheque.setVisibility(View.GONE);
            filePath = "";
            ediBankName.setText("");
            ediAccountNo.setText("");
            ediChequeNo.setText("");
        } else if (strPaymentType.equalsIgnoreCase("Bank")) {
            layoutBank.setVisibility(View.VISIBLE);
            layoutCheque.setVisibility(View.GONE);
            filePath = "";

            ediChequeNo.setText("");
            brandIdPos = 2;
        } else if (strPaymentType.equalsIgnoreCase("Cheque")) {
            layoutBank.setVisibility(View.GONE);
            layoutCheque.setVisibility(View.VISIBLE);
            ediBankName.setText("");
            ediAccountNo.setText("");
            brandIdPos = 3;
        } else {
            brandIdPos = 0;
            layoutBank.setVisibility(View.GONE);
            layoutCheque.setVisibility(View.GONE);
            filePath = "";
            ediBankName.setText("");
            ediAccountNo.setText("");
            ediChequeNo.setText("");
            tvUploadPhoto.setText("");
        }
    }

    private void initSpinUser(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spinUser.setAdapter(spinAdapter);
        spinUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double balanceAmt = 0;
                if (position > 0) {
                    tvBalanceAmt.setVisibility(View.VISIBLE);
                    strSelectedId = beanUserItems.get(position).getId();
                    strUnqCustomer = beanUserItems.get(position).getUnic_customer();
                    balanceAmt = beanUserItems.get(position).getBalance();
                    if (balanceAmt > 0) {
                        tvBalanceAmt.setText(mContext.getString(R.string.debit) + ":  " + String.format("%.2f", balanceAmt));
                        tvBalanceAmt.setBackgroundColor(mContext.getResources().getColor(R.color.colorGreen));
                    } else {
                        balanceAmt = Math.abs(balanceAmt);
                        tvBalanceAmt.setText(mContext.getString(R.string.credit) + ":  " + String.format("%.2f", balanceAmt));
                        tvBalanceAmt.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));

                    }

                } else {
                    strSelectedId = "";
                    tvBalanceAmt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        System.out.println("customerIdPos>>>>" + customerIdPos);

        spinUser.setSelection(customerIdPos);
    }

    private void setVoucher(BeanVoucherItem album) {
        invoiceId = album.getInvoice_id();
        editUserId = album.getCustomer_id();
        strPaymentType = album.getP_type();
        filePath = album.getImage();


        System.out.println("editProductId>>>" + strSelectedId);
        amount = album.getDr();
        strBankAcc = album.getAc_no();
        strBankName = album.getBank_name();
        strRefNo = album.getReffrence_no();
        strChequeNo = album.getCheque_no();
        strDescription = album.getDescription();

        tvDate.setText(album.getReceipt_date());

        ediReferenceNo.setText(strRefNo);
        ediBankName.setText(strBankName);
        ediAccountNo.setText(strBankAcc);
        ediChequeNo.setText(strChequeNo);
        ediAmount.setText(new DecimalFormat("#.##").format(amount));
        tvUploadPhoto.setText(album.getImage());
        ediDescription.setText(strDescription);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvUploadPhoto:
                Dexter.withActivity((Activity) mContext)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {

                                    showImagePickerOptions();
                                }
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog(((Activity) mContext));
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();


                break;
            case R.id.tvDate:
                getDate();
                break;
            case R.id.btnSubmit:
                amount = 0;
                if (ediAmount.getText().toString().length() > 0) {
                    amount = Double.parseDouble(ediAmount.getText().toString().trim());
                }
                strRefNo = ediReferenceNo.getText().toString().trim();
                strChequeNo = ediChequeNo.getText().toString().trim();
                strBankName = ediBankName.getText().toString().trim();
                strBankAcc = ediAccountNo.getText().toString().trim();
                strDescription = ediDescription.getText().toString().trim();
                if (strSelectedId.length() == 0) {
                    showAlertWithButton(mContext, selectLabel + mContext.getString(R.string.Customer));
                } else if (strPaymentType.length() == 0) {
                    showAlertWithButton(mContext, selectLabel + mContext.getString(R.string.paymentType));
                } else if (amount <= 0) {
                    ediReferenceNo.clearFocus();
                    ediAmount.requestFocus();
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Enter_Amount));
                } else if (layoutBank.getVisibility() == View.VISIBLE) {
                    if (strBankName.length() == 0) {
                        ediBankName.requestFocus();
                        ediAmount.clearFocus();
                        showAlertWithButton(mContext, mContext.getString(R.string.bankName));
                    } else if (strBankAcc.length() < 9) {
                        ediBankName.clearFocus();
                        ediAccountNo.requestFocus();
                        showAlertWithButton(mContext, mContext.getString(R.string.accountNo));
                    } else {
                        ediReferenceNo.clearFocus();
                        ediAmount.clearFocus();
                        ediBankName.clearFocus();
                        ediAccountNo.clearFocus();
                        addPaymentVoucher();
                    }
                } else if (layoutCheque.getVisibility() == View.VISIBLE) {

                    if (strChequeNo.length() == 0) {
                        ediChequeNo.requestFocus();
                        ediAmount.clearFocus();
                        showAlertWithButton(mContext, mContext.getString(R.string.chequeNo));
                    } else if (filePath.length() == 0) {
                        ediChequeNo.clearFocus();
                        showAlertWithButton(mContext, mContext.getString(R.string.PleaseUploadImageFile));
                    } else {
                        ediReferenceNo.clearFocus();
                        ediAmount.clearFocus();
                        ediChequeNo.clearFocus();
                        addPaymentVoucher();
                    }
                } else {
                    addPaymentVoucher();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getParcelableExtra("path");
                if (uri != null) {
                    try {
                        filePath = uri.getPath();
                        file = new File(filePath);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                        tvUploadPhoto.setText(file.getName());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void addPaymentVoucher() {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        String voucharType = "recieve";
                        if (strFrom.equals(mContext.getString(R.string.Pay))) {
                            voucharType = "debit";
                        }
                        Fragment fragment = new VoucharSignatureFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("FromWhere", strFrom);
                        bundle.putString("CustomerId", strSelectedId);
                        bundle.putString("transactionID", jsonObject.getString("transactions_id"));
                        bundle.putString("type", voucharType);
                        bundle.putString("unic_customer", strUnqCustomer);
                        fragment.setArguments(bundle);
                        goNextFragmentReplace(mContext, fragment);

                    } else {
                        UtilityMethod.showAlertWithButton(mContext, jsonObject.getString("user_status_message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        multipartBuilder.addFormDataPart("dairy_id", dairyId);
        multipartBuilder.addFormDataPart("type", strType);
        multipartBuilder.addFormDataPart("invoice_id", invoiceId);
        multipartBuilder.addFormDataPart("receipt_date", formattedDate);
        multipartBuilder.addFormDataPart("customer_id", strSelectedId);
        multipartBuilder.addFormDataPart("p_type", strPaymentType);
        multipartBuilder.addFormDataPart("reffrence_no", strRefNo);
        multipartBuilder.addFormDataPart("dr", amount + "");
        multipartBuilder.addFormDataPart("acno", strBankAcc);
        multipartBuilder.addFormDataPart("bank_name", strBankName);
        multipartBuilder.addFormDataPart("cheque", strChequeNo);
        multipartBuilder.addFormDataPart("description", strDescription);
        if (file != null) {
            multipartBuilder.addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
        }
        RequestBody body = multipartBuilder.build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(strUrl);
    }


    public void getUserList() {
        beanUserItems = new ArrayList<>();
        customerIdPos = 0;
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<String> listItem = new ArrayList<>();
                    listItem.add(selectLabel + mContext.getString(R.string.Customer));
                    beanUserItems.add(new BeanTransactionUserItem("",
                            selectLabel + mContext.getString(R.string.Customer), "", "", 0, 0, 0));
                    if (jsonObject.getString("status").equals("success")) {
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            String userType = mContext.getString(R.string.Seller);
                            if (jobj.getInt("user_group_id") == 4) {
                                userType = mContext.getString(R.string.Buyer);
                            }

                            listItem.add(jobj.getString("unic_customer") + ". " + jobj.getString("name") + " (" + userType + ")");
                            beanUserItems.add(new BeanTransactionUserItem(jobj.getString("id"),
                                    jobj.getString("name"), nullCheckFunction(jobj.getString("unic_customer")),
                                    jobj.getString("user_group_id"), jobj.getDouble("total_dr"), jobj.getDouble("total_cr"),
                                    jobj.getDouble("balance")));
                            if (editUserId.equalsIgnoreCase(jobj.getString("id"))) {
                                customerIdPos = i + 1;
                            }
                        }
                    }
                    initSpinUser(listItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getUserTransactionBalanceAPI);

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

                        day = checkDigit(dayOfMonth);
                        tvDate.setText(day + "-" + month + "-" + year);
                        formattedDate = day + "-" + month + "-" + year;
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

    private void showImagePickerOptions() {
        ImagePickerAcitvity.showImagePickerOptions(mContext, new ImagePickerAcitvity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                Intent intent = getCameraIntent(getActivity());
                startActivityForResult(intent, REQUEST_IMAGE);
            }

            @Override
            public void onChooseGallerySelected() {
                Intent intent = getGalleryIntent(getActivity());
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
