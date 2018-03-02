package hr.math.android.kuharica.hr.math.android.kuharica.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import hr.math.android.kuharica.R;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Sastojak;

/**
 * Created by Branimirko on 27/02/18.
 */

public class SastojciAdapter extends BaseAdapter {
    private Context context;
    public static ArrayList<Sastojak> SastojakArrayList;


    public SastojciAdapter(Context context, ArrayList<Sastojak> SastojakArrayList) {

        this.context = context;
        this.SastojakArrayList = SastojakArrayList;

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
        return SastojakArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return SastojakArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.redak_sastojci, null, true);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.sastojak_checkbox);
            holder.tvSastojak = (TextView) convertView.findViewById(R.id.sastojak_text);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }


        holder.tvSastojak.setText(SastojakArrayList.get(position).getSastojak());

        holder.checkBox.setChecked(SastojakArrayList.get(position).getSelected());

        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag( position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                TextView tv = (TextView) tempview.findViewById(R.id.sastojak_text);
                Integer pos = (Integer)  holder.checkBox.getTag();

                if(SastojakArrayList.get(pos).getSelected()){
                    SastojakArrayList.get(pos).setSelected(false);
                    tv.setPaintFlags(tv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

                }else {
                    SastojakArrayList.get(pos).setSelected(true);
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

            }
        });

        return convertView;
    }

    private class ViewHolder {

        protected CheckBox checkBox;
        private TextView tvSastojak;

    }



}

