package com.feeleasy.project.sw;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        SharedPreferences auto = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
        String loginName = auto.getString("UserName",null);
        String loginTel = auto.getString("UserTel",null);

        //자동 로그인
        if(loginName !=null && loginTel != null) {
            login(loginName, loginTel);
            finish();
        }

        (findViewById(R.id.btnLogin)).setOnClickListener(this);
        (findViewById(R.id.txtJoin)).setOnClickListener(this);
    }

    public void login(String name, String tel) {
        Login task = new Login(getApplicationContext(), name, tel);
        task.execute(name, tel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                LoginDialog dialog = new LoginDialog(this);
                dialog.callFunction();
                break;
            case R.id.txtJoin:
                startActivity(new Intent(this, JoinActivity.class));
                break;
        }
    }

}