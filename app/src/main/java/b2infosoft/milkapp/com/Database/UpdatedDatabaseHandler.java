package b2infosoft.milkapp.com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class UpdatedDatabaseHandler extends SQLiteOpenHelper {

    // Database Name
    private static final String UDATABASE_NAME = "updateMilkDb";
    private static final int DATABASE_VERSION = 11;
    public static final String UniqueId = "unique_id", TenDaysPrice = "ten_days_price", checkBoxStatus = "check_box_status";
    Context mContext;
    private static UpdatedDatabaseHandler instance;
    //Table name
    private static final String Table_TenDaysPrice = "ten_days_price_dataa";

    public UpdatedDatabaseHandler(Context context) {
        super(context, UDATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    public static synchronized UpdatedDatabaseHandler getDbHelperr(Context context) {
        if (instance == null) instance = new UpdatedDatabaseHandler(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String tenDaysPriceTable = "CREATE TABLE " + Table_TenDaysPrice + "(" + UniqueId + " INTEGER," + TenDaysPrice + " FLOAT," + checkBoxStatus + "INTEGER" + ")";
        sqLiteDatabase.execSQL(tenDaysPriceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Table_TenDaysPrice);
        onCreate(sqLiteDatabase);

    }

    public void addTenDaysEntry(int unic_customerId, float tenDaysPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UniqueId, unic_customerId);
        contentValues.put(TenDaysPrice, tenDaysPrice);
        db.insert(Table_TenDaysPrice, null, contentValues);
        //   db.close();
    }

    public void updateSaveSmsDefaultSetting(String uniqueCustomerId,String checkBoxSetting) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(checkBoxStatus, checkBoxSetting);
        db.update(UDATABASE_NAME, contentValues, "check_box_status=?",  new String[] { uniqueCustomerId, checkBoxSetting});
       // db.close();
    }


}
