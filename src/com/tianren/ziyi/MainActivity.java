package com.tianren.ziyi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;

import com.alibaba.fastjson.JSON;
import com.tianren.adapter.PhotoAdapter;
import com.tianren.bean.PhotoBean;
import com.tianren.tool.ACache;
import com.tianren.tool.FileUtils;
import com.tianren.tool.PreferenceUtils;
import com.tianren.tool.TimeUtil;
import com.tianren.tool.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends Activity {
	private Button send;
	private String picturepath;
	private ListView photolist;
	List<PhotoBean> photo = new ArrayList<PhotoBean>();
	private PhotoBean photobean;
	private PhotoAdapter photoadapter;
	private static final int INTENT_ACTION_PICTURE = 0;
	// private String newpath = FileUtils.fileUtils.getStorageDirectory()
	// + "/photo/";
	private File newfolder = Environment.getExternalStorageDirectory();
	private List<String> pathgroup = new ArrayList<String>();
	ACache mcache;
	String[] paths = {};
	private JSONArray jsonarray;
	private org.json.JSONArray ja;
	private JSON json;
	private String jsonparse;
	private static final int CHOOSE_PIC = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		photolist = (ListView) findViewById(R.id.photolist);
		File sd = Environment.getExternalStorageDirectory();
		String path = sd.getPath() + "/ziyi";
		File file = new File(path);
		mcache = ACache.get(this);
		// Toast.makeText(getApplicationContext(),
		// PreferenceUtils.getPrefString(getApplicationContext(), "photo",
		// ""),1).show();
		if(PreferenceUtils.getPrefString(
				getApplicationContext(), "photo", "")!=""){
		photo = JSON.parseArray(PreferenceUtils.getPrefString(
				getApplicationContext(), "photo", ""), PhotoBean.class);
		photoadapter = new PhotoAdapter(getApplicationContext(), photo);
		photolist.setAdapter(photoadapter);
		}
		if (!file.exists())
			file.mkdir();

		send = (Button) findViewById(R.id.send);

		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				if(TimeUtil.isSameDayOfMillis(PreferenceUtils.getPrefLong(
//						getApplicationContext(), "createtime", 0), System
//						.currentTimeMillis())==true){
//					Toast.makeText(getApplicationContext(), "娘亲，每天仅限一张哦", 1).show();
//				}
//				else{
					getPicture();
			        

//				}
			}
		});

		photolist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Log.v("my", "fdfdfdfdonItemClick clicked" + position);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setMessage("确认删除这张照片？");
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								photo.remove(position);
								jsonparse = JSON.toJSONString(photo);
								PreferenceUtils.setPrefString(
										getApplicationContext(), "photo",
										jsonparse);
								photoadapter.notifyDataSetChanged();
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});

				builder.create().show();
				return false;
			}

		});

	}

	private void getPicture() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, INTENT_ACTION_PICTURE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int a = 0;
		if (requestCode == INTENT_ACTION_PICTURE
				&& resultCode == Activity.RESULT_OK && null != data) {
			Cursor c = getContentResolver().query(data.getData(), null, null,
					null, null);
			c.moveToNext();
			Uri uri=data.getData();
			
			String path = c.getString(c
					.getColumnIndex(MediaStore.MediaColumns.DATA));
			c.close();
			System.out.println("onActivityResult == " + path);
			// Toast.makeText(getApplicationContext(), path, 1).show();
			// photobean.setPicturepath(path);
			Log.v("sssssssssssss", path);
			pathgroup.add(newfolder.toString() + "/ziyi/" + getFileName(path)
					+ ".jpg");

			copy(path, newfolder.toString() + "/ziyi/" + getFileName(path)
					+ ".jpg");
			long createtime = System.currentTimeMillis();
			photo.add(new PhotoBean(Utils.timeAccount(createtime), path));
			photoadapter = new PhotoAdapter(getApplicationContext(), photo);
			photolist.setAdapter(photoadapter);

			// Toast.makeText(getApplicationContext(), pathgroup.toString(), 1)
			// .show();
			jsonparse = JSON.toJSONString(photo);
			PreferenceUtils.setPrefString(getApplicationContext(), "photo",
					jsonparse);
			PreferenceUtils.setSettingLong(getApplicationContext(),
					"createtime", createtime);
			if (new File(path).exists()) {
				System.out.println("onActivityResult == " + path + " == exist");

			}
		}
	}

	private boolean copy(String fileFrom, String fileTo) {
		try {
			FileInputStream in = new java.io.FileInputStream(fileFrom);
			FileOutputStream out = new FileOutputStream(fileTo);
			byte[] bt = new byte[1024];
			int count;
			while ((count = in.read(bt)) > 0) {
				out.write(bt, 0, count);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	public String getFileName(String pathandname) {

		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return null;
		}

	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}
	
	

}
