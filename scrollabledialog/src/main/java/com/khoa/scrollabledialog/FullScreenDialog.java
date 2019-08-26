package com.khoa.scrollabledialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;

public class FullScreenDialog {

    private Dialog dialog;

    public FullScreenDialog(Context context) {
        this.dialog = new Dialog(context, R.style.Theme_Dialog);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public Dialog getIntanse(){
        return this.dialog;
    }
}
