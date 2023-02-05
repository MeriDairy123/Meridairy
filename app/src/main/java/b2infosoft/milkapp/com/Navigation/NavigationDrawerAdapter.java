package b2infosoft.milkapp.com.Navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Dairy.DairyUserProfileActivity;
import b2infosoft.milkapp.com.Dairy.MainActivity;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.LoginWithPasswordActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;

import static b2infosoft.milkapp.com.Dairy.MainActivity.isDashboard;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadPlantMilkEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadPlantSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere2;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Mobile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.shareApp;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> implements milkEntryUploadListner {
    private static final int UNSELECTED = -1;
    public Boolean x = false;
    Integer itemPostion;
    Fragment fragment;
    DatabaseHandler databaseHandler;
    SessionManager sessionManager;
    String navTitle = "";
    String innerNavTitle = "";
    private List<NavDrawerItem> data;
    private RecyclerView recyclerView;
    private int selectedItem = UNSELECTED;
    private Context context;
    private View view1;
    private ImageView temp;


    NavigationDrawerAdapter(Context context, RecyclerView recyclerView, List<NavDrawerItem> data) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.data = data;
        navTitle = context.getString(R.string.Deshbord);
        innerNavTitle = "";
        sessionManager = new SessionManager(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        itemPostion = position;
        boolean isSelected = position == selectedItem;
        NavDrawerItem item = data.get(position);
        holder.title.setText(item.getTitle());
        if (item.getThumbnail() != 0)
            holder.navImage.setImageDrawable(context.getResources().getDrawable(item.getThumbnail()));
        holder.title.setSelected(isSelected);
        holder.expandableLayout.setExpanded(isSelected, true);

        if (x)
            holder.itemView.animate().alpha(1f).translationX(0).setDuration(500).start();
        if (data.get(holder.getLayoutPosition()).getSubMenu() != null)
            holder.arrow.setVisibility(View.VISIBLE);
        else
            holder.arrow.setVisibility(View.GONE);


    }

    private void nextFragment(Fragment fragment) {
        innerNavTitle = "";
        isDashboard = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goNextFragmentReplace(context, fragment);
            }
        }, 275);
    }

    private void nextInnerFragment(Fragment fragment) {
        isDashboard = false;
        // clearBackStack();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                goNextFragmentReplace(context, fragment);
            }
        }, 275);
    }

    private void clearBackStack() {
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        for (int i = count; count > 1; --count) {
            fm.popBackStackImmediate();
            // count--;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void Logout() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.Are_You_Sure_Want_To_Logout))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (isNetworkAvaliable(context)) {
                                    uploadEntry();
                                } else {
                                    showToast(context, context.getString(R.string.you_are_not_connected_to_internet));
                                }
                            }
                        })
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void uploadEntry() {
        databaseHandler = DatabaseHandler.getDbHelper(context);
        ArrayList<CustomerEntryListPojo> CustomerEntryList;
        CustomerEntryList = databaseHandler.getMilkBuyEntryRecordsOffline();
        boolean islogout = true;

        if (CustomerEntryList.size() != 0) {
            islogout = false;
            uploadEntryToServer(context, "NavigationBase", this);

        }
        if (databaseHandler.getSaleMilkEntryOfflineEntry().size() != 0) {
            uploadSaleMilkEntryToServer(context, "NavigationBase", this);
            islogout = false;
        }
        if (databaseHandler.getPlantSaleMilkEntryRecords().size() != 0) {
            uploadPlantSaleMilkEntryToServer(context, "NavigationBase");
            islogout = false;
        }

        if (databaseHandler.getPlantEntryRecords().size() != 0) {
            islogout = false;
            uploadPlantMilkEntryToServer(context, "NavigationBase");
        }


        if (islogout) {
            tempMobileNumber = sessionManager.getValueSesion(KEY_Mobile);
            sessionManager.logoutUser();
            UtilityMethod.goNextClass(context, LoginWithPasswordActivity.class);
        } else {
            showToast(context, context.getString(R.string.UploadingOnline));
        }

    }

    @Override
    public void onMilkEntryUploaded(String from) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        TextView title;
        ImageView navImage, arrow;
        RecyclerView recyclerViewSubMenu;
        NavigationInnerAdapter mAdapter;
        private ExpandableLayout expandableLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            navImage = itemView.findViewById(R.id.navImage);
            arrow = itemView.findViewById(R.id.arrow);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            recyclerViewSubMenu = itemView.findViewById(R.id.drawerInnerList);

            title.setOnClickListener(this);
            navImage.setOnClickListener(this);
            LinearLayoutManager llm = new LinearLayoutManager(context);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewSubMenu.setLayoutManager(llm);
            temp = arrow;
        }


        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.expandableLayout.collapse();
                arrow.animate().rotationX(0).setDuration(200).start();
            }
            int position = getAdapterPosition();
            String s = data.get(position).getTitle();
            fragment = data.get(position).getFragment();
            Boolean nav = true;
            System.out.println("=======click===main==lis====" + s);
            System.out.println("=======previous=========" + navTitle);
            navTitle = s;
            switch (position) {
                case 0:
                    nextFragment(fragment);
                    break;
                case 1:
                    Intent intent = new Intent(context, DairyUserProfileActivity.class);
                    context.startActivity(intent);
                    break;
                case 2:
                    nextFragment(fragment);
                    break;
                case 3:
                    nextFragment(fragment);
                    break;
                case 4:
                    nextFragment(fragment);
                    break;
                case 5:
                    nextFragment(fragment);
                    break;
                case 6:
                    FromWhere2 = "Deshboard";
                    nextFragment(fragment);
                    break;
                case 7:
                    nextFragment(fragment);
                    break;
                case 8:
                    nav = false;
                    navTitle = "";
                    if (data.get(position).getSubMenu() != null) {
                        mAdapter = new NavigationInnerAdapter(context, data.get(position).getSubMenu());
                        recyclerViewSubMenu.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                    temp.setRotationX(0);
                    arrow.animate().rotationX(180).setDuration(200).start();
                    temp = arrow;
                    expandableLayout.expand();
                    selectedItem = position;
                    break;
                case 9:
                    nav = false;
                    navTitle = "";

                    if (data.get(position).getSubMenu() != null) {
                        mAdapter = new NavigationInnerAdapter(context, data.get(position).getSubMenu());
                        recyclerViewSubMenu.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                    temp.setRotationX(0);
                    arrow.animate().rotationX(180).setDuration(200).start();
                    temp = arrow;
                    expandableLayout.expand();
                    selectedItem = position;


                    break;
                case 10:
                    nav = false;
                    navTitle = "";
                    if (data.get(position).getSubMenu() != null) {
                        mAdapter = new NavigationInnerAdapter(context, data.get(position).getSubMenu());
                        recyclerViewSubMenu.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                    temp.setRotationX(0);
                    arrow.animate().rotationX(180).setDuration(200).start();
                    temp = arrow;
                    expandableLayout.expand();
                    selectedItem = position;

                    break;
                case 11:
                    nextFragment(fragment);
                    break;
                    /*case 12:
                    nav = false;
                    navTitle = "";
                    if (data.get(position).getSubMenu() != null) {
                        mAdapter = new NavigationInnerAdapter(context, data.get(position).getSubMenu());
                        recyclerViewSubMenu.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                    temp.setRotationX(0);
                    arrow.animate().rotationX(180).setDuration(200).start();
                    temp = arrow;
                    expandableLayout.expand();
                    selectedItem = position;

                    break;*/
                case 12:
                    nextFragment(fragment);
                    break;
                case 13:
                    nextFragment(fragment);
                    break;
                case 14:
                    shareApp(context);
                    break;
                case 15:
                    Logout();
                    break;
            }
            if (nav) {
                ((MainActivity) context).closeDrawer();
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }
    }

    public class NavigationInnerAdapter extends RecyclerView.Adapter<NavigationInnerAdapter.MyViewHolder> {
        List<NavInnerItem> navInnerItems;

        private Context context;


        NavigationInnerAdapter(Context context, List<NavInnerItem> data) {
            this.context = context;
            this.navInnerItems = data;

        }

        public void delete(int position) {
            navInnerItems.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.nav_drawer_row_inner, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            NavInnerItem current = navInnerItems.get(position);
            holder.title.setText(current.getTitle());

            holder.navImage.setImageDrawable(context.getResources().getDrawable(current.getThumbnail()));
            holder.itemView.setAlpha(0.1f);
            holder.itemView.setTranslationY(20 + (position * 20));
            holder.itemView.animate().alpha(1f).translationY(0).setDuration(500).start();
        }

        @Override
        public int getItemCount() {
            return navInnerItems.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            ImageView navImage;

            public MyViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                navImage = itemView.findViewById(R.id.navImage);

                title.setOnClickListener(this);
                navImage.setOnClickListener(this);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                ((MainActivity) context).closeDrawer();
                String s = navInnerItems.get(getAdapterPosition()).getTitle();

                navTitle = s;
                innerNavTitle = s;
                final int position = getLayoutPosition();
                fragment = navInnerItems.get(position).getFragment();
                nextInnerFragment(fragment);

            }
        }
    }
}

