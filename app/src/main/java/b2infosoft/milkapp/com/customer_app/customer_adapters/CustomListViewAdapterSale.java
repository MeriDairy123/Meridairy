package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SharedPrefData;

public class CustomListViewAdapterSale extends BaseAdapter {
    SharedPrefData sharedPrefData;
    private Context context;
    public static ArrayList<CustomerListPojo> modelArrayList;


    public CustomListViewAdapterSale(Context context, ArrayList<CustomerListPojo> modelArrayList) {

        this.context = context;
        this.modelArrayList = modelArrayList;

    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        sharedPrefData = new SharedPrefData();
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.whatsapp_setting_row_list, null, true);

            holder.checkBoxOff = (CheckBox) convertView.findViewById(R.id.offIntent);
            holder.checkBoxSms = (CheckBox) convertView.findViewById(R.id.smsIntent);
            holder.checkBoxWhatsapp = (CheckBox) convertView.findViewById(R.id.WhatsappIntent);

            holder.smsId = (TextView) convertView.findViewById(R.id.smsId);
            holder.smsName = (TextView) convertView.findViewById(R.id.smsName);

            convertView.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) convertView.getTag();
        }
        holder.smsId.setText(modelArrayList.get(position).unic_customer);
        holder.smsName.setText(modelArrayList.get(position).name);


        if (sharedPrefData.retriveDataFromPrefrence(context, String.valueOf(modelArrayList.get(position).unic_customer)+"sale").equals("checkBoxOff")) {
            holder.checkBoxOff.setChecked(true);
        } else if (sharedPrefData.retriveDataFromPrefrence(context, String.valueOf(modelArrayList.get(position).unic_customer)+"sale").equals("checkBoxSms")) {
            holder.checkBoxSms.setChecked(true);
        } else if (sharedPrefData.retriveDataFromPrefrence(context, String.valueOf(modelArrayList.get(position).unic_customer)+"sale").equals("checkBoxWhatsapp")) {
            holder.checkBoxWhatsapp.setChecked(true);
        } else {
            holder.checkBoxOff.setChecked(true);
        }


        holder.checkBoxOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBoxOff.isChecked() == true) {
                    holder.checkBoxSms.setChecked(false);
                    holder.checkBoxWhatsapp.setChecked(false);
                    sharedPrefData.saveDataToPrefrence(context, String.valueOf(modelArrayList.get(position).unic_customer)+"sale", "checkBoxOff");
                }

            }
        });

        holder.checkBoxSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBoxSms.isChecked() == true) {
                    holder.checkBoxOff.setChecked(false);
                    holder.checkBoxWhatsapp.setChecked(false);
                    sharedPrefData.saveDataToPrefrence(context, String.valueOf(modelArrayList.get(position).unic_customer)+"sale", "checkBoxSms");
                }
            }
        });

        holder.checkBoxWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBoxWhatsapp.isChecked() == true) {
                    holder.checkBoxSms.setChecked(false);
                    holder.checkBoxOff.setChecked(false);
                    sharedPrefData.saveDataToPrefrence(context, String.valueOf(modelArrayList.get(position).unic_customer)+"sale", "checkBoxWhatsapp");
                }
            }
        });


        return convertView;
    }

    private class ViewHolder {

        protected CheckBox checkBoxOff;
        protected CheckBox checkBoxSms;
        protected CheckBox checkBoxWhatsapp;
        private TextView smsId;
        private TextView smsName;


    }

}