package b2infosoft.milkapp.com.Model;

/**
 * Created by u on 11-Dec-19.
 */

public class SnfFatListPojo {
    public String id = "", SNF = "", Fat = "", Rate = "", snf_fat_category = "";

    public SnfFatListPojo(String id, String SNF, String fat, String rate, String snf_fat_category) {
        this.id = id;
        Fat = fat;
        Rate = rate;
        this.SNF = SNF;
        this.id = id;
        this.snf_fat_category = snf_fat_category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSNF() {
        return SNF;
    }

    public void setSNF(String SNF) {
        this.SNF = SNF;
    }

    public String getFat() {
        return Fat;
    }

    public void setFat(String fat) {
        Fat = fat;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getSnf_fat_category() {
        return snf_fat_category;
    }

    public void setSnf_fat_category(String snf_fat_category) {
        this.snf_fat_category = snf_fat_category;
    }
}
