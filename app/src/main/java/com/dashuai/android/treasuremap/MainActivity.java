package com.dashuai.android.treasuremap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dashuai.android.treasuremap.db.BZListStatusDao;
import com.dashuai.android.treasuremap.entity.BZListStatus;
import com.dashuai.android.treasuremap.entity.BzList;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.ui.HomepageActivity;
import com.dashuai.android.treasuremap.ui.RegisteActivity;
import com.dashuai.android.treasuremap.util.DefaultRationale;
import com.dashuai.android.treasuremap.util.DialogUtil;
import com.dashuai.android.treasuremap.util.Installation;
import com.dashuai.android.treasuremap.util.JsonUtil;
import com.dashuai.android.treasuremap.util.PermissionSetting;
import com.dashuai.android.treasuremap.util.RequestUtil;
import com.dashuai.android.treasuremap.util.RequestUtil.Reply;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity implements Reply {
    //    static {
//        System.loadLibrary("JniLibName"); //和生成so文件的名字对应。
//    }
    private RequestUtil requestUtil;
    private DialogUtil dialogUtil;
    private TextView textView;
    private ProgressBar progressBar;
    private MyHandler handler;

    String[] mPermissions = {
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE,
            Permission.READ_PHONE_STATE
    };

    String[] mPermissions2 = {
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CPQApplication.hideActionBar(this);
        init();
        requestPermission();
//        if(CPQApplication.VERSION == Constant.TV){
//            check_reg();
//        }
    }

    private void requestPermission() {
        DefaultRationale mRationale = new DefaultRationale();
        final PermissionSetting mSetting = new PermissionSetting(this);
        AndPermission.with(this)
                .permission(CPQApplication.VERSION == Constant.PHONE ? mPermissions : mPermissions2)
                .rationale(mRationale)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(MainActivity.this, "权限获取成功", Toast.LENGTH_SHORT).show();
                        check_reg();
                    }
                }).onDenied(new Action() {

            @Override
            public void onAction(List<String> permissions) {
                if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, permissions)) {
                    mSetting.showSetting(permissions, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "权限获取失败", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "权限获取失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).start();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void toast(int id) {
        toast(getResources().getString(id));
    }

    /**
     * 打开Setting
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }

    private void init() {
        requestUtil = new RequestUtil(this, this);
        dialogUtil = new DialogUtil(this);
        handler = new MyHandler(this);
        textView = (TextView) findViewById(R.id.main_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView.setBackgroundResource(CPQApplication.VERSION == Constant.PHONE ? R.drawable.welcome
                : R.drawable.welcome_tv);
    }

    /**
     * 获取备注分类列表信息
     */
    private void getBzInfo() {
        requestUtil.postRequest(Constant.URL_IP + Constant.BZINDEX, 1);
    }

    /**
     * 检查设备
     */
    private void check_reg() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("device_id", Installation.id(getBaseContext()));
        map.put("type_id", CPQApplication.getID_KEY());
        requestUtil.postRequest(Constant.URL_IP + Constant.CHECK_REG, map, 0);
        Log.d("check_reg", "device_id" + map.get("device_id") + "  type_id = " + map.get("type_id"));
    }

    @Override
    public void onSuccess(JSONObject response, int what) {
        if (what == 1) {
            List<String> nets = JsonUtil.getBzInfoList(response);
            if (nets.size() > 0) {
                Constant.clearStatus();
                Constant.addAllStatus(nets);
                checkBzList(CPQApplication.getDB(), Constant.getBzStatus());
            }
            initHongbao();
            CPQApplication.setLogined(true);
            handler.sendEmptyMessageDelayed(1, 2000);
        } else {
            getBzInfo();
        }
    }

    public void initHongbao() {
        CPQApplication.bzList = new ArrayList<>();
        for (int i = 0; i < Constant.getBzStatusSize(); i++) {
            CPQApplication.bzList.add(new BzList(i, new ArrayList<Stock>()));
        }
    }

    public void checkBzList(SQLiteDatabase db, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            BZListStatusDao bzListStatusDao = new BZListStatusDao(db);
            BZListStatus bzListStatus = new BZListStatus();
            bzListStatus.setBeizhu(i);
            bzListStatus.setIsOpen(true);
            bzListStatus.setFlag(i);
            bzListStatus.setName(list.get(i));
            if (bzListStatusDao.isExits(bzListStatus)) {
                continue;
            }
            bzListStatusDao.add(bzListStatus);
        }
    }

    @Override
    public void onErrorResponse(String error, int what) {
        CPQApplication.setLogined(false);
        dialogUtil.setErrorMessage(error, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    @Override
    public void onFailed(String error, int what) {
        CPQApplication.setLogined(false);
        if (what == 0) {
            dialogUtil.setMessage("提示", error, "退出", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogUtil.dismiss();
//                    startActivity(new Intent(MainActivity.this,
//                            HomepageActivity.class));
                    finish();
                }
            }, "前往注册", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogUtil.dismiss();
                    startActivity(new Intent(MainActivity.this,
                            RegisteActivity.class));
                    finish();
                }
            });
        } else {
            dialogUtil.setErrorMessage(error);
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = (MainActivity) mActivity.get();
            switch (msg.what) {
                case 1:
                    // CPQApplication.setStocks();
                    theActivity.startActivity(new Intent(theActivity,
                            HomepageActivity.class));
                    theActivity.finish();
                    break;
                case 2:
                    theActivity.textView.setText("正在修复本地数据...");
                    theActivity.progressBar.setMax(msg.arg1);
                    theActivity.progressBar.setProgress(msg.arg2);
                    theActivity.progressBar.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }
}
