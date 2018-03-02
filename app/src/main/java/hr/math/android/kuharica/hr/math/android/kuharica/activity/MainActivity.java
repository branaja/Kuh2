package hr.math.android.kuharica.hr.math.android.kuharica.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Arrays;
import java.util.List;

import hr.math.android.kuharica.hr.math.android.kuharica.adapter.DBRAdapter;
import hr.math.android.kuharica.hr.math.android.kuharica.fragment.FAMFragment;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Kategorija;
import hr.math.android.kuharica.R;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Recept;
import hr.math.android.kuharica.hr.math.android.kuharica.adapter.KategorijaAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DBRAdapter db;
    private KategorijaAdapter kategorijaAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        initializeDatabase();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new FAMFragment()).commit();
        }

        navigationView.setCheckedItem(R.id.home);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment fragment = null;
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            fragment = new FAMFragment();
            ft.replace(R.id.fragment, fragment).commit();
            return true;
        }
    };

    private void initializeDatabase() {
         db = new DBRAdapter(this);
         db.open();
         if(db.getAllKategorije().size() == 0) {

             Kategorija kategorija = new Kategorija("Slatko", String.valueOf(R.drawable.cake));
             Recept r = new Recept();
             r.setImeRecepta("palačinke");
             r.setPhotoRecept(String.valueOf(R.drawable.pancakes));
             r.setNotes(null);
             r.setBrOsoba(4);
             r.setSastojci(Arrays.asList("300g brašna", "3 kašike šećera", "3 jaja"));
             r.setUpute(Arrays.asList("umiješati brašno sa šećerom", "zagrijati tavu 3 minute", "peći palačinke"));

             Recept r2 = new Recept();
             r2.setImeRecepta("princes krofne");
             r2.setPhotoRecept(null);
             r2.setNotes(null);
             r2.setSastojci(Arrays.asList("3 bjeljanjka", "3 kašike šećera", "konzerva šlaga"));
             r2.setUpute(Arrays.asList("its a kind of magic", "MAGIC", "MAGIC MAGIC"));

             kategorija.setRecepti(Arrays.asList(r));
             kategorija.setRecepti(Arrays.asList(r2));

             r.setId(db.insertRecept(r));
             r2.setId(db.insertRecept(r2));

             kategorija.setId(db.insertKategorija(kategorija));
             db.insertReceptUKategoriju(kategorija, r);
             db.insertReceptUKategoriju(kategorija, r2);

             Kategorija kat = new Kategorija("slano", null);
             Recept r1 = new Recept();
             r1.setImeRecepta("kifla");
             r1.setPhotoRecept(null);
             r1.setNotes(null);
             r1.setSastojci(Arrays.asList("300g brašna", "3 jaja"));
             r1.setUpute(Arrays.asList("peći u pećnici 15min"));
             kat.setRecepti(Arrays.asList(r1));
             r1.setId(db.insertRecept(r1));
             kat.setId(db.insertKategorija(kat));
             db.insertReceptUKategoriju(kat, r1);

             Kategorija kat1 = new Kategorija("Bez kategorije", String.valueOf(R.drawable.ic_close_black_24dp));
             db.insertKategorija(kat1);
         }

        kategorijaAdapter = new KategorijaAdapter(db.getAllKategorije(), this, recyclerView);
        recyclerView.setAdapter(kategorijaAdapter);
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.search:
                openSearchActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
  
    public void noviRecept(View view) {
        Intent intent = new Intent(this, AddRecipeActivity.class);
        startActivity(intent);
    }

    public void novaKategorija(View view) {
        Intent intent = new Intent(this, NovaKategorijaActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        kategorijaAdapter.setData(db.getAllKategorije());
        db.close();
        kategorijaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        List<Kategorija> odabrani = kategorijaAdapter.getSelectedItems();
        if(odabrani != null){
            long[] ids = new long[odabrani.size()];

            int i = 0;
            for(Kategorija r : odabrani){
                ids[i] = r.getId();
                ++i;
            }

            bundle.putLongArray("ids", ids);
            Log.e("OVOOOOOOOOOOOOOOOOO", String.valueOf(ids.length));
        }

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            long[] ids = savedInstanceState.getLongArray("ids");

            if(ids != null){
                kategorijaAdapter.setSelectedData(ids);
                kategorijaAdapter.notifyDataSetChanged();
            }
        }

    }


}
