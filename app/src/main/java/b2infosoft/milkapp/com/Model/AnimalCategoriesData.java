package b2infosoft.milkapp.com.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class AnimalCategoriesData implements Parcelable {
    public static final Creator<AnimalCategoriesData> CREATOR = new Creator<AnimalCategoriesData>() {
        @Override
        public AnimalCategoriesData createFromParcel(Parcel in) {
            return new AnimalCategoriesData(in);
        }

        @Override
        public AnimalCategoriesData[] newArray(int size) {
            return new AnimalCategoriesData[size];
        }
    };
    String Animalname;
    String Categoryid;
    String AnimalImage;


    public AnimalCategoriesData(String animalname, String categoryid, String animalImage) {
        Animalname = animalname;
        Categoryid = categoryid;
        AnimalImage = animalImage;
    }

    protected AnimalCategoriesData(Parcel in) {
        Animalname = in.readString();
        Categoryid = in.readString();
        AnimalImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Animalname);
        dest.writeString(Categoryid);
        dest.writeString(AnimalImage);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAnimalname() {
        return Animalname;
    }

    public String getCategoryid() {
        return Categoryid;
    }

    public String getAnimalImage() {
        return AnimalImage;
    }
}
