package com.dashuai.android.treasuremap.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.util.AllCapTransformationMethod;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.Installation;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisteActivity extends Activity implements Reply {

    private Button submit, getVerf;
    private EditText name, phone, verf;
    private EditText edit1, edit2, edit3, edit4;
    private EditText factorId;
    private DialogUtil dialogUtil;
    private RequestUtil requestUtil;
    private Handler handler;
    private int verfTime = 60;// 验证码作用时间单位秒
    private View registTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CPQApplication.initActionBar(this, false, "产品注册");
        setContentView(R.layout.activity_registe);
        initView();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            verfTime--;
            if (verfTime <= 0) {
                getVerf.setOnClickListener(new VerfOnClick());
                getVerf.setText("获取验证码");
            } else {
                getVerf.setText("重新获取(" + verfTime + "s)");
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    }

    private void initView() {
        dialogUtil = new DialogUtil(this);
        requestUtil = new RequestUtil(this, this);
        handler = new MyHandler();
        name = (EditText) findViewById(R.id.registe_name);
        phone = (EditText) findViewById(R.id.registe_phone);
        verf = (EditText) findViewById(R.id.registe_verf);
        getVerf = (Button) findViewById(R.id.registe_getVerf);
        submit = (Button) findViewById(R.id.submit);
        registTitle = findViewById(R.id.registCode_title);
        factorId = (EditText) findViewById(R.id.registe_factorId);
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);
        edit3 = (EditText) findViewById(R.id.edit3);
        edit4 = (EditText) findViewById(R.id.edit4);
        edit1.setVisibility(View.VISIBLE);
        edit2.setVisibility(View.VISIBLE);
        edit3.setVisibility(View.VISIBLE);
        edit4.setVisibility(View.VISIBLE);
        registTitle.setVisibility(View.VISIBLE);
        edit1.setTransformationMethod(new AllCapTransformationMethod());
        edit2.setTransformationMethod(new AllCapTransformationMethod());
        edit3.setTransformationMethod(new AllCapTransformationMethod());
        edit4.setTransformationMethod(new AllCapTransformationMethod());
        edit1.addTextChangedListener(new MyTextWatcher(edit1));
        edit2.addTextChangedListener(new MyTextWatcher(edit2));
        edit3.addTextChangedListener(new MyTextWatcher(edit3));
        getVerf.setOnClickListener(new VerfOnClick());
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                registe();
            }
        });
    }

    class VerfOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (!isMobileNO(phone.getText().toString())) {
                dialogUtil.setErrorMessage("电话号码格式不对");
                return;
            }
            getVerf.setOnClickListener(null);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("phone", phone.getText().toString().trim());
            requestUtil.postRequest(Constant.URL_IP + Constant.VERF, map, 1);

            getVerf.setText("重新获取(" + verfTime + "s)");
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private void registe() {
        if (isNull(name)) {
            dialogUtil.setErrorMessage("姓名不能为空");
        } else if (isNull(phone)) {
            dialogUtil.setErrorMessage("电话不能为空");
        } else if (isNull(verf)) {
            dialogUtil.setErrorMessage("短信验证码不能为空");
        } else if (isEditsEmpty() && isNull(factorId)) {
            dialogUtil.setErrorMessage("请填写推荐人ID或者注册码");
        }
//        else if (!isEditsEmpty() && !isEditsOk()) {
//            dialogUtil.setErrorMessage("注册码格式不对");
//        }
        else {
            dialogUtil.setProgress("注册中...");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("device_id", Installation.id(this.getBaseContext()));
            map.put("type_id", CPQApplication.getID_KEY());
            map.put("name", name.getText().toString());
            map.put("phone", phone.getText().toString());
            map.put("verf", verf.getText().toString());
            map.put("sy", isEditsEmpty() ? 1 : 0);
            map.put("accountdl_id",factorId.getText().toString());
            if (!isEditsEmpty()) {
                map.put("numbers", getAuthCode());
            }
            requestUtil.postRequest(Constant.URL_IP + Constant.REG, map, 0);
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    private boolean isNull(EditText editText) {
        return editText.getText().length() <= 0
                || null == editText.getText().toString()
                || editText.getText().toString().trim().equals("");
    }

    private String getAuthCode() {
        return edit1.getText().toString() + "-" + edit2.getText().toString()
                + "-" + edit3.getText().toString() + "-"
                + edit4.getText().toString();
    }

    private boolean isEditsOk() {
        return isEditOk(edit1) && isEditOk(edit2) && isEditOk(edit3)
                && isEditOk(edit4);
    }

    private boolean isEditOk(EditText editText) {
        return editText.getText().toString().trim().length() == 4;
    }

    private boolean isEditsEmpty() {
        return isEditEmpty(edit1) && isEditEmpty(edit2) && isEditEmpty(edit3)
                && isEditEmpty(edit4);
    }

    private boolean isEditEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    class MyTextWatcher implements TextWatcher {
        private View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() >= 4) {
                switch (view.getId()) {
                    case R.id.edit1:
                        edit2.requestFocus();
                        break;
                    case R.id.edit2:
                        edit3.requestFocus();
                        break;
                    case R.id.edit3:
                        edit4.requestFocus();
                        break;
                    default:
                        break;
                }
            }

        }
    }

    // public char string2Char(String text) {
    // int iValue = Integer.parseInt(text, 16);
    // return (char) iValue;
    // }

    @Override
    public void onSuccess(JSONObject response, int what) {
        if (what == 0) {
            dialogUtil.setMessage("恭喜", "产品注册成功！", "立刻体验",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogUtil.dismiss();
                            startActivity(new Intent(RegisteActivity.this,
                                    HomepageActivity.class));
                            finish();
                            CPQApplication.setLogined(true);
                        }
                    }, null, null);
        }
    }

    @Override
    public void onErrorResponse(String error, int what) {
        dialogUtil.setErrorMessage(error);
    }

    @Override
    public void onFailed(String error, int what) {
        dialogUtil.setErrorMessage(error);
    }
}
