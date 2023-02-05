package b2infosoft.milkapp.com.Dairy.ViewMilkEntry;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import b2infosoft.milkapp.com.Dairy.Customer.AddCustomerFragment;
import b2infosoft.milkapp.com.Dairy.Customer.CustomerSallerListFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.Adapter.ViewMilkEntryAdapter;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnDownLoadListener;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.Model.TenDaysMilkSellHistory;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.DownloadFileFromURL;
import b2infosoft.milkapp.com.useful.PDFUtills;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintTenDaysReciept;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.ViewUserEntryEndDate;
import static b2infosoft.milkapp.com.appglobal.Constant.ViewUserEntryStartDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;

public class ViewSellerMilkEntryFragment extends Fragment implements View.OnClickListener, OnDownLoadListener {

    AutoCompleteTextView actv_CustomerID;
    TextView et_Cname_fathername, tvStartDate, tvEndDate, tvTotalWeight, tvTotalFat, tvFatRat, tvTotalAmt, tvTotalBonus;
    ImageView ivUserIcon;
    View lvStartDate, lvEndDate;
    Button btnSubmit;//btnVerify;
    RecyclerView recycler_allEntry;
    CircularProgressBar yourCircularProgressbar;
    Context mContext;
    String formattedDate = "";
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    DownloadFileFromURL downloadFileFromURL;

