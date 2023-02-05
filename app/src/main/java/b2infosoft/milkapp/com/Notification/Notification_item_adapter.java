package b2infosoft.milkapp.com.Notification;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;

import java.util.List;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;


/**
 * Created by Choudhary on 10/07/2019.
 */

public class Notification_item_adapter extends
        RecyclerView.Adapter<Notification_item_adapter.MyViewHolder>
        implements SwipeAdapterInterface, SwipeItemMangerInterface {

    DatabaseHandler db;
    SwipeItemRecyclerMangerImpl mItemManger;
    NotificationClickListner notificationClickListner;
    BeanNotification_Item album;
    private Context mContext;
    private List<BeanNotification_Item> albumList;

    public Notification_item_adapter(Context context, List<BeanNotification_Item> albumList, NotificationClickListner listner) {
        this.mContext = context;
        this.albumList = albumList;
        this.notificationClickListner = listner;
        db = DatabaseHandler.getDbHelper(mContext);
        mItemManger = new SwipeItemRecyclerMangerImpl(this);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        album = albumList.get(position);
        holder.tvTitle.setText(Html.fromHtml(album.getTitle()));
        String desc = album.getDescription();
        holder.tvDesc.setText(Html.fromHtml(desc));




        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumList.remove(position);
                db.deleteNotification(album.getId());
                notifyDataSetChanged();
                holder.swipeLayout.close();
            }
        });


        // Handling different events when swiping
        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                mItemManger.closeAllExcept(layout);

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        holder.swipeLayout.getSurfaceView().

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //   Toast.makeText(mContext, " onClick : " + item.getName() + " \n" + item.getEmailId(), Toast.LENGTH_SHORT).show();
                    }
                });
        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }

    @Override
    public void openItem(int position) {

    }

    @Override
    public void closeItem(int position) {

    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {

    }

    @Override
    public void closeAllItems() {

    }

    @Override
    public List<Integer> getOpenItems() {
        return null;
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return null;
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {

    }

    @Override
    public boolean isOpen(int position) {
        return false;
    }

    @Override
    public Attributes.Mode getMode() {
        return null;
    }

    @Override
    public void setMode(Attributes.Mode mode) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTitle, tvDesc;
        SwipeLayout swipeLayout;
        ImageView image, imgDelete;
        View layoutTop;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDesc = view.findViewById(R.id.tvDesc);
            imgDelete = view.findViewById(R.id.imgDelete);
            layoutTop = view.findViewById(R.id.layoutTop);
            swipeLayout = view.findViewById(R.id.swipeLayout);
            tvTitle.setOnClickListener(this);
            tvDesc.setOnClickListener(this);
            layoutTop.setOnClickListener(this);
            image.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image:
                case R.id.tvTitle:
                case R.id.tvDesc:
                case R.id.layoutTop:
                    album = albumList.get(getLayoutPosition());
                    notificationClickListner.onAdapterClick(album);
                    break;
            }
        }
    }


}
