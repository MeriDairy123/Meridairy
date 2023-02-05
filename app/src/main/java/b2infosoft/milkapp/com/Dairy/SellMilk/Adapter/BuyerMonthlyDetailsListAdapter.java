package b2infosoft.milkapp.com.Dairy.SellMilk.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import b2infosoft.milkapp.com.Interface.RefreshBuyerMonthList;
import b2infosoft.milkapp.com.Model.BuyerMonthlyListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;


/**
 * Created by u on 29-Dec-17.
 */

public class BuyerMonthlyDetailsListAdapter extends RecyclerSwipeAdapter<BuyerMonthlyDetailsListAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<BuyerMonthlyListPojo> morningList = new ArrayList<>();

    String name = "";
    String dairyID = "";

    SessionManager sessionManager;
    String dateStauts = "";
    RefreshBuyerMonthList refreshBuyerMonthList;
    Dialog dialog;

    public BuyerMonthlyDetailsListAdapter(Context mContext, ArrayList<BuyerMonthlyListPojo> MorningmilkHistoryListPojos, String milkRate, RefreshBuyerMonthList listener) {
        this.mContext = mContext;
        this.morningList = MorningmilkHistoryListPojos;
        sessionManager = new SessionManager(mContext);
        this.dairyID = milkRate;
        refreshBuyerMonthList = listener;
    }


    @Override
    public BuyerMonthlyDetailsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_buyer_transaction_list_row, parent, false);
        return new BuyerMonthlyDetailsListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BuyerMonthlyDetailsListAdapter.MyViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = new Date();
                //date = dateFormat.format(date);
                String currentDate = dateFormat.format(date);
                Date yesterdauDate = null;
                Date currentDateA = null;
                try {
                    currentDateA = dateFormat.parse(currentDate);
                    yesterdauDate = dateFormat.parse(morningList.get(position).selling_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (currentDateA.after(yesterdauDate)) {
                    holder.layoutCancel.setClickable(false);
                    holder.layoutEdit.setClickable(false);
                } else if (currentDateA.equals(yesterdauDate)) {
                    Log.d("equal>>>", "equal");
                    if (!morningList.get(position).morning_milk.equals("0") && morningList.get(position).evening_milk.equals("0")) {
                        if (morningList.get(position).morning_sell_sts.equals("yes")) {
                            dateStauts = "morning Sale";
                        }
                    } else if (!morningList.get(position).evening_milk.equals("0") && morningList.get(position).morning_milk.equals("0")) {
                        if (morningList.get(position).evening_sell_sts.equals("yes")) {
                            dateStauts = "evening Sale";
                        }
                    } else if (!morningList.get(position).morning_milk.equals("0") && !morningList.get(position).evening_milk.equals("0")) {
                        if (morningList.get(position).evening_sell_sts.equals("yes") && morningList.get(position).morning_sell_sts.equals("yes")) {
                            dateStauts = "both Sale";
                            holder.layoutEdit.setClickable(false);
                            holder.layoutCancel.setClickable(false);
                        }
                    }
                }
                //before() will return true if and only if date1 is before date2
                else if (currentDateA.before(yesterdauDate)) {
                    dateStauts = "";
                    System.out.println("Date1 is before Date2");
                    //Before
                }

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

        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if (morningList.get(position).morning_milk.equals("0") && morningList.get(position).evening_milk.equals("0")) {
            holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
        } else if (!morningList.get(position).morning_milk.equals("0") || !morningList.get(position).evening_milk.equals("0")) {
            if (!morningList.get(position).morning_sell_sts.equals("no") && !morningList.get(position).evening_sell_sts.equals("no")) {
                holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_list_gray));
            }
        }
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date2 = sdf.parse(morningList.get(position).selling_date);
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yyyy");
            String date3 = formatter2.format(date2);
            Log.d("Date3>>>", date3);
            holder.tvDate.setText(date3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (morningList.get(position).morning_milk.equals("0") && morningList.get(position).morning_milk.equals("")) {
            holder.tvMorning.setText("     ---");
        } else {
            holder.tvMorning.setText(morningList.get(position).morning_milk);
        }
        if (morningList.get(position).evening_milk.equals("0") && morningList.get(position).evening_milk.equals("")) {
            holder.tvEvening.setText("---");
        } else {
            holder.tvEvening.setText(morningList.get(position).evening_milk);
        }
        double morningPrice = 0.0, eveningPrice = 0.0, morningPriceTotal = 0.0, eveningPriceTotal = 0.0;

        double morningWeight = 0.0, eveningWeight = 0.0, totalWeight = 0.0;
        if (morningList.get(position).morning_sell_sts.equals("yes")) {
            if (!morningList.get(position).morning_milk.equals("0") && !morningList.get(position).morning_milk.equals("")) {
                morningWeight = Double.parseDouble(morningList.get(position).morning_milk);
                morningPrice = Double.parseDouble(morningList.get(position).morning_rate);
            }
        }
        if (morningList.get(position).evening_sell_sts.equals("yes")) {
            if (!morningList.get(position).evening_milk.equals("0") && !morningList.get(position).evening_milk.equals("")) {
                eveningWeight = Double.parseDouble(morningList.get(position).evening_milk);
                eveningPrice = Double.parseDouble(morningList.get(position).evening_rate);
            }
        }
        morningPriceTotal = morningWeight * morningPrice;
        eveningPriceTotal = eveningWeight * eveningPrice;
        totalWeight = morningPriceTotal + eveningPriceTotal;
        //  double totAmt = (milkRate) * totalWeight;
        holder.tvAmount.setText("" + String.format("%.2f", totalWeight));

        holder.layoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_update_buyer_entry);

                dialog.show();
                TextInputLayout layoutMilkRate = dialog.findViewById(R.id.layoutMilkRate);
                TextInputLayout layoutEvening = dialog.findViewById(R.id.layoutEvening);
                TextInputLayout layoutMorning = dialog.findViewById(R.id.layoutMorning);

                View priceView = dialog.findViewById(R.id.priceView);
                View eveningView = dialog.findViewById(R.id.eveningView);
                View morningView = dialog.findViewById(R.id.morningView);
                final EditText edtMorningMilk = dialog.findViewById(R.id.ediMorningMilk);
                final EditText edtEveningMilk = dialog.findViewById(R.id.ediEveningMilk);
                final EditText edtMilkRate = dialog.findViewById(R.id.edtMilkRate);
                edtEveningMilk.setText(morningList.get(position).evening_milk);
                edtMorningMilk.setText(morningList.get(position).morning_milk);
                edtMilkRate.setText("" + morningList.get(position).milk_rate);
                if (dateStauts.equals("morning Sale")) {
                    layoutMorning.setVisibility(View.GONE);
                    edtMorningMilk.setVisibility(View.GONE);
                    morningView.setVisibility(View.GONE);
                } else if (dateStauts.equals("evening Sale")) {
                    layoutEvening.setVisibility(View.GONE);
                    edtEveningMilk.setVisibility(View.GONE);
                    eveningView.setVisibility(View.GONE);
                }
                Button btnNext = dialog.findViewById(R.id.btnNext);
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                            @Override
                            public void handleResponse(String response) {

                                try {
                                    dialog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("success")) {
                                        dialog.dismiss();
                                        morningList.get(position).morning_milk = edtMorningMilk.getText().toString();
                                        morningList.get(position).evening_milk = edtEveningMilk.getText().toString();
                                        holder.tvEvening.setText(edtEveningMilk.getText().toString());
                                        holder.tvMorning.setText(edtMorningMilk.getText().toString());
                                        if (!holder.tvEvening.getText().toString().equals("0") || !holder.tvMorning.getText().toString().equals("0")) {
                                            holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                                        } else {
                                            holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));

                                        }
                                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Updating_Success));
                                        double morningWeight = 0.0, eveningWeight = 0.0, totalAmt = 0.0, pricePerLtr = 0.0, morningPrice = 0.0, eveningPrice = 0.0, totalWeight = 0.0;
                                        if (!morningList.get(position).morning_milk.equals("")) {
                                            morningWeight = morningWeight + Double.parseDouble(morningList.get(position).morning_milk);
                                        }
                                        if (!morningList.get(position).evening_milk.equals("")) {
                                            eveningWeight = eveningWeight + Double.parseDouble(morningList.get(position).evening_milk);
                                        }
                                        if (!morningList.get(position).morning_rate.equals("")) {
                                            morningPrice = Double.parseDouble(morningList.get(position).morning_rate);
                                        }
                                        if (!morningList.get(position).evening_rate.equals("")) {
                                            eveningPrice = Double.parseDouble(morningList.get(position).evening_rate);
                                        }
                                        double morningTotAmt = morningWeight * morningPrice;
                                        double eveningTotAmt = eveningWeight * eveningPrice;
                                        holder.tvAmount.setText("" + String.format("%.2f", (morningTotAmt + eveningTotAmt)));
                                        refreshBuyerMonthList.refreshBuyerData(morningList);
                                    } else {
                                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Updating_Failed));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        String morningMilk = "0", eveningMilk = "0";
                        if (!edtMorningMilk.getText().toString().equals("")) {
                            morningMilk = edtMorningMilk.getText().toString().trim();
                        }
                        if (!edtEveningMilk.getText().toString().equals("")) {
                            eveningMilk = edtEveningMilk.getText().toString().trim();
                        }
                        // dairy_id=2&customer_id=8&schedule_date=23-Nov-2017&schedule_shift=both&morning_milk=5&evening_milk=7
                        RequestBody body = new FormEncodingBuilder()
                                .addEncoded("dairy_id", dairyID)
                                .addEncoded("customer_id", morningList.get(position).customer_id)
                                .addEncoded("schedule_shift", "both")
                                .addEncoded("morning_milk", morningMilk)
                                .addEncoded("evening_milk", eveningMilk)
                                .addEncoded("schedule_date", morningList.get(position).selling_date)
                                .build();
                        serviceCaller.addRequestBody(body);
                        serviceCaller.execute(Constant.DailySaveMilkSchedule);
                    }
                });
            }
        });

        holder.layoutCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                    @Override
                    public void handleResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")) {
                                morningList.get(position).morning_milk = "0";
                                morningList.get(position).evening_milk = "0";
                                holder.tvEvening.setText("0");
                                holder.tvMorning.setText("0");
                                holder.tvAmount.setText("0");
                                if (!holder.tvEvening.getText().toString().equals("0") || !holder.tvMorning.getText().toString().equals("0")) {
                                    holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                                } else {
                                    holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));

                                }
                                UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Cancel_Success));
                            } else {
                                UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Cancel_Failed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //dairy_id=2&customer_id=8&schedule_date=23-Nov-2017&schedule_shift=both&morning_milk=5&evening_milk=7
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("dairy_id", dairyID)
                        .addEncoded("customer_id", morningList.get(position).customer_id)
                        .addEncoded("schedule_shift", "both")
                        .addEncoded("evening_milk", "0")
                        .addEncoded("morning_milk", "0")
                        .addEncoded("schedule_date", morningList.get(position).selling_date)
                        .build();
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(Constant.DailySaveMilkSchedule);
            }
        });
        mItemManger.bindView(holder.itemView, position);
    }

    public int getItemCount() {
        return morningList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDate, tvMorning, tvEvening, tvAmount;
        SwipeLayout swipeLayout;
        TextView tvEdit, tvCancel;
        LinearLayout parentLayout, layoutEdit, layoutCancel;

        public MyViewHolder(View view) {
            super(view);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            layoutEdit = itemView.findViewById(R.id.layoutEdit);
            layoutCancel = itemView.findViewById(R.id.layoutCancel);
            tvDate = view.findViewById(R.id.tvDate);
            tvMorning = view.findViewById(R.id.tvMorning);
            tvEvening = view.findViewById(R.id.tvEvening);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvCancel = view.findViewById(R.id.tvCancel);
            swipeLayout = itemView.findViewById(R.id.swipe);
            tvEdit = itemView.findViewById(R.id.tvEdit);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {


        }
    }
}
