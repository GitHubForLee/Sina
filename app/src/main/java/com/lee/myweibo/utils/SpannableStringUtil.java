package com.lee.myweibo.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * Created by lee on 2017/2/28.
 */

public class SpannableStringUtil {
    public static SpannableString getSpannaleString(String string){
        string="@"+string;
        SpannableString spannableString=new SpannableString(string);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0C59B2")), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}
