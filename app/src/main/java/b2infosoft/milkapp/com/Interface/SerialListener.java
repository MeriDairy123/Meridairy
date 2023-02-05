package b2infosoft.milkapp.com.Interface;

public interface SerialListener {
    void onSerialConnect      ();
    void onSerialConnectError (Exception e);
    void onSerialRead         (String data);
    void onSerialIoError      (Exception e);
    void onReceiveMachineData( String message,String fat,String weight,String snf,String clr);
}
