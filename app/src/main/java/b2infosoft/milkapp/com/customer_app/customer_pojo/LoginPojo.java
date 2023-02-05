package b2infosoft.milkapp.com.customer_app.customer_pojo;

/**
 * Created by u on 12-Oct-17.
 */


public class LoginPojo {
    //{"success":true,"user_id":5,"message":"REGISTRATION SUCCESSFULL!"}
    public String success;
    public String user_id;
    public String message;

    public LoginPojo(String success, String user_id, String message) {
        this.success = success;
        this.user_id = user_id;
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
