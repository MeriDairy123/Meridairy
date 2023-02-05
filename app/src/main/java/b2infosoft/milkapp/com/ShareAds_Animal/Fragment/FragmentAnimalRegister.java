package b2infosoft.milkapp.com.ShareAds_Animal.Fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import b2infosoft.milkapp.com.Advertisement.ImagePickerAcitvity;
import b2infosoft.milkapp.com.Dairy.SellMilk.SearchPlaceFragment;
import b2infosoft.milkapp.com.Model.CityPojo;
import b2infosoft.milkapp.com.Model.StatePojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Adapter.Animal_Image_item_adapter;
import b2infosoft.milkapp.com.ShareAds_Animal.Animal_AdsActivity;
import b2infosoft.milkapp.com.ShareAds_Animal.BeanUploadImage;
import b2infosoft.milkapp.com.ShareAds_Animal.VideoPlayerActivity;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.ShareAds_Animal.Fragment.Fragment_Animal_Categories.beanUploadImages;
import static b2infosoft.milkapp.com.appglobal.Constant.AnimalName;
import static b2infosoft.milkapp.com.appglobal.Constant.getCityAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.registerAnimalAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalMainCategoryId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalSubCategoryId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_GALLERY_IMAGE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_IMAGE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_IMAGE_CAPTURE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextClass;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showSnackBar;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class FragmentAnimalRegister extends Fragment {
    private static final int REQUEST_VIDEO = 1888;
    private static final String VIDEO_DIRECTORY = "/demonuts";
    public boolean ClickCount = true;
    View view;
    ImageView btnupload_image;
    FragmentActivity mContext;
    ImageView btnupload_video;
    Uri videoUri = null;
    ImageView video_play_button;
    String selectedVideoPath = "";
    String recordedVideoPath = "";
    Button btnsubmit_animal_details;
    int count = 0;
    View layoutAnimalDetails, layoutImgUpload, layoutAnimalVaccian;
    Toolbar toolbar;
    List<String> imageCowRegisterRowList;
    EditText et_contact, et_description, et_pregnancy_month, et_pregnancy_day,
            et_fmd_vaccination_period, et_hs_vaccination_period, et_quarter_vaccination_period, et_brucellousis_vaccination_period;
    EditText et_tagNo, et_nickName, et_months, et_lactation, et_lastCalving, et_peak_milk_yield, et_years,
            et_deworming_period, et_selling_price;
    TextInputLayout tvpeak_milk_yield, tvfmd_vaccination_period, tvhs_vaccination_period,
            tvquarter_vaccination_period, tvbrucellousis_vaccination_period, tvdeworming_period;
    TextView tvlocation;
    Spinner spinState, spinCity;
    RadioButton radio_btn_HS_vaccination_yes, radio_btn_HS_vaccination_no, radio_btn_quarter_vaccination_yes, radio_btn_quarter_vaccination_no, radio_btn_brucellousis_vaccination_yes, radio_btn_brucellousis_vaccination_no, radio_btn_deworming_yes, radio_btn_deworming_no;
    RadioButton radio_btn_FMD_vaccination_yes, radio_btn_FMD_vaccination_no;
    RadioGroup radio_group_castration, radiogroup_status_calf, radio_group_deworming, radio_group_gender, radio_group_milk_status, radiogroup_brucellousis_vaccination, radio_group_pregnanacy, radiogroup_gender_of_calf, radio_group_inter_calving, radiogroup_fmd_vaccination, radiogroup_hs_vaccination, radiogroup_black_quarter_vaccination;
    RadioButton radio_btn_castration_yes, radio_btn_castration_no, radio_btn_male, radio_btn_female, radio_btn_inMilk, radio_btn_dry, radio_btn_male_calf, radio_btn_female_calf, radio_btn_lessThan_oneYear, radio_btn_one_onepointFive, radio_btn_pointfive_two, radio_btn_moreThan_twoYear, radio_btn_pregnant_yes, radio_btn_pregnant_no;
    RadioButton radio_btn_calf_rearing, radio_btn_calf_sold, radio_btn_calf_died;
    LinearLayout layout_visbility_female, layout_visibility_pregnancy, layout_calf_status, layout_castration;
    SessionManager sessionManager;
    Animal_Image_item_adapter image_adapter;
    RecyclerView image_upload_recycle;
    String calfStatus = "", peakYeild = "", tagNo = "", nickName = "", gender = "",
            sellingPrice = "", lactationNo = "", lastCalvingOccured = "", milkingStatus = "", pregnantMonth = "",
            pregnantDay = "", genderCalf = "", interCalvingPeriod = "", pregnant = "", contact = "", description = "", months = "", years = "";
    String fmdVaccinationStatus = "", hsVaccinationStatus = "", blackQuarterVaccinationStatus = "",
            brucellousisVaccinationStatus = "", dewormingStatus = "";
    String dewormingPeriod = "", fmdVaccinationPeriod = "", hsVaccinationPeriod = "",
            quarterVaccinationPeriod = "", brucellousisVaccinationPeriod = "";
    String stateId = "", cityId = "";
    String strPleaseSelectState = "", strPleaseSelectCity = "";
    ArrayList<String> citylist = new ArrayList<>();
    ArrayList<String> statelist = new ArrayList<>();
    String strVideoPath = "";
    String mainCategory = "", subCategory = "", castration = "";
    String title = "";
    String strKeyAddress = "address_refress_notification";
    BroadcastReceiver receiver;
    private VideoView first_video_cow;
    private int GALLERY = 1, CAMERA = 2;
    private int GALLERY_VIDEO = 3, CAMERA_VIDEO = 4;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_animal_details_register, container, false);
        mContext = getActivity();
        imageCowRegisterRowList = new ArrayList<String>();
        sessionManager = new SessionManager(mContext);
        strPleaseSelectState = mContext.getResources().getString(R.string.Please_Select_State);
        strPleaseSelectCity = mContext.getResources().getString(R.string.Please_Select_City);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        title = mContext.getString(R.string.Register) + " " + AnimalName;
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBackButton();
            }
        });

        layoutAnimalDetails = view.findViewById(R.id.layoutAnimalDetails);
        layoutImgUpload = view.findViewById(R.id.layoutImgUpload);
        layoutAnimalVaccian = view.findViewById(R.id.layoutAnimalVaccian);

        btnupload_image = view.findViewById(R.id.btnupload_image);
        image_upload_recycle = view.findViewById(R.id.image_upload_recycle);
        btnupload_video = view.findViewById(R.id.btnupload_video);
        btnsubmit_animal_details = view.findViewById(R.id.btnsubmit_animal_details);
        first_video_cow = view.findViewById(R.id.first_video_cow);
        video_play_button = view.findViewById(R.id.video_play_button);

        //edittext
        et_tagNo = view.findViewById(R.id.et_tagNo);
        et_nickName = view.findViewById(R.id.et_nickName);
        spinState = view.findViewById(R.id.spinState);
        tvlocation = view.findViewById(R.id.tvlocation);
        spinCity = view.findViewById(R.id.spinCity);
        et_months = view.findViewById(R.id.et_months);
        et_lastCalving = view.findViewById(R.id.et_lastCalving);
        et_lactation = view.findViewById(R.id.et_lactation);
        et_years = view.findViewById(R.id.et_years);
        et_description = view.findViewById(R.id.et_description);
        et_peak_milk_yield = view.findViewById(R.id.et_peak_milk_yield);
        et_pregnancy_month = view.findViewById(R.id.et_pregnancy_month);
        et_pregnancy_day = view.findViewById(R.id.et_pregnancy_day);
        et_fmd_vaccination_period = view.findViewById(R.id.et_fmd_vaccination_period);
        et_hs_vaccination_period = view.findViewById(R.id.et_hs_vaccination_period);
        et_quarter_vaccination_period = view.findViewById(R.id.et_quarter_vaccination_period);
        et_brucellousis_vaccination_period = view.findViewById(R.id.et_brucellousis_vaccination_period);
        et_deworming_period = view.findViewById(R.id.et_deworming_period);
        et_selling_price = view.findViewById(R.id.et_selling_price);
        et_contact = view.findViewById(R.id.et_contact);
        // Text InputLayout
        tvpeak_milk_yield = view.findViewById(R.id.tvPeak_milk_yield);
        tvfmd_vaccination_period = view.findViewById(R.id.tvfmd_vaccination_period);
        tvhs_vaccination_period = view.findViewById(R.id.tvhs_vaccination_period);
        tvquarter_vaccination_period = view.findViewById(R.id.tvquarter_vaccination_period);
        tvbrucellousis_vaccination_period = view.findViewById(R.id.tvbrucellousis_vaccination_period);
        tvdeworming_period = view.findViewById(R.id.tvdeworming_period);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setAddress();

            }
        };

        tvlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                str_location_address = "";
                str_location_city = "";
                str_location_state = "";
                str_location_postCode = "";
                str_location_Latitude = "";
                str_location_Longitude = "";

                Fragment fragment = new SearchPlaceFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.container_share_ads, fragment).addToBackStack(null).commit();

            }
        });
        tvlocation.setText(str_location_address);
        //radiobuttons
        radio_btn_male = view.findViewById(R.id.radio_btn_male);
        radio_btn_female = view.findViewById(R.id.radio_btn_female);
        radio_btn_dry = view.findViewById(R.id.radio_btn_dry);
        radio_btn_inMilk = view.findViewById(R.id.radio_btn_inMilk);
        radio_btn_male_calf = view.findViewById(R.id.radio_btn_male_calf);
        radio_btn_female_calf = view.findViewById(R.id.radio_btn_female_calf);
        radio_btn_lessThan_oneYear = view.findViewById(R.id.radio_btn_lessThan_oneYear);
        radio_btn_one_onepointFive = view.findViewById(R.id.radio_btn_one_onepointFive);
        radio_btn_pointfive_two = view.findViewById(R.id.radio_btn_pointfive_two);
        radio_btn_moreThan_twoYear = view.findViewById(R.id.radio_btn_moreThan_twoYear);
        radio_btn_pregnant_yes = view.findViewById(R.id.radio_btn_pregnant_yes);
        radio_btn_pregnant_no = view.findViewById(R.id.radio_btn_pregnant_no);
        radio_btn_FMD_vaccination_yes = view.findViewById(R.id.radio_btn_FMD_vaccination_yes);
        radio_btn_FMD_vaccination_no = view.findViewById(R.id.radio_btn_FMD_vaccination_no);
        radio_btn_HS_vaccination_yes = view.findViewById(R.id.radio_btn_HS_vaccination_yes);
        radio_btn_HS_vaccination_no = view.findViewById(R.id.radio_btn_HS_vaccination_no);
        radio_btn_quarter_vaccination_yes = view.findViewById(R.id.radio_btn_quarter_vaccination_yes);
        radio_btn_quarter_vaccination_no = view.findViewById(R.id.radio_btn_quarter_vaccination_no);
        radio_btn_brucellousis_vaccination_yes = view.findViewById(R.id.radio_btn_brucellousis_vaccination_yes);
        radio_btn_brucellousis_vaccination_no = view.findViewById(R.id.radio_btn_brucellousis_vaccination_no);
        radio_btn_deworming_yes = view.findViewById(R.id.radio_btn_deworming_yes);
        radio_btn_deworming_no = view.findViewById(R.id.radio_btn_deworming_no);
        radio_btn_calf_rearing = view.findViewById(R.id.radio_btn_calf_rearing);
        radio_btn_calf_sold = view.findViewById(R.id.radio_btn_calf_sold);
        radio_btn_calf_died = view.findViewById(R.id.radio_btn_calf_died);
        radio_btn_castration_no = view.findViewById(R.id.radio_btn_castration_no);
        radio_btn_castration_yes = view.findViewById(R.id.radio_btn_castration_yes);

        //radio group
        radio_group_gender = view.findViewById(R.id.radio_group_gender);
        radio_group_milk_status = view.findViewById(R.id.radio_group_milk_status);
        radio_group_pregnanacy = view.findViewById(R.id.radio_group_pregnanacy);
        radiogroup_gender_of_calf = view.findViewById(R.id.radiogroup_gender_of_calf);
        radio_group_inter_calving = view.findViewById(R.id.radio_group_inter_calving);
        radiogroup_fmd_vaccination = view.findViewById(R.id.radiogroup_fmd_vaccination);
        radiogroup_hs_vaccination = view.findViewById(R.id.radiogroup_hs_vaccination);
        radiogroup_black_quarter_vaccination = view.findViewById(R.id.radiogroup_black_quarter_vaccination);
        radiogroup_brucellousis_vaccination = view.findViewById(R.id.radiogroup_brucellousis_vaccination);
        radio_group_deworming = view.findViewById(R.id.radio_group_deworming);
        radiogroup_status_calf = view.findViewById(R.id.radiogroup_status_calf);
        radio_group_castration = view.findViewById(R.id.radio_group_castration);


        intitImageRecycleView();

        //layout visiblity
        layout_visbility_female = view.findViewById(R.id.layout_visbility_female);
        layout_visibility_pregnancy = view.findViewById(R.id.layout_visibility_pregnancy);
        layout_calf_status = view.findViewById(R.id.layout_calf_status);
        layout_castration = view.findViewById(R.id.layout_castration);

        layout_visbility_female.setVisibility(View.GONE);
        layout_visibility_pregnancy.setVisibility(View.GONE);
        layout_calf_status.setVisibility(View.GONE);
        layout_castration.setVisibility(View.GONE);


        if (statelist.size() == 0) {
            getStateList();
        }

        et_months.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    months = s.toString().trim();
                    System.out.println("==mon===" + months);

                    int month_value = Integer.parseInt(months);
                    if (month_value > 11) {
                        months = "11";
                        et_months.setText(months);
                        System.out.println("==mon===" + month_value);
                    }
                }


            }
        });
        et_lactation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    lactationNo = s.toString().trim();
                    System.out.println("==mon===" + lactationNo);

                    int lactation_value = Integer.parseInt(lactationNo);
                    if (lactation_value > 11) {
                        lactationNo = "10";
                        et_lactation.setText(lactationNo);
                        System.out.println("==mon===" + lactation_value);
                    }
                }


            }
        });
        et_years.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    years = s.toString().trim();
                    System.out.println("==year===" + years);

                    int year_value = Integer.parseInt(years);
                    if (year_value > 30) {
                        years = "30";
                        et_years.setText(years);
                        System.out.println("==year===" + year_value);
                    }
                }


            }
        });
        et_lastCalving.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    lastCalvingOccured = s.toString().trim();
                    System.out.println("==lastCalving===" + lastCalvingOccured);
                    int last_calving_value = 0;
                    last_calving_value = Integer.parseInt(lastCalvingOccured);
                    if (last_calving_value > 30) {
                        //   et_lastCalving.setText("");
                        lastCalvingOccured = "30";
                        et_lastCalving.setText(lastCalvingOccured);
                        System.out.println("==lastCalving===" + last_calving_value);
                    }
                }


            }
        });
        et_pregnancy_day.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    pregnantDay = s.toString().trim();
                    System.out.println("==pregD===" + pregnantDay);
                    int pregnanct_day_value = 0;
                    pregnanct_day_value = Integer.parseInt(pregnantDay);
                    if (pregnanct_day_value > 31) {
                        //   et_lastCalving.setText("");
                        pregnantDay = "31";
                        et_pregnancy_day.setText(pregnantDay);
                        System.out.println("==pregD===" + pregnantDay);
                    }
                }


            }
        });
        et_pregnancy_month.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    pregnantMonth = s.toString().trim();
                    System.out.println("==pregM===" + pregnantMonth);
                    int pregnanct_month_value = 0;
                    pregnanct_month_value = Integer.parseInt(pregnantMonth);
                    if (pregnanct_month_value > 11) {
                        //   et_lastCalving.setText("");
                        pregnantMonth = "11";
                        et_pregnancy_month.setText(pregnantMonth);
                        System.out.println("==pregM===" + pregnantMonth);
                    }
                }


            }
        });

        et_fmd_vaccination_period.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    fmdVaccinationPeriod = s.toString().trim();
                    System.out.println("==fmd===" + fmdVaccinationPeriod);

                    int fmd_vaccination_value = Integer.parseInt(fmdVaccinationPeriod);
                    if (fmd_vaccination_value > 12) {
                        fmdVaccinationPeriod = "12";
                        et_fmd_vaccination_period.setText(fmdVaccinationPeriod);
                        System.out.println("==fmd===" + fmdVaccinationPeriod);
                    }
                }

            }
        });
        et_hs_vaccination_period.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    hsVaccinationPeriod = s.toString().trim();
                    System.out.println("==hs===" + hsVaccinationPeriod);

                    int hs_vaccination_value = Integer.parseInt(hsVaccinationPeriod);
                    if (hs_vaccination_value > 12) {
                        hsVaccinationPeriod = "12";
                        et_hs_vaccination_period.setText(hsVaccinationPeriod);
                        System.out.println("==hs===" + hsVaccinationPeriod);
                    }
                }


            }
        });
        et_quarter_vaccination_period.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    quarterVaccinationPeriod = s.toString().trim();
                    System.out.println("==bQ===" + quarterVaccinationPeriod);

                    int quarter_vaccination_value = Integer.parseInt(quarterVaccinationPeriod);
                    if (quarter_vaccination_value > 12) {
                        quarterVaccinationPeriod = "12";
                        et_quarter_vaccination_period.setText(quarterVaccinationPeriod);
                        System.out.println("==bQ===" + quarterVaccinationPeriod);
                    }
                }


            }
        });

        et_brucellousis_vaccination_period.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    brucellousisVaccinationPeriod = s.toString().trim();
                    System.out.println("==bru===" + brucellousisVaccinationPeriod);

                    int brucellousis_vaccination_value = Integer.parseInt(brucellousisVaccinationPeriod);
                    if (brucellousis_vaccination_value > 12) {
                        brucellousisVaccinationPeriod = "12";
                        et_brucellousis_vaccination_period.setText(brucellousisVaccinationPeriod);
                        System.out.println("==bru===" + brucellousisVaccinationPeriod);
                    }

                }
            }
        });
        et_deworming_period.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {

                    dewormingPeriod = s.toString().trim();
                    System.out.println("==dew===" + dewormingPeriod);

                    int deworming_value = Integer.parseInt(dewormingPeriod);
                    if (deworming_value > 12) {
                        dewormingPeriod = "12";
                        et_deworming_period.setText(dewormingPeriod);
                        System.out.println("==dew===" + dewormingPeriod);
                    }
                }


            }
        });


        radio_group_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_female.isChecked()) {
                    gender = "Female";
                    layout_visbility_female.setVisibility(View.VISIBLE);
                    layout_castration.setVisibility(View.GONE);

                } else if (radio_btn_male.isChecked()) {
                    gender = "Male";
                    layout_castration.setVisibility(View.VISIBLE);
                    layout_visbility_female.setVisibility(View.GONE);
                }
            }
        });
        radio_group_castration.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_castration_yes.isChecked()) {
                    castration = "1";


                } else if (radio_btn_castration_no.isChecked()) {
                    castration = "0";

                }
            }
        });

        radio_group_milk_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_inMilk.isChecked()) {
                    milkingStatus = "In Milk";
                    tvpeak_milk_yield.setVisibility(View.VISIBLE);
                    layout_calf_status.setVisibility(View.VISIBLE);

                } else if (radio_btn_dry.isChecked()) {
                    milkingStatus = "Dry";
                    tvpeak_milk_yield.setVisibility(View.GONE);
                    layout_calf_status.setVisibility(View.GONE);
                }
            }
        });

        radio_group_pregnanacy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_pregnant_yes.isChecked()) {

                    pregnant = "1";
                    layout_visibility_pregnancy.setVisibility(View.VISIBLE);
                } else if (radio_btn_pregnant_no.isChecked()) {
                    pregnant = "0";
                    layout_visibility_pregnancy.setVisibility(View.GONE);

                }
            }
        });

        radiogroup_gender_of_calf.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_female_calf.isChecked()) {
                    genderCalf = "Female";
                } else if (radio_btn_male_calf.isChecked()) {
                    genderCalf = "Male";
                }
            }
        });
        radio_group_inter_calving.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_lessThan_oneYear.isChecked()) {
                    interCalvingPeriod = "Less than 1 year";
                } else if (radio_btn_one_onepointFive.isChecked()) {
                    interCalvingPeriod = "1-1.5 years";
                } else if (radio_btn_pointfive_two.isChecked()) {
                    interCalvingPeriod = "1.5-2 years";
                } else if (radio_btn_moreThan_twoYear.isChecked()) {
                    interCalvingPeriod = "More than 2 years";
                }
            }
        });
        radiogroup_fmd_vaccination.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_FMD_vaccination_yes.isChecked()) {
                    fmdVaccinationStatus = "1";
                    tvfmd_vaccination_period.setVisibility(View.VISIBLE);
                } else if (radio_btn_FMD_vaccination_no.isChecked()) {
                    fmdVaccinationStatus = "0";
                    tvfmd_vaccination_period.setVisibility(View.GONE);
                }
            }
        });

        radiogroup_hs_vaccination.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_HS_vaccination_yes.isChecked()) {
                    hsVaccinationStatus = "1";
                    tvhs_vaccination_period.setVisibility(View.VISIBLE);
                } else if (radio_btn_HS_vaccination_no.isChecked()) {
                    hsVaccinationStatus = "0";
                    tvhs_vaccination_period.setVisibility(View.GONE);
                }
            }
        });
        radiogroup_black_quarter_vaccination.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_quarter_vaccination_yes.isChecked()) {
                    blackQuarterVaccinationStatus = "1";
                    tvquarter_vaccination_period.setVisibility(View.VISIBLE);
                } else if (radio_btn_quarter_vaccination_no.isChecked()) {
                    blackQuarterVaccinationStatus = "0";
                    tvquarter_vaccination_period.setVisibility(View.GONE);

                }
            }
        });
        radiogroup_brucellousis_vaccination.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_brucellousis_vaccination_yes.isChecked()) {
                    brucellousisVaccinationStatus = "1";
                    tvbrucellousis_vaccination_period.setVisibility(View.VISIBLE);

                } else if (radio_btn_brucellousis_vaccination_no.isChecked()) {
                    brucellousisVaccinationStatus = "0";
                    tvbrucellousis_vaccination_period.setVisibility(View.GONE);

                }
            }
        });
        radio_group_deworming.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_deworming_yes.isChecked()) {
                    dewormingStatus = "1";
                    tvdeworming_period.setVisibility(View.VISIBLE);
                } else if (radio_btn_deworming_no.isChecked()) {
                    dewormingStatus = "0";
                    tvdeworming_period.setVisibility(View.GONE);

                }
            }
        });
        radiogroup_status_calf.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio_btn_calf_rearing.isChecked()) {
                    calfStatus = "rearing";
                } else if (radio_btn_calf_sold.isChecked()) {
                    calfStatus = "sold";
                } else if (radio_btn_calf_died.isChecked()) {
                    calfStatus = "died";
                }
            }
        });

        video_play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, VideoPlayerActivity.class);
                in.putExtra("contentUri", videoUri);
                Log.d("videoUri===", String.valueOf(videoUri));
                startActivity(in);


            }
        });
        btnupload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(mContext)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    if (beanUploadImages.size() == 5) {
                                        showSnackBar(getView(), mContext.getString(R.string.MaximumPhotosUpload));
                                    } else {
                                        showImagePickerOptions();
                                    }

                                }
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        btnupload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();

            }
        });

        btnsubmit_animal_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutAnimalDetails.getVisibility() == View.VISIBLE) {
                    checkAnimalDetailValidation();
                } else if (layoutImgUpload.getVisibility() == View.VISIBLE && beanUploadImages.isEmpty()) {
                    showToast(mContext, mContext.getResources().getString(R.string.PleaseUploadImageFile));

                } else if (layoutImgUpload.getVisibility() == View.VISIBLE && !beanUploadImages.isEmpty()) {
                    layoutAnimalDetails.setVisibility(View.GONE);
                    layoutImgUpload.setVisibility(View.GONE);
                    layoutAnimalVaccian.setVisibility(View.VISIBLE);
                    btnsubmit_animal_details.setText(mContext.getResources().getString(R.string.Submit));
                } else if (layoutAnimalVaccian.getVisibility() == View.VISIBLE) {
                    checkAnimalVaccinValidation();
                }
            }
        });

        return view;
    }

    public void getStateList() {

        final ArrayList<StatePojo> stateList = new ArrayList<StatePojo>();


        NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONArray mainJsonArray = new JSONArray(response);


                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        // Log.d("StateId", object.getString("state_id"));
                        // Log.d("StateName", object.getString("sate_name"));
                        stateList.add(new StatePojo(object.getString("id"), object.getString("name")));
                    }

                    if (!stateList.isEmpty()) {
                        setStateList(stateList);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        serviceCaller.execute(Constant.getStateAPI);
    }

    public void setStateList(final ArrayList<StatePojo> stateList) {
        System.out.println("StateList====>>>" + stateList.size());

        statelist.add(strPleaseSelectState);

        for (int i = 0; i < stateList.size(); i++) {
            statelist.add(stateList.get(i).sate_name);
        }
        ArrayAdapter<String> stateSpinnAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, statelist);
        stateSpinnAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinState.setAdapter(stateSpinnAdapter);

        ArrayList<String> list = new ArrayList<>();
        list.addAll(statelist);

        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String selectedState = parent.getItemAtPosition(position).toString();
                if (!selectedState.equals(strPleaseSelectState)) {
                    stateId = stateList.get(position - 1).state_id;
                    Log.d("stateId===>>>", stateId);
                    getCityList(stateId);
                } else {
                    stateId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void getCityList(String state_id) {
        final ArrayList<CityPojo> cityList = new ArrayList<>();

        NetworkTask caller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);

                        Log.d("CityId>>>>", jsonObject1.getString("id") + "  " + jsonObject1.getString("name"));
                        cityList.add(new CityPojo(jsonObject1.getString("id"), jsonObject1.getString("name")));
                    }
                    if (!cityList.isEmpty()) {


                        setCityAdapter(cityList);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        //   caller.addNameValuePair("state_id", state_id);
        caller.execute(getCityAPI.replace("@state_id", state_id));
    }

    public void setCityAdapter(final ArrayList<CityPojo> cityList) {
        Log.d("CityList==>>>", "" + cityList.size());
        citylist = new ArrayList<>();
        citylist.add(strPleaseSelectCity);

        for (int i = 0; i < cityList.size(); i++) {
            citylist.add(cityList.get(i).city_name);
        }

        ArrayAdapter<String> citySpinnAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, citylist);
        citySpinnAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinCity.setAdapter(citySpinnAdapter);
        citySpinnAdapter.notifyDataSetChanged();


        spinCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCity = parent.getItemAtPosition(position).toString();
                if (!selectedCity.equals(strPleaseSelectCity)) {

                    cityId = cityList.get(position - 1).city_id;
                    Log.d("CityID==>>>", cityId);
                } else {
                    cityId = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void checkAnimalDetailValidation() {

        mainCategory = sessionManager.getValueSesion(KEY_AnimalMainCategoryId);
        subCategory = sessionManager.getValueSesion(KEY_AnimalSubCategoryId);
        tagNo = et_tagNo.getText().toString().trim();
        nickName = et_nickName.getText().toString().trim();
        years = et_years.getText().toString().trim();
        months = et_months.getText().toString().trim();
        lactationNo = et_lactation.getText().toString().trim();
        lastCalvingOccured = et_lastCalving.getText().toString().trim();
        pregnantMonth = et_pregnancy_month.getText().toString().trim();
        pregnantDay = et_pregnancy_day.getText().toString().trim();
        peakYeild = et_peak_milk_yield.getText().toString().trim();
        sellingPrice = et_selling_price.getText().toString().trim();
        contact = et_contact.getText().toString().trim();
        description = et_description.getText().toString().trim();

        if (TextUtils.isEmpty(nickName)) {
            et_nickName.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.PleaseEnterName));

        } else if (TextUtils.isEmpty(years)) {
            et_nickName.clearFocus();
            et_years.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.YearIsRequired));
            return;
        } else if (TextUtils.isEmpty(sellingPrice)) {
            et_years.clearFocus();
            et_selling_price.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Selling_Price));
            return;
        } else if (stateId.length() == 0) {
            et_selling_price.clearFocus();
            spinState.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Please_Select_State));
        } else if (cityId.length() == 0) {
            spinState.clearFocus();
            spinCity.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Please_Select_City));
        } else if (gender.length() == 0) {
            spinCity.clearFocus();
            radio_group_gender.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Gender));
        } else if (gender.equalsIgnoreCase("Male") && castration.length() == 0) {
            radio_group_gender.clearCheck();
            showToast(mContext, mContext.getString(R.string.Castration));
        } else if (gender.equalsIgnoreCase("Female") && TextUtils.isEmpty(lactationNo)) {
            radio_group_gender.clearCheck();
            et_selling_price.clearFocus();
            showToast(mContext, mContext.getResources().getString(R.string.LactationNo));
            et_lactation.requestFocus();
        } else if (gender.equalsIgnoreCase("Female") && TextUtils.isEmpty(lastCalvingOccured)) {
            et_lactation.clearFocus();
            et_lastCalving.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.LastCalvingOccured));

        } else if (gender.equalsIgnoreCase("Female") && milkingStatus.length() == 0) {
            showToast(mContext, mContext.getResources().getString(R.string.MilkingStatus));
        } else if (gender.equalsIgnoreCase("Female") && milkingStatus.equalsIgnoreCase("In Milk")
                && TextUtils.isEmpty(peakYeild)) {
            et_lastCalving.requestFocus();
            et_peak_milk_yield.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.PeakMilkYield));

        } else if (gender.equalsIgnoreCase("Female") && milkingStatus.equalsIgnoreCase("In Milk")
                && genderCalf.length() == 0) {
            et_peak_milk_yield.clearFocus();
            radiogroup_gender_of_calf.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Gender_of_calf_at_foot));

        } else if (gender.equalsIgnoreCase("Female") && milkingStatus.equalsIgnoreCase("In Milk")
                && calfStatus.length() == 0) {
            radiogroup_gender_of_calf.clearFocus();
            radiogroup_status_calf.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.CalfStatus));

        } else if (gender.equalsIgnoreCase("Female") && interCalvingPeriod.length() == 0) {
            radiogroup_status_calf.clearFocus();
            et_peak_milk_yield.clearFocus();
            radio_group_inter_calving.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Inter_CalvingPeriod));
        } else if (gender.equalsIgnoreCase("Female") && pregnant.length() == 0) {
            radio_group_inter_calving.clearFocus();
            radio_group_pregnanacy.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Is_pregnant));
        } else if (gender.equalsIgnoreCase("Female") && pregnant.equalsIgnoreCase("1") && TextUtils.isEmpty(pregnantDay)) {
            et_pregnancy_day.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Preganancy_Day));

        } else if (gender.equalsIgnoreCase("Female") && milkingStatus.equalsIgnoreCase("In Milk")
                && TextUtils.isEmpty(interCalvingPeriod)) {
            radio_group_inter_calving.requestFocus();
            et_pregnancy_day.clearFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Inter_CalvingPeriod));

        } else if (contact.length() < 10) {

            radio_group_inter_calving.clearFocus();
            et_contact.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Please_enter_valid_mobile_number));
        } else if (TextUtils.isEmpty(description)) {
            et_contact.clearFocus();
            et_description.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Description));
        } else {
            et_lactation.clearFocus();
            et_pregnancy_day.clearFocus();
            et_peak_milk_yield.clearFocus();
            et_lastCalving.clearFocus();
            et_pregnancy_month.clearFocus();
            et_pregnancy_day.clearFocus();
            et_contact.clearFocus();
            et_description.clearFocus();
            layoutAnimalDetails.setVisibility(View.GONE);
            layoutImgUpload.setVisibility(View.VISIBLE);

        }


    }

    private void checkAnimalVaccinValidation() {
        fmdVaccinationPeriod = et_fmd_vaccination_period.getText().toString().trim();
        hsVaccinationPeriod = et_hs_vaccination_period.getText().toString().trim();
        quarterVaccinationPeriod = et_quarter_vaccination_period.getText().toString().trim();
        brucellousisVaccinationPeriod = et_brucellousis_vaccination_period.getText().toString().trim();
        dewormingPeriod = et_deworming_period.getText().toString().trim();


        if (fmdVaccinationStatus.length() == 0) {
            showToast(mContext, mContext.getResources().getString(R.string.FMD_Vaccination_done));
        } else if (fmdVaccinationStatus.equalsIgnoreCase("1") && TextUtils.isEmpty(fmdVaccinationPeriod)) {
            et_fmd_vaccination_period.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.how_many_month_before_fmd_vaccination_done));
        } else if (hsVaccinationStatus.length() == 0) {
            et_fmd_vaccination_period.clearFocus();
            showToast(mContext, mContext.getResources().getString(R.string.H_S_Vaccination_done));
        } else if (hsVaccinationStatus.equalsIgnoreCase("1") && TextUtils.isEmpty(hsVaccinationPeriod)) {
            et_hs_vaccination_period.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.how_manyBefore_H_S_Vaccination_done));
        } else if (blackQuarterVaccinationStatus.length() == 0) {
            et_hs_vaccination_period.clearFocus();

            showToast(mContext, mContext.getResources().getString(R.string.BlackQuarter_VaccinationDone));

        } else if (blackQuarterVaccinationStatus.equalsIgnoreCase("1") && TextUtils.isEmpty(quarterVaccinationPeriod)) {
            et_quarter_vaccination_period.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.how_manyMonth_black_quarter_vaccination_period));

        } else if (brucellousisVaccinationStatus.length() == 0) {
            et_quarter_vaccination_period.clearFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Brucellousis_VaccinationatCalf_Foot));
        } else if (brucellousisVaccinationStatus.equalsIgnoreCase("1") && TextUtils.isEmpty(brucellousisVaccinationPeriod)) {

            et_brucellousis_vaccination_period.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.how_many_month_before_brucellousis_vaccination_done));
        } else if (dewormingStatus.length() == 0) {
            et_brucellousis_vaccination_period.clearFocus();
            radio_group_deworming.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Please_select_deworming_status));
        } else if (dewormingStatus.equalsIgnoreCase("1") && TextUtils.isEmpty(dewormingPeriod)) {
            et_deworming_period.requestFocus();
            showToast(mContext, mContext.getResources().getString(R.string.Deworming));

        } else {
            et_fmd_vaccination_period.clearFocus();
            et_hs_vaccination_period.clearFocus();
            et_quarter_vaccination_period.clearFocus();
            et_brucellousis_vaccination_period.clearFocus();
            et_deworming_period.clearFocus();
            registerAnimal();
        }


    }


    private void intitImageRecycleView() {

        image_adapter = new Animal_Image_item_adapter(getActivity(), beanUploadImages);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        image_upload_recycle.setLayoutManager(horizontalLayoutManager);
        image_upload_recycle.setAdapter(image_adapter);


    }

    private void showImagePickerOptions() {
        ImagePickerAcitvity.showImagePickerOptions(mContext, new ImagePickerAcitvity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(mContext, ImagePickerAcitvity.class);
        intent.putExtra(ImagePickerAcitvity.INTENT_IMAGE_PICKER_OPTION, REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerAcitvity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerAcitvity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerAcitvity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(mContext, ImagePickerAcitvity.class);
        intent.putExtra(ImagePickerAcitvity.INTENT_IMAGE_PICKER_OPTION, REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerAcitvity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode====" + requestCode);

        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getParcelableExtra("path");
                if (uri != null) {
                    beanUploadImages.add(new BeanUploadImage("", uri.getPath()));
                    System.out.println("uri====" + uri);
                    intitImageRecycleView();
                }
            }
        } else if (requestCode == GALLERY_VIDEO) {
            System.out.println("result===" + requestCode);
            if (data != null) {
                videoUri = data.getData();
                selectedVideoPath = getPath(videoUri);
                System.out.println("====onActivityResult==selectedVideoPath======" + selectedVideoPath);
                saveVideoToInternalStorage(selectedVideoPath);
                first_video_cow.setVideoURI(videoUri);
                first_video_cow.requestFocus();
                first_video_cow.start();
                first_video_cow.setVisibility(View.VISIBLE);
                video_play_button.setVisibility(View.VISIBLE);

            }

        } else if (requestCode == CAMERA_VIDEO) {
            videoUri = data.getData();
            selectedVideoPath = getPath(videoUri);
            System.out.println("recordedVideoPath====" + selectedVideoPath);
            saveVideoToInternalStorage(selectedVideoPath);
            first_video_cow.setVideoURI(videoUri);
            first_video_cow.requestFocus();
            first_video_cow.start();
            first_video_cow.setVisibility(View.VISIBLE);
            video_play_button.setVisibility(View.VISIBLE);
        }

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                FragmentAnimalRegister.this.openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle(R.string.Choose_from);
        String[] pictureDialogItems = {
                mContext.getResources().getString(R.string.SelectVideoFromGallery),
                mContext.getResources().getString(R.string.RecordVideoFromCamera)};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseVideoFromGallary();
                                break;
                            case 1:
                                takeVideoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void chooseVideoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        galleryIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        galleryIntent.putExtra(String.valueOf(mContext), GALLERY_VIDEO);

        startActivityForResult(galleryIntent, GALLERY_VIDEO);
    }


    private void takeVideoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(String.valueOf(mContext), CAMERA_VIDEO);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, CAMERA_VIDEO);
    }

    public void checkBackButton() {

        if (layoutAnimalDetails.getVisibility() == View.VISIBLE) {

            btnsubmit_animal_details.setText(mContext.getResources().getString(R.string.Next));
            getActivity().onBackPressed();
        } else if (layoutImgUpload.getVisibility() == View.VISIBLE) {
            layoutImgUpload.setVisibility(View.GONE);
            layoutAnimalVaccian.setVisibility(View.GONE);
            layoutAnimalDetails.setVisibility(View.VISIBLE);
            btnsubmit_animal_details.setText(mContext.getResources().getString(R.string.Next));
        } else if (layoutAnimalVaccian.getVisibility() == View.VISIBLE) {
            layoutImgUpload.setVisibility(View.VISIBLE);
            layoutAnimalVaccian.setVisibility(View.GONE);
            layoutAnimalDetails.setVisibility(View.GONE);
            btnsubmit_animal_details.setText(mContext.getResources().getString(R.string.Next));
        }


    }

    public void setAddress() {
        tvlocation.setText(str_location_address);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver),
                new IntentFilter(strKeyAddress));
        setAddress();
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }

    private void saveVideoToInternalStorage(String filePath) {
        File newfile;
        try {

            File currentFile = new File(filePath);
            File videoDirectory = new File(Environment.getExternalStorageDirectory() + VIDEO_DIRECTORY);
            newfile = new File(videoDirectory, Calendar.getInstance().getTimeInMillis() + ".mp4");


            if (!videoDirectory.exists()) {
                videoDirectory.mkdirs();
            }
            if (currentFile.exists()) {
                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                selectedVideoPath = newfile.getPath();
                System.out.println("video file====" + "Video file saved successfully.");
            } else {
                Log.v("vii", "Video saving failed. Source file missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPath(Uri uriVideo) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uriVideo, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void registerAnimal() {
        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Registering...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        showToast(mContext, jsonObject.getString("user_status_message"));
                        goNextClass(mContext, Animal_AdsActivity.class);
                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);


        multipartBuilder.addFormDataPart("user_id", sessionManager.getValueSesion(KEY_UserID));
        System.out.println("userId=====" + sessionManager.getValueSesion(KEY_UserID));
        multipartBuilder.addFormDataPart("main_cat", mainCategory);
        System.out.println("mainCat=====" + mainCategory);
        multipartBuilder.addFormDataPart("sub_cat", subCategory);
        System.out.println("subC=====" + subCategory);
        multipartBuilder.addFormDataPart("tag_no", tagNo);
        System.out.println("tag=====" + tagNo);
        multipartBuilder.addFormDataPart("nick_name", nickName);
        System.out.println("nick=====" + nickName);
        multipartBuilder.addFormDataPart("year", String.valueOf(years));
        System.out.println("year=====" + years);
        multipartBuilder.addFormDataPart("month", String.valueOf(months));
        System.out.println("month=====" + months);
        multipartBuilder.addFormDataPart("state_id", stateId);
        System.out.println("state_id=====" + stateId);
        multipartBuilder.addFormDataPart("city_id", cityId);
        System.out.println("cityId=====" + cityId);
        multipartBuilder.addFormDataPart("gender", gender);
        System.out.println("gender=====" + gender);
        multipartBuilder.addFormDataPart("lactation_no", lactationNo);
        multipartBuilder.addFormDataPart("castration", castration);
        System.out.println("lact=====" + lactationNo);
        multipartBuilder.addFormDataPart("last_calving_occured", lastCalvingOccured);
        System.out.println("last=====" + lastCalvingOccured);
        multipartBuilder.addFormDataPart("milk_status", milkingStatus);
        multipartBuilder.addFormDataPart("peak_milk_yeild", peakYeild);
        System.out.println("peakYeild=====" + peakYeild);
        multipartBuilder.addFormDataPart("sex_of_calf", genderCalf);
        System.out.println("genderC=====" + genderCalf);
        multipartBuilder.addFormDataPart("calf_status", calfStatus);
        System.out.println("CS=====" + calfStatus);
        multipartBuilder.addFormDataPart("inter_calving_period", interCalvingPeriod);
        System.out.println("interC=====" + interCalvingPeriod);
        multipartBuilder.addFormDataPart("is_pragnant", pregnant);
        System.out.println("preg=====" + pregnant);
        multipartBuilder.addFormDataPart("pragnant_month", pregnantMonth);
        System.out.println("pM=====" + pregnantMonth);
        multipartBuilder.addFormDataPart("pragnant_day", pregnantDay);
        System.out.println("pregD=====" + pregnantDay);
        multipartBuilder.addFormDataPart("fmd_vaccination", fmdVaccinationStatus);
        System.out.println("fmd=====" + fmdVaccinationStatus);
        multipartBuilder.addFormDataPart("fmd_vaccination_period", fmdVaccinationPeriod);
        System.out.println("FP=====" + fmdVaccinationPeriod);
        multipartBuilder.addFormDataPart("hs_vaccination", hsVaccinationStatus);
        System.out.println("hs=====" + hsVaccinationPeriod);
        multipartBuilder.addFormDataPart("hs_vaccination_period", hsVaccinationPeriod);
        System.out.println("hsP=====" + hsVaccinationPeriod);
        multipartBuilder.addFormDataPart("black_quarter_vaccination", blackQuarterVaccinationStatus);
        System.out.println("blac=====" + blackQuarterVaccinationStatus);
        multipartBuilder.addFormDataPart("black_quarter_vaccination_period", quarterVaccinationPeriod);
        System.out.println("BP=====" + quarterVaccinationPeriod);
        multipartBuilder.addFormDataPart("brucellousis_vaccination", brucellousisVaccinationStatus);
        System.out.println("bru=====" + brucellousisVaccinationStatus);
        multipartBuilder.addFormDataPart("brucellousis_vaccination_period", brucellousisVaccinationPeriod);
        System.out.println("bruP=====" + brucellousisVaccinationPeriod);
        multipartBuilder.addFormDataPart("deworming", dewormingStatus);
        System.out.println("dew=====" + dewormingPeriod);
        multipartBuilder.addFormDataPart("deworming_period", dewormingPeriod);
        System.out.println("DP=====" + dewormingPeriod);
        multipartBuilder.addFormDataPart("selling_price", sellingPrice);
        System.out.println("SP=====" + sellingPrice);
        multipartBuilder.addFormDataPart("contact", contact);
        System.out.print("===contact" + contact);
        multipartBuilder.addFormDataPart("description", description);
        System.out.println("Des=====" + description);


        for (int i = 0; i < beanUploadImages.size(); i++) {
            String filePaths = beanUploadImages.get(i).getPath();
            File sourceFile = new File(filePaths);
            System.out.print(sourceFile + "img=====");
            multipartBuilder.addFormDataPart("image[" + i + "]", sourceFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), sourceFile));

        }
        if (selectedVideoPath.length() > 0) {
            System.out.println("video==" + selectedVideoPath);
            File fileVideo = new File(selectedVideoPath);
            System.out.println("==fileV==" + fileVideo);
            multipartBuilder.addFormDataPart("video", fileVideo.getName(), RequestBody.create(MediaType.parse("video/mp4"), fileVideo));
        }
        RequestBody body = multipartBuilder.build();
        webServiceCaller.addRequestBody(body);
        webServiceCaller.execute(registerAnimalAPI);


    }


}
