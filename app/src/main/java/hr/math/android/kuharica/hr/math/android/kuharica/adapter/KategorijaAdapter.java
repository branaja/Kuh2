package hr.math.android.kuharica.hr.math.android.kuharica.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import hr.math.android.kuharica.hr.math.android.kuharica.activity.CategoryActivity;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Kategorija;
import hr.math.android.kuharica.R;

/**
 * Created by mabel on 27-Feb-18.
 */

public class KategorijaAdapter extends RecyclerView.Adapter<KategorijaAdapter.ViewHolder> {
    private List<Kategorija> kategorije;
    private List<Kategorija> selKategorije;
    private boolean multiSelect = false;
    private Context context;
    private RecyclerView recyclerView;
    private DBRAdapter db;

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;

            menu.add(0,0,0, R.string.delete_option);
            menu.add(0,2,0, R.string.selectall_option);
            menu.add(0,3,0, R.string.cancel_option);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case 0:
                    db.open();
                    for(Kategorija k : selKategorije){
                        db.deleteKategorija(k);
                        kategorije.remove(k);
                    }
                    db.close();
                    selKategorije.clear();
                    notifyDataSetChanged();
                    mode.finish();
                    return true;
                case 2:
                    for(Kategorija k : kategorije){
                        if(!selKategorije.contains(k))
                            selKategorije.add(k);
                    }
                    notifyDataSetChanged();
                    return true;
                case 3:
                    mode.finish();
                    onDestroyActionMode(mode);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selKategorije.clear();
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView imeKategorije;
        public CardView cardView;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            image = (ImageView) v.findViewById(R.id.image);
            imeKategorije = (TextView) v.findViewById(R.id.name);
            cardView = (CardView) v.findViewById(R.id.kartica);
        }

        void update(final Kategorija k) {

            imeKategorije.setText(k.getImeKategorije());
            //ako je photoKategorije null onda se uzima defaultni
            int vrijednost = 0;
            String path = k.getPhotoKategorije();
            boolean uDrawableFolderu = true;
            if(path == null) {
                vrijednost = R.drawable.default_kategorija;
            } else {
                //vidi jel slika spremljena vani ili u drawable
                try {
                    vrijednost = Integer.parseInt(path);
                } catch (Exception e){
                    uDrawableFolderu = false;
                }
            }

            if(uDrawableFolderu) {
                Picasso.with(context).load(vrijednost)
                        .placeholder(R.drawable.default_kategorija).into(image);
            } else {
                loadImageFromStorage(k.getPhotoKategorije(), this);
            }

            if (selKategorije != null) {
                if (selKategorije.contains(k)) {
                    cardView.setBackgroundColor(Color.LTGRAY);
                } else {
                    cardView.setBackgroundColor(Color.WHITE);
                }
            }

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(k);

                    if(selKategorije.size() == 0){
                        ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks).finish();
                    }

                    return true;
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selKategorije.size() > 0)
                        selectItem(k);
                    else {
                        Intent receptIntent = new Intent(context, CategoryActivity.class);
                        receptIntent.putExtra("categoryId", k.getId());
                        context.startActivity(receptIntent);
                    }

                    if(selKategorije.size() == 0){
                        ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks).finish();
                    }

                    Toast.makeText(context, "Odabrano " + k.getId(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        void selectItem(Kategorija k) {
            if (multiSelect) {
                if (selKategorije.contains(k)) {
                    selKategorije.remove(k);
                    cardView.setBackgroundColor(Color.WHITE);
                } else {
                    selKategorije.add(k);
                    cardView.setBackgroundColor(Color.LTGRAY);
                }
            }
        }
    }

    public void add(int position, Kategorija kategorija) {
        kategorije.add(position, kategorija);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        kategorije.remove(position);
        notifyItemRemoved(position);
    }

    public KategorijaAdapter(List<Kategorija> kategorije, Context context, RecyclerView recyclerView) {
        this.kategorije = kategorije;
        this.context = context;
        this.recyclerView = recyclerView;

        selKategorije = new ArrayList<>();
        db = new DBRAdapter(context);
    }

    public void setData(List<Kategorija> kategorije) {
        this.kategorije = kategorije;
    }

    public void setSelectedData(long[] ids){
        selKategorije.clear();

        this.multiSelect = true;

        ((AppCompatActivity)context).startSupportActionMode(actionModeCallbacks);

        for(int i = 0; i < ids.length; ++i){
            for(Kategorija k : kategorije){
                if(k.getId() == ids[i]){
                    selKategorije.add(k);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.single_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.update(kategorije.get(position));
    }

    private void loadImageFromStorage(String path, ViewHolder holder)
    {

        try {
            File f=new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            holder.image.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return kategorije.size();
    }

    public List<Kategorija> getSelectedItems(){return selKategorije;}
}
