package com.tianren.ziyi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import com.tianren.tool.GetTextImage;
import com.tianren.tool.IatSettings;
import com.tianren.tool.JsonParser;
import com.tianren.tool.MosaicView;
import com.tianren.tool.MosaicView.Effect;
import com.tianren.tool.MyThread;
import com.tianren.tool.PreferenceUtils;
import com.tianren.ziyi.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BigImage extends Activity {
	Thread mThread = null;
	byte[] data = null;
	Handler mHandler;

	private ImageView largeimage;
	private String imagepath;
	ViewGroup _root;
	private int _xDelta;
	private int _yDelta;
	private Button shuohua;
	private Button xinqing;
	private Button save;

	private TextView mood;

	private StringBuffer resultBuffer;
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	private SpeechRecognizer mIat;
	private SharedPreferences mSharedPreferences;
	private RecognizerDialog mIatDialog;
	int ret = 0; // 锟斤拷锟斤拷锟斤拷梅锟斤拷锟�
	private String filename = System.currentTimeMillis() + ".pcm";
	private Dialog dialog;
	private String path;
	private Boolean a = true;
	private String mark;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bigimage);
		File sd = Environment.getExternalStorageDirectory();
		path = sd.getPath() + "/ziyi/voice";
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
		largeimage = (ImageView) findViewById(R.id.largeimage);
		shuohua = (Button) findViewById(R.id.shuohua);
		xinqing = (Button) findViewById(R.id.xinqing);
		mood = (TextView) findViewById(R.id.mood);
		mood.setMovementMethod(ScrollingMovementMethod.getInstance());
		// mvImage = (MosaicView) findViewById(R.id.iv_content);
		imagepath = getIntent().getStringExtra("imagepath");
		mark=getFileName(imagepath);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(imagepath, options);
		largeimage.setImageBitmap(bm);
//		Toast.makeText(getApplicationContext(), PreferenceUtils.getPrefString(getApplicationContext(),
//		imagepath, ""),1).show();
		if (PreferenceUtils
				.getPrefString(getApplicationContext(), mark, "") != "") {
			mood.setText(PreferenceUtils.getPrefString(getApplicationContext(),
					mark, ""));
//			Toast.makeText(getApplicationContext(), PreferenceUtils.getPrefString(getApplicationContext(),
//					filename, ""), 1).show();
}
		xinqing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (a) {
					a = false;
					mood.setVisibility(View.VISIBLE);
				} else {
					a = true;
					mood.setVisibility(View.GONE);
				}
			}
		});
		play();
//		Toast.makeText(
//				getApplicationContext(),
//				PreferenceUtils.getPrefString(getApplicationContext(),
//						imagepath, ""), 1).show();
		MediaPlayer mp = new MediaPlayer();
		if (PreferenceUtils.getPrefString(getApplicationContext(), imagepath,
				"") != "") {
			try {
//				Toast.makeText(
//						getApplicationContext(),
//						path
//								+ "/"
//								+ PreferenceUtils.getPrefString(
//										getApplicationContext(), imagepath, ""),
//						1).show();
				mp.setDataSource(path
						+ "/"
						+ PreferenceUtils.getPrefString(
								getApplicationContext(), imagepath, ""));
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		shuohua.setOnTouchListener(radioButtonTouchListener);

		SpeechUtility.createUtility(this, "appid=5533725e");
		mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
		mIatDialog = new RecognizerDialog(this, mInitListener);
		mEngineType = SpeechConstant.TYPE_CLOUD;
		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);

	}

	OnTouchListener radioButtonTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				setParam();
				Toast.makeText(getApplicationContext(), "请开始说话", 1).show();

				boolean isShowDialog = mSharedPreferences.getBoolean(
						getString(R.string.pref_key_iat_show), true);
				if (!isShowDialog) {
					mIatDialog.setListener(recognizerDialogListener);
				} else {
					ret = mIat.startListening(recognizerListener);
					if (ret != ErrorCode.SUCCESS) {
					}
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mIat.stopListening();

			}
			return false;
		}
	};

	public void setParam() {
		mIat.setParameter(SpeechConstant.PARAMS, null);
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", "1000"));
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "1"));
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, path + "/" + filename);
		mIat.setParameter(SpeechConstant.ASR_DWA,
				mSharedPreferences.getString("iat_dwa_preference", "0"));
	}

	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.v("TAG", "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
			}
		}
	};
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		public void onError(SpeechError error) {
		}

	};

	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// showTip("锟斤拷始说锟斤拷");
		}

		@Override
		public void onError(SpeechError error) {

		}

		@Override
		public void onEndOfSpeech() {
			// showTip("锟斤拷锟斤拷说锟斤拷");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d("TAG", results.getResultString());
			// Toast.makeText(getApplicationContext(),
			// results.getResultString(), 0).show();
			printResult(results);
			if (isLast) {
				dialog();
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			// showTip("锟斤拷前锟斤拷锟斤拷说锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷小锟斤拷" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(BigImage.this);
		builder.setMessage("没有结果");
		if (resultBuffer != null) {
			builder.setMessage(resultBuffer.toString());
		}
		builder.setTitle("确认存下来？");

		builder.setPositiveButton("是的",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						PreferenceUtils.setPrefString(getApplicationContext(),
								imagepath, filename);
						mood.setText(resultBuffer);
						PreferenceUtils.setPrefString(getApplicationContext(),
								mark, resultBuffer.toString());
						dialog.dismiss();
						Log.v("aaaaaaaa", PreferenceUtils.getPrefString(
								getApplicationContext(), imagepath, ""));

					}
				});
		builder.setNegativeButton("不",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	public byte[] getPCMData() {
		File file = new File(path
				+ "/"
				+ PreferenceUtils.getPrefString(getApplicationContext(),
						imagepath, ""));
		if (file == null) {
			return null;
		}

		FileInputStream inStream;
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		if (inStream == null) {
			Toast.makeText(this, "No inStream...", 200).show();

		}
		byte[] data_pack = null;
		if (inStream != null) {
			long size = file.length();

			data_pack = new byte[(int) size];
			try {
				inStream.read(data_pack);
			} catch (IOException e) {
				// TODO Auto-generated catch block hander
				e.printStackTrace();
				return null;
			}
		}
		return data_pack;
	}

	public void play() {
		data = getPCMData();

		if (data == null) {
			Toast.makeText(this, "您还没有表达您的心情呢", 1).show();
			return;
		}

		if (mThread == null) {
			mThread = new Thread(new MyThread(data, mHandler));
			mThread.start();
		}
	}

	public void stop() {
		if (data == null) {
			return;
		}

		if (mThread != null) {
			mThread.interrupt();
			mThread = null;
		}
	}

	public void onDestroy() {
		stop();

		super.onDestroy();
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
}
