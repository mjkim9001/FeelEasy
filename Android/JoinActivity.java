package com.feeleasy.project.sw;

import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    RadioGroup rgUser, rgFur, rgRec;
    LinearLayout linearUser1, linearUser2;

    int numFur;
    String type = "", name, tel;
    static String fur, agree, related = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        rgUser = findViewById(R.id.rgUser);
        rgFur = findViewById(R.id.rgFur);
        rgRec = findViewById(R.id.rgRec);

        linearUser1 = findViewById(R.id.linearUser1);
        linearUser2 = findViewById(R.id.linearUser2);

        rgUser.setOnCheckedChangeListener(this);
        rgFur.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        if (radioGroup == rgUser) {
            switch (i) {
                case R.id.radioUser1:
                    type = "R";
                    linearUser1.setVisibility(View.VISIBLE);
                    linearUser2.setVisibility(View.GONE);
                    break;
                case R.id.radioUser2:
                    type = "P";
                    linearUser1.setVisibility(View.GONE);
                    linearUser2.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (radioGroup == rgFur) {
            switch (i) {
                case R.id.radioFur1:
                case R.id.radioFur2:
                case R.id.radioFur3:
                    findViewById(R.id.linearFur4).setVisibility(View.GONE);
                    numFur = 1;
                    break;
                case R.id.radioFur4:
                    findViewById(R.id.linearFur4).setVisibility(View.VISIBLE);
                    numFur = 2;
                    break;
            }
        }
    }

    public void makeToast(String s, String v) {
        Toast.makeText(this, s + " " + v + "하세요.", Toast.LENGTH_LONG).show();
    }

    public boolean getDatas() {
        name = ((EditText)findViewById(R.id.editName)).getText().toString();
        if (name.isEmpty()) {
            makeToast("이름을", "입력");
            return false;
        }

        tel = ((EditText)findViewById(R.id.editTel)).getText().toString();
        if (tel.isEmpty()) {
            makeToast("연락처를", "입력");
            return false;
        }

        switch (type) {
            case "R":  //거주자의 경우
                switch (numFur) {
                    case 1:
                        fur = ((RadioButton)findViewById(rgFur.getCheckedRadioButtonId())).getText().toString();
                        break;
                    case 2:
                        fur = ((EditText)findViewById(R.id.editFur4)).getText().toString();
                        if (fur.isEmpty()) {
                            makeToast("자주 사용하는 가구를", "입력");
                            return false;
                        }
                        break;
                    default:
                        makeToast("자주 사용하는 가구를", "선택");
                        return false;
                }
                switch (rgRec.getCheckedRadioButtonId()) {
                    case R.id.radioAgree:
                        agree = "1";
                        break;
                    case R.id.radioDisagree:
                        agree = "0";
                        break;
                    default:
                        makeToast("모니터링 수신 여부를", "선택");
                        return false;
                }
                break;
            case "P":  //보호자의 경우
                if (related.equals("-1")) {
                    Toast.makeText(this, "거주자 확인을 해주세요.", Toast.LENGTH_LONG).show();
                    return false;
                }
                break;
            default:
                makeToast("거주자/보호자 여부를", "선택");
                return false;
        }

        return true;
    }

    public void checkResiExist(String resName, String resTel) {
        ConfirmResi task = new ConfirmResi(getApplicationContext());
        task.execute(resName, resTel);
    }

    public void join(String type, String name, String tel, String fur, String agree, String related) {
        InsertUser task = new InsertUser();
        task.execute(type, name, tel, fur, agree, related);
    }

    public void login(String name, String tel) {
        Login task = new Login(getApplicationContext(), name, tel);
        task.execute(name, tel);
    }

    public void btnClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCheck:
                String resName = ((EditText)findViewById(R.id.editResName)).getText().toString();
                String resTel = ((EditText)findViewById(R.id.editResTel)).getText().toString();

                if (resName.isEmpty() || resTel.isEmpty()) {
                    makeToast("거주자 정보를", "입력");
                } else {
                    //거주자가 존재하는지 검사
                    checkResiExist(resName, resTel);
                }
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnComplete:
                if (getDatas()) {
                    join(type, name, tel, fur, agree, related);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                    dlg.setTitle("회원가입이 완료되었습니다!").setNeutralButton("로그인",
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            login(name, tel);
                        }
                    }).show();
                }
                break;
        }
    }
}