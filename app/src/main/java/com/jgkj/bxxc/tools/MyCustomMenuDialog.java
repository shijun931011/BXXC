package com.jgkj.bxxc.tools;

/**
 * Created by tongshoujun on 2017/5/12.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.jgkj.bxxc.R;

public class MyCustomMenuDialog extends Dialog {

    private OnCustomDialogListener customDialogListener;
    private Button cancel;

    public MyCustomMenuDialog(Context context,OnCustomDialogListener customDialogListener) {
        super(context);
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_dialog);
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(clickListener);
    }

    //定义回调事件，用于dialog的点击事件
    public interface OnCustomDialogListener{
        public void back(String name);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MyCustomMenuDialog.this.dismiss();
        }
    };

}

