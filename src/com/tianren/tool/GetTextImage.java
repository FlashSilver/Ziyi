package com.tianren.tool;

import com.tianren.ziyi.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

	public class GetTextImage extends View  
	{  
	    private float x = 20, y = 40;  
	    private static float windowWidth;  
	    private static float windowHeight;  
	    private static float left = 0;      //ͼƬ����Ļ��λ��X����  
	    private static float top = 0;       //ͼƬ����Ļ��λ��Y����  
	    private String str = "�Ұ���";  
	    private DisplayMetrics dm = new DisplayMetrics();  //���ڻ�ȡ��Ļ�ĸ߶ȺͿ��  
	    private WindowManager windowManager;  
	    private Bitmap newbitmap;  
	    private String path;
	    
	    public GetTextImage(Context context)  
	    {  
	        super(context);  
	        windowManager = (WindowManager) context  
	                .getSystemService(Context.WINDOW_SERVICE);  
	        //��Ļ�Ŀ��  
	        windowWidth = windowManager.getDefaultDisplay().getWidth();  
	        //��Ļ�ĸ߶�  
	        windowHeight = windowManager.getDefaultDisplay().getHeight();  
	    }  
	  
	    public void onDraw(Canvas canvas)  
	    {  
	        Resources res = getResources();  
	        Bitmap bmp = BitmapFactory.decodeFile(path); 
	        newbitmap = getTextImage(bmp, str, x, y);  
	        canvas.drawBitmap(newbitmap, 0, 0, null);  
	    }  
	    /** 
	     * ����ֵ: Bitmap ������ԭͼƬ,���� ����: ���ݸ���������������ӦͼƬ 
	     *  
	     * @param originalMap  
	     * @param text  ���� 
	     * @param x  �����X���� 
	     * @param y  �����Y���� 
	     * @return 
	     */  
	    public static Bitmap getTextImage(Bitmap originalMap, String text, float x,  
	            float y)  
	    {  
	        float bitmapWidth = originalMap.getWidth();  
	        float bitmapHeight = originalMap.getHeight();  
	        // ���廭��  
	        Canvas canvas = new Canvas(originalMap);  
	        // ���廭��  
	        Paint paint = new Paint();  
	        //����ı��ĳ��ȣ����أ�  
	        float textWidth = paint.measureText(text);   
	        canvas.drawBitmap(originalMap, 0, 0, null);  
	          
	        // ���ͼƬ���С����Ļ���  
	        if (left + bitmapWidth < windowWidth)  
	        {  
	            // �ұ߽�  
	            if (x >= left + bitmapWidth - textWidth)  
	            {  
	                x = left + bitmapWidth - textWidth;  
	            }  
	            // ��߽�  
	            else if (x <= left)  
	            {  
	                x = left;  
	            }  
	        }  
	        else  
	        {  
	            // �ұ߽�  
	            if (x >= windowWidth - textWidth)  
	            {  
	                x = windowWidth - textWidth;  
	            }  
	            // ��߽�  
	            else if (x <= 0)  
	            {  
	                x = 0;  
	            }  
	        }  
	        // ���ͼƬ�߶�С����Ļ�߶�  
	        if (top + bitmapHeight < windowHeight)  
	        {  
	            // ��  
	            if (y >= top + bitmapHeight)  
	            {  
	                y = top + bitmapHeight;  
	            }  
	            // ��  
	            else if (y <= top + 10)  
	            {  
	                y = top + 10;  
	            }  
	        }  
	        else  
	        {  
	            if (y >= windowHeight)  
	            {  
	                y = windowHeight;  
	            }  
	            else if (y <= 0)  
	            {  
	                y = 0;  
	            }  
	        }  
	          
	        // �����  
	        canvas.drawText(text, x, y, paint);  
	        return originalMap;  
	    }  
	    public boolean onTouchEvent(MotionEvent event)  
	    {  
	        if (event.getAction() == MotionEvent.ACTION_DOWN)  
	        {  
	            x = event.getX();  
	            y = event.getY();  
	            // �ػ�  
	            invalidate();  
	        }  
	        return true;  
	    }  
	}  

