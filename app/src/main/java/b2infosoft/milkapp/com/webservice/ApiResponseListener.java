package b2infosoft.milkapp.com.webservice;

public interface ApiResponseListener {
    void onSuccess(String beanTag, SuperClassCastBean superClassCastBean);
    void onFailure(String msg);
    void onError(String msg);
}
