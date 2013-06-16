package com.example.classbp;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewUtils {
	private ViewUtils() {
    }
	

    public static void setViewWidths(View view, View[] views) {
        int w = view.getWidth();
        int h = view.getHeight();
        for (int i = 0; i < views.length; i++) {
            View v = views[i];
            v.layout((i + 1) * w, 0, (i + 2) * w, h);
            printView("view[" + i + "]", v);
        }
}


	private static void printView(String msg, View v) {
		// print stuff out
		System.out.println(msg + "=" + v);
        if (null == v) {
            return;
        }
        System.out.print("[" + v.getLeft());
        System.out.print(", " + v.getTop());
        System.out.print(", w=" + v.getWidth());
        System.out.println(", h=" + v.getHeight() + "]");
        System.out.println("mw=" + v.getMeasuredWidth() + ", mh=" + v.getMeasuredHeight());
        System.out.println("scroll [" + v.getScrollX() + "," + v.getScrollY() + "]");
		
	}
	
	public static void initListView(Context context, ListView listView, String prefix, int numItems, int layout) {
        // By using setAdpater method in listview we an add string array in list.
        String[] arr = new String[numItems];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = prefix + (i + 1);
        }
        listView.setAdapter(new ArrayAdapter<String>(context, layout, arr));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                String msg = "item[" + position + "]=" + parent.getItemAtPosition(position);
                Toast.makeText(context, msg, 1000).show();
                System.out.println(msg);
            }
        });
    }
	
	public static void menuListView(Context context, ListView listView, String prefix, int numItems, int layout) {
		
		 String[] arr = new String[10];
	        
	            arr[0] = "Profile";
	            arr[1] ="News Feed";
				arr[2]= "Messaeges";
				arr[3]= "Friends";
				arr[4]= "Courses";
				arr[5]= "Major";
				arr[6]= "Uploads";
				arr[7]= "Skills";
				arr[8]= "Jobs";
				arr[9]= "Settings";
	       
	        listView.setAdapter(new ArrayAdapter<String>(context, layout, arr));
	        listView.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                Context context = view.getContext();
	                String msg = "item[" + position + "]=" + parent.getItemAtPosition(position);
	                Toast.makeText(context, msg, 1000).show();
	                System.out.println(msg);
	            }
	        });
		
		
	}

}