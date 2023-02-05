package b2infosoft.milkapp.com.Dairy.BulkMilkSale.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import b2infosoft.milkapp.com.Model.BeanUserRequestItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.acceptPlanRequest;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


/**
 * Created by Choudhary on 20/09/2019.
 */

public class Request_item_adapter extends RecyclerView.Adapter<Request_item_adapter.MyViewHolder> {

    BeanUserRequestItem album;
    String userId = "", userRelationId = "", status = "";
    SessionManager sessionManager;
    int pos = 0;
    private Context mContext;
    private List<BeanUserRequestItem> albumList;

    public Request_item_adapter(Context context, List<BeanUserRequestItem> albumList) {
        this.mContext = context;
        this.albumList = albumList;

        sessionManager = new SessionManager(mContext);
        userId = sessionManager.getValueSesion(KEY_UserID);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        album = albumList.get(position);
        holder.tvName.setText(Html.fromHtml(album.name));
        String type = album.type;

        holder.tvType.setText(Html.fromHtml(type));
        if (album.status.equalsIgnoreCase("0")) {
            holder.tvAccept.setVisibility(View.VISIBLE);
            holder.imgSuccess.setVisibility(View.GONE);

        } else if (album.status.equalsIgnoreCase("1")) {
            holder.tvAccept.setVisibility(View.GONE);
            holder.imgSuccess.setVisibility(View.VISIBLE);

        }

        holder.itemView.setTranslationY(-(5 + position * 5));
        holder.itemView.setAlpha(0.5f);
        holder.itemView.animate().alpha(1f).translationY(0).setDuration(700).start();

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void acceptRequest() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {

                @Override
                public void handleResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            albumList.get(pos).status = status;
                            notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", userId)
                    .addEncoded("user_relation_id", userRelationId)
                    .addEncoded("status", status).build();
            caller.addRequestBody(body);

            caller.execute(acceptPlanRequest);
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName, tvType, tvAccept, tvCancel;
        ImageView imgSuccess;

        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvType = view.findViewById(R.id.tvType);
            tvAccept = view.findViewById(R.id.tvAccept);
            tvCancel = view.findViewById(R.id.tvCancel);
            imgSuccess = view.findViewById(R.id.imgSuccess);
            tvAccept.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvAccept:
                    pos = getAdapterPosition();
                    userRelationId = albumList.get(pos).id;
                    status = "1";
                    acceptRequest();
                    break;
                default:
                    break;
            }
        }
    }
}
