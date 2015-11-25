package com.tianren.tool;

import java.util.Timer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

public class MyThread implements Runnable{
	final int EVENT_PLAY_OVER = 0x100;
	
	byte []data;
	Handler mHandler;
	
	public MyThread(byte []data, Handler handler) {
		// TODO Auto-generated constructor stub
		this.data = data;
		mHandler = handler;
	}
	
	public void run() {
		Log.i("MyThread", "run..");
		
		if (data == null || data.length == 0){
			return ;
		}

		// MyAudioTrack:   对AudioTrack进行简单封装的类
		MyAudioTrack myAudioTrack = new MyAudioTrack(8000, 
									AudioFormat.CHANNEL_CONFIGURATION_STEREO, 
									AudioFormat.ENCODING_PCM_16BIT);
		
		myAudioTrack.init();
		
		int playSize = myAudioTrack.getPrimePlaySize();
		
		Log.i("MyThread", "total data size = " + data.length + ", playSize = " + playSize);
		
		int index = 0;
		int offset = 0;
		while(true){
			try {
				Thread.sleep(0);//便于中断
				
				offset = index * playSize;
				
			    if (offset >= data.length){
                    break;
                }
                                
				
				// 这里是真正播放音频数据的地方
				myAudioTrack.playAudioTrack(data, offset, playSize);
					
			} catch (Exception e) {
				// TODO: handle exception
				break;
			}
			
			index++;
			// if (index >= data.length){
			// 	break;
			// }
		}
		
		myAudioTrack.release();
	
		Message msg = Message.obtain(mHandler, EVENT_PLAY_OVER);
	}
}