package hr.math.android.kuharica.hr.math.android.kuharica.activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hr.math.android.kuharica.hr.math.android.kuharica.adapter.DBRAdapter;
import hr.math.android.kuharica.R;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Kategorija;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Recept;
import hr.math.android.kuharica.Utility;

public class AddRecipeActivity extends AppCompatActivity {

    ArrayList<String> ingredientList;
    ArrayAdapter<String> adapterIngredients;
    ArrayList<String> stepList;
    ArrayAdapter<String> adapterSteps;
    ListView ingredientView;
    ListView stepView;
    String recipeName;
    String tempIngredient;
    String tempStep;
    String recipeNotesText;
    String userChoosenTask;
    int peopleText;
    ImageView ivImage;
    Boolean flag;
    Uri imageURI;
    String currentImagePath;

    Kategorija izabrana_kategorija;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        Intent intent = getIntent();
        Long Id = intent.getLongExtra("ID", -1);

        if(Id != -1){
            flag = true;
            DBRAdapter db = new DBRAdapter(this);
            db.open();
            Recept recept = db.getReceptZaId(Id);
            db.close();

            EditText editTextName = (EditText) findViewById(R.id.EditText_recipeName);
            editTextName.setText(recept.getImeRecepta());
            EditText editTextNotes = (EditText) findViewById(R.id.recipeNotes);
            editTextName.setText(recept.getNotes());
            EditText editTextPeople = (EditText) findViewById(R.id.EditText_people);
            editTextPeople.setText(Integer.toString(recept.getBrOsoba()));

            ingredientList = new ArrayList<String>();
            ingredientList.addAll(recept.getSastojci());

            stepList = new ArrayList<String>();
            stepList.addAll(recept.getUpute());

            adapterIngredients = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingredientList);
            ingredientView = (ListView)findViewById(R.id.ingredientsListView);
            ingredientView.setAdapter(adapterIngredients);
            adapterSteps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepList);
            stepView = (ListView)findViewById(R.id.stepsListView);
            stepView.setAdapter(adapterSteps);

            //jos sliku treba
        } else {
            flag = false;
            ingredientList = new ArrayList<String>();
            adapterIngredients = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingredientList);
            ingredientView = (ListView)findViewById(R.id.ingredientsListView);
            ingredientView.setAdapter(adapterIngredients);

            stepList = new ArrayList<String>();
            adapterSteps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepList);
            stepView = (ListView)findViewById(R.id.stepsListView);
            stepView.setAdapter(adapterSteps);
            ivImage = (ImageView)findViewById(R.id.recipeImage);
        }

        DBRAdapter db = new DBRAdapter(this);
        db.open();
        final List<Kategorija> temp  = db.getAllKategorije();
        db.close();
        ArrayList<String> lista_kategorija = new ArrayList<String>();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        for(Kategorija k:temp)
            lista_kategorija.add(k.getImeKategorije());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lista_kategorija);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                izabrana_kategorija=temp.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                izabrana_kategorija=temp.get(0);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        EditText editTextName = (EditText)findViewById(R.id.EditText_recipeName);
        recipeName = editTextName.getText().toString();
        savedInstanceState.putString("recipeName", recipeName);

        EditText editTextIngredient = (EditText)findViewById(R.id.EditText_ingredient);
        tempIngredient = editTextIngredient.getText().toString();
        savedInstanceState.putString("tempIngredient", tempIngredient);

        EditText editTextDetails = (EditText)findViewById(R.id.EditText_step);
        tempStep = editTextDetails.getText().toString();
        savedInstanceState.putString("tempStep", tempStep);

        EditText editTextNotes = (EditText)findViewById(R.id.recipeNotes);
        recipeNotesText = editTextNotes.getText().toString();
        savedInstanceState.putString("recipeNotesText", recipeNotesText);

        EditText editTextPeople = (EditText)findViewById(R.id.EditText_people);
        peopleText = Integer.parseInt(editTextPeople.getText().toString());
        savedInstanceState.putInt("peopleText", peopleText);

        savedInstanceState.putStringArrayList("ingredientList", ingredientList);
        savedInstanceState.putStringArrayList("stepList", stepList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        recipeName = savedInstanceState.getString("recipeName");
        tempIngredient = savedInstanceState.getString("tempIngredient");
        ingredientList = savedInstanceState.getStringArrayList("ingredientList");
        tempStep = savedInstanceState.getString("tempStep");
        recipeNotesText = savedInstanceState.getString("recipeNotesText");
        peopleText = savedInstanceState.getInt("peopleText");

        EditText editTextName = (EditText)findViewById(R.id.EditText_recipeName);
        if(!recipeName.isEmpty()) editTextName.setText(recipeName);
        else {
            editTextName.setText("");
            editTextName.setHint(R.string.addRecipe_recipeNameHint);
        }
        EditText editTextIngredient = (EditText)findViewById(R.id.EditText_ingredient);
        if(!tempIngredient.isEmpty()) editTextIngredient.setText(tempIngredient);
        else {
            editTextIngredient.setText("");
            editTextIngredient.setHint(R.string.addRecipe_ingredientHint);
        }
        EditText editTextDetails = (EditText)findViewById(R.id.EditText_step);
        if(!tempStep.isEmpty()) editTextDetails.setText(tempStep);
        else {
            editTextDetails.setText("");
            editTextDetails.setHint(R.string.addRecipe_recipeDetailsHint);
        }
        EditText editTextNotes = (EditText)findViewById(R.id.recipeNotes);
        if(!recipeNotesText.isEmpty()) editTextNotes.setText(recipeNotesText);
        else {
            editTextNotes.setText("");
            editTextNotes.setHint(R.string.addRecipe_recipeNotesHint);
        }
        EditText editTextPeople = (EditText)findViewById(R.id.EditText_people);
        if(peopleText < 1) editTextPeople.setText(peopleText);
        else {
            editTextPeople.setText("1");
            editTextPeople.setHint(R.string.addRecipe_peopleHint);
        }

        adapterIngredients = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingredientList);
        ingredientView = (ListView)findViewById(R.id.ingredientsListView);
        ingredientView.setAdapter(adapterIngredients);
        setListViewHeightBasedOnChildren(ingredientView);

        adapterSteps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stepList);
        stepView = (ListView)findViewById(R.id.stepsListView);
        stepView.setAdapter(adapterSteps);
        setListViewHeightBasedOnChildren(stepView);
        ivImage = (ImageView)findViewById(R.id.recipeImage);
    }

    public void addBtnClick(View view){
        EditText editText = (EditText)findViewById(R.id.EditText_ingredient);
        String ingredient = editText.getText().toString();

        if(ingredient.isEmpty()) return;

        ingredientList.add(ingredient);
        editText.setText("");
        editText.setHint(R.string.addRecipe_ingredientHint);
        adapterIngredients.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(ingredientView);
    }

    public void stepBtnClick(View view){
        EditText editText = (EditText)findViewById(R.id.EditText_step);
        String step = editText.getText().toString();

        if(step.isEmpty()) return;

        stepList.add(step);
        editText.setText("");
        editText.setHint(R.string.addRecipe_recipeStepHint);
        adapterSteps.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(stepView);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    public void saveBtnClick(View view){
        if(!flag){
            EditText editTextName = (EditText)findViewById(R.id.EditText_recipeName);
            recipeName = editTextName.getText().toString();

            EditText editTextNotes = (EditText)findViewById(R.id.recipeNotes);
            recipeNotesText = editTextNotes.getText().toString();

            EditText editTextPeople = (EditText)findViewById(R.id.EditText_people);
            peopleText = Integer.parseInt(editTextPeople.getText().toString());

            if(recipeName.isEmpty()) {
                Toast.makeText(this, "Obavezno je popuniti ime recepta", Toast.LENGTH_LONG).show();
            }

            if(ingredientList.isEmpty()) {
                Toast.makeText(this, "Obavezno je staviti barem 1 sastojak", Toast.LENGTH_LONG).show();
            }
            if(stepList.isEmpty()) {
                Toast.makeText(this, "Obavezno je imati bar jednu uputu", Toast.LENGTH_LONG).show();
            }
            if( peopleText < 1 || peopleText > 10){
                Toast.makeText(this, "Form is not filled properply. Check if the number of people is set between 1 and 10.", Toast.LENGTH_LONG).show();
                return;
            }

            Recept recept = new Recept();
            recept.setImeRecepta(recipeName);
            recept.setNotes(recipeNotesText);
            recept.setUpute(stepList);
            recept.setSastojci(ingredientList);
            recept.setBrOsoba(peopleText);
            if(currentImagePath!=null)
                if(!currentImagePath.isEmpty()) recept.setPhotoRecept(currentImagePath);
                else recept.setPhotoRecept(null);

            DBRAdapter db = new DBRAdapter(this);
            db.open();
            recept.setId(db.insertRecept(recept));

            Recept receptIzBaze = db.getReceptZaId(recept.getId());
            Log.w("MojaKlasa:", receptIzBaze.getImeRecepta());
            Log.w("MojaKlasa:", receptIzBaze.getNotes());

            db.insertReceptUKategoriju(izabrana_kategorija, recept);

            db.close();
        } else {
            EditText editTextName = (EditText)findViewById(R.id.EditText_recipeName);
            recipeName = editTextName.getText().toString();

            EditText editTextNotes = (EditText)findViewById(R.id.recipeNotes);
            recipeNotesText = editTextNotes.getText().toString();

            if(recipeName.isEmpty() || stepList.isEmpty() || recipeNotesText.isEmpty() || ingredientList.isEmpty() || peopleText < 1 || peopleText > 10){
                Toast.makeText(this, "Form is not filled properply. Check if the number of people is set between 1 and 10.", Toast.LENGTH_LONG).show();
                return;
            }

            Recept recept = new Recept();
            recept.setImeRecepta(recipeName);
            recept.setNotes(recipeNotesText);
            recept.setUpute(stepList);
            recept.setSastojci(ingredientList);
            recept.setBrOsoba(peopleText);
            if(!currentImagePath.isEmpty()) recept.setPhotoRecept(currentImagePath);
            else recept.setPhotoRecept(null);

            DBRAdapter db = new DBRAdapter(this);
            db.open();
            db.updateRecept(recept);
            db.close();
        }
        onBackPressed();
    }

    public void selectImage(View view) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipeActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(AddRecipeActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1)
                onSelectFromGalleryResult(data);
            else if (requestCode == 0)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                imageURI = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
                Random r = new Random();
                int name = r.nextInt(3000 - 1000) + 3000;
                currentImagePath = saveToInternalStorage(bitmap, Integer.toString(name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            imageURI = data.getData();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
            Random r = new Random();
            int name = r.nextInt(3000 - 1000) + 3000;
            currentImagePath = saveToInternalStorage(bitmap, Integer.toString(name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivImage.setImageBitmap(thumbnail);
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
