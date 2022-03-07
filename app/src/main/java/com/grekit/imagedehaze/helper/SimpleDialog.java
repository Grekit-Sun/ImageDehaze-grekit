package com.grekit.imagedehaze.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.grekit.imagedehaze.R;

public class SimpleDialog extends Dialog implements MyDialog {

    private Context context;

    public SimpleDialog(Context context){
        super(context, R.style.iphone_progress_dialog);
        this.context = context;
        this.create();
    }

    @Override
    public void create() {
        //加载布局文件
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.dialog_progress_layout, null);
        //dialog添加视图
        setContentView(view);
    }
}
