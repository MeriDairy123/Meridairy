package b2infosoft.milkapp.com.PaymentRegister;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.Invoice.ViewUserMilkEntryFragment;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PayOrReceiveAmountFragment;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentTransactionHistoryFragment;
import b2infosoft.milkapp.com.Interface.OnRefreshPaymentRegister;
import b2infosoft.milkapp.com.Model.BeanTransactionUserItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentAddBackStack;


/**
 * Created by Choudhary on 20-July-2020.
 */

public class PaymentBhugtanListAdapter extends RecyclerSwipeAdapter<PaymentBhugtanListAdapter.MyViewHolder> {


    private static final int MENU_PAY_ITEM = Menu.FIRST;
    private static final int MENU_RECEIVE_ITEM = Menu.FIRST + 1;
    private static final int MENU_VIEWENTRY_ITEM = Menu.FIRST + 2;
    private static final int MENU_TRANSACTION_ITEM = Menu.FIRST + 3;
    public Context mContext;
    ArrayList<BeanTransactionUserItem> mList;
    ArrayList<BeanTransactionUserItem> mListFilter = new ArrayList<>();
    SessionManager sessionManager;
    OnRefreshPaymentRegister onLoadMoreListener;
    Bundle bundle;
    Fragment fragment;
    int MENU_PAY = 0;
    int MENU_RECEIVE = 1;
    int MENU_VIEWENTRY = 2;
    int MENU_TRANSACTION = 3;

    int position = 0;
    String startDate = "", endDate = "";
    SwipeLayout mainSwipeLayout;

