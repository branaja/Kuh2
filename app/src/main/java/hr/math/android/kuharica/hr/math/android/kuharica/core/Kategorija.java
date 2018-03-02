package hr.math.android.kuharica.hr.math.android.kuharica.core;

import java.util.List;

/**
 * Created by mabel on 27-Feb-18.
 */

public class Kategorija {

    private long id;
    private String imeKategorije;
    private String photoKategorije;
    private List<Recept> recepti;

    public Kategorija() {}
    public Kategorija(String imeKategorije, String photoKategorije) {
        this.imeKategorije = imeKategorije;
        this.photoKategorije = photoKategorije;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImeKategorije() {
        return imeKategorije;
    }

    public void setImeKategorije(String imeKategorije) {
        this.imeKategorije = imeKategorije;
    }

    public String getPhotoKategorije() {
        return photoKategorije;
    }

    public void setPhotoKategorije(String photoKategorije) {
        this.photoKategorije = photoKategorije;

    }

    public List<Recept> getRecepti() {
        return recepti;
    }

    public void setRecepti(List<Recept> recepti) {
        this.recepti = recepti;
    }
}
