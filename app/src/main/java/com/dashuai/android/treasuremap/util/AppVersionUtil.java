package com.dashuai.android.treasuremap.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.dashuai.android.treasuremap.CPQApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AppVersionUtil {
	private final int DOWN_ERROR = -1;
	private final int UPDATA_CLIENT = 0;
	private final int GET_UNDATAINFO_ERROR = 1;
	private final int DOWNLOAD = 2;
	private final int DOWNLOAD_FINISH = 3;
	private final int IS_LATEST = 4;
	private Context context;
	private UpdataInfo updataInfo;
	private int progress;
	private String apkPath;
	private String url;
	private boolean isHand;

	public AppVersionUtil(Context context, String apkPath, String url) {
		this.context = context;
		this.apkPath = apkPath;
		this.url = url;
	}

	public void checkVersion(boolean isHand) {
		this.isHand = isHand;
		getVersionFromServer();
	}

	/*
	 * 获取当前程序的版本号
	 */
	private int getVersionCode() throws NameNotFoundException {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionCode;
	}

	/**
	 * 是否有更新
	 * 
	 * @param serverVersion
	 * @return
	 * @throws NameNotFoundException
	 */
	private boolean isCanUpdate(int serverVersion) {
		try {
			return getVersionCode() < serverVersion ? true : false;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取服务器版本号
	 */
	private void getVersionFromServer() {
		RequestQueue mQueue = Volley.newRequestQueue(context);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type_id", CPQApplication.getID_KEY());
		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, url, jsonObject,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							updataInfo = getServerVersion(response);
							if (isCanUpdate(updataInfo.getV_num())) {
								handler.sendEmptyMessage(UPDATA_CLIENT);
							} else {
								handler.sendEmptyMessage(IS_LATEST);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
					}
				}) {
		};
		mQueue.add(jsonRequest);
	}

	private UpdataInfo getServerVersion(JSONObject response)
			throws JSONException {
		UpdataInfo updataInfo = new UpdataInfo();
		JSONObject retRes = response.getJSONObject("retRes");
		updataInfo.setV_num(retRes.getInt("v_num"));
		updataInfo.setV_title(retRes.getString("v_title"));
		updataInfo.setHttp_url(retRes.getString("http_url"));
		return updataInfo;

	}

	// public File getFileFromServer(String path, ProgressDialog pd)
	// throws Exception {
	// // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
	// if (Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED)) {
	// URL url = new URL(path);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// conn.setConnectTimeout(5000);
	// // 获取到文件的大小
	// pd.setMax(conn.getContentLength());
	// InputStream is = conn.getInputStream();
	// File file = new File(Constant.APK_PATH);
	// // 判断文件目录是否存在
	// if (!file.exists()) {
	// file.mkdir();
	// }
	// File apkFile = new File(Constant.APK_PATH, fileName);
	// FileOutputStream fos = new FileOutputStream(apkFile);
	// BufferedInputStream bis = new BufferedInputStream(is);
	// byte[] buffer = new byte[1024];
	// int len;
	// int total = 0;
	// while ((len = bis.read(buffer)) != -1) {
	// fos.write(buffer, 0, len);
	// total += len;
	// // 获取当前下载量
	// pd.setProgress(total);
	// }
	// fos.close();
	// bis.close();
	// is.close();
	// return apkFile;
	// } else {
	// return null;
	// }
	// }

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_CLIENT:
				// 对话框通知用户升级程序
				showUpdataDialog("有新版本，要更新到最新版本吗？", false);
				break;
			case IS_LATEST:
				// 对话框通知用户升级程序
				if (isHand) {
					showUpdataDialog("当前已经是最新版本了！", true);
				}
				break;
			case GET_UNDATAINFO_ERROR:
				if (isHand) {
					// 服务器超时
					showUpdataDialog("服务器超时,获取更新信息失败", true);
				}
				break;
			case DOWN_ERROR:
				if (isHand) {
					// 下载apk失败
					showUpdataDialog("下载新版本失败", true);
				}
				break;
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				mProgress.dismiss();
				// 安装文件
				installApk();
				break;
			}
		}
	};

	/**
	 * 弹出对话框通知用户是否需要更新
	 * 
	 * @param isLatest
	 *            是否是最新的
	 */
	protected void showUpdataDialog(String text, final boolean isLatest) {
		AlertDialog.Builder builer = new Builder(context);
		builer.setTitle("版本检查");
		builer.setMessage(text);
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (isLatest) {
					return;
				}
				downLoadApk();
			}
		});
		if (!isLatest) {
			// 当点取消按钮时进行登录
			builer.setNegativeButton("取消", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					loginMain();
				}
			});
		}
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		showDownloadDialog();
		new downloadApkThread().start();
	}

	private ProgressDialog mProgress; // 进度条对话框

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		mProgress = new ProgressDialog(context);
		mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgress.setMessage("正在下载更新");
		mProgress.show();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 * @date 2012-4-26
	 * @blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					URL url = new URL(updataInfo.getHttp_url());
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File apkFile = new File(apkPath);
					// 判断文件目录是否存在
					if (!apkFile.getParentFile().exists()) {
						apkFile.mkdirs();
					}
					if (!apkFile.exists()) {
						apkFile.createNewFile();
					} else {
						apkFile.delete();
						apkFile.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					int len = 0;
					// 写入到文件中
					while ((len = is.read(buf)) != -1) {
						count += len;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						handler.sendEmptyMessage(DOWNLOAD);
						// 写入文件
						fos.write(buf, 0, len);
					}
					// 下载完成
					handler.sendEmptyMessage(DOWNLOAD_FINISH);
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(apkPath);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
	}

	private void loginMain() {

	}

	public class UpdataInfo {
		private int v_num;
		private String http_url;
		private String v_title;

		public int getV_num() {
			return v_num;
		}

		public void setV_num(int v_num) {
			this.v_num = v_num;
		}

		public String getHttp_url() {
			return http_url;
		}

		public void setHttp_url(String http_url) {
			this.http_url = http_url;
		}

		public String getV_title() {
			return v_title;
		}

		public void setV_title(String v_title) {
			this.v_title = v_title;
		}

		@Override
		public String toString() {
			return "UpdataInfo [v_num=" + v_num + ", http_url=" + http_url
					+ ", v_title=" + v_title + "]";
		}

	}

}
