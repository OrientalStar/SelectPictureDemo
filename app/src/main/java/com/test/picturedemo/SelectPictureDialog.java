package com.test.picturedemo;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * Created by ponos
 * 2017/9/12.
 * Description:照片选择器
 */

public class SelectPictureDialog extends Dialog {
    private Context context;
    private TextView photographBtn,albumBtn,cancelBtn;
    private AlertDialog dialog;
    private TwoOnClickInterface twoOnClickInterface;

    public SelectPictureDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public void setTwoOnClickInterface(TwoOnClickInterface onClickInterface){
        this.twoOnClickInterface = onClickInterface;
    }

    private void init(Context context) {
        this.context = context;

        LayoutInflater inflaterDl = LayoutInflater.from(context);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_selectpicture, null);

        photographBtn = (TextView) layout.findViewById(R.id.btn_photograph);
        albumBtn = (TextView) layout.findViewById(R.id.btn_album);
        cancelBtn = (TextView) layout.findViewById(R.id.btn_cancel);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        layout.setMinimumWidth((int) (dm.widthPixels * 0.8));

        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        photographBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                twoOnClickInterface.photoGraph();
                dialog.dismiss();
            }
        });

        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                twoOnClickInterface.album();
                dialog.dismiss();
            }
        });
    }

}
