package hr.math.android.kuharica;

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

public class NovaKategorijaActivity extends AppCompatActivity {

    public static int counter = 0;
    Random rand = new Random();
    EditText imeKategorije;
    String path;
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

        //Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //pickIntent.setType("image/*");

        //Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(getIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
}
