package hr.math.android.kuharica.hr.math.android.kuharica.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import hr.math.android.kuharica.R;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Uputa;

/**
 * Created by Branimirko on 27/02/18.
 */

public class UputaAdapter extends BaseAdapter {
    private Context context;
    public static ArrayList<Uputa> UputaArrayList;


    public UputaAdapter(Context context, ArrayList<Uputa> UputaArrayList) {

        this.context = context;
        this.UputaArrayList = UputaArrayList;

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
        return UputaArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return UputaArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.redak_upute, null, true);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.uputa_checkbox);
            holder.tvUputa = (TextView) convertView.findViewById(R.id.uputa_text);
            holder.timer = (Button) convertView.findViewById(R.id.timer);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }


        holder.tvUputa.setText(UputaArrayList.get(position).getUputa());


        if(holder.tvUputa.getText().toString().contains("min")) {
            holder.timer.setVisibility(View.VISIBLE);
            holder.timer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Integer pos = (Integer) holder.checkBox.getTag();
                    if (!UputaArrayList.get(pos).getSelected()) {


                        String min = UputaArrayList.get(pos).getUputa().toString().replaceAll("[^0-9]", "");
                        new CountDownTimer(Long.parseLong(min) * 60 * 1000, 1000) {
                            private boolean tik = false;

                            public void onTick(long millisUntilFinished) {
                                if (tik) {
                                    holder.timer.setBackgroundColor(Color.BLUE);
                                    tik = false;
                                } else {
                                    holder.timer.setBackgroundColor(Color.GREEN);
                                    tik = true;
                                }
                                holder.timer.setText("" + millisUntilFinished / 1000);
                            }

                            public void onFinish() {
                                holder.timer.setBackgroundColor(Color.RED);
                                UputaArrayList.get(pos).setSelected(true);
                                holder.checkBox.setChecked(true);
                                holder.tvUputa.setPaintFlags(holder.tvUputa.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                holder.timer.setText("");
                                holder.timer.setVisibility(View.GONE);
                            }
                        }.start();
                    }
                }

            });
        }
        else
            holder.timer.setVisibility(View.GONE);

        holder.checkBox.setChecked(UputaArrayList.get(position).getSelected());

        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag( position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                TextView tv = (TextView) tempview.findViewById(R.id.uputa_text);
                Integer pos = (Integer)  holder.checkBox.getTag();

                if(UputaArrayList.get(pos).getSelected()){
                    UputaArrayList.get(pos).setSelected(false);
                    tv.setPaintFlags(tv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

                }else {
                    UputaArrayList.get(pos).setSelected(true);
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

            }
        });

        return convertView;
    }

    private class ViewHolder {

        protected CheckBox checkBox;
        private TextView tvUputa;
        private Button timer;

    }

}

