package b2infosoft.milkapp.com.Model;


public class BeanUserRequestItem {

    public String id = "", user_id = "", name = "", relationship_user_id = "", type = "",
            status = "", created_at = "";


    public BeanUserRequestItem(String id, String user_id, String name, String relationship_user_id, String type, String status, String created_at) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.relationship_user_id = relationship_user_id;
        this.type = type;
        this.status = status;
        this.created_at = created_at;
    }
}
