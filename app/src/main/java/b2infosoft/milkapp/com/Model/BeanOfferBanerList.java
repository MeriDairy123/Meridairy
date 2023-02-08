package b2infosoft.milkapp.com.Model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getBanerAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerCustomText;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerImage;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerImageId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerImagePath;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_skip_ad;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


/**
 * Created by B2infosoft on 19/02/2019.
 */

public class BeanOfferBanerList {

    public int id = 0, product_id = 0;
    public double price = 0;
    public String thumb = "";
    public String image = "";
    public String title = "";
    public String description = "";
    public String type = "";

    public BeanOfferBanerList(int id, int product_id, double price,
                              String thumb, String image, String title, String description, String type) {
        this.id = id;
        this.product_id = product_id;
        this.price = price;
        this.image = image;
        this.thumb = thumb;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public static void getBannerOfferList(final Context mContext, boolean DialogYesNo) {
        if (isNetworkAvaliable(mContext)) {
            final SessionManager sessionManager = new SessionManager(mContext);


            NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Loading Offer...", DialogYesNo) {
                @Override
                public void handleResponse(String response) {
                    try {
                        final ArrayList<BeanOfferBanerList> offerList;
                        JSONObject jsonObject = new JSONObject(response);
                        String imgpath = "", baner_image = "", custom_msg = "",skip_status="";
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            BaseImageUrl = jsonObject.getString("path");
                            imgpath = jsonObject.getString("path");
                            JSONArray dataJsonArray = jsonObject.getJSONArray("data");
                            offerList = new ArrayList<BeanOfferBanerList>();

                            for (int i = 0; i < dataJsonArray.length(); i++) {
                                JSONObject object = dataJsonArray.getJSONObject(i);
                                String type = object.getString("type");
                                if (!type.equalsIgnoreCase("slider")) {

                                    baner_image = object.getString("image");
                                    custom_msg = object.getString("description");
                                    skip_status = object.getString("skip_status");
                                    sessionManager.setValueSession(KEY_BannerImageId, String.valueOf(object.getInt("id")));
                                    sessionManager.setValueSession(KEY_BannerImagePath, imgpath);
                                    sessionManager.setValueSession(KEY_BannerImage, baner_image);
                                    sessionManager.setValueSession(KEY_BannerCustomText, custom_msg);
                                    sessionManager.setValueSession(Key_skip_ad, skip_status);

                                } else {
                                    offerList.add(new BeanOfferBanerList(
                                            object.getInt("id"),
                                            object.getInt("product_id"),
                                            object.getDouble("price"),
                                            BaseImageUrl + object.getString("thumb"),
                                            BaseImageUrl + object.getString("image"),
                                            object.getString("title"),
                                            object.getString("description"),
                                            object.getString("type")));
                                }
                            }

                            DatabaseHandler db = DatabaseHandler.getDbHelper(mContext);
                            if (!offerList.isEmpty()) {
                                ArrayList<BeanOfferBanerList> beanOfferList = db.getBannerOfferList();
                                if (beanOfferList.size() != 0) {
                                    db.deleteBannerOffer();
                                }
                                for (int i = 0; i < offerList.size(); i++) {
                                    db.addBannerOffer(offerList.get(i).id, offerList.get(i).product_id,
                                            offerList.get(i).price, offerList.get(i).thumb,
                                            offerList.get(i).image, offerList.get(i).title,
                                            offerList.get(i).description, offerList.get(i).type);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            serviceCaller.execute(getBanerAPI);
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }


}
