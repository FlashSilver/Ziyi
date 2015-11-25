package com.tianren.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tianren.bean.PhotoBean;
import com.tianren.ziyi.BigImage;
import com.tianren.ziyi.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.media.Image;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhotoAdapter extends BaseAdapter {
	private Context context;
	private List<PhotoBean> photo;

	public PhotoAdapter(Context context, List<PhotoBean> photo) {
		super();
		this.context = context;
		this.photo = photo;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return photo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return photo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout relaytivelayout;
		if(convertView==null){
			relaytivelayout=(RelativeLayout) View.inflate(context,R.layout.photo_item, null);			
		}
		else{
			relaytivelayout=(RelativeLayout) convertView;
		}
		//View view = super.getView(position, convertView, parent);
		final PhotoBean photobean=(PhotoBean) getItem(position);
		TextView createtime=(TextView) relaytivelayout.findViewById(R.id.time);
		ImageView icon = (ImageView) relaytivelayout.findViewById(R.id.icon);
//		File file=new File(photobean.getPicturepath());
//		Bitmap bm = BitmapFactory.decodeFile(photobean.getPicturepath());
		createtime.setText(photobean.getCreatetime());
		ImageLoader.getInstance().displayImage(photobean.getPicturepath(), icon);
//		//lazyImageHelper.loadImage(avater, questionbean.getPresenter().getAvatar(), false);
//		icon.setImageBitmap(bm);
	//	icon.setImageBitmap(compressImageFromFile(photobean.getPicturepath()));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(photobean.getPicturepath(), options);
		icon.setImageBitmap(bm);
		icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(context,BigImage.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("imagepath", photobean.getPicturepath());
				context.startActivity(intent);
			}
		});
		
		return relaytivelayout;
	}
	public List<PhotoBean> getList() {
		return photo;
	}
	public void setList(List<PhotoBean> photobean) {
		this.photo = photo;
	}

	private Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;//只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置采样率
		
		newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
		
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//		return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
									//其实是无效的,大家尽管尝试
		return bitmap;
	}
	

}
