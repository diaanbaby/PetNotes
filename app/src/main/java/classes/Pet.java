package classes;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pet implements Serializable, Parcelable {
    private long id;
    private String name;
    private String birthDate;
    private int weight;
    private Bitmap photo;
    private List<Vaccination> vaccinations;
    private List<VetVisit> vetVisits;

    public Pet() {
    }

    public Pet(long id, String name, String birthDate, int weight, Bitmap photo) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.weight = weight;
        this.photo = photo;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public List<Vaccination> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(List<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public List<VetVisit> getVetVisits() {
        return vetVisits;
    }

    public void setVetVisits(List<VetVisit> vetVisits) {
        this.vetVisits = vetVisits;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }
}
