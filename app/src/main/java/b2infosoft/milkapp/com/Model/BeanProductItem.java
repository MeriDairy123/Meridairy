package b2infosoft.milkapp.com.Model;

public class BeanProductItem {
    public Integer id, min_qty, qty;
    public double price, mrp, gst;
    public String title, type, image, thumb, status, description;


    public BeanProductItem(Integer id, Integer min_qty, Integer qty, double price, double mrp, double gst, String title, String type,
                           String image, String thumb, String status, String description) {
        this.id = id;
        this.min_qty = min_qty;
        this.qty = qty;
        this.price = price;
        this.mrp = mrp;
        this.gst = gst;
        this.title = title;
        this.type = type;
        this.image = image;
        this.thumb = thumb;
        this.status = status;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMin_qty() {
        return min_qty;
    }

    public void setMin_qty(Integer min_qty) {
        this.min_qty = min_qty;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getGst() {
        return gst;
    }

    public void setGst(double gst) {
        this.gst = gst;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
