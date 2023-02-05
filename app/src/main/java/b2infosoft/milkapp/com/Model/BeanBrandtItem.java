package b2infosoft.milkapp.com.Model;


public class BeanBrandtItem {
    public String id = "", name = "", categoryId = "", categoryName = "", code = "";

    public BeanBrandtItem(String id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public BeanBrandtItem(String id, String name, String categoryId, String categoryName, String code) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
