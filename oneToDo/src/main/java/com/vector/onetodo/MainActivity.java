package com.vector.onetodo;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.simonvt.datepicker.DatePicker;
import net.simonvt.datepicker.DatePicker.OnDateChangedListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.astuetz.PagerSlidingTabStrip;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import com.google.gson.Gson;
import com.vector.model.TaskData;
import com.vector.model.TaskData.Todos;
import com.vector.onetodo.db.gen.Label;
import com.vector.onetodo.db.gen.LabelDao;
import com.vector.onetodo.db.gen.LabelNameDao;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Constants;
import com.vector.onetodo.utils.TypeFaces;
import com.vector.onetodo.utils.Utils;

import de.greenrobot.dao.query.QueryBuilder;

public class MainActivity extends BaseActivity implements
		ViewPager.OnPageChangeListener, OnItemClickListener, ConnectionCallbacks, OnConnectionFailedListener {
	public static GoogleApiClient mGoogleApiClient;
	public static final int RC_SIGN_IN = 0;
	public boolean mIntentInProgress;
	public ConnectionResult mConnectionResult;
	public boolean mSignInClicked;
	ProgressDialog progressDialog;
	final List<String> Permissions = Arrays.asList("public_profile", "email",
			"user_likes", "user_status", "offline_access", "read_stream", 
            "publish_stream","create_event","user_events","friends_events",
            "publish_checkins", "friends_checkins","read_friendlists");
	public static SharedPreferences setting;
	Editor setting_editor;
	public static Calendar CurrentDate;
	static int check = -1, check1 = 0;;
	public static int menuchange = 0;
	private PopupWindow popupWindowTask;
	public static RelativeLayout layout_MainMenu;
	private InputMethodManager inputMethodManager;
	private AlertDialog date_time_alert;
	public static List<ToDo> todos;
	public static int pager_number = 0;
	private AlarmManagerBroadcastReceiver alarm;
	static ToDoDao tododao;
	static LabelNameDao labelnamedao;
	static LabelDao labeldao;
	static List<ToDo> todo_obj;
	public ViewPager pager;
	public TabPagerAdapter tabPagerAdapter;
	private PagerSlidingTabStrip tabs;
	public static QueryBuilder<Label> label1, label2, label3, label4, label5,
			label6;
	public static List<Todos> Today, Tomorrow, Upcoming;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private DrawerLayout drawerLayout;

	// ************** Phone COntacts

	String phoneNumber = null;
	Cursor cursor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				////////////////////////////////////////////////////////////////////
		PlusOptions plus = new PlusOptions.Builder().build();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this).addApi(Plus.API, plus)
		.addScope(Plus.SCOPE_PLUS_PROFILE).build();
				//////////////////////////////////////////////////////////////////
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_top);
		if (toolbar != null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setTitle(R.string.close_drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				toolbar, R.string.close_drawer, R.string.open_drawer);
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		// ******* Phone contact , name list
		Constants.Name = new ArrayList<String>();
		Constants.Contact = new ArrayList<String>();
		// new Phone_contact().execute();

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		db_initialize();
		todo_obj = tododao.loadAll();
		alarm = new AlarmManagerBroadcastReceiver();
		startRepeatingTimer();

		// ***** Initializinf Registration shared prefrences**********//
		Constants.user_id = App.prefs.getUserId();

		// **************************Api Call for Landing data
		if (Constants.user_id != -1) {
			aq.ajax("http://api.heuristix.net/one_todo/v1/tasks/"
					+ Constants.user_id, JSONObject.class,
					new AjaxCallback<JSONObject>() {
						@Override
						public void callback(String url, JSONObject json,
								AjaxStatus status) {
							if (json != null) {
								Gson gson = new Gson();
								TaskData obj = new TaskData();
								obj = gson.fromJson(json.toString(),
										TaskData.class);
								TaskData.getInstance().setList(obj);
								Log.v("JSON",
										TaskData.getInstance().todos.get(0).notes
												+ "");
							}
							init();
						}
					});
		} else {
			init();
		}

	}

	Menu menu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.clear();
		getMenuInflater().inflate(R.menu.main, menu);

		this.menu = menu;
		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView search = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
		search.setOnSearchClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setDisplayShowHomeEnabled(true);
				actionBarDrawerToggle.syncState();

			}
		});
		search.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String query) {
				// loadHistory(query);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return false;
			}

		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_notification:
			toggleRightDrawer();
			break;

		default:
			break;
		}
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_gmail) {
			if (!mGoogleApiClient.isConnected())
			{
				g_plus_LogIn();					
			}
			return true;
		}
		if (id == R.id.action_facebook) {
			{
				Fb_Clicked();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void Fb_Clicked() {
		 Session currentSession = Session.getActiveSession();
		    if (currentSession == null || currentSession.getState().isClosed()) {
		        Session session = new Session.Builder(this).build();
		        Session.setActiveSession(session);
		        currentSession = session;
		    }

		    if (currentSession.isOpened()) {
		        // Do whatever u want. User has logged in

		    } else if (!currentSession.isOpened()) {
		        // Ask for username and password
		        OpenRequest op = new Session.OpenRequest(this);

		        op.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
		        op.setCallback(null);
		        op.setPermissions(Permissions);

		        Session session = new Session(MainActivity.this);
		        Session.setActiveSession(session);
		        session.openForPublish(op);
		    }
		}
	public void g_plus_LogIn() {
		if (!mGoogleApiClient.isConnecting()) {
			progressDialog = new ProgressDialog(
					MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Please wait. . .");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
			mSignInClicked = true;
			resolveSignInError();
		}
	}
	private void resolveSignInError() {
		try {
			if (mConnectionResult.hasResolution()) {
				try {
					mIntentInProgress = true;
					mConnectionResult
							.startResolutionForResult(this, RC_SIGN_IN);
				} catch (SendIntentException e) {
					mIntentInProgress = false;
					mGoogleApiClient.connect();
				}
			}
		} catch (Exception e) {
		}
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}
		if (!mIntentInProgress) {
			mConnectionResult = result;
			if (mSignInClicked) {
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.e("Conn", "Conn");
		mSignInClicked = false;	
		new GetGoogleCalendarEvents().execute();
		try{
		progressDialog.dismiss();
		}catch(Exception e){}
	}
	@Override
	public void onConnectionSuspended(int arg0) {
		//mGoogleApiClient.connect();
	}
	
	public String getProfileInformation() {
		String name = null;
		try {			
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				com.google.android.gms.plus.model.people.Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				name=  currentPerson.getId().toString();
				Log.e("Name","//"+name);
			}
			mGoogleApiClient.disconnect();
			mSignInClicked = false;	
			mIntentInProgress = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	class GetGoogleCalendarEvents extends AsyncTask<Void, Void, String> {
		String ok  = getProfileInformation();
		String url = "https://www.googleapis.com/calendar/v3/users/me/calendarList?userId="+ok;
		        String sResponse;

		        // private ProgressDialog dialog;
		        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		        @Override
		        protected void onPreExecute() {
		            dialog.setMessage("Please wait...");
		            dialog.show();
		        }

		        @Override
		        protected String doInBackground(Void... params) {
		            try {
		                HttpClient httpclient = new DefaultHttpClient();
		                HttpGet httppost = new HttpGet(url);		               
		                HttpResponse response = httpclient.execute(httppost);
		                BufferedReader reader = new BufferedReader(
		                        new InputStreamReader(
		                                response.getEntity().getContent(), "UTF-8"));
		                Log.e("Request", "executing request " + httppost.getRequestLine());
		                sResponse = reader.readLine();

		                return sResponse;
		            } catch (Exception e) {
		                // something went wrong. connection with the server error
		            }
		            return null;
		        }

		        @Override
		        protected void onPostExecute(String result) {
		            this.dialog.dismiss();
		            Log.e("result", result);
		            if (this.sResponse != null) {
		                
		            }
		        }
		    }
@Override
protected void onActivityResult(int requestCode, int responseCode,
		Intent intent) {
	 if (Session.getActiveSession() != null)
	        Session.getActiveSession().onActivityResult(this, requestCode, responseCode,intent);

	    Session currentSession = Session.getActiveSession();
	    if (currentSession == null || currentSession.getState().isClosed()) {
	        Session session = new Session.Builder(this).build();
	        Session.setActiveSession(session);
	        currentSession = session;
	    }

	    if (currentSession.isOpened()) {
	        Session.openActiveSession(this, true, new Session.StatusCallback() {

	            @SuppressWarnings("deprecation")
				@Override
	            public void call(final Session session, SessionState state,
	                    Exception exception) {

	                if (session.isOpened()) {

	                    Request.executeMeRequestAsync(session,
	                            new Request.GraphUserCallback() {

									@Override
									public void onCompleted(GraphUser user,
											Response response) {
										// TODO Auto-generated method stub
										 if (user != null) {
											 String fqlQuery = "SELECT eid, name, pic, creator, start_time FROM event WHERE eid IN (SELECT eid FROM event_member WHERE uid='"+user.getId()+"')";       
											 Log.e("Result:" , fqlQuery.toString());      
											 Bundle params = new Bundle();
											    params.putString("q", fqlQuery);
											    Request request = new Request(session, "/fql", params, HttpMethod.GET, 
											            new Request.Callback() {

											                @Override
											                public void onCompleted(Response response) {
											                    Log.e("Result:" ,response.toString());
											                        try {
											                            FileWriter file = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/videocvname");
											                            file.write(response.toString());
											                            file.flush();
											                            file.close();
											                        } catch (IOException e) {
											                            e.printStackTrace();
											                        }
											                }
											            });											    
											    Request.executeBatchAsync(request);
		                                    }
									}
	                            });
	                }
	            }
	        });
	    }
	if (requestCode == RC_SIGN_IN) {
		if (responseCode != RESULT_OK) {
			mSignInClicked = false;
		}
		mIntentInProgress = false;
		if (!mGoogleApiClient.isConnecting()) {
			mGoogleApiClient.connect();
		}
	}
}
	private void toggleRightDrawer() {
		if (drawerLayout.isDrawerVisible(Gravity.RIGHT)) {
			drawerLayout.closeDrawer(Gravity.RIGHT);
			getSupportActionBar().setTitle(R.string.close_drawer);
		} else {
			drawerLayout.openDrawer(Gravity.RIGHT);
			getSupportActionBar().setTitle("Notifications");
		}
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}
	private void init() {

		// ListView notif_list = (ListView) findViewById(R.id.notif_list);
		// Notify_adapter adapter = new Notify_adapter(this);
		// notif_list.setAdapter(adapter);

		// ***** LeftMenudrawer Mange Account feld**********//
		if (App.prefs.getUserId() == -1) {
			aq.id(R.id.manage_img).image(R.drawable.verify_number);
			aq.id(R.id.manage_text).text("Verify number");
		} else {
			aq.id(R.id.manage_img).image(R.drawable.manage_account);
			aq.id(R.id.manage_text).text("Manage Account");
			aq.id(R.id.username).text(App.prefs.getUserName());
			aq.id(R.id.imageView1).text(App.prefs.getInitials());
		}

		aq.id(R.id.manage_account).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (check1 == 0) {
					check1 = 1;
					aq.id(R.id.manage_account)
							.getTextView()
							.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
									R.drawable.ic_up);
					aq.id(R.id.manage).visibility(View.VISIBLE);
				} else {
					check1 = 0;
					aq.id(R.id.manage_account)
							.getTextView()
							.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
									R.drawable.ic_down);
					aq.id(R.id.manage).visibility(View.GONE);
				}

			}
		});

		// ***** Initializinf Today, tomorrow, upcoming and seprate save for
		// each**********//
		Today = new ArrayList<Todos>();
		Tomorrow = new ArrayList<Todos>();
		Upcoming = new ArrayList<Todos>();

		CurrentDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today = null;
		try {
			today = sdf.parse(sdf.format(CurrentDate.getTime()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		CurrentDate.add(Calendar.DATE, 1);
		Date tomorrow = null;
		try {
			tomorrow = sdf.parse(sdf.format(CurrentDate.getTime()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		Date date = null;
		for (int i = 0; i < TaskData.getInstance().todos.size(); i++) {
			if (TaskData.getInstance().todos.get(i).start_date != null) {
				try {
					date = sdf
							.parse(TaskData.getInstance().todos.get(i).start_date);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (date.equals(today)) {
					Log.e("Today", date + "   " + today);
					Today.add((TaskData.getInstance().todos.get(i)));
				} else if (date.equals(tomorrow)) {
					Log.v("Tomorrow", date + "   " + tomorrow);
					Tomorrow.add((TaskData.getInstance().todos.get(i)));
				} else if (date.after(tomorrow)) {
					Log.v("upcoming", date + "   " + tomorrow);
					Upcoming.add((TaskData.getInstance().todos.get(i)));
				}
			}
		}

		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		// ***** left drawer open close**********//
		layout_MainMenu = (RelativeLayout) findViewById(R.id.container);
		// layout_MainMenu.getForeground().setAlpha(0);
		final View view = getLayoutInflater().inflate(R.layout.landing_menu,
				null, false);
		aq_menu = new AQuery(this, view);
		popupWindowTask = new PopupWindow(view, Utils.getDpValue(200, this),
				WindowManager.LayoutParams.WRAP_CONTENT, true);

		popupWindowTask.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.dialog_holo_light_frame));
		popupWindowTask.setOutsideTouchable(true);
		// popupWindowTask.setAnimationStyle(R.style.Animation);

		popupWindowTask.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
			}
		});

		// DATE Dialog
		View dateTimePickerDialog = getLayoutInflater().inflate(
				R.layout.landing_date_dialog, null, false);
		AlertDialog.Builder builder4 = new AlertDialog.Builder(
				MainActivity.this);
		builder4.setView(dateTimePickerDialog);

		date_time_alert = builder4.create();
		aqd = new AQuery(dateTimePickerDialog);
		aqd.id(R.id.cancel).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				date_time_alert.dismiss();
			}
		});

		aqd.id(R.id.ok).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

		DatePicker dialogDatePicker = (DatePicker) dateTimePickerDialog
				.findViewById(R.id.date_picker_dialog);
		Calendar cal = Calendar.getInstance();
		cal.set(Utils.getCurrentYear(1), Utils.getCurrentMonthDigit(1),
				Utils.getCurrentDayDigit(1));
		aqd.id(R.id.title).text(
				cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
						Locale.US)
						+ ", "
						+ Utils.getCurrentDayDigit(1)
						+ " "
						+ cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
								Locale.US) + " " + Utils.getCurrentYear(1));

		dialogDatePicker.init(Utils.getCurrentYear(1),
				Utils.getCurrentMonthDigit(1), Utils.getCurrentDayDigit(1),
				new OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, monthOfYear, dayOfMonth);
						aqd.id(R.id.title).text(
								cal.getDisplayName(Calendar.DAY_OF_WEEK,
										Calendar.SHORT, Locale.US)
										+ ", "
										+ dayOfMonth
										+ " "
										+ cal.getDisplayName(Calendar.MONTH,
												Calendar.SHORT, Locale.US)
										+ " " + year);

					}

				});

		aq_menu.id(R.id.menu_item1).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindowTask.dismiss();
				if (menuchange == 1) {

					Fragment fr = new Invitations();
					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.replace(R.id.container, fr);
					transaction.addToBackStack("invitation");
					transaction.commit();

				}
			}
		});
		aq_menu.id(R.id.menu_item2).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindowTask.dismiss();
			}
		});
		aq_menu.id(R.id.menu_item3).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindowTask.dismiss();
				date_time_alert.show();
			}
		});

		aq.id(R.id.search_button).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				inputMethodManager.toggleSoftInput(
						InputMethodManager.SHOW_FORCED, 0);

				aq.id(R.id.search_layout).getView().setVisibility(View.VISIBLE);
				// aq.id(R.id.header_layout).getView().setVisibility(View.GONE);
				aq.id(R.id.search_text).getEditText().setFocusable(true);
			}
		});

		// Menu Drawer on click change items

		aq.id(R.id.settings).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				getSupportFragmentManager().popBackStack("ACCOUNTS",
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				Fragment fr = new Setting();
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.container, fr);
				transaction.addToBackStack("SETTING");
				transaction.commit();
				drawerLayout.closeDrawer(Gravity.LEFT);

			}
		});

		aq.id(R.id.manage).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// if (Constants.user_id == -1) {
				//
				// } else {
				getSupportFragmentManager().popBackStack("SETTING",
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				Fragment fr = new Accounts();
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.container, fr);
				transaction.addToBackStack("ACCOUNTS");
				transaction.commit();
				drawerLayout.closeDrawer(Gravity.LEFT);
				// }

			}
		});

		aq.id(R.id.todo_layout).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// // TextView title = (TextView) findViewById(R.id.weather);
				// title.setText("To-do's");
				getSupportFragmentManager().popBackStack();

				refreshMenu();
				drawerLayout.closeDrawer(Gravity.LEFT);
				arg0.setBackgroundColor(Color.parseColor("#F2F2F2"));

				aq.id(R.id.todo_image).image(R.drawable.list_blue);
				aq.id(R.id.todo_text).textColor(Color.parseColor("#33B5E5"));

				aq.id(R.id.calendar_layout).getView()
						.setBackgroundColor(Color.parseColor("#ffffff"));
				aq.id(R.id.calendar_image).image(R.drawable.calendar_black);
				aq.id(R.id.calendar_text)
						.textColor(Color.parseColor("#000000"));

				aq.id(R.id.project_layout).getView()
						.setBackgroundColor(Color.parseColor("#ffffff"));
				aq.id(R.id.project_image).image(R.drawable.progress_black);
				aq.id(R.id.project_text).textColor(Color.parseColor("#000000"));
			}
		});

		aq.id(R.id.calendar_layout).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				aq_menu.id(R.id.menu_item1).text("Invitations");
				aq_menu.id(R.id.menu_item2).text("Visible calenders");
				aq_menu.id(R.id.menu_item3).text("Go to")
						.visibility(View.VISIBLE);

				getSupportFragmentManager().popBackStack();
				drawerLayout.closeDrawers();
				menuchange = 1;

				arg0.setBackgroundColor(Color.parseColor("#F2F2F2"));

				aq.id(R.id.todo_image).image(R.drawable.list_black);
				aq.id(R.id.todo_text).textColor(Color.parseColor("#000000"));

				aq.id(R.id.todo_layout).getView()
						.setBackgroundColor(Color.parseColor("#ffffff"));
				aq.id(R.id.calendar_image).image(R.drawable.calendar_blue);
				aq.id(R.id.calendar_text)
						.textColor(Color.parseColor("#33B5E5"));

				aq.id(R.id.project_layout).getView()
						.setBackgroundColor(Color.parseColor("#ffffff"));
				aq.id(R.id.project_image).image(R.drawable.progress_black);
				aq.id(R.id.project_text).textColor(Color.parseColor("#000000"));

				Fragment fr = new Calender();
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction(); //
				/*
				 * transaction.setCustomAnimations(R.anim.slide_in,
				 * R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
				 */
				transaction.replace(R.id.container_inner, fr);
				transaction.addToBackStack("CALENDAR");
				transaction.commit();

			}
		});

		aq.id(R.id.project_layout).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getSupportFragmentManager().popBackStack();
				// TextView title = (TextView) findViewById(R.id.weather);
				// title.setText("Projects");

				drawerLayout.closeDrawer(Gravity.LEFT);
				arg0.setBackgroundColor(Color.parseColor("#F2F2F2"));

				aq.id(R.id.todo_image).image(R.drawable.list_black);
				aq.id(R.id.todo_text).textColor(Color.parseColor("#000000"));

				aq.id(R.id.calendar_layout).getView()
						.setBackgroundColor(Color.parseColor("#ffffff"));
				aq.id(R.id.calendar_image).image(R.drawable.calendar_black);
				aq.id(R.id.calendar_text)
						.textColor(Color.parseColor("#000000"));

				aq.id(R.id.todo_layout).getView()
						.setBackgroundColor(Color.parseColor("#Ffffff"));
				aq.id(R.id.project_image).image(R.drawable.progress_blue);
				aq.id(R.id.project_text).textColor(Color.parseColor("#33B5E5"));

				Fragment fr = new Projects();
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction(); //
				/*
				 * transaction.setCustomAnimations(R.anim.slide_in,
				 * R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
				 */

				transaction.replace(R.id.container_inner, fr);
				transaction.addToBackStack("PROJECTS");
				transaction.commit();

			}
		});

		final LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.gravity = Gravity.CENTER_HORIZONTAL;


		if (pager_number == 0) {
			updateDate(TODAY);
		}
		if (pager_number == 1) {
			updateDate(Work);
		}

		// Initialize the ViewPager and set an adapter
		pager = (ViewPager) findViewById(R.id.pager);
		tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(tabPagerAdapter);

		// Bind the tabs to the ViewPager
		tabs = (PagerSlidingTabStrip) aq.id(R.id.tabs).getView();
		tabs.setShouldExpand(false);
		tabs.setDividerColorResource(android.R.color.transparent);
		// tabs.setIndicatorColorResource(R.color.graytab);
		tabs.setUnderlineColorResource(android.R.color.transparent);
		tabs.setTextSize(Utils.getPxFromDp(this, 13));
		tabs.setIndicatorHeight(Utils.getPxFromDp(this, 3));
		tabs.setIndicatorColor(Color.parseColor("#ffffff"));
		tabs.setSmoothScrollingEnabled(true);
		tabs.setShouldExpand(true);
		// tabs.setTextColorResource(R.color.graytab);
		tabs.setAllCaps(false);
		tabs.setTypeface(null, Typeface.NORMAL);
		tabs.setOnPageChangeListener(this);

		tabs.setViewPager(pager);
		tabPagerAdapter.notifyDataSetChanged();

		aq.id(R.id.add_task_button).typeface(
				TypeFaces.get(this, Constants.ICON_FONT));
		aq.id(R.id.add_task_button).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this, AddTask.class);
				intent.putExtra("position", pager.getCurrentItem());
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in1, R.anim.slide_out1);
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		if (tabPagerAdapter != null) {
			tabPagerAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
			refreshMenu();
		}
	}

	public void refreshMenu() {
		getSupportActionBar().setTitle(R.string.close_drawer);
		menu.clear();
		onCreateOptionsMenu(menu);
	}

	void updateDate(int days) {
		aq.id(R.id.current_date).text(
				String.valueOf(Utils.getCurrentDate(days)));
		aq.id(R.id.day).text(Utils.getCurrentDay(days, Calendar.LONG));
		aq.id(R.id.mon_year).text(
				Utils.getCurrentMonth(days, Calendar.SHORT) + "'"
						+ Utils.getCurrentYear(days));
	}

	public class TabPagerAdapter extends FragmentPagerAdapter {

		private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
		private ScrollTabHolder mListener;

		public TabPagerAdapter(FragmentManager fm) {
			super(fm);
			mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
		}

		public void setTabHolderScrollingContent(ScrollTabHolder listener) {
			mListener = listener;
		}

		@Override
		public int getCount() {
			if (pager_number == 0)
				return pageName.size();
			else if (pager_number == 1)
				return pagename2.size();
			else
				return typeName.size();
			// return 3; // no. of tabs are Today, Tomorrow & Upcoming
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (pager_number == 0)
				return pageName.get(position);
			else if (pager_number == 1)
				return pagename2.get(position);
			else
				return typeName.get(position);
		}

		@Override
		public Fragment getItem(int position) {
			ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) TaskListFragment
					.newInstance(position);

			mScrollTabHolders.put(position, fragment);
			if (mListener != null) {
				fragment.setScrollTabHolder(mListener);
			}
			return fragment;
		}

		public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
			return mScrollTabHolders;
		}

	}

	public void startRepeatingTimer() {
		// Context context = this.getApplicationContext();
		if (alarm != null) {
			// alarm.SetAlarm(this);

		} else {
			Toast.makeText(this, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}

	public void cancelRepeatingTimer(View view) {
		Context context = this.getApplicationContext();
		if (alarm != null) {
			alarm.CancelAlarm(context);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}

	public void onetimeTimer(View view) {
		Context context = this.getApplicationContext();
		if (alarm != null) {

			alarm.setOnetimeTimer(context);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}

	public void db_initialize() {
		tododao = App.daoSession.getToDoDao();
		labeldao = App.daoSession.getLabelDao();
	}

	public static void alrammmm(Context context) {
		NotificationHandler nHandler;
		nHandler = NotificationHandler.getInstance(context);
		nHandler.createSimpleNotification(context);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// nothing

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// nothing

	}

	@Override
	public void onPageSelected(int position) {

	}

	// ****************** Notification List Adapter*********

	public class Notify_adapter extends BaseAdapter {

		Context context;

		public Notify_adapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			aq.id(R.id.no_notification).visibility(View.GONE);
			return 10;
		}

		@Override
		public java.lang.Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {

			Holder holder = null;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.menu_drawer_right_list_item,
						arg2, false);
				holder = new Holder();
				holder.message = (TextView) view.findViewById(R.id.message);
				holder.time = (TextView) view.findViewById(R.id.time);
				Utils.RobotoRegular(context, holder.message);
				Utils.RobotoRegular(context, holder.time);
				view.setTag(holder);
			} else {
				holder = (Holder) view.getTag();
			}

			return view;
		}

		class Holder {
			TextView message, time;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Toast.makeText(MainActivity.this, "asdasdasd", Toast.LENGTH_SHORT)
				.show();
	}

	public class Phone_contact extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cursor = getContentResolver()
					.query(ContactsContract.Contacts.CONTENT_URI,
							null,
							ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1",
							null,
							"UPPER(" + ContactsContract.Contacts.DISPLAY_NAME
									+ ") ASC");
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					int hasPhoneNumber = Integer
							.parseInt(cursor.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
					if (hasPhoneNumber > 0) {
						Constants.Name
								.add(cursor.getString(cursor
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
						// Query and loop for every phone number of the contact
						Cursor phoneCursor = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = ?",
										new String[] { cursor.getString(cursor
												.getColumnIndex(ContactsContract.Contacts._ID)) },
										null);
						// while (phoneCursor.moveToNext()) {
						phoneCursor.moveToNext();
						phoneNumber = phoneCursor
								.getString(phoneCursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						Constants.Contact.add(phoneNumber);
						// }
						phoneCursor.close();
					}
				}
			}

			return null;
		}

	}
}