    public PaymentBhugtanListAdapter(Context mContext, ArrayList<BeanTransactionUserItem>
            mList, String startDate, String endDate, OnRefreshPaymentRegister onLoadMoreListener) {
        this.mContext = mContext;
        this.onLoadMoreListener = onLoadMoreListener;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mList = mList;
        sessionManager = new SessionManager(mContext);
        mListFilter = new ArrayList<>();
        mListFilter.addAll(mList);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_payment_register_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvID.setText(mList.get(position).getUnic_customer());
        holder.tvName.setText(mList.get(position).getName());
        double balanceAmt = mList.get(position).getBalance();
        if (balanceAmt > 0) {
            holder.tvTotalPrice.setText(String.format("%.2f", balanceAmt));
            holder.tvTotalPrice.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
        } else {
            balanceAmt = Math.abs(balanceAmt);
            holder.tvTotalPrice.setText(String.format("%.2f", balanceAmt));
            holder.tvTotalPrice.setTextColor(mContext.getResources().getColor(R.color.colorRed));

        }
        System.out.println("==OnCreate===isChecked>>>>" + mList.get(position).isChecked());
        if (mList.get(position).isChecked()) {
            holder.chkSelect.setChecked(true);
        } else {
            holder.chkSelect.setChecked(false);
        }
        holder.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = mList.get(position).isChecked();
                System.out.println("Click   isChecked>>>>" + isChecked);
                if (isChecked) {
                    isChecked = false;
                    holder.chkSelect.setChecked(isChecked);

                } else {
                    isChecked = true;
                    holder.chkSelect.setChecked(isChecked);

                }
                mList.get(position).setChecked(isChecked);
                onLoadMoreListener.onRefreshList(mList);

            }
        });

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

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottomWrapper));


        mItemManger.bindView(holder.itemView, position);


    }

    public void filterSearch(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (BeanTransactionUserItem wp : mListFilter) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                } else if (wp.getUnic_customer().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


    public int getItemCount() {
        return mList.size();
    }

    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }

    private void showPopup(View view, int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_PAY, MENU_PAY_ITEM, 0, mContext.getString(R.string.Pay));
        popup.getMenu().add(MENU_RECEIVE, MENU_RECEIVE_ITEM, 1, mContext.getString(R.string.recieve));
        popup.getMenu().add(MENU_VIEWENTRY, MENU_VIEWENTRY_ITEM, 2, mContext.getString(R.string.viewEntry));
        popup.getMenu().add(MENU_TRANSACTION, MENU_TRANSACTION_ITEM, 3, mContext.getString(R.string.Transaction));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fragment = null;
                Bundle bundle = new Bundle();
                bundle.putString("FromWhere", "PaymentRegister");
                bundle.putString("CustomerId", mList.get(position).getId());
                bundle.putString("CustomerName", mList.get(position).getName());
                bundle.putString("CustomerFatherName", "");
                bundle.putString("unic_customer", mList.get(position).getUnic_customer());
                bundle.putString("user_group_id", mList.get(position).getUserGroupId());
                bundle.putString("startDate", startDate);
                bundle.putString("endDate", endDate);
                switch (item.getItemId()) {

                    case MENU_PAY_ITEM:
                        fragment = new b2infosoft.milkapp.com.Dairy.PaymentRegister.PayOrReceiveAmountFragment();
                        bundle.putString("type", "add");
                        bundle.putString("title", mContext.getString(R.string.Pay));
                        bundle.putString("url", Constant.addPayVoucherAPI);
                        break;
                    case MENU_RECEIVE_ITEM:
                        fragment = new b2infosoft.milkapp.com.Dairy.PaymentRegister.PayOrReceiveAmountFragment();
                        bundle.putString("type", "add");
                        bundle.putString("title", mContext.getString(R.string.recieve));
                        bundle.putString("url", Constant.addRecieptVoucherAPI);
                        break;
                    case MENU_VIEWENTRY_ITEM:
                        fragment = new ViewUserMilkEntryFragment();
                        bundle.putString("FromWhere", "Transaction");
                        break;
                    case MENU_TRANSACTION_ITEM:
                        fragment = new b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentTransactionHistoryFragment();
                        break;
                    default:
                        break;

                }
                if (fragment != null) {
                    if (mainSwipeLayout != null && mainSwipeLayout.getOpenStatus() == SwipeLayout.Status.Open) {
                        mainSwipeLayout.close();
                    }
                    fragment.setArguments(bundle);
                    goNextFragmentAddBackStack(mContext, fragment);
                }
                return false;

            }

        });

        popup.show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvID, tvName, tvTotalPrice, tvViewEntry, tvPay, tvReceive;
        View layoutUpper;
        ImageView imgMoreDetail;
        SwipeLayout swipeLayout;
        CheckBox chkSelect;

        public MyViewHolder(View view) {
            super(view);
            layoutUpper = view.findViewById(R.id.layoutUpper);
            swipeLayout = view.findViewById(R.id.swipeLayout);
            tvID = view.findViewById(R.id.tvID);
            tvName = view.findViewById(R.id.tvName);
            tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
            chkSelect = view.findViewById(R.id.chkSelect);
            imgMoreDetail = view.findViewById(R.id.imgMoreDetail);
            tvViewEntry = view.findViewById(R.id.tvViewEntry);
            tvPay = view.findViewById(R.id.tvPay);
            tvReceive = view.findViewById(R.id.tvReceive);


            tvID.setOnClickListener(this);
            tvName.setOnClickListener(this);
            tvTotalPrice.setOnClickListener(this);
            imgMoreDetail.setOnClickListener(this);

            tvPay.setOnClickListener(this);
            tvReceive.setOnClickListener(this);
            tvViewEntry.setOnClickListener(this);

        }


        public void onClick(View view) {
            position = getLayoutPosition();
            fragment = null;
            mainSwipeLayout = swipeLayout;
            Bundle bundle = new Bundle();
            bundle.putString("FromWhere", "PaymentRegister");
            bundle.putString("CustomerId", mList.get(position).getId());
            bundle.putString("CustomerName", mList.get(position).getName());
            bundle.putString("CustomerFatherName", "");
            bundle.putString("unic_customer", mList.get(position).getUnic_customer());
            bundle.putString("user_group_id", mList.get(position).getUserGroupId());
            bundle.putString("startDate", startDate);
            bundle.putString("endDate", endDate);
            switch (view.getId()) {
                case R.id.tvTotalPrice:
                case R.id.imgMoreDetail:
                    if (swipeLayout.getOpenStatus() == SwipeLayout.Status.Open) {
                        swipeLayout.close();
                    } else {
                        swipeLayout.open();
                    }

                    break;
                case R.id.tvID:
                case R.id.tvName:
                    if (swipeLayout.getOpenStatus() == SwipeLayout.Status.Open) {
                        swipeLayout.close();
                    }
                    fragment = new PaymentTransactionHistoryFragment();
                    break;
                case R.id.tvViewEntry:
                    swipeLayout.close();
                    fragment = new ViewUserMilkEntryFragment();
                    bundle.putString("FromWhere", "Transaction");

                    break;
                case R.id.tvPay:
                    swipeLayout.close();
                    fragment = new b2infosoft.milkapp.com.Dairy.PaymentRegister.PayOrReceiveAmountFragment();
                    bundle.putString("type", "add");
                    bundle.putString("title", mContext.getString(R.string.Pay));
                    bundle.putString("url", Constant.addPayVoucherAPI);

                    break;
                case R.id.tvReceive:
                    swipeLayout.close();
                    fragment = new PayOrReceiveAmountFragment();
                    bundle.putString("type", "add");
                    bundle.putString("title", mContext.getString(R.string.recieve));
                    bundle.putString("url", Constant.addRecieptVoucherAPI);
                    break;
            }
            if (fragment != null) {
                fragment.setArguments(bundle);
                goNextFragmentAddBackStack(mContext, fragment);
            }


        }
    }

}
