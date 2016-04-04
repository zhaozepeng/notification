package com.android.notification;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.libcore.log.L;
import com.android.libcore_ui.activity.BaseActivity;

/**
 * Description: #TODO
 *
 * @author zzp(zhao_zepeng@hotmail.com)
 * @since 2016-04-04
 */
public class ChildActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        useToolbar = false;
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        textView.setTextSize(30);
        textView.setText("i'm child");
        setContentView(textView, new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e("onDestroy");
    }
}
