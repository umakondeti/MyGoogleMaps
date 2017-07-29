package com.example.umamaheshwari.mygooglemaps;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Reena on 03-08-2015.
 */
public class Htext extends TextView {

    public Htext(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Htext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Htext(Context context) {
        super(context);
        init();
    }

    private void init() {
        try{
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"HEL.OTF");

            setTypeface(tf);

        }catch(Exception e){
        }
    }
}

