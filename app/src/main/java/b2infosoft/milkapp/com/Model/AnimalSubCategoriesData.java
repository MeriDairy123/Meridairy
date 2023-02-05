package b2infosoft.milkapp.com.Model;

public class AnimalSubCategoriesData {
    String Animalname;
    String Categoryid;
    String AnimalMainId;

    public AnimalSubCategoriesData(String animalname, String categoryid, String animalMainId) {
        Animalname = animalname;
        Categoryid = categoryid;
        AnimalMainId = animalMainId;
    }

    public AnimalSubCategoriesData(String animalname, String categoryid) {
        Animalname = animalname;
        Categoryid = categoryid;
    }

    public String getAnimalMainId() {
        return AnimalMainId;
    }

    public void setAnimalMainId(String animalMainId) {
        AnimalMainId = animalMainId;
    }

    public String getAnimalname() {
        return Animalname;
    }

    public void setAnimalname(String animalname) {
        Animalname = animalname;
    }

    public String getCategoryid() {
        return Categoryid;
    }

    public void setCategoryid(String categoryid) {
        Categoryid = categoryid;
    }
}
