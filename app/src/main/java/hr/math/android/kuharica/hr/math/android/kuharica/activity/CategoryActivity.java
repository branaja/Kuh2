package hr.math.android.kuharica.hr.math.android.kuharica.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import hr.math.android.kuharica.hr.math.android.kuharica.adapter.DBRAdapter;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Kategorija;
import hr.math.android.kuharica.R;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Recept;
import hr.math.android.kuharica.hr.math.android.kuharica.adapter.ReceptAdapter;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ReceptAdapter adapter;
    private DBRAdapter db;
    private Kategorija mcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //populate recyclerview
        Intent categoryIntent = getIntent();
        db = new DBRAdapter(this);
        db.open();
        mcategory = db.getKategorija(categoryIntent.getExtras().getLong("categoryId"));
        List<Recept> categoryRecipes = db.getAllReceptiFromKategorija(mcategory.getId());
        db.close();

        adapter = new ReceptAdapter(categoryRecipes, this, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setTitle(mcategory.getImeKategorije());

    }

    @Override
    protected void onResume() {
        super.onResume();

        db.open();
        adapter.setData(db.getAllReceptiFromKategorija(mcategory.getId()));
        db.close();

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        List<Recept> odabrani = adapter.getSelectedItems();
        if(odabrani != null){
            long[] ids = new long[odabrani.size()];

            int i = 0;
            for(Recept r : odabrani){
                ids[i] = r.getId();
                ++i;
            }

            bundle.putLongArray("ids", ids);

        }

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            long[] ids = savedInstanceState.getLongArray("ids");

            if(ids != null){
                adapter.setSelectedData(ids);
                adapter.notifyDataSetChanged();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_category, menu);
        MenuItem splitViewItem = menu.findItem(R.id.split_view_settings);
        splitViewItem.setChecked(getFromSP(getString(R.string.splitViewEnabled)));
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.split_view_settings:
                if(item.isChecked()){
                    item.setChecked(false);
                    saveInSP(getString(R.string.splitViewEnabled), false);
                }
                else{
                    item.setChecked(true);
                    saveInSP(getString(R.string.splitViewEnabled), true);
                }
                break;
            case R.id.search:
                openSearchActivity();
                break;
            case R.id.sort_AZ:
                sortAlphabetically();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private boolean getFromSP(String key){
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(
                getString(R.string.mPrefs), MODE_PRIVATE);
        return mPrefs.getBoolean(key, false);
    }

    private void saveInSP(String key, boolean value){
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(
                getString(R.string.mPrefs), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void openSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("categoryId", mcategory.getId());
        startActivity(intent);
    }

    private void sortAlphabetically(){
        db.open();
        List<Recept> recepti = db.getAllReceptiFromKategorija(mcategory.getId());
        db.close();

        Collections.sort(recepti);

        adapter = new ReceptAdapter(recepti, this, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
    }

    public void noviRecept(View view) {
        Toast.makeText(this, "Dodaj novi recept", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AddRecipeActivity.class);
        startActivity(intent);
    }
}
