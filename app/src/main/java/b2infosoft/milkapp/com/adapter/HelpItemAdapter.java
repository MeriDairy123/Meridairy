package b2infosoft.milkapp.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.Model.Help_item;
import b2infosoft.milkapp.com.R;


public class HelpItemAdapter extends RecyclerView.Adapter<HelpItemAdapter.MyViewHolder> {

    private Context mContext;
    private List<Help_item> albumList;


    public HelpItemAdapter(Context mContext, List<Help_item> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;

    }

    public void updateData(List<Help_item> albumList) {
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Help_item album = albumList.get(position);

        holder.tv_videoTilteName.setText(album.getVideo_title());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = album.getVideo_link();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mContext.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_videoTilteName;
        public ImageView youTubeThumbnail;
        public MyViewHolder(View view) {
            super(view);
            youTubeThumbnail = view.findViewById(R.id.youTubeThumbnail);
            tv_videoTilteName = view.findViewById(R.id.tv_videoTilteName);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
