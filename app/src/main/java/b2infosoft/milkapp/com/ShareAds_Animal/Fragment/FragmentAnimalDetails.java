package b2infosoft.milkapp.com.ShareAds_Animal.Fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import b2infosoft.milkapp.com.Model.BeanAnimalDashboard;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Adapter.AnimalImagePagerAdapter;
import b2infosoft.milkapp.com.ShareAds_Animal.VideoPlayerActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


public class FragmentAnimalDetails extends Fragment {
    private static final Integer[] IMAGES = {R.drawable.ic_buyer, R.drawable.ic_desktop_computer, R.drawable.ic_buffalo};
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    View view;
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    LinearLayout first_detail_layout;
    ViewPager viewPager;
    CirclePageIndicator indicator;
    SessionManager sessionManager;
    BeanAnimalDashboard beanAnimalDashboard;
    VideoView animalVideoView;
    Drawable drawableYes, drawableNO;
    String strYes = "", strNO = "";
    String strMale = "", strFemale = "";
    String year = "", month = "";
    String video_path = "";
    Uri videoUri;
    View layoutVideo;
    String ageMonth = "", ageYear = "";
    Animation slide;
    //layout
    LinearLayout more_details_layout, castration_layout, pregnant_layout, milking_status_layout, peak_yeild_layout,
            lactation_layout, last_calving_layout, layout_calf_gender, inter_calving_layout;
    ImageView btn_video_player;
    TextView tvId, tvAge, tvCategory, tvBreed, tvGender, tvMilkStatus,
            tvYeild, tvLactation, tvLast_calving, tvInter_calving, tvIsPregnant, tvCalf_gender;
    TextView tvCastration;
    TextView tvPrice, btn_more_details,
            tvAddress, tvContact,
            tvDescription;
    ImageView img_fmd_vaccination_value, img_hs_vaccination_value, img_black_quarter_vaccination_value, img_deworming_value,
            img_brucellousis_vaccination_value;
    TextView tv_fmd_vaccination_period_value,
            tv_hs_vaccination_period_value, tv_black_quarter_vaccination_period_value,
            tv_brucellousis_vaccination_period_value, tv_deworming_value_period;
    LinearLayout layout_deworming_period, layout_brucellousis_period, layout_quarter_period, layout_hs_period, layout_fmd_period;
    String strmilkStatus = "", strInterCalvingPeriod = "", strIsPregnant = "", strPregnantMonth = "", strPregnantDays = "", strAddress = "";
    String strCastration = "";
    private ArrayList<String> ImagesArray = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_animal_details, container, false);
        mContext = getActivity();
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = view.findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar_title.setText("Animal Detail");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        first_detail_layout = view.findViewById(R.id.first_detail_layout);
        slide = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
        first_detail_layout.startAnimation(slide);

        sessionManager = new SessionManager(mContext);
        ImagesArray = new ArrayList<>();
        Gson gson = new Gson();
        String json = sessionManager.getValueSesion("beanAnimalList");
        beanAnimalDashboard = gson.fromJson(json, BeanAnimalDashboard.class);
        String title = nullCheckFunction(beanAnimalDashboard.getNick_name()).toUpperCase();
        toolbar_title.setText(title);
        drawableYes = mContext.getResources().getDrawable(R.drawable.ic_checked);
        drawableNO = mContext.getResources().getDrawable(R.drawable.ic_close);
        year = mContext.getResources().getString(R.string.Year);
        month = mContext.getResources().getString(R.string.Months);

        strYes = mContext.getResources().getString(R.string.yes);
        strNO = mContext.getResources().getString(R.string.no);
        strMale = mContext.getResources().getString(R.string.Male);
        strFemale = mContext.getResources().getString(R.string.Female);


        layoutVideo = view.findViewById(R.id.layoutVideo);
        animalVideoView = view.findViewById(R.id.animal_video);
        video_path = nullCheckFunction(beanAnimalDashboard.getVideo());
        videoUri = Uri.parse(video_path);
        System.out.println("video_path====" + video_path);
        if (video_path.contains(".mp4")) {
            video_path = BaseImageUrl + nullCheckFunction(beanAnimalDashboard.getVideo());
            videoUri = Uri.parse(video_path);
            layoutVideo.setVisibility(View.VISIBLE);
            // animalVideoView.setVideoURI(videoUri);
        } else {
            layoutVideo.setVisibility(View.GONE);
        }

        btn_video_player = view.findViewById(R.id.btn_video_icon);
        btn_video_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                intent.putExtra("contentUri", videoUri);
                System.out.println("videoUri====" + videoUri);
                startActivity(intent);
            }
        });

        //textView value
        tvId = view.findViewById(R.id.tvId);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvCategory = view.findViewById(R.id.tvCategory);
        tvBreed = view.findViewById(R.id.tvBreed);
        tvGender = view.findViewById(R.id.tvGender);
        tvAge = view.findViewById(R.id.tvAge);
        tvMilkStatus = view.findViewById(R.id.tvMilkStatus);
        tvContact = view.findViewById(R.id.tvContact);
        tvYeild = view.findViewById(R.id.tvYeild);
        tvLactation = view.findViewById(R.id.tvLactation);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvCalf_gender = view.findViewById(R.id.tvCalf_gender);
        tvLast_calving = view.findViewById(R.id.tvLast_calving);
        tvInter_calving = view.findViewById(R.id.tvInter_calving);
        tvCastration = view.findViewById(R.id.tvCastration);
        tvIsPregnant = view.findViewById(R.id.tvIsPregnant);
        btn_more_details = view.findViewById(R.id.btn_more_details);
        btn_more_details.setPaintFlags(btn_more_details.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        img_fmd_vaccination_value = view.findViewById(R.id.img_fmd_vaccination_value);
        tv_fmd_vaccination_period_value = view.findViewById(R.id.fmd_vaccination_period_value);
        img_hs_vaccination_value = view.findViewById(R.id.img_hs_vaccination_value);
        tv_hs_vaccination_period_value = view.findViewById(R.id.hs_vaccination_period_value);
        img_black_quarter_vaccination_value = view.findViewById(R.id.img_black_quarter_vaccination_value);
        tv_black_quarter_vaccination_period_value = view.findViewById(R.id.black_quarter_vaccination_period_value);
        img_brucellousis_vaccination_value = view.findViewById(R.id.img_brucellousis_vaccination_value);
        tv_brucellousis_vaccination_period_value = view.findViewById(R.id.brucellousis_vaccination_period_value);
        img_deworming_value = view.findViewById(R.id.img_deworming_value);
        tv_deworming_value_period = view.findViewById(R.id.deworming_value_period);
        tvPrice = view.findViewById(R.id.tvPrice);


        //layout
        milking_status_layout = view.findViewById(R.id.milking_status_layout);
        peak_yeild_layout = view.findViewById(R.id.peak_yeild_layout);
        lactation_layout = view.findViewById(R.id.lactation_layout);
        last_calving_layout = view.findViewById(R.id.last_calving_layout);
        layout_calf_gender = view.findViewById(R.id.layout_calf_gender);
        inter_calving_layout = view.findViewById(R.id.inter_calving_layout);
        castration_layout = view.findViewById(R.id.castration_layout);
        pregnant_layout = view.findViewById(R.id.pregnant_layout);
        more_details_layout = view.findViewById(R.id.more_details_layout);
        layout_brucellousis_period = view.findViewById(R.id.layout_brucellousis_period);
        layout_deworming_period = view.findViewById(R.id.layout_deworming_period);
        layout_fmd_period = view.findViewById(R.id.layout_fmd_period);
        layout_hs_period = view.findViewById(R.id.layout_hs_period);
        layout_quarter_period = view.findViewById(R.id.layout_quarter_period);


        more_details_layout.setVisibility(View.GONE);


        //view Pager
        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.circle_indicator);
        imageAnimalSlider();

        btn_more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_more_details.setVisibility(View.GONE);
                more_details_layout.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void imageAnimalSlider() {

        String[] imageUrl = beanAnimalDashboard.getImage().split("\\|");

        System.out.println("imageUrl==array==" + imageUrl.length);

        for (int i = 0; i < imageUrl.length; i++) {
            ImagesArray.add(BaseImageUrl + imageUrl[i]);
        }

        viewPager.setAdapter(new AnimalImagePagerAdapter(mContext, ImagesArray));
        indicator.setViewPager(viewPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(3 * density);

        NUM_PAGES = IMAGES.length;

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });

        getAnimalData();
    }

    public void getAnimalData() {


        tvPrice.setText(mContext.getResources().getString(R.string.Rupee_symbol) + " " + nullCheckFunction(beanAnimalDashboard.getSelling_price()));

        if (beanAnimalDashboard.getGender().trim().equalsIgnoreCase("Female")) {

            animalFemale();

        } else if (beanAnimalDashboard.getGender().trim().equalsIgnoreCase("Male")) {
            animalMale();
        }
        tvId.setText(beanAnimalDashboard.getId());
        strAddress = beanAnimalDashboard.getCity_name() + "," + beanAnimalDashboard.getState_name();
        tvAddress.setText(strAddress);

        if (beanAnimalDashboard.getGender().trim().equalsIgnoreCase("Male")) {
            tvGender.setText(strMale);
        } else {
            tvGender.setText(strFemale);

        }


        tvCategory.setText(nullCheckFunction(beanAnimalDashboard.getCategory_name()));
        tvBreed.setText(nullCheckFunction(beanAnimalDashboard.getBreed_name()));
        tvDescription.setText(nullCheckFunction(beanAnimalDashboard.getDescription()));
        ageMonth = nullCheckFunction(beanAnimalDashboard.getMonth());
        ageYear = nullCheckFunction(beanAnimalDashboard.getYear());
        if (ageMonth.length() > 0 && ageYear.length() > 0) {
            tvAge.setText(ageYear + "." + ageMonth + " " + year);
        } else if (ageMonth.length() > 0 && ageYear.length() == 0) {
            tvAge.setText(ageMonth + " " + month);
        } else if (ageMonth.length() == 0 && ageYear.length() > 0) {
            tvAge.setText(ageYear + " " + year);
        }

        animalVaccination();

//          beanAnimalDashboard.getPragnant_day();
//          beanAnimalDashboard.getPragnant_month();
        // beanAnimalDashboard.getDeworming_period();


    }


    private void animalMale() {
        milking_status_layout.setVisibility(View.GONE);
        peak_yeild_layout.setVisibility(View.GONE);
        lactation_layout.setVisibility(View.GONE);
        last_calving_layout.setVisibility(View.GONE);
        layout_calf_gender.setVisibility(View.GONE);
        inter_calving_layout.setVisibility(View.GONE);
        castration_layout.setVisibility(View.VISIBLE);

        strCastration = funYesorNo(beanAnimalDashboard.getCastration());

        tvCastration.setText(strCastration);

    }

    public String funYesorNo(String text) {
        String result = strNO;
        text = nullCheckFunction(text);
        if (text.equalsIgnoreCase("0")) {
            result = strNO;
        } else {
            result = strYes;
        }

        return result;
    }

    private void animalFemale() {

        milking_status_layout.setVisibility(View.VISIBLE);
        peak_yeild_layout.setVisibility(View.VISIBLE);
        lactation_layout.setVisibility(View.VISIBLE);
        last_calving_layout.setVisibility(View.VISIBLE);
        layout_calf_gender.setVisibility(View.VISIBLE);
        inter_calving_layout.setVisibility(View.VISIBLE);
        pregnant_layout.setVisibility(View.VISIBLE);
        castration_layout.setVisibility(View.GONE);


        tvYeild.setText(beanAnimalDashboard.getPeak_milk_yeild() + " " + mContext.getString(R.string.Ltr));
        System.out.println("yield===" + tvYeild);
        tvLactation.setText(nullCheckFunction(beanAnimalDashboard.getLactation_no()));

        if (beanAnimalDashboard.getSex_of_calf().trim().equalsIgnoreCase("Male")) {
            tvCalf_gender.setText(strMale);
        } else {
            tvCalf_gender.setText(strFemale);

        }
        tvMilkStatus.setText(nullCheckFunction(beanAnimalDashboard.getMilk_status()));
        tvContact.setText(nullCheckFunction(beanAnimalDashboard.getContact()));
        tvLast_calving.setText(nullCheckFunction(beanAnimalDashboard.getLast_calving_occured()));
        tvInter_calving.setText(nullCheckFunction(beanAnimalDashboard.getInter_calving_period()));

        strInterCalvingPeriod = beanAnimalDashboard.getInter_calving_period();

        if (strInterCalvingPeriod.equalsIgnoreCase("Less than 1 year")) {
            tvInter_calving.setText(mContext.getString(R.string.less_than_oneYear));
        } else if (strInterCalvingPeriod.equalsIgnoreCase("1-1.5 years")) {
            tvInter_calving.setText(mContext.getString(R.string.one_onepointFive));

        } else if (strInterCalvingPeriod.equalsIgnoreCase("1.5-2 years")) {
            tvInter_calving.setText(mContext.getString(R.string.onePointFive_to_two));
        } else if (strInterCalvingPeriod.equalsIgnoreCase("More than 2 years")) {
            tvInter_calving.setText(mContext.getString(R.string.more_than_twoYear));
        }


        strmilkStatus = beanAnimalDashboard.getMilk_status();

        if (strmilkStatus.equalsIgnoreCase("In Milk")) {
            tvMilkStatus.setText(mContext.getString(R.string.InMilk));

        } else {
            tvMilkStatus.setText(mContext.getString(R.string.Dry));

        }
        strIsPregnant = funYesorNo(beanAnimalDashboard.getIs_pragnant());

        if (!beanAnimalDashboard.getIs_pragnant().equalsIgnoreCase("0")) {
            strPregnantDays = beanAnimalDashboard.getPragnant_day();
            strPregnantMonth = beanAnimalDashboard.getPragnant_month();
            tvIsPregnant.setText(strIsPregnant + "  " + strPregnantMonth + "." + strPregnantDays + " " + month);

        } else {
            tvIsPregnant.setText(strIsPregnant);

        }


    }

    private void animalVaccination() {


        if (nullCheckFunction(beanAnimalDashboard.getFmd_vaccination()).equalsIgnoreCase("1")) {
            img_fmd_vaccination_value.setImageDrawable(drawableYes);
            layout_fmd_period.setVisibility(View.VISIBLE);
        } else if (nullCheckFunction(beanAnimalDashboard.getFmd_vaccination()).equalsIgnoreCase("0")) {
            img_fmd_vaccination_value.setImageDrawable(drawableNO);
            layout_fmd_period.setVisibility(View.GONE);
        }
        if (nullCheckFunction(beanAnimalDashboard.getHs_vaccination()).equalsIgnoreCase("1")) {
            img_hs_vaccination_value.setImageDrawable(drawableYes);
            layout_hs_period.setVisibility(View.VISIBLE);
        } else if (nullCheckFunction(beanAnimalDashboard.getHs_vaccination()).equalsIgnoreCase("0")) {
            img_hs_vaccination_value.setImageDrawable(drawableNO);
            layout_hs_period.setVisibility(View.GONE);
        }

        if (nullCheckFunction(beanAnimalDashboard.getBlack_quarter_vaccination()).equalsIgnoreCase("1")) {
            img_black_quarter_vaccination_value.setImageDrawable(drawableYes);
            layout_quarter_period.setVisibility(View.VISIBLE);
        } else if (nullCheckFunction(beanAnimalDashboard.getBlack_quarter_vaccination()).equalsIgnoreCase("0")) {
            img_black_quarter_vaccination_value.setImageDrawable(drawableNO);
            layout_quarter_period.setVisibility(View.GONE);
        }
        if (nullCheckFunction(beanAnimalDashboard.getBrucellousis_vaccination()).equalsIgnoreCase("1")) {
            img_brucellousis_vaccination_value.setImageDrawable(drawableYes);
            layout_brucellousis_period.setVisibility(View.VISIBLE);
        } else if (nullCheckFunction(beanAnimalDashboard.getBrucellousis_vaccination()).equalsIgnoreCase("0")) {
            img_brucellousis_vaccination_value.setImageDrawable(drawableNO);
            layout_brucellousis_period.setVisibility(View.GONE);
        }
        if (nullCheckFunction(beanAnimalDashboard.getDeworming()).equalsIgnoreCase("1")) {
            img_deworming_value.setImageDrawable(drawableYes);
            layout_deworming_period.setVisibility(View.VISIBLE);
        } else if (nullCheckFunction(beanAnimalDashboard.getDeworming()).equalsIgnoreCase("0")) {
            img_deworming_value.setImageDrawable(drawableNO);
            layout_deworming_period.setVisibility(View.GONE);
        }


        tv_fmd_vaccination_period_value.setText(nullCheckFunction(beanAnimalDashboard.getFmd_vaccination_period()));
        tv_hs_vaccination_period_value.setText(nullCheckFunction(beanAnimalDashboard.getHs_vaccination_period()));
        tv_black_quarter_vaccination_period_value.setText(nullCheckFunction(beanAnimalDashboard.getBlack_quarter_vaccination_period()));
        tv_brucellousis_vaccination_period_value.setText(nullCheckFunction(beanAnimalDashboard.getBrucellousis_vaccination_period()));
        tv_deworming_value_period.setText(nullCheckFunction(beanAnimalDashboard.getDeworming_period()));


    }
}
