package b2infosoft.milkapp.com.useful;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter  implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforePoint,int digitsAfterPoint) {
        mPattern=Pattern.compile("[0-9]{0," + (digitsBeforePoint-1) + "}+((\\.[0-9]{0," + (digitsAfterPoint-1) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        Matcher matcher=mPattern.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }

}