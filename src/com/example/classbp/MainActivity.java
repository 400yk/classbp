package com.example.classbp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classbp.MyHorizontalScrollView.SizeCallback;
//import android.view.Menu;
//import android.view.Menu;

public class MainActivity extends Activity {
	
	  MyHorizontalScrollView scrollView;
	    static View menu;
	    View app;
	    View app_new; 
	    ImageView btnSlide;
	    Bitmap profile_photo_image;
	    String user_name;
	    String user_id;
	    ArrayList<String> majors;
	    ArrayList<Integer> majors_id;
	    String courses[][][]; // 1d: no. in the semester; 2d: semester; 3d: info
	    boolean menuOut = false;
	    Handler handler = new Handler();
	    int btnWidth;
	    //DefaultHttpClient client;
	    JsonReader profileReader;
	    JSONObject json;
	    JSONArray json_major;
	    JSONArray json_course;
	    RelativeLayout profile;
	    boolean loggedIn = true;
	    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final LayoutInflater inflater = LayoutInflater.from(this);
		if(loggedIn == true) {
	        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.activity_main, null);
	        
			setContentView(scrollView);
			
			// Server connection setup
			//client = new DefaultHttpClient();
			profileReader = new JsonReader();
			user_id = "1";
			
			new Reads().execute(user_id); // 1 is the userID
			
			
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
				
					app_new = inflater.inflate(R.layout.view_profile, null);
					ViewGroup tabBar = (ViewGroup) app_new.findViewById(R.id.tabBar);
					ImageView btnSlide = (ImageView) tabBar.findViewById(R.id.BtnSlide);
					
					
					if (profile_photo_image != null) {
						ImageView profile_photo = (ImageView) app_new.findViewById(R.id.profile_photo);
						profile_photo.setImageBitmap(profile_photo_image);
					}
					
					if (user_name != null) {
						TextView user_name_view = (TextView) app_new.findViewById(R.id.user_name_view);
						user_name_view.setText(user_name);
					}
					
					if (majors != null) {
						TextView majors_view = (TextView) app_new.findViewById(R.id.majors_view);
						
						String listString = "";
						for (String s : majors)
						{
						    listString += s + ", ";
						}
						listString = listString.substring(0, listString.length() - 2);
						Toast.makeText(getApplicationContext(), listString, Toast.LENGTH_LONG).show();
						majors_view.setText(listString);
					}
					
					createCourseSchedule(app_new);
					
					btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));
					final View[] children = new View[] { menu, app_new };
					scrollView.changeViews(app, children, 1, new SizeCallbackForMenu(btnSlide));
					
					// Get JSON object from server
				}
			});
		
		
	 	} else {    	
	 		EditText editEmail;  	
		    setContentView(R.layout.login_page);
		    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		         
	 	}
	}

	public class Reads extends AsyncTask<String, Integer, String> {
		
		@Override
		protected String doInBackground(String...params) {
			try {
				json = JsonReader.readJsonFromUrl("http://mobile.classblueprint.com/user/user_profile/" + params[0]);
				return params[0];
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
				String photo_url_str = "http://classblueprint.com/public/uploads/members_pic/" + json.get("profile_picture").toString();
				user_name = json.get("username").toString();
				new loadImage().execute(photo_url_str);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public class Reads_major extends AsyncTask<String, Integer, String> {	
		@Override
		protected String doInBackground(String...params) {
			try {
				json_major = JsonReader.readJsonArrayFromUrl("http://mobile.classblueprint.com/user/user_majors/" + params[0]);
				//Toast.makeText(getApplicationContext(), json_major.toString(), Toast.LENGTH_LONG).show();
				//json_major = new JSONArray(jsonString);				
				return params[0];
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
				if (json_major != null) {
					// For majors
					for(int index = 0; index < json_major.length(); index++) {
					    JSONObject jsonObject = json_major.getJSONObject(index);
					    if (majors == null) {
					    	majors = new ArrayList<String>(); 
					    }
					    majors.add(jsonObject.getString("name"));
					    
					    if (majors_id == null) {
					    	majors_id = new ArrayList<Integer>();
					    }
					    majors_id.add(Integer.parseInt(jsonObject.getString("index")));
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public class loadImage extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String...params) {			 
			try {
				URL newurl = new URL(params[0]);
				profile_photo_image = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
				return "Successfully loaded the profile image.";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			//Toast.makeText(getApplicationContext(), "Image loaded", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
			new Reads_major().execute(user_id); 
		}
	}

	private void createCourseSchedule(View view) {
		final int row = 5; // total number of textviews to add
		final int col = 12;
		final int info_count = 3;
		
		courses = new String[row][col][info_count];

		final TextView[] myTextViews = new TextView[row * col]; // create an empty array;
	    GridLayout course_schedule = (GridLayout) view.findViewById(R.id.course_schedule_layout);
	    
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				
			    // create a new textview
			    final TextView rowTextView = new TextView(this);
	
			    // set some properties of rowTextView or something
			    String string_id = "semester_" + Integer.toString(j) + "_course_" + Integer.toString(i);
			    rowTextView.setId(j * 10 + i); // encoding: semester as first one/two digits, then course number as the last digit
	
			    // add the textview to the linearlayout
			    course_schedule.addView(rowTextView);
	
			    // save a reference to the textview for later
			    myTextViews[i] = rowTextView;
			}
		}
		
		new getCoursesFromServer().execute("need to be changed");
	}

	public class getCoursesFromServer extends AsyncTask<String, Integer, String> {	
		@Override
		protected String doInBackground(String...params) {
			try {
				json_course = JsonReader.readJsonArrayFromUrl("http://mobile.classblueprint.com/user/blueprint_courses/" + params[0]);			
				return params[0];
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
				if (json_course != null) {
					// For majors
					for(int index = 0; index < json_course.length(); index++) {
					    JSONObject jsonObject = json_course.getJSONObject(index);
					    if (majors == null) {
					    	majors = new ArrayList<String>(); 
					    }
					    majors.add(jsonObject.getString("name"));
					    
					    if (majors_id == null) {
					    	majors_id = new ArrayList<Integer>();
					    }
					    majors_id.add(Integer.parseInt(jsonObject.getString("index")));
					}
				}
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
                //Toast.makeText(context1, msg1, 2000).show();
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
               // Toast.makeText(context, msg, 1000).show();
                
            }
        }
    }
    
    
}
