package b2infosoft.milkapp.com.ShareAds_Animal.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadrosid.svgloader.SvgLoader;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Model.BeanAnimalDashboard;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Fragment.FragmentAnimalDetails;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.ShareAds_Animal.Animal_AdsActivity.mListAnimalData;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getAnimalsAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getDeleteAnimalsAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nameFirstLatterCapitalize;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class AnimalMyUpload_ListingAdapter extends RecyclerView.Adapter<AnimalMyUpload_ListingAdapter.MyViewHolder> {
    Context mContext;
    List<BeanAnimalDashboard> mList;

    SessionManager sessionManager;
    ImageView img_delete;
    String imgUrl = "";

    public AnimalMyUpload_ListingAdapter(Context mContext, List<BeanAnimalDashboard> list) {
        this.mContext = mContext;
        this.mList = list;
        sessionManager = new SessionManager(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.animal_dashboard_row_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        final BeanAnimalDashboard album = mList.get(position);
        final String name = nameFirstLatterCapitalize(album.getNick_name());
        viewHolder.tvTitle.setText(name);
        viewHolder.tvPrice.setText(nullCheckFunction(mContext.getString(R.string.Rupee_symbol) + "  " + album.getSelling_price()));

        // Glide.with(mContext).load(album.getImage()).into(viewHolder.animal_image);
        Glide.with(mContext).load(album.getImage())
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader))
                .error(R.color.color_light_white)
                .into(viewHolder.animal_image);


        imgUrl = album.getImage();
        System.out.println("image====" + imgUrl);

        String[] imageUrl = imgUrl.split("\\|");
        imgUrl = "";
        System.out.println("imageUrl==array==" + imageUrl.length);
        if (imageUrl.length > 0) {
            imgUrl = BaseImageUrl + imageUrl[0];
        }
        String imageType = imageUrl[imageUrl.length - 1];

        if (imageType.equals("svg")) {
            SvgLoader.pluck().with((Activity) mContext).load(album.getImage(), viewHolder.animal_image);
            // .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
        } else {
            Glide.with(mContext).load(imgUrl).into(viewHolder.animal_image);
        }

        viewHolder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getResources().getString(R.string.Are_you_sure_to_delete_the_animal))
                        .setCancelable(false)
                        .setPositiveButton(mContext.getResources().getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deleteAnimal(album.getId(), position);

                                    }
                                })
                        .setNegativeButton(mContext.getResources().getString(R.string.no), null)
                        .show();


            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String json = gson.toJson(album);
                sessionManager.setValueSession("beanAnimalList", json);

                Fragment fragment = new FragmentAnimalDetails();
                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.container_share_ads, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void deleteAnimal(String animalId, final int postion) {

        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Deleting Animal...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        showToast(mContext, jsonObject.getString("user_status_message"));
                        mList.remove(postion);
                        notifyDataSetChanged();
                        getRefreshAnimalData();

                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceCaller.execute(getDeleteAnimalsAPI + "animal_id=" + animalId);
    }

    private void getRefreshAnimalData() {

        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please Wait...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println("response" + "==animal Data" + response);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                        BaseImageUrl = jsonObject.getString("path");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        mListAnimalData = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject dataObj = jsonArray.getJSONObject(i);
                            mListAnimalData.add(new BeanAnimalDashboard(dataObj.getString("id"),
                                    dataObj.getString("user_id"), dataObj.getString("main_cat"),
                                    dataObj.getString("sub_cat"), dataObj.getString("tag_no"),
                                    dataObj.getString("nick_name"), dataObj.getString("year"),
                                    dataObj.getString("month"), dataObj.getString("gender"),
                                    dataObj.getString("castration"), dataObj.getString("lactation_no"),
                                    dataObj.getString("last_calving_occured"), dataObj.getString("milk_status"),
                                    dataObj.getString("peak_milk_yeild"), dataObj.getString("sex_of_calf"),
                                    dataObj.getString("calf_status"), dataObj.getString("inter_calving_period"),
                                    dataObj.getString("is_pragnant"), dataObj.getString("pragnant_month"),
                                    dataObj.getString("pragnant_day"), dataObj.getString("method_conception"),
                                    dataObj.getString("fmd_vaccination"), dataObj.getString("fmd_vaccination_period"),
                                    dataObj.getString("hs_vaccination"), dataObj.getString("hs_vaccination_period"),
                                    dataObj.getString("black_quarter_vaccination"), dataObj.getString("black_quarter_vaccination_period"),
                                    dataObj.getString("brucellousis_vaccination"), dataObj.getString("brucellousis_vaccination_period"),
                                    dataObj.getString("deworming"), dataObj.getString("deworming_period"),
                                    dataObj.getString("selling_price"), dataObj.getString("description"),
                                    dataObj.getString("contact"), dataObj.getString("image"),
                                    dataObj.getString("video")
                                    , dataObj.getString("category_name"), dataObj.getString("breed_name"),
                                    dataObj.getString("status"), dataObj.getString("state_id")
                                    , dataObj.getString("city_id"), dataObj.getString("state_name"), dataObj.getString("city_name")));
                        }

                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceCaller.execute(getAnimalsAPI + "user_id=" + sessionManager.getValueSesion(KEY_UserID));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice;
        ImageView animal_image, img_delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            animal_image = itemView.findViewById(R.id.image);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_delete.setVisibility(View.VISIBLE);

        }
    }


}
