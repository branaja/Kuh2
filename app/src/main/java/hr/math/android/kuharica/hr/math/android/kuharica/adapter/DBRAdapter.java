package hr.math.android.kuharica.hr.math.android.kuharica.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hr.math.android.kuharica.hr.math.android.kuharica.core.Kategorija;
import hr.math.android.kuharica.hr.math.android.kuharica.core.Recept;

/**
 * Created by mabel on 27-Feb-18.
 */

public class DBRAdapter {
    static final String KEY_ROWID1 = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_EMAIL = "email";
    static final String TAG1 = "DBRAdapter";

    static final String ID_RECEPTA = "id_recepta";
    static final String IME_RECEPTA = "ime_recepta";
    static final String PHOTO_RECEPT = "photo_path";
    static final String NOTES = "notes";
    static final String BR_OSOBA = "br_osoba";

    static final String ID_SASTOJKA = "id_sastojka";
    static final String TEKST_SASTOJKA = "tekst_sastojka";

    static final String ID_UPUTE = "id_upute";
    static final String TEKST_UPUTE = "tekst_upute";

    static final String ID_KATEGORIJE = "id_kategorije";
    static final String IME_KATEGORIJE = "ime_kategorije";
    static final String PHOTO_KATEGORIJA = "photo_path";

    static final String DATABASE_NAME = "KuharicaDB";
    static final String DATABASE_RECEPT = "recept";
    static final String DATABASE_KATEGORIJA = "kategorija";
    //baza popisuje sve sastojke za zadani recept
    static final String DATABASE_SASTOJCI = "sastojci";
    //baza popisuje sve upute za zadani recept
    static final String DATABASE_UPUTE = "upute";
    //popisuje za zadanu kategoriju sve recepte koji su u njoj
    static final String DATABASE_RECEPT_U_KATEGORIJI = "recept_kategorija";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE_RECEPT =
            "create table " + DATABASE_RECEPT + " (" + ID_RECEPTA + " integer primary key autoincrement, "
                    + IME_RECEPTA + " text not null, "
                    + PHOTO_RECEPT + " text, " + NOTES + " text, " +
                    BR_OSOBA + " integer);";

    static final String DATABASE_CREATE_KATEGORIJA =
            "create table " + DATABASE_KATEGORIJA + " (" + ID_KATEGORIJE + " integer primary key autoincrement, "
                    + IME_KATEGORIJE + " text not null, " + PHOTO_KATEGORIJA + " text);";

    static final String DATABASE_CREATE_SASTOJCI =
            "create table " + DATABASE_SASTOJCI + " (" + ID_RECEPTA + " integer , "
                    + TEKST_SASTOJKA + " text not null);";

    static final String DATABASE_CREATE_UPUTE =
            "create table " + DATABASE_UPUTE + " (" + ID_RECEPTA + " integer, "
                    + TEKST_UPUTE + " text not null);";

