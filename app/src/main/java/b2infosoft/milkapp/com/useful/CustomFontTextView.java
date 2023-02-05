package b2infosoft.milkapp.com.useful;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import b2infosoft.milkapp.com.R;


/**
 * Created by Choudhary Computer on 02/09/2019.
 */

public class CustomFontTextView extends androidx.appcompat.widget.AppCompatTextView {

    String customFont;

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        style(context, attrs);

    }

    private void style(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        int cf = a.getInteger(R.styleable.CustomFontTextView_fontName, 0);
        int fontName = 0;
        switch (cf) {
            case 1:
                fontName = R.string.Poppins_Regular;
                break;
            case 2:
                fontName = R.string.Roboto_Regular;
                break;
            case 3:
                fontName = R.string.Roboto_Bold;
                break;
            case 4:
                fontName = R.string.OpenSans_Regular;
                break;
            default:
                fontName = R.string.Poppins_Regular;
                break;
        }

        customFont = getResources().getString(fontName);

        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + customFont + ".ttf");
            setTypeface(tf);
        }catch(Exception e) {}



        a.recycle();
    }
}