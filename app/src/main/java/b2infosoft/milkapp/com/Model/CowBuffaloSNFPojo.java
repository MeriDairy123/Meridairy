package b2infosoft.milkapp.com.Model;

/**
 * Created by u on 18-Dec-17.
 */

public class CowBuffaloSNFPojo {

    public String id = "";
    public String snf_list = "";
    public String fat_list = "";
    public String snf_fat_category = "";
    public String dairy_id = "";
    public String created_by = "";
    public String ctegory_name = "";

    public CowBuffaloSNFPojo(String id, String snf_list, String fat_list, String snf_fat_category, String dairy_id, String created_by, String ctegory_name) {
        this.id = id;
        this.snf_list = snf_list;
        this.snf_fat_category = snf_fat_category;
        this.fat_list = fat_list;
        this.dairy_id = dairy_id;
        this.created_by = created_by;
        this.ctegory_name = ctegory_name;
    }


}
