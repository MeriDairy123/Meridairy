package b2infosoft.milkapp.com.Dairy.PaymentVoucher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import b2infosoft.milkapp.com.Dairy.Bhugtan.SallerBhugtanFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan.ReceiveCashFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static android.content.Context.MODE_PRIVATE;
import static b2infosoft.milkapp.com.appglobal.Constant.SaveSignature;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class VoucharSignatureFragment extends Fragment implements FragmentBackPressListener {

    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    SignaturePad mSignaturePad;
    Button btnClear, btnSave;
    Context mContext;
    String imagePath = "";
    SessionManager sessionManager;
    String strFrom = "", transactionID = "", CutomerID = "", CustomerName = "", CustomerFatherName = "", unic_customer = "", type = "", fromWhere = "";
    DatabaseHandler databaseHandler;
    String dairyid;
    Fragment fragment = null;
    Bundle bundle = null;
    View view;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signature, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        permissionAccess();
        sessionManager = new SessionManager(mContext);
        bundle = getArguments();
        permissionStatus = mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        strFrom = bundle.getString("FromWhere");
        unic_customer = bundle.getString("unic_customer");
        transactionID = bundle.getString("transactionID");
        CutomerID = bundle.getString("CustomerId");
        type = bundle.getString("type");
        System.out.println("Signature=fromWhere>>>" + fromWhere);

        //TransactionHistory,ReceiveCash
        mSignaturePad = view.findViewById(R.id.signature_pad);
        btnClear = view.findViewById(R.id.btnClear);
        btnSave = view.findViewById(R.id.btnSave);
        btnClear.setEnabled(false);
        btnSave.setEnabled(false);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        // showAlertWithButton(mContext, mContext.getString(R.string.Please_Do_Signature_And_Then_Click_Save));
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                btnClear.setEnabled(true);
                btnSave.setEnabled(true);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                btnClear.setEnabled(false);
                btnSave.setEnabled(false);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionAccess();
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                //call api
                Activity activity = (Activity) mContext;
                Bitmap resized = Bitmap.createScaledBitmap(signatureBitmap, 1200, 1200, true);
                Uri tempUri = UtilityMethod.getImageUri(mContext, resized);
                imagePath = UtilityMethod.getPath(tempUri, activity);
                System.out.println("imagepath===>>>" + imagePath);
                uploadData(imagePath);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    showToast(mContext, "Unable to get Permission");
                }
            }
        }
    }

    public void permissionAccess() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        showToast(mContext, "Go to Permissions to Grant Storage");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.commit();

        }
    }


    private void uploadData(String imagePath) {
        @SuppressLint("StaticFieldLeak") NetworkTask networkTask = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject mainObj = new JSONObject(response);
                    if (mainObj.getString("status").equals("success")) {
                        mSignaturePad.clear();
                        showToast(mContext, mContext.getString(R.string.Signature_Uploaded_Succesfully));
                        getActivity().onBackPressed();
                        if (strFrom.equals("PaymentRegister")) {
                            getActivity().onBackPressed();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        multipartBuilder.addFormDataPart("customer_id", CutomerID);
        multipartBuilder.addFormDataPart("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
        multipartBuilder.addFormDataPart("transactions_id", transactionID);


        multipartBuilder.addFormDataPart("type", type);

        File signaturefFile = new File(imagePath);
        System.out.println("file name=>>>>" + signaturefFile.getName());
        System.out.println("imagePath==>>>" + signaturefFile.getAbsolutePath());

        if (signaturefFile != null) {
            System.out.println("signature_image= not null=>>>>" + signaturefFile.getAbsolutePath());
            multipartBuilder.addFormDataPart("signature_image", signaturefFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), signaturefFile));
        }

        RequestBody body = multipartBuilder.build();
        networkTask.addRequestBody(body);

        networkTask.execute(SaveSignature);
    }


    @Override
    public void OnFragmentBackPressListener() {
        if (fromWhere.equals("ReceiveCash")) {
            fragment = new ReceiveCashFragment();
            goNextFragmentReplace(mContext, fragment);
        } else if (fromWhere.equals("Bhugtan")) {

            fragment = new SallerBhugtanFragment();
            bundle = new Bundle();
            bundle.putString("CustomerId", CutomerID);
            bundle.putString("unic_customer", unic_customer);
            bundle.putString("CustomerName", CustomerName);
            bundle.putString("CustomerFatherName", CustomerFatherName);
            fragment.setArguments(bundle);
            goNextFragmentReplace(mContext, fragment);

        }
    }
}


