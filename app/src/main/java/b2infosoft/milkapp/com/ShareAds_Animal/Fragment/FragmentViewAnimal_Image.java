package b2infosoft.milkapp.com.ShareAds_Animal.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.useful.TouchImageView;

public class FragmentViewAnimal_Image extends Fragment {

    TouchImageView animal_image_view;
    Toolbar toolbar;
    TextView toolbar_title;
    Context mContext;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_animal_image, container, false);

        mContext = getActivity();
        animal_image_view = view.findViewById(R.id.animal_image_view);
        toolbar_title = view.findViewById(R.id.toolbar_title);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(mContext.getString(R.string.Description));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        String image_path = getArguments().getString("imgUrl");

        Glide.with(mContext)
                .load(image_path)
                .placeholder(R.color.color_light_white)
                .into(animal_image_view);

        return view;
    }


}
