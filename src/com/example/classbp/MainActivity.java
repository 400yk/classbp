package com.example.classbp;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.classbp.MyHorizontalScrollView.SizeCallback;
//import android.view.Menu;
//import android.view.Menu;

public class MainActivity extends Activity {
	
	  MyHorizontalScrollView scrollView;
	    static View menu;
	    View app;
	    ImageView btnSlide;
	    boolean menuOut = false;
	    Handler handler = new Handler();
	    int btnWidth;
	    //DefaultHttpClient client;
	    JsonReader profileReader;
	    JSONObject json;
	    RelativeLayout profile;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final LayoutInflater inflater = LayoutInflater.from(this);
        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.activity_main, null);
        
		setContentView(scrollView);
		
		// Server connection setup
		//client = new DefaultHttpClient();
		profileReader = new JsonReader();
		new Reads().execute("index");
		
		menu = inflater.inflate(R.layout.horz_scroll_menu, null);
        app = inflater.inflate(R.layout.horz_scroll_app, null);
        ViewGroup tabBar = (ViewGroup) app.findViewById(R.id.tabBar);
//app main lists
        ListView listView = (ListView) app.findViewById(R.id.list);
        profile = (RelativeLayout) menu.findViewById(R.id.app_profile);        

		
        ViewUtils.initListView(this, listView, "Item ", 10, android.R.layout.simple_list_item_1);

   //menu list
   /*     listView = (ListView) menu.findViewById(R.id.list);
        ViewUtils.menuListView(this, listView, "Menu ", 9, android.R.layout.simple_list_item_1);
*/
        btnSlide = (ImageView) tabBar.findViewById(R.id.BtnSlide);
        btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));

        final View[] children = new View[] { menu, app };

        // Scroll to app (view[1]) when layout finished.
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnSlide));
        
        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(menu.getWindowToken(),0);
		
		profile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Goto main page
				Toast.makeText(getBaseContext(), "Thumbnail", Toast.LENGTH_LONG).show();
			
				View app_new = inflater.inflate(R.layout.view_profile, null);
				ViewGroup tabBar = (ViewGroup) app_new.findViewById(R.id.tabBar);
				ImageView btnSlide = (ImageView) tabBar.findViewById(R.id.BtnSlide);
				btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));
				final View[] children = new View[] { menu, app_new };
				scrollView.changeViews(app, children, 1, new SizeCallbackForMenu(btnSlide));
				
				// Get JSON object from server
			}
		});
	}

	public class Reads extends AsyncTask<String, Integer, String> {
		
		@Override
		protected String doInBackground(String...params) {
			try {
				json = JsonReader.readJsonFromUrl("http://mobile.classblueprint.com/user/user_profile/1");
				return json.getString(params[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			//httpStuff.setText(result);
			super.onPostExecute(result);
			try {
				Toast.makeText(getApplicationContext(), json.get("username").toString(), Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	/**
     * Helper for examples with a HSV that should be scrolled by a menu View's width.
     */
    static class ClickListenerForScrolling implements OnClickListener {
        HorizontalScrollView scrollView;
        View menu;
        /**
         * Menu must NOT be out/shown to start with.
         */
        boolean menuOut = false;

        public ClickListenerForScrolling(HorizontalScrollView scrollView, View menu) {
            super();
            this.scrollView = scrollView;
            this.menu = menu;
        }

        @Override
        public void onClick(View v) {
           /* Context context = menu.getContext();
            String msg = "Slide " + new Date();
            Toast.makeText(context, msg, 1000).show();
            System.out.println(msg);
            */
            int menuWidth = menu.getMeasuredWidth();

            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);

            if (!menuOut) {
                // Scroll to 0 to reveal menu
                int left = 0;//100;
                scrollView.smoothScrollTo(left, 0);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
                int left = menuWidth;
                scrollView.smoothScrollTo(left, 0);
                
                Context context1 = menu.getContext();
                String msg1 = "width " + menuWidth;
                Toast.makeText(context1, msg1, 2000).show();
                System.out.println(msg1);
            }
            menuOut = !menuOut;
        }
    }

    /**
     * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
     * showing.
     */
    static class SizeCallbackForMenu implements SizeCallback {
        int btnWidth;
        View btnSlide;

        public SizeCallbackForMenu(View btnSlide) {
            super();
            this.btnSlide = btnSlide;
        }

        @Override
        public void onGlobalLayout() {
            btnWidth = btnSlide.getMeasuredWidth();
            System.out.println("btnWidth=" + btnWidth);
        }

        @Override
        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            final int menuIdx = 0;
            if (idx == menuIdx) {
                dims[0] = w- btnWidth-900;
                
                Context context = menu.getContext();
                String msg = "Slide " + btnWidth;
                Toast.makeText(context, msg, 1000).show();
                
            }
        }
    }
    
    
}
