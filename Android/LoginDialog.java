package com.feeleasy.project.sw;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginDialog {
    private Context context;

    public LoginDialog(Context context) {
        this.context = context;
    }

    public void callFunction() {
        final Dialog dlg = new Dialog(context);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_login);
        dlg.show();

        final EditText etName = dlg.findViewById(R.id.editName);
        final EditText etTel = dlg.findViewById(R.id.editTel);
        final Button btnOk = dlg.findViewById(R.id.btnOk);
        final Button btnCancel = dlg.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String tel = etTel.getText().toString();

                Login task = new Login(context, name, tel);
                task.execute(name, tel);

                dlg.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
    }
}