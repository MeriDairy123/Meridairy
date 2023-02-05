package b2infosoft.milkapp.com.ShareAds_Animal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.BeanUploadImage;

import static b2infosoft.milkapp.com.ShareAds_Animal.Fragment.Fragment_Animal_Categories.beanUploadImages;


public class Animal_Image_item_adapter extends RecyclerView.Adapter<Animal_Image_item_adapter.MyViewHolder> {

    private Context mContext;
    private List<BeanUploadImage> albumList;

    public Animal_Image_item_adapter(Context mContext, List<BeanUploadImage> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        BeanUploadImage beanUploadImage = albumList.get(position);

        Glide.with(mContext).load(beanUploadImage.getPath())
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader)).into(holder.img_Attach);
/*
        Glide.with(mContext).load(beanUploadImage.getPath())
                        .into(holder.img_Attach);*/
        holder.btn_remove.setVisibility(View.VISIBLE);
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumList.remove(position);
                System.out.println("filedetails===" + beanUploadImages.size());
                notifyItemRemoved(position);
                notifyDataSetChanged();

            }
        });


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_Attach, btn_remove;


        public MyViewHolder(View view) {
            super(view);
            img_Attach = view.findViewById(R.id.img_Attach);
            btn_remove = view.findViewById(R.id.btn_remove);

        }
    }


}