    String pdfUrl = "", filePath = "", folderName = "", dairyid = "", selectedName = "", selectedFatherName = "", selectedId = "",
            from_date = "", to_date = "";
    Double fateD = 0d, weight = 0d, rsPerKg = 0d, total = 0d, rate = 0d, totalBonus = 0.0;
    Bundle bundle = null;
    String entryID = "", unic_customer = "";
    ImageView imgPrint;
    ArrayList<TenDaysMilkSellHistory> pdfEntryList = new ArrayList<>();
    ProgressDialog progressDialog = null;
    int Totcount = 0, count = 0;
    String printTypeDialog = "";
    Fragment fragment = null;
    View view;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String Pdfcreted = "";
    private String avg_fat_tenDays = "", avg_rate_tenDays = "", total_milk_tenDays = "", grnd_total_amt_tenDays = "", user_data_name = "";
    private String user_data_unic_customer = "", user_data_phone_number = "";
    private Document document;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_user_all_entry, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        downloadFileFromURL = new DownloadFileFromURL(this.mContext, this);
        initView();
        return view;
    }

    private void initView() {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);

        toolbar_title.setText(mContext.getString(R.string.View_All_Entry));
        imgPrint = toolbar.findViewById(R.id.imgPrint);
        actv_CustomerID = view.findViewById(R.id.actv_CustomerID);
        ivUserIcon = view.findViewById(R.id.ivUserIcon);
        et_Cname_fathername = view.findViewById(R.id.tvFathername);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tvTotalFat = view.findViewById(R.id.tvTotalFat);
        tvFatRat = view.findViewById(R.id.tvFatRat);
        tvTotalAmt = view.findViewById(R.id.tvTotalAmt);
        tvTotalBonus = view.findViewById(R.id.tvTotalBonus);
        lvStartDate = view.findViewById(R.id.lvStartDate);
        lvEndDate = view.findViewById(R.id.lvEndDate);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        imgPrint.setVisibility(View.VISIBLE);
        imgPrint.setOnClickListener(this);
        if (FromWhere.equalsIgnoreCase("Bhugtan")) {
            toolbar.setNavigationIcon(R.drawable.back_arrow);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FromWhere.equalsIgnoreCase("Bhugtan")) {
                    getActivity().onBackPressed();
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);

        recycler_allEntry = view.findViewById(R.id.recycler_allEntry);
        yourCircularProgressbar = view.findViewById(R.id.yourCircularProgressbar);
        btnSubmit.setOnClickListener(this);
        bundle = getArguments();
        if (bundle != null) {
            entryID = "";
            selectedId = bundle.getString("CustomerId");
            selectedName = bundle.getString("CustomerName");
            unic_customer = bundle.getString("unic_customer");
            selectedFatherName = bundle.getString("CustomerFatherName");
            if (bundle.getString("startDate") != null) {
                Constant.ViewUserEntryStartDate = bundle.getString("startDate");
                Constant.ViewUserEntryEndDate = bundle.getString("endDate");
            }
            if (bundle.getString("CustomerId") != null && bundle.getString("CustomerName") != null && bundle.getString("CustomerFatherName") != null) {
                actv_CustomerID.setText(bundle.getString("unic_customer"));
                System.out.print("strSelectedId====>>>>" + selectedId + " " + selectedFatherName + " " + selectedName);
                et_Cname_fathername.setText(bundle.getString("CustomerName") + " s/o " + bundle.getString("CustomerFatherName"));
                if (Constant.ViewUserEntryStartDate != null && Constant.ViewUserEntryEndDate != null) {
                    tvStartDate.setText(Constant.ViewUserEntryStartDate);
                    from_date = Constant.ViewUserEntryStartDate;
                    to_date = Constant.ViewUserEntryEndDate;
                    tvEndDate.setText(Constant.ViewUserEntryEndDate);
                    if (tvEndDate.getText().toString().length() > 0 && tvStartDate.getText().toString().length() > 0) {
                        getTenDaysData();
                    }
                }
            } else {
                actv_CustomerID.setText("");
                et_Cname_fathername.setText("");
                tvStartDate.setText("");
                tvEndDate.setText("");
            }


        }
        getCustomerList();

        lvStartDate.setOnClickListener(this);
        lvEndDate.setOnClickListener(this);

        et_Cname_fathername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ViewUserEntryStartDate = tvStartDate.getText().toString();
                ViewUserEntryEndDate = tvEndDate.getText().toString();
                FromWhere = "ViewUserEntry";
                fragment = new CustomerSallerListFragment();
                bundle = new Bundle();
                bundle.putString("FromWhere", "ViewUserEntry");
                fragment.setArguments(bundle);
                goNextFragmentReplace(mContext, fragment);

            }
        });
        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ViewUserEntryStartDate = tvStartDate.getText().toString();
                ViewUserEntryEndDate = tvEndDate.getText().toString();
                FromWhere = "";
                FromWhere = "ViewUserEntry";
                fragment = new CustomerSallerListFragment();
                bundle = new Bundle();
                bundle.putString("FromWhere", "ViewUserEntry");
                fragment.setArguments(bundle);
                goNextFragmentReplace(mContext, fragment);
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:

                if (tvStartDate.getText().toString().length() > 0 &&
                        tvEndDate.getText().toString().length() > 0 && selectedId != null
                        && !et_Cname_fathername.getText().toString().equals("")) {
                    if (!unic_customer.equals("")) {
                        from_date = tvStartDate.getText().toString().trim();
                        to_date = tvEndDate.getText().toString().trim();
                        getTenDaysData();
                    }
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Incomplete_Entry));
                }
                break;
            case R.id.lvStartDate:
                getDate("StartDate");
                break;
            case R.id.lvEndDate:
                getDate("EndDate");
                break;

            case R.id.imgPrint:
                DialogPrint();
                break;


        }
    }

    public void DialogPrint() {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_print_document);
        dialog.setCancelable(false);
        ImageView imgClosed;
        TextView tv_downloadPDF, tv_print_reciept;

        // set the custom dialog components - text, image and button
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tv_downloadPDF = dialog.findViewById(R.id.tv_downloadPDF);
        tv_print_reciept = dialog.findViewById(R.id.tv_print_reciept);
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        tv_downloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printTypeDialog = "pdf";
                dialog.dismiss();
                printData();
            }
        });
        tv_print_reciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBluetoothHeadsetConnected()) {
                    if (mDevice == null || mSocket == null || mOutputStream == null || mInputStream == null) {
                        dialogBluetooth(mContext);
                    } else {
                        printTypeDialog = "reciept";
                        dialog.dismiss();
                        printData();
                    }
                } else {
                    showAlertWithTitle("Please enable Bluetooth device", mContext);
                    enableBluetooth();
                    dialogBluetooth(mContext);

                }
            }
        });

        dialog.show();
    }

    public boolean enableBluetooth() {
        try {
            BluetoothAdapter badapter = BluetoothAdapter.getDefaultAdapter();
            if (badapter != null) {
                badapter.enable();
                if (!badapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void printData() {

        if (pdfEntryList.size() == 0) {
            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.You_have_no_entry_to_Print));
        } else {
            if (printTypeDialog.equalsIgnoreCase("pdf")) {
                Pdfcreted = "true";
                Totcount = pdfEntryList.size();
                Pdfcreted = "true";
                downloadFileFromURL = new DownloadFileFromURL(this.mContext, this);
                this.Totcount = this.pdfEntryList.size();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        filePath = user_data_unic_customer + "_" + user_data_name + System.currentTimeMillis() + ".pdf";
                        folderName = "MeriDairy/View All Entry/";
                        try {
                            ViewSellerMilkEntryFragment.this.downloadFileFromURL.initURL(folderName, filePath);
                            ViewSellerMilkEntryFragment.this.downloadFileFromURL.execute(pdfUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }, 10);
            } else {
                pdfEntryList.get(0).setFrom_date(from_date);
                pdfEntryList.get(0).setTo_date(to_date);
                PrintTenDaysReciept(mContext, user_data_name, user_data_unic_customer, pdfEntryList);

            }

        }
    }

    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ArrayList<String> monthList = getMonthList();
                        String month = "";
                        for (int i = 0; i < monthList.size(); i++) {
                            if (monthOfYear == i) {
                                month = monthList.get(i);
                            }
                        }
                        String day = checkDigit(dayOfMonth);
                        if (from.equals("StartDate")) {
                            from_date= day + "-" + checkDigit((monthOfYear + 1)) + "-" + year;
                            tvStartDate.setText(from_date);
                        } else {
                            to_date= day + "-" + checkDigit((monthOfYear + 1)) + "-" + year;
                            tvEndDate.setText(to_date);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void setCustomerList(final ArrayList<CustomerListPojo> customerList) {
        if (customerList.isEmpty()) {
            FromWhere = "Dashboard";
            fragment = new AddCustomerFragment();
            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Add_Customer));
            goNextFragmentWithBackStack(mContext, fragment);
        } else {
            actv_CustomerID.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable actid) {
                    if (actid.toString().equals("")) {
                        selectedId = "";
                        unic_customer = "";
                        et_Cname_fathername.setText("");
                    } else {
                        for (int i = 0; i < customerList.size(); i++) {
                            if (customerList.get(i).user_group_id.equals("3")) {
                                if (actid.toString().equals(customerList.get(i).unic_customer)) {
                                    unic_customer = customerList.get(i).unic_customer;
                                    selectedId = customerList.get(i).id;
                                    selectedName = customerList.get(i).name;
                                    selectedFatherName = customerList.get(i).father_name;
                                    Log.d("strSelectedId>>>>", selectedId + " " + unic_customer + " " + selectedFatherName + " " + selectedName);
                                    et_Cname_fathername.setText(customerList.get(i).name + " s/o " + customerList.get(i).father_name);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void setBackgroundProgressBar() {
        yourCircularProgressbar.setVisibility(View.VISIBLE);
        recycler_allEntry.setVisibility(View.GONE);
        CircularProgressBar circularProgressBar = view.findViewById(R.id.yourCircularProgressbar);
        circularProgressBar.setColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        circularProgressBar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_light_dark_gray_for_switch));
        circularProgressBar.setProgressBarWidth(getResources().getDimension(R.dimen._5sdp));
        circularProgressBar.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen._5sdp));
        int animationDuration = 2500; // 2500ms = 2,5s
        circularProgressBar.setProgressWithAnimation(65, animationDuration); // Default duration = 1500ms
    }

    public void setTransactionListAdapter(ArrayList<TenDaysMilkSellHistory> mList) {
        if (mList.isEmpty()) {
            recycler_allEntry.setVisibility(View.INVISIBLE);
            yourCircularProgressbar.setVisibility(View.GONE);
            tvTotalFat.setText(mContext.getString(R.string.Average_Fat) + "\n0.0%");
            tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n0.000 " + mContext.getString(R.string.Ltr));
            tvTotalAmt.setText(mContext.getString(R.string.Total_Amount) + "\n" + " " + mContext.getString(R.string.Rs) + ". 0.00");
            tvFatRat.setText(mContext.getString(R.string.Fat_Rat) + "\n" + " " + mContext.getString(R.string.Rs) + " 0.00");
            tvTotalBonus.setText(mContext.getString(R.string.TOTAL_BONUS) + "\n" + " " + mContext.getString(R.string.Rs) + " 0.00");
            avg_fat_tenDays = "";
            total_milk_tenDays = "";
            grnd_total_amt_tenDays = "";
            avg_rate_tenDays = "";
            user_data_name = "";
            user_data_phone_number = "";
            user_data_unic_customer = "";
        } else {
            ArrayList<TenDaysMilkSellHistory> entryList = new ArrayList<>();

            entryList = mList.get(0).tenDaysList;
            if (pdfEntryList.size() == 0) {
                pdfEntryList = entryList;
            } else {
                pdfEntryList.clear();
                pdfEntryList = entryList;
            }

            recycler_allEntry.setVisibility(View.VISIBLE);
            yourCircularProgressbar.setVisibility(View.GONE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recycler_allEntry.setHasFixedSize(true);
            ViewMilkEntryAdapter customerListAdapter2 = new ViewMilkEntryAdapter(mContext, entryList);
            recycler_allEntry.setLayoutManager(mLayoutManager);
            recycler_allEntry.setAdapter(customerListAdapter2);
            customerListAdapter2.notifyDataSetChanged();
            totalBonus = 0d;
            for (int i = 0; i < entryList.size(); i++) {
                if (!entryList.get(i).total_bonus.equals("-") && !entryList.get(i).total_bonus.equals("")) {
                    totalBonus = totalBonus + Double.parseDouble(entryList.get(i).total_bonus);
                }
            }
            avg_fat_tenDays = mList.get(0).avg_fat_tenDays;
            total_milk_tenDays = mList.get(0).total_milk_tenDays;
            grnd_total_amt_tenDays = mList.get(0).grnd_total_amt_tenDays;
            avg_rate_tenDays = mList.get(0).avg_rate_tenDays;
            user_data_name = mList.get(0).user_data_name;
            user_data_phone_number = mList.get(0).user_data_phone_number;
            user_data_unic_customer = mList.get(0).user_data_unic_customer;

            tvTotalFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + mList.get(0).avg_fat_tenDays + "%");
            tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + /*String.format("%.3f", totalWeight)*/mList.get(0).total_milk_tenDays + " " + mContext.getString(R.string.Ltr));
            tvTotalAmt.setText((mContext.getString(R.string.Total_Amount) + "\n" + mContext.getString(R.string.Rs) + " " +/*String.format("%.2f", totalAmt)*/mList.get(0).grnd_total_amt_tenDays));
            tvFatRat.setText((mContext.getString(R.string.Fat_Rat) + "\n" + mContext.getString(R.string.Rs) + " " + /*String.format("%.2f", fatRate)*/mList.get(0).avg_rate_tenDays));
            tvTotalBonus.setText((mContext.getString(R.string.TOTAL_BONUS) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", totalBonus)/*mList.get(0).avg_rate_tenDays*/));
        }
    }

    public void createPDF(String userName, String unic_id, String mobile_no, ArrayList<TenDaysMilkSellHistory> mList, String avFat, String avgRate) {
        String FILE = Environment.getExternalStorageDirectory().toString()
                + "/MeriDairy/View All Entry/" + unic_id + "_" + userName + System.currentTimeMillis() + ".pdf";
        document = new Document(PageSize.A4);
        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MeriDairy/View All Entry/");
        myDir.mkdirs();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            // Open Document for Writting into document
            document.open();
            addTitlePage(document, unic_id, userName, mobile_no, mList, avFat, avgRate);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        document.close();

    }

    public PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD)));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public void addTitlePage(Document document, String unic_id, String userName, String mobile_no, ArrayList<TenDaysMilkSellHistory> mList, String avgFat, String avgRate) throws DocumentException {
        // Create new Page in PDF
        Pdfcreted = "true";
        Drawable d = getResources().getDrawable(R.drawable.app_icon);
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bmp = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image image = null;
        try {
            image = Image.getInstance(stream.toByteArray());
            // image.setAlignment(Image.ALIGN_TOP);
            image.setAlignment(Image.ALIGN_CENTER);
            image.scaleToFit(40, 40);
            document.add(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
        Bitmap bitmap = null;
        if (sessionManager.getValueSesion(SessionManager.KEY_center_name).equals("")) {
            //prHead.add("मेरी डेयरी");
            bitmap = UtilityMethod.textAsBitmap("मेरी डेयरी", 70, mContext);
        } else {
            // bitmap = textAsBitmap("मेरी डेयरी", 70);
            bitmap = UtilityMethod.textAsBitmap("" + sessionManager.getValueSesion(SessionManager.KEY_center_name), 70, mContext);
        }
        Paragraph prHead = new Paragraph();
        prHead.setSpacingAfter(3);
        prHead.setSpacingBefore(3);
        ByteArrayOutputStream streamDairy2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, streamDairy2);
        Image imageDairy2 = null;
        try {
            imageDairy2 = Image.getInstance(streamDairy2.toByteArray());
            // image.setAlignment(Image.ALIGN_TOP);
            imageDairy2.setAlignment(Image.ALIGN_CENTER);
            imageDairy2.scaleToFit(100, 100);
            // document.add(imageDairy2);
            prHead.add(new Chunk(imageDairy2, 0, 0, true));
            prHead.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(prHead);
        } catch (IOException e) {
            e.printStackTrace();
        }


        PdfPTable tableHead = new PdfPTable(2);
        tableHead.setWidthPercentage(100);
        tableHead.addCell(getCell("ID:- " + unic_id, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Name), PdfPCell.ALIGN_RIGHT));
        tableHead.addCell(getCell("Name:- " + userName, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_RIGHT));
        tableHead.addCell(getCell("Mobile Number:- " + mobile_no, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("", PdfPCell.ALIGN_RIGHT));
        document.add(tableHead);

        //Date

        Paragraph prDate = new Paragraph();
        prDate.setSpacingAfter(4);
        prDate.setSpacingBefore(4);
        prDate.setFont(catFont);
        // Add item into Paragraph
        prDate.add(tvStartDate.getText().toString() + "   To   " + tvEndDate.getText().toString());
        prDate.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(prDate);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 4, 4, 4, 6, 4, 4, 6});
        table.setSpacingBefore(4);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(new Phrase("Date", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Session", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Fat", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("SNF", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Rs/Ltr", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Weight", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Bonus", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Total Amount", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.setHeaderRows(1);

        double totalMilk = 0.0, AvgFat = 0.0, TotalFat = 0.0, TotalRate = 0.0, AvgRate = 0.0, TotalAmount = 0.0, TotalBonus = 0.0;
        int countData = 0;
        count = 0;
        for (int i = 0; i < mList.size(); i++) {
            count = count + 1;
            if (mList.get(i).shift.equals("morning")) {
                table.addCell(new Phrase(mList.get(i).for_date + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            } else {
                table.addCell(new Phrase("", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            }
            table.addCell(new Phrase(mList.get(i).session + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).fat + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).snf + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).per_kg_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).entry_total_milk + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).total_bonus + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).total_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

        }
        document.add(table);
        PdfPTable pTable = new PdfPTable(5);
        pTable.setWidthPercentage(100);
        pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        pTable.addCell(new Phrase("Total Milk " + total_milk_tenDays, new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Avg Fat " + avg_fat_tenDays, new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Avg Rate " + avg_rate_tenDays, new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Total Bonus " + String.format("%.2f", totalBonus), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Total Amount " + grnd_total_amt_tenDays, new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        document.add(pTable);

        // bottom line
        Paragraph bottomHead = new Paragraph();
        bottomHead.setFont(catFont);
        bottomHead.setSpacingAfter(3);
        bottomHead.setSpacingBefore(3);
        // Add item into Paragraph
        bottomHead.add("Meri Dairy Download App Now :");
        bottomHead.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(bottomHead);

        Drawable d2 = getResources().getDrawable(R.drawable.qr_code);
        BitmapDrawable bitDw2 = ((BitmapDrawable) d2);
        Bitmap bmp2 = bitDw2.getBitmap();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        Image image2 = null;
        try {
            image2 = Image.getInstance(stream2.toByteArray());
            // image.setAlignment(Image.ALIGN_TOP);
            image2.setAlignment(Image.ALIGN_LEFT);
            image2.scaleToFit(120, 120);
            image2.setSpacingBefore(3);
            document.add(image2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // document.newPage();
        if (count == Totcount) {
            if (document != null) {
                //tvPrint.setVisibility(View.VISIBLE);
                // ProgressBar01.setVisibility(View.GONE);
                progressDialog.dismiss();
                UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.OPen_PDF));
                Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/MeriDairy/");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedUri, "application/pdf");
                if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
                    startActivity(intent);
                } else {
                    // if you reach this place, it means there is no any file
                    // explorer app installed on your device
                }

            }
        }

    }

    public void getCustomerList() {

        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        ArrayList<CustomerListPojo> mCustomerList = databaseHandler.getCustomerList();
        System.out.println("customerList= in List POJO=>>" + mCustomerList.size());

        if (!mCustomerList.isEmpty()) {

            setCustomerList(mCustomerList);

        }
    }

    public void getTenDaysData() {
        final ArrayList<TenDaysMilkSellHistory> tenDaysMainList = new ArrayList<>();


        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        ArrayList<TenDaysMilkSellHistory> dataList = new ArrayList<>();
                        JSONArray mainArray = object.getJSONArray("main");
                        pdfUrl = object.getString("pdf_url");
                        for (int j = 0; j < mainArray.length(); j++) {
                            JSONObject dataObj = mainArray.getJSONObject(j);
                            dataList.add(new TenDaysMilkSellHistory(
                                    dataObj.getString("for_date"), dataObj.getString("session"),
                                    dataObj.getString("fat"), dataObj.getString("snf"),
                                    dataObj.getString("per_kg_price"), dataObj.getString("total_price"),
                                    dataObj.getString("total_milk"), dataObj.getString("shift"), dataObj.getString("total_bonus")
                            ));

                        }
                        JSONObject user_data = object.getJSONObject("user_data");
                        tenDaysMainList.add(new TenDaysMilkSellHistory(
                                object.getString("avg_fat"), object.getString("total_milk"),
                                object.getString("avg_rate"), object.getString("opening_balance"), object.getString("grnd_total_amt"),
                                user_data.getString("id"), user_data.getString("unic_customer"),
                                user_data.getString("name"), user_data.getString("father_name"),
                                user_data.getString("phone_number"), pdfUrl,dataList));
                    }

                    setTransactionListAdapter(tenDaysMainList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("from_date", from_date)
                .addEncoded("to_date", to_date)
                .addEncoded("dairy_id", dairyid)
                .addEncoded("customer_ids", selectedId).build();
        caller.addRequestBody(body);

        caller.execute(Constant.getMultiSellerTenDaysDataAPI);
    }


    @Override
    public void onDownLoadComplete(String folderName, File file) {
        UtilityMethod.openPdfFile(this.mContext, file);
    }
}
