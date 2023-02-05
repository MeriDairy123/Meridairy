package b2infosoft.milkapp.com.useful;

import android.content.Context;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;


public class Validations {

    public static boolean isValidMobile(EditText mobile, String error) {

        boolean val = false;
        String mob = mobile.getText().toString().trim();


        if (mob.length() == 10 && !mob.isEmpty()) {

            val = true;
        } else {

            //  mobile.setError(error);
            //  mobile.requestFocus();

        }

        return val;
    }


    public static boolean isValidPin(EditText pin) {

        boolean val = false;
        String postal = pin.getText().toString().trim();


        if (postal.length() == 6 && !postal.isEmpty()) {

            val = true;
        } else {

            pin.setError("Invalid pin code");
            pin.requestFocus();

        }

        return val;
    }


    public static boolean isValidEmail(EditText email, String error, TextInputLayout textInputLayout) {
        boolean val = false;

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {

            val = true;

        } else {

            textInputLayout.setError(error);
            //  textInputLayout.requestFocus();
        }
        return val;
    }

    public static boolean checkEmailMail(Context context, String email, String error) {
        boolean val = false;

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            val = true;

        } else {

            // email.setError(error); Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            // email.requestFocus();
        }

        return val;
    }

    public static boolean isValidName(EditText name) {

        boolean val = false;
        String mob = name.getText().toString().trim();


        if (!mob.isEmpty()) {

            val = true;
        } else {

            name.setError("Invalid name");
            name.requestFocus();

        }

        return val;
    }

    public static boolean isEditTextFilled(EditText name, String ErrorMsg) {

        boolean val = false;
        String mob = name.getText().toString().trim();


        if (!mob.isEmpty()) {

            val = true;
        } else {

            name.setError(ErrorMsg);
            name.requestFocus();

        }

        return val;
    }

    public static boolean isStringFilled(String value, String ErrorMsg, Context context) {

        boolean val = false;
        if (!value.isEmpty()) {

            val = true;
        } else {
            UtilityMethod.showAlert(ErrorMsg, context);
        }

        return val;
    }

    public static Boolean inputLayoutEmail(EditText editText, TextInputLayout textInputLayout, String message) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textInputLayout.setError(message);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public static Boolean inputLayoutMobile(EditText editText, TextInputLayout textInputLayout, String message) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty() || value.length() == 10) {
            editText.setError(message);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }
    /*private void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }*/
}
