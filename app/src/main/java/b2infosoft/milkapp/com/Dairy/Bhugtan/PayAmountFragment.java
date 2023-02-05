package b2infosoft.milkapp.com.Dairy.Bhugtan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.SellProduct;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getCalanderDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;

public class PayAmountFragment extends Fragment implements View.OnClickListener,
        View.OnKeyListener,
        FragmentBackPressListener {

    String strdate = "";
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    RadioGroup radioGroup;
    RadioButton rdFullAmt, rdOtherAmt;
    TextView tvDate, tvFullAmount, tvRemainAmt;
    EditText edtAmtType, edtOtherAmount;
    Button btnPayAmt;
    String fullAmt, amount = "", amountType = "";
    String strRemainAmt = "";
    String type = "", fromwhere = "", unic_customer = "", name = "", selectedId = "", fatherName = "";
    Fragment fragment = null;
    Bundle bundle = null;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_paynow, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        bundle = getArguments();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        edtAmtType = view.findViewById(R.id.edtAmtType);
        radioGroup = view.findViewById(R.id.radioGroup);
        rdFullAmt = view.findViewById(R.id.rdFullAmt);
        rdOtherAmt = view.findViewById(R.id.rdOtherAmt);
        tvDate = view.findViewById(R.id.tvDate);
        tvFullAmount = view.findViewById(R.id.tvFullAmount);
        edtOtherAmount = view.findViewById(R.id.edtOtherAmount);
        tvRemainAmt = view.findViewById(R.id.tvRemainAmt);
        btnPayAmt = view.findViewById(R.id.btnPayAmt);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        tvRemainAmt.setVisibility(View.GONE);
        edtOtherAmount.setVisibility(View.GONE);

        toolbar_title.setText(mContext.getString(R.string.Pay));
        strdate = getSimpleDate();

        tvDate.setText(strdate);
        tvDate.setOnClickListener(this);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnFragmentBackPressListener();
            }
        });

        if (bundle != null) {
            fullAmt = bundle.getString("fullAmt");
            fromwhere = bundle.getString("fromwhere");
            type = bundle.getString("type");
            selectedId = bundle.getString("CustomerId");
            unic_customer = bundle.getString("unic_customer");
            name = bundle.getString("CustomerName");
            fatherName = bundle.getString("CustomerFatherName");
        }
        tvFullAmount.setText(fullAmt);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdFullAmt:
                        tvFullAmount.setVisibility(View.VISIBLE);
                        edtOtherAmount.setVisibility(View.GONE);
                        tvRemainAmt.setVisibility(View.GONE);

                        amount = tvFullAmount.getText().toString().trim();
                        break;
                    case R.id.rdOtherAmt:
                        tvFullAmount.setVisibility(View.GONE);
                        edtOtherAmount.setVisibility(View.VISIBLE);
                        tvRemainAmt.setVisibility(View.VISIBLE);
                        amount = edtOtherAmount.getText().toString().trim();
                        break;
                    default:
                }
                System.out.println("amount=====" + amount);
            }
        });
        edtOtherAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double fullamt = 0;
                double amt = 0;
                double remaining = 0;
                if (s.length() == 0) {
                    tvRemainAmt.setText("");


                }


                try {
                    if (fullAmt.length() == 0) {
                        fullamt = 0;
                    }
                    fullamt = Double.parseDouble(fullAmt);
                    amt = Double.parseDouble(s.toString());
                    remaining = fullamt - amt;
                    strRemainAmt = String.format("%.2f", remaining);

                    tvRemainAmt.setText(mContext.getString(R.string.REMAIN_AMOUNT) + ":-   " + strRemainAmt);
                } catch (NumberFormatException ex) { // handle your exception

                }
            }
        });

        btnPayAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strdate = tvDate.getText().toString();
                amountType = edtAmtType.getText().toString();
                if (rdFullAmt.isChecked()) {
                    amount = fullAmt;
                } else {
                    amount = edtOtherAmount.getText().toString();
                }
                if (amountType.length() == 0) {
                    showToast(mContext, "please enter amount type");
                    edtAmtType.requestFocus();
                } else if (amount.length() == 0) {
                    showToast(mContext, mContext.getString(R.string.Please_Enter_Amount));
                    edtAmtType.requestFocus();
                    edtAmtType.clearFocus();
                } else {
                    edtAmtType.clearFocus();
                    edtAmtType.clearFocus();
                    payAmount();
                }
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
        return view;

    }


    private void payAmount() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {

                        fragment = new SignatureFragment();
                        bundle = new Bundle();
                        bundle.putString("CustomerId", selectedId);
                        bundle.putString("CustomerName", name);
                        bundle.putString("CustomerFatherName", fatherName);
                        bundle.putString("transactionID", mainObject.getString("id"));
                        bundle.putString("type", type);
                        bundle.putString("unic_customer", unic_customer);
                        bundle.putString("fromwhere", fromwhere);
                        fragment.setArguments(bundle);
                        goNextFragmentReplace(mContext, fragment);

                    } else {
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Amount_Paying_Fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            JSONArray productArray = new JSONArray();
            JSONObject mainObject = new JSONObject();
            JSONObject productData = new JSONObject();
            try {
                productData.put("products_id", "0");
                productData.put("qty", "1");
                productData.put("total_price", amount);
                productData.put("products_name", amountType);
                productArray.put(productData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mainObject.put("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
            mainObject.put("total_price", amount);
            mainObject.put("transactions_date", strdate);
            mainObject.put("total_product", "1");
            mainObject.put("customer_id", selectedId);
            mainObject.put("list_data", productArray);
            System.out.println("mainObject=>>>>" + mainObject.toString());
            RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
            caller.addRequestBody(body);
            caller.execute(SellProduct);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDate:
                getCalanderDate(mContext, tvDate);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {


        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                OnFragmentBackPressListener();
                return true;
            }
        }

        return false;
    }


    @Override
    public void OnFragmentBackPressListener() {
        if (fromwhere.equalsIgnoreCase("TransactionHistory")) {
            fragment = new TransactionOldHistoryFragment();
            bundle = new Bundle();
            bundle.putString("CustomerId", selectedId);
            bundle.putString("CustomerName", name);
            bundle.putString("unic_customer", unic_customer);
            bundle.putString("CustomerFatherName", fatherName);
            fragment.setArguments(bundle);
            goNextFragmentReplace(mContext, fragment);
        } else {
            getActivity().onBackPressed();
        }
    }
}
