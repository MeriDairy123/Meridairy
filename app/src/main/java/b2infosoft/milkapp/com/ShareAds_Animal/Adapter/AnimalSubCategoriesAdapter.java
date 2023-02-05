package b2infosoft.milkapp.com.ShareAds_Animal.Adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.Model.AnimalSubCategoriesData;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Fragment.FragmentAnimalRegister;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalMainCategoryId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalSubCategoryId;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nameFirstLatterCapitalize;

public class AnimalSubCategoriesAdapter extends RecyclerView.Adapter<AnimalSubCategoriesAdapter.MyViewHolder> {
    Context mContext;
    List<AnimalSubCategoriesData> subCategoryList;

    public AnimalSubCategoriesAdapter(Context mContext, List<AnimalSubCategoriesData> subCategoryList) {
        this.mContext = mContext;
        this.subCategoryList = subCategoryList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.animal_sub_categories_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        final AnimalSubCategoriesData album = subCategoryList.get(position);
        String name = nameFirstLatterCapitalize(album.getAnimalname());
        viewHolder.text_animal_name.setText(name);
        viewHolder.text_animal_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.print("=====mainCat====" + album.getAnimalMainId());
                System.out.print("=====subC====" + album.getCategoryid());
                SessionManager sessionManager = new SessionManager((Activity) mContext);
                sessionManager.setValueSession(KEY_AnimalMainCategoryId, album.getAnimalMainId());
                sessionManager.setValueSession(KEY_AnimalSubCategoryId, album.getCategoryid());

                Fragment fragment = new FragmentAnimalRegister();
                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container_share_ads, fragment)
                        .addToBackStack(null).commit();
            }


        });


    }

    @Override
    public int getItemCount() {
        return subCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_animal_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            text_animal_name = itemView.findViewById(R.id.text_animal_name);


        }
    }
}
