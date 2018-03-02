package hr.math.android.kuharica.hr.math.android.kuharica.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import hr.math.android.kuharica.R;
import hr.math.android.kuharica.hr.math.android.kuharica.activity.ReceptActivity;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Recept;

/**
 * Created by mabel on 28-Feb-18.
 */

public class ReceptAdapter extends RecyclerView.Adapter<ReceptAdapter.ViewHolder>{
    private List<Recept> recepti;
    private List<Recept> selRecepti;
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
                    for(Recept r : selRecepti){
                        db.deleteRecept(r.getId());
                        recepti.remove(r);
                    }
                    db.close();
                    selRecepti.clear();
                    notifyDataSetChanged();
                    mode.finish();
                    return true;
                case 2:
                    for(Recept r : recepti){
                        if(!selRecepti.contains(r))
                            selRecepti.add(r);
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
            selRecepti.clear();
            notifyDataSetChanged();
        }
    };

    private void loadImageFromStorage(String path, ReceptAdapter.ViewHolder holder)
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView imeRecepta;
        public CardView cardView;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            image = (ImageView) v.findViewById(R.id.image);
            imeRecepta = (TextView) v.findViewById(R.id.name);
            cardView = (CardView) v.findViewById(R.id.kartica);
        }


        void update(final Recept r) {

            imeRecepta.setText(r.getImeRecepta());
            //ako je photoKategorije null onda se uzima defaultni
            int vrijednost = 1;
            String path = r.getPhotoRecept();
            boolean uDrawableFolderu = true;
            if(path == null) {
                vrijednost = R.drawable.default_recept;
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
                        .placeholder(R.drawable.default_recept).into(image);
            } else {
                loadImageFromStorage(r.getPhotoRecept(), this);
            }


            if (selRecepti != null) {
                if (selRecepti.contains(r)) {
                    cardView.setBackgroundColor(Color.LTGRAY);
                } else {
                    cardView.setBackgroundColor(Color.WHITE);
                }
            }

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(r);
                    return true;
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selRecepti.size() > 0)
                        selectItem(r);
                    else {
                        Intent receptIntent = new Intent(context, ReceptActivity.class);
                        receptIntent.putExtra("Id", r.getId());
                        context.startActivity(receptIntent);
                    }

                    if(selRecepti.size() == 0){
                        ((AppCompatActivity)context).startSupportActionMode(actionModeCallbacks).finish();
                    }
                }
            });
        }

        void selectItem(Recept recept) {
            if (multiSelect) {
                if (selRecepti.contains(recept)) {
                    selRecepti.remove(recept);
                    cardView.setBackgroundColor(Color.WHITE);
                } else {
                    selRecepti.add(recept);
                    cardView.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

    }


    public void add(int position, Recept recept) {
        recepti.add(position, recept);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        recepti.remove(position);
        notifyItemRemoved(position);
    }

    public void setData(List<Recept> recepti) {
        this.recepti = recepti;
    }

    public void setSelectedData(long[] ids){
        selRecepti.clear();

        this.multiSelect = true;

        ((AppCompatActivity)context).startSupportActionMode(actionModeCallbacks);

        for(int i = 0; i < ids.length; ++i){
            for(Recept r : recepti){
                if(r.getId() == ids[i]){
                    selRecepti.add(r);
                }
            }
        }
    }

    public ReceptAdapter(List<Recept> recepti, Context context, RecyclerView recyclerView) {
        this.recepti = recepti;
        this.context = context;
        this.recyclerView = recyclerView;

        selRecepti = new ArrayList<>();
        db = new DBRAdapter(context);
    }

    @Override
    public ReceptAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.single_row, parent, false);
        ReceptAdapter.ViewHolder vh = new ReceptAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ReceptAdapter.ViewHolder holder, int position) {
        holder.update(recepti.get(position));
    }

    @Override
    public int getItemCount() {
        return recepti.size();
    }


    public List<Recept> getSelectedItems(){
        return selRecepti;
    }

    public int getSelectedItemCount(){
        return getSelectedItems().size();
    }

}