    static final String DATABASE_CREATE_RECEPT_KATEGORIJA =
            "create table " + DATABASE_RECEPT_U_KATEGORIJI + " ("
                    + ID_KATEGORIJE + " integer, "
                    + ID_RECEPTA + " integer);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBRAdapter (Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(ctx);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(DATABASE_CREATE_RECEPT);
                sqLiteDatabase.execSQL(DATABASE_CREATE_SASTOJCI);
                sqLiteDatabase.execSQL(DATABASE_CREATE_UPUTE);
                sqLiteDatabase.execSQL(DATABASE_CREATE_KATEGORIJA);
                sqLiteDatabase.execSQL(DATABASE_CREATE_RECEPT_KATEGORIJA);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.w("DBRAdapter", "upgrading db from" + i + "to" + i1);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_RECEPT);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_SASTOJCI);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_UPUTE);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_KATEGORIJA);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_RECEPT_U_KATEGORIJI);

            onCreate(sqLiteDatabase);
        }
    }

    public DBRAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long insertKategorija(Kategorija kategorija) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(IME_KATEGORIJE, kategorija.getImeKategorije());
        initialValues.put(PHOTO_KATEGORIJA, kategorija.getPhotoKategorije());
        return db.insert(DATABASE_KATEGORIJA, null, initialValues);
    }

    public boolean deleteKategorija(Kategorija kategorija) {
        long id = kategorija.getId();
        return db.delete(DATABASE_KATEGORIJA,  ID_KATEGORIJE + "=" + id, null) > 0;
    }

    public List<Kategorija> getAllKategorije() {
        Cursor c = db.query(DATABASE_KATEGORIJA, new String[] {ID_KATEGORIJE, IME_KATEGORIJE, PHOTO_KATEGORIJA},
                null, null, null, null, null);

        List<Kategorija> lista = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                Kategorija kategorija = new Kategorija();

                kategorija.setId(c.getLong(c.getColumnIndex(ID_KATEGORIJE)));
                kategorija.setImeKategorije(c.getString(c.getColumnIndex(IME_KATEGORIJE)));
                kategorija.setPhotoKategorije(c.getString(c.getColumnIndex(PHOTO_KATEGORIJA)));
                kategorija.setRecepti(getAllReceptiFromKategorija(kategorija.getId()));
                lista.add(kategorija);
            } while (c.moveToNext());
        }
        return lista;
    }

    public List<Kategorija> searchKategorijeByFilter(String filter) {
        Cursor c = db.query(DATABASE_KATEGORIJA, new String[] {ID_KATEGORIJE, IME_KATEGORIJE, PHOTO_KATEGORIJA},
                IME_KATEGORIJE + " LIKE '%" + filter + "%'", null, null, null, null);

        List<Kategorija> lista = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                Kategorija kategorija = new Kategorija();

                kategorija.setId(c.getLong(c.getColumnIndex(ID_KATEGORIJE)));
                kategorija.setImeKategorije(c.getString(c.getColumnIndex(IME_KATEGORIJE)));
                kategorija.setPhotoKategorije(c.getString(c.getColumnIndex(PHOTO_KATEGORIJA)));
                lista.add(kategorija);
            } while (c.moveToNext());
        }
        return lista;
    }

    public List<Recept> searchReceptiByFilter(String filter) {
        Cursor c = db.query(DATABASE_RECEPT, null, IME_RECEPTA + " LIKE '%" + filter + "%'",
                null, null, null, null);

        List<Recept> lista = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                Recept recept = getReceptZaId(c.getLong(c.getColumnIndex(ID_RECEPTA)));
                lista.add(recept);
            } while (c.moveToNext());
        }
        return lista;
    }

    //vraca listu recepata koji u sastojcima i uputama sadrzavaju filter
    public List<Recept> searchSastojkeIUputeByFilter(String filter) {
        Cursor c = db.query(DATABASE_SASTOJCI, null, TEKST_SASTOJKA + " LIKE '%" + filter + "%'",
                null, null, null, null);
        List<Recept> lista = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                Recept recept = getReceptZaId(c.getLong(c.getColumnIndex(ID_RECEPTA)));
                lista.add(recept);
            } while(c.moveToNext());
        }

        c = db.query(DATABASE_UPUTE, null, TEKST_UPUTE + " LIKE '%" + filter + "%'",
                null, null, null, null);
        if(c.moveToFirst()) {
            do {
                Recept recept = getReceptZaId(c.getLong(c.getColumnIndex(ID_RECEPTA)));
                lista.add(recept);
            } while(c.moveToNext());
        }
        return lista;
    }

    public Kategorija getKategorija(long id) throws SQLException {
        Cursor c = db.query(true, DATABASE_KATEGORIJA, new String[]{ID_KATEGORIJE, IME_KATEGORIJE, PHOTO_KATEGORIJA},
                ID_KATEGORIJE + "=" + id, null, null, null, null, null);

        Kategorija kategorija = new Kategorija();
        if(c != null && c.getCount() > 0) {
            c.moveToFirst();

            kategorija.setId(c.getLong(c.getColumnIndex(ID_KATEGORIJE)));
            kategorija.setImeKategorije(c.getString(c.getColumnIndex(IME_KATEGORIJE)));
            kategorija.setPhotoKategorije(c.getString(c.getColumnIndex(PHOTO_KATEGORIJA)));
        }
        return kategorija;
    }

    public long insertRecept(Recept recept) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(IME_RECEPTA, recept.getImeRecepta());
        initialValues.put(PHOTO_RECEPT, recept.getPhotoRecept());
        initialValues.put(NOTES, recept.getNotes());
        if(recept.getBrOsoba() == null) {
            initialValues.put(BR_OSOBA, 1);
        } else {
            initialValues.put(BR_OSOBA, recept.getBrOsoba());
        }
        long id = db.insert(DATABASE_RECEPT, null, initialValues);

        //ubaci sastojke i upute u svoje baze s id-em
        List<String> sastojci = recept.getSastojci();
        List<String> upute = recept.getUpute();
        for(String s : sastojci) {
            insertSastojak(id, s);
        }
        for(String u : upute) {
            insertUpute(id, u);
        }
        return id;
    }

    public void deleteRecept(long id) {
        db.delete(DATABASE_RECEPT, ID_RECEPTA + "=" + id, null);

        //za taj id obriši i sve njegove sastojke
        db.delete(DATABASE_SASTOJCI, ID_RECEPTA + "=" + id, null);
        db.delete(DATABASE_UPUTE, ID_RECEPTA + "=" + id, null);
        db.delete(DATABASE_RECEPT_U_KATEGORIJI, ID_RECEPTA + "=" + id, null);
    }

    //za pretrazivanje po svim receptima - za dobivanje svih staviti ""
    public List<Recept> getAllRecepti(String filter) throws SQLException {

        String query;
        if(filter.equals("")) {
            query = "SELECT * FROM " + DATABASE_RECEPT;
        } else {
            query = "SELECT * FROM " + DATABASE_RECEPT + " WHERE "
                    + IME_RECEPTA + " LIKE '%" + filter + "%'";
        }

        List<Recept> recepti = new ArrayList<>();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            do {
                recepti.add(getReceptZaId(c.getLong(c.getColumnIndex(ID_RECEPTA))));
            } while(c.moveToNext());
        }

        return recepti;
    }

    //za zadani id vrati recept
    public Recept getReceptZaId(long id) {
        Cursor c = db.query(DATABASE_RECEPT, null, ID_RECEPTA + "=" + id, null, null, null, null);
        Recept r = new Recept();
        if(c != null && c.getCount() > 0) {
            c.moveToFirst();

            r.setId(c.getLong(c.getColumnIndex(ID_RECEPTA)));
            r.setImeRecepta(c.getString(c.getColumnIndex(IME_RECEPTA)));
            r.setPhotoRecept(c.getString(c.getColumnIndex(PHOTO_RECEPT)));
            r.setNotes(c.getString(c.getColumnIndex(NOTES)));
            r.setSastojci(getAllSastojciZaRecept(r.getId()));
            r.setUpute(getAllUputeZaRecept(r.getId()));
            r.setBrOsoba(c.getInt(c.getColumnIndex(BR_OSOBA)));
        }
        return r;
    }

    public void updateRecept(Recept recept) {
        ContentValues values = new ContentValues();
        values.put(IME_RECEPTA, recept.getImeRecepta());
        values.put(NOTES, recept.getNotes());
        values.put(BR_OSOBA, recept.getBrOsoba());
        values.put(PHOTO_RECEPT, recept.getPhotoRecept());

        db.update(DATABASE_RECEPT, values, ID_RECEPTA + "=" + recept.getId(), null);

        deleteAllSastojciZaRecept(recept.getId());
        deleteAllUputeZaRecept(recept.getId());

        List<String> sastojci = recept.getSastojci();
        List<String> upute = recept.getUpute();
        for(String s : sastojci) {
            insertSastojak(recept.getId(), s);
        }
        for(String u : upute) {
            insertUpute(recept.getId(), u);
        }
    }

    //za zadani id kategorije nadji sve recepte koji su u kategoriji
    public List<Recept> getAllReceptiFromKategorija(long idKategorije) throws SQLException {

        Cursor c = db.query(DATABASE_RECEPT_U_KATEGORIJI, new String[]{ID_RECEPTA},
                ID_KATEGORIJE + "=" + idKategorije, null, null, null, null);

        List<Recept> recepti = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                recepti.add(getReceptZaId(c.getLong(c.getColumnIndex(ID_RECEPTA))));
            } while(c.moveToNext());
        }

        return recepti;
    }

    //za zadani string provjeri sadržava li ime recepta taj string
    //public Cursor getAllReceptiKaoTekst(String tekst) throws SQLException {
    //   return db.query(DATABASE_RECEPT, new String[] {IME_RECEPTA, PHOTO_RECEPT, NOTES},
    //            IME_RECEPTA + " LIKE " + tekst, null, null, null, null, null);
    //}

    public long insertSastojak(long idRecepta, String tekst) {
        ContentValues values = new ContentValues();
        values.put(ID_RECEPTA, idRecepta);
        values.put(TEKST_SASTOJKA, tekst);
        return db.insert(DATABASE_SASTOJCI, null, values);
    }

    public long insertUpute(long idRecepta, String tekst) {
        ContentValues values = new ContentValues();
        values.put(ID_RECEPTA, idRecepta);
        values.put(TEKST_UPUTE, tekst);
        return db.insert(DATABASE_UPUTE, null, values);
    }

    public boolean deleteAllSastojciZaRecept(long idRecepta) {
        return db.delete(DATABASE_SASTOJCI, ID_RECEPTA + "=" + idRecepta, null) > 0;
    }

    public boolean deleteAllUputeZaRecept(long idRecepta) {
        return db.delete(DATABASE_UPUTE, ID_RECEPTA + "=" + idRecepta, null) > 0;
    }

    //za zadani recept vrati sve sastojke
    public List<String> getAllSastojciZaRecept(long idRecepta) throws SQLException{
        Cursor c =  db.query(DATABASE_SASTOJCI, new String[] {TEKST_SASTOJKA},
                ID_RECEPTA + "=" + idRecepta, null, null, null, null, null);

        List<String> sastojci = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                String s = c.getString(c.getColumnIndex(TEKST_SASTOJKA));
                sastojci.add(s);
            } while(c.moveToNext());
        }
        return sastojci;
    }

    //za zadani recept vrati sve upute
    public List<String> getAllUputeZaRecept(long idRecepta) throws SQLException {
        Cursor c = db.query(DATABASE_UPUTE, new String[] {TEKST_UPUTE},
                ID_RECEPTA + "=" + idRecepta, null, null, null, null, null);

        List<String> upute = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                String u = c.getString(c.getColumnIndex(TEKST_UPUTE));
                upute.add(u);
            } while(c.moveToNext());
        }
        return upute;
    }

    public long insertReceptUKategoriju(Kategorija k, Recept r) {
        ContentValues values = new ContentValues();
        values.put(ID_KATEGORIJE, k.getId());
        values.put(ID_RECEPTA, r.getId());
        return db.insert(DATABASE_RECEPT_U_KATEGORIJI, null, values);
    }
}
