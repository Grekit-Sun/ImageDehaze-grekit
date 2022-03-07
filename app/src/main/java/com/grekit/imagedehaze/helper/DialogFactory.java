package com.grekit.imagedehaze.helper;

import android.content.Context;
import android.os.Handler;

public class DialogFactory {

    public static final String TYPE_SIMPLE_DIALOG = "simple";

    public static MyDialog createDialog(Context context, String type) {
        if (type.equals(TYPE_SIMPLE_DIALOG)) {
            return new SimpleDialog(context);
        }
        return null;
    }
}
