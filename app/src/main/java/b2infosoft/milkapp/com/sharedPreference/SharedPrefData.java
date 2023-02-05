package b2infosoft.milkapp.com.sharedPreference;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefData {

    public static void saveDataToPrefrence(Context c, String key, String saveString) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("MeriDairyPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(key, saveString);
        myEdit.commit();
    }

    public static String retriveDataFromPrefrence(Context c, String retriveString) {
        SharedPreferences sh = c.getSharedPreferences("MeriDairyPref", MODE_PRIVATE);
        String s1 = sh.getString(retriveString, "");
        return s1;
    }
}
