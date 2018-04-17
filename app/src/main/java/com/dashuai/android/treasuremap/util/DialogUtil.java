package com.dashuai.android.treasuremap.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class DialogUtil {
    public static List<DialogUtil> dialogs = new ArrayList<DialogUtil>();
    private AlertDialog dialog;
    private Context context;
    private boolean isAll;// 是否全局性

    public DialogUtil(Context context) {
        this.context = context;
    }

    public DialogUtil(Context context, boolean isAll) {
        this.context = context;
        this.isAll = isAll;
    }

    public void setProgress() {
        setProgress("正在加载...");
    }

    public void setProgress(String message) {
        dismiss();
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        show();
    }

    public void setErrorMessage(String error) {
        setMessage("提示：", error, null, null, null, null);
    }

    public void setErrorMessage(String error, OnClickListener onClickListener) {
        setMessage("提示：", error, null, onClickListener, null, null);
    }

    public void setErrorMessage(String title, String error,
                                OnClickListener onClickListener) {
        setMessage(null == title ? "提示：" : title, error, null, onClickListener,
                null, null);
    }

    public void setMessage(String title, String message, String okStr,
                           OnClickListener onClickListener, String cancel,
                           OnClickListener cancelClickListener) {
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, null == okStr ? "确定"
                : okStr, null == onClickListener ? new FinishListener()
                : onClickListener);
        if (null != cancel) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    null == cancel ? "取消" : cancel,
                    null == cancelClickListener ? new FinishListener()
                            : cancelClickListener);
        }
        show();
    }

    public void setThreeBtnMessage(String title, String message, String okStr,
                                   OnClickListener onClickListener, String miStr,
                                   OnClickListener miClickListener, String cancel,
                                   OnClickListener cancelClickListener) {
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, null == okStr ? "确定"
                : okStr, null == onClickListener ? new FinishListener()
                : onClickListener);
        if (null != miStr) {
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, miStr, null == miClickListener ? new FinishListener()
                    : miClickListener);
        }
        if (null != cancel) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel,
                    null == cancelClickListener ? new FinishListener()
                            : cancelClickListener);
        }
        show();
    }

    public void showKeyboard(EditText editText) {
        if (editText != null) {
            // 设置可获得焦点
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            // 请求获得焦点
            editText.requestFocus();
            // 调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) editText
                    .getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }
    }

    public void show() {
        dialogs.add(this);
        if (isAll) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            if (null != dialog) {
                dialog.show();
            }
        } else {
            if (null != dialog && !dialog.isShowing()
                    && !((Activity) context).isFinishing()) {
                dialog.show();
            }
        }
    }

    public boolean isShowing() {
        return null != dialog && dialog.isShowing();
    }

    public void dismiss() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void dismiss(boolean isClear) {
        if (isClear) {
            for (int i = 0; i < dialogs.size(); i++) {
                DialogUtil dialogUtil = dialogs.get(i);
                dialogUtil.dismiss();
            }
            dialogs.clear();
        } else {
            dismiss();
        }
    }

    private class FinishListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dismiss();
        }
    }
}
