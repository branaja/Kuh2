package hr.math.android.kuharica.hr.math.android.kuharica.activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.clans.fab.Label;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import hr.math.android.kuharica.hr.math.android.kuharica.adapter.DBRAdapter;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Kategorija;
import hr.math.android.kuharica.R;

public class NovaKategorijaActivity extends AppCompatActivity {

    public static int counter = 0;
    Random rand = new Random();
    EditText imeKategorije;
    String path = null;
    boolean imePostoji = false;
    Label label;
    Label statusSlike;
    Button novaKategorija;
    DBRAdapter db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_kategorija);
        db = new DBRAdapter(this);
        label = (Label)findViewById(R.id.imePostojiLabel);
        statusSlike = (Label)findViewById(R.id.statusSlike);
        novaKategorija = (Button)findViewById(R.id.novaKategorija);

        imeKategorije = (EditText)findViewById(R.id.imeKategorije);
        imeKategorije.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ime = imeKategorije.getText().toString();

                db.open();
                List<Kategorija> lista = db.getAllKategorije();
                db.close();

                for(Kategorija k : lista) {
                    if(k.getImeKategorije().toLowerCase().equals(ime.toLowerCase())) {
                        imePostoji = true;
                        break;
                    }
                    imePostoji = false;
                }

                if(imePostoji) {
                    label.setVisibility(View.VISIBLE);
                    novaKategorija.setEnabled(false);
                } else {
                    label.setVisibility(View.INVISIBLE);
                    novaKategorija.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String ime = imeKategorije.getText().toString();
                if(ime.equals("")) {
                    novaKategorija.setEnabled(false);
                }
            }
        });
    }

    public void odustani(View view) {
        finish();
    }

    public void napraviNovuKategoriju(View view) {
        db.open();
        Kategorija k = new Kategorija(imeKategorije.getText().toString(), path);
        db.insertKategorija(k);
        db.close();
        finish();
    }

    public static final int PICK_IMAGE = 1;
    public void birajSliku(View view) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        startActivityForResult(getIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w("activityesult", "kad ja udjem ovdje");
        if(resultCode == Activity.RESULT_OK) {
            if(data == null) {
                statusSlike.setText("Slika nije uspješno odabrana");
                statusSlike.setVisibility(View.VISIBLE);
                return;
            }

            statusSlike.setText("Slika uspješno odabrana");
            statusSlike.setVisibility(View.VISIBLE);

            try {
                counter = rand.nextInt(1000);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                path = saveToInternalStorage(bitmap, String.valueOf(counter));

                Log.w("path: ", path);
            } catch (Exception e) {
                path = null;
                statusSlike.setText("Slika nije uspješno odabrana");
                statusSlike.setVisibility(View.INVISIBLE);
            }


        } else {
            statusSlike.setText("Slika nije odabrana");
            statusSlike.setVisibility(View.VISIBLE);
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,name + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {


        if(imeKategorije.getText().toString() != null) {
            bundle.putString("ime", imeKategorije.getText().toString());
        }
        Log.w("pathvanifa", "" + path);
        if(path != null) {
            Log.w("pathsaveinstancestate", "" + path);
            bundle.putString("path", path);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);

        String ime = bundle.getString("ime");
        String staza = bundle.getString("path");
        if(ime != null) {
            imeKategorije.setText(ime);
            Log.w("imeurestore", ime);
        }

        if(staza != null) {
            statusSlike.setText("Slika uspješno odabrana");
            statusSlike.setVisibility(View.VISIBLE);
            Log.w("pathurestoreinstance", "" + staza);
        }
    }
}
