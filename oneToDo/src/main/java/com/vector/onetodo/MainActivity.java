package com.vector.onetodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
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
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.vector.model.NotificationData;
import com.vector.model.TaskData;
import com.vector.model.TaskData.Todos;
import com.vector.onetodo.db.gen.LabelDao;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Constants;
import com.vector.onetodo.utils.Utils;

import net.simonvt.datepicker.DatePicker;
import net.simonvt.datepicker.DatePicker.OnDateChangedListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.dao.query.WhereCondition;

;

public class MainActivity extends BaseActivity {
//    private GoogleApiClient mGoogleApiClient;
//    public static final int RC_SIGN_IN = 0;
//    private boolean mIntentInProgress;
//    private ConnectionResult mConnectionResult;
//    private boolean mSignInClicked;
//    ProgressDialog progressDialog;
//    final List<String> Permissions = Arrays.asList("public_profile", "email",
//            "user_likes", "user_status", "offline_access", "read_stream",
//            "publish_stream","create_event","user_events","friends_events",
//            "publish_checkins", "friends_checkins","read_friendlists");
    public static SharedPreferences setting;
    public static Calendar CurrentDate;
    static int  check1 = 0;
    public static int menuchange = 0;
    private PopupWindow popupWindowTask;
    public static RelativeLayout layout_MainMenu;
    private AlertDialog date_time_alert;
    public static int pager_number = 0;
    private AlarmManagerBroadcastReceiver alarm;
    static ToDoDao tododao;
    static LabelDao labeldao;
    static List<ToDo> todo_obj;
    public static ViewPager pager;
    private TabPagerAdapter tabPagerAdapter;
    private LabelPagerAdapter labelAdapter;
    private PagerSlidingTabStrip tabs;
    public static List<Todos> Today, Tomorrow, Upcoming;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    public static Activity act;
    public static WhereCondition currentCondition = null;
    private boolean isNotificationDrawerSelected = false;
//    CircularImageView imageEvent;

    private final int TYPE = 10000, ALL = 10001, TASK = 10002, EVENT = 10003, SCHEDULE = 10004, APPOINTMENT = 10005;

    // ************** Phone Contacts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aq = new AQuery(this);
        act = this;


        ////////////////////////////////////////////////////////////////////
//        PlusOptions plus = new PlusOptions.Builder().build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).addApi(Plus.API, plus)
//                .addScope(Plus.SCOPE_PLUS_PROFILE).build();
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
        Constants.Name = new ArrayList<>();
        Constants.Contact = new ArrayList<>();

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        db_initialize();
        todo_obj = tododao.loadAll();
        alarm = new AlarmManagerBroadcastReceiver();

        // ***** Initializing Registration shared preferences **********//
        Constants.user_id = App.prefs.getUserId();
        Log.e("user_id", App.prefs.getUserId()+"");

        // **************************Api Call for Landing data
        if (Constants.user_id != -1) {

            aq.ajax("http://api.heuristix.net/one_todo/v1/tasks/"
                            + Constants.user_id, JSONObject.class,
                    new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json,
                                             AjaxStatus status) {
                            if (json != null) {
                                Log.v("JSON", json.toString());
                                Gson gson = new Gson();
                                TaskData obj = gson.fromJson(json.toString(),
                                        TaskData.class);
                                TaskData.getInstance().setList(obj);
                            }
                        }
                    });
            final ListView notif_list = (ListView) findViewById(R.id.notif_list);
            notif_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    long todoId = -1;
                    for(int i = 0; i < todo_obj.size(); i++){
                        if(Integer.valueOf(NotificationData.getInstance().result.get(position).todo_id).equals(todo_obj.get(i).getTodo_server_id())){
                            todoId = todo_obj.get(i).getId();
                        }
                    }
                    Intent intent = new Intent(MainActivity.this, TaskView.class);
                    intent.putExtra("todo_id", todoId);
                    intent.putExtra("is_assigned_task", false);
                    startActivity(intent);
//                    callNotificationClicked(NotificationData.getInstance().result.get(position).id);
                }
            });
            aq.ajax("http://api.heuristix.net/one_todo/v1/notifications/"
                            + Constants.user_id, JSONObject.class,
                    new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json,
                                             AjaxStatus status) {
                            if (json != null) {
                                Gson gson = new Gson();
                                NotificationData obj = gson.fromJson(json.toString(),
                                        NotificationData.class);
                                NotificationData.getInstance().setList(obj);
                                Notify_adapter adapter = new Notify_adapter(MainActivity.this);
                                notif_list.setAdapter(adapter);
                            }
                        }
                    });

            init();
        } else {

            init();

        }


    }

    private void callNotificationClicked(String notificationId){
        Map<String, String> params = new HashMap<>();
        params.put("id", notificationId);
        aq.ajax("http://api.heuristix.net/one_todo/v1/notification"
                        + Constants.user_id, params ,JSONObject.class,
                new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json,
                                         AjaxStatus status) {
                        if (json != null) {
                            Log.e("notification", json.toString());
                            //notification most probably updated
                        }
                    }
                }.method(AQuery.METHOD_POST));
    }

    Menu menu;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        if(!isNotificationDrawerSelected){
        getMenuInflater().inflate(R.menu.main, menu);

        this.menu = menu;



        SubMenu submenu = menu.addSubMenu(0, TYPE, 1, "By Type");
        submenu.addSubMenu(0,ALL,100,"All");
        submenu.addSubMenu(0,TASK,101,"Task");
        submenu.addSubMenu(0,EVENT,102,"Event");
        submenu.addSubMenu(0,SCHEDULE,103,"Schedule");
        submenu.addSubMenu(0,APPOINTMENT,104,"Appointment");

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
            public boolean onQueryTextChange(String text) {
                SearchFragment.qb = App.daoSession.getToDoDao().queryBuilder();
                SearchFragment.qb
                        .where(ToDoDao.Properties.Title.like("%" + text + "%"))
                        .orderAsc(ToDoDao.Properties.Start_date).build();

                if(SearchFragment.listView != null) {
                    SearchFragment.filteredAdapter = new TaskListAdapter(MainActivity.this, SearchFragment.qb.list(),2);
                    SearchFragment.listView.setAdapter(SearchFragment.filteredAdapter);
                    SearchFragment.filteredAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                return true;
            }

        });
        MenuItem menuItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, SearchFragment.newInstance()).addToBackStack(null).commit();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
        });
        }else{
            getMenuInflater().inflate(R.menu.menu_notification, menu);

            this.menu = menu;
        }
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
                isNotificationDrawerSelected = true;
                toggleRightDrawer();
                break;
            case R.id.action_notifi:
                isNotificationDrawerSelected = false;
                toggleRightDrawer();
                break;
            case R.id.action_clear:
                clearNotifications();
                break;
            case R.id.action_label:
//                initLabelsFragment();
            case TYPE:
                initTabFragment();
            case TASK:
                currentCondition = ToDoDao.Properties.Todo_type_id.eq(1);
                break;
            case EVENT:
                currentCondition = ToDoDao.Properties.Todo_type_id.eq(2);
                break;
//            case SCHEDULE:
//                currentCondition = ToDoDao.Properties.Todo_type_id.eq(3);
//                break;
//            case APPOINTMENT:
//                currentCondition = ToDoDao.Properties.Todo_type_id.eq(4);
//                break;
            case ALL:
                currentCondition = null;
            default:
                break;
        }
        //update all pages manually
        TaskListFragment.setAdapter(this, 0, currentCondition);
        TaskListFragment.setAdapter(this, 1, currentCondition);
        TaskListFragment.setAdapter(this, 2, currentCondition);
        return super.onOptionsItemSelected(item);
    }
    public void clearNotifications(){
        Log.d("cn", "called............................");
//;        ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("Please wait...");
        aq.ajax("http://api.heuristix.net/one_todo/v1/notifications/"
                        + Constants.user_id, JSONObject.class,
                new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json,
                                         AjaxStatus status) {

                        if (json != null) {
                            ListView emptyList = (ListView) findViewById(R.id.notif_list);
                            NotificationData.getInstance().clearList();
                            Notify_adapter adapter = new Notify_adapter(MainActivity.this);
                            emptyList.setAdapter(adapter);
                        }
                    }
                }.method(AQuery.METHOD_DELETE));
    }
    //    public void Fb_Clicked() {
//        Session currentSession = Session.getActiveSession();
//        if (currentSession == null || currentSession.getState().isClosed()) {
//            Session session = new Session.Builder(this).build();
//            Session.setActiveSession(session);
//            currentSession = session;
//        }
//
//        if (currentSession.isOpened()) {
//            // Do whatever u want. User has logged in
//
//        } else if (!currentSession.isOpened()) {
//            // Ask for username and password
//            OpenRequest op = new Session.OpenRequest(this);
//
//            op.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
//            op.setCallback(null);
//            op.setPermissions(Permissions);
//
//            Session session = new Session(MainActivity.this);
//            Session.setActiveSession(session);
//            session.openForPublish(op);
//        }
//    }
//
//
//
//    public void g_plus_LogIn() {
//        if (!mGoogleApiClient.isConnecting()) {
//            progressDialog = new ProgressDialog(
//                    MainActivity.this);
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.setMessage("Please wait. . .");
//            progressDialog.setIndeterminate(true);
//            progressDialog.setCancelable(true);
//            progressDialog.show();
//            mSignInClicked = true;
//            resolveSignInError();
//        }
//    }
//    private void resolveSignInError() {
//        try {
//            if (mConnectionResult.hasResolution()) {
//                try {
//                    mIntentInProgress = true;
//                    mConnectionResult
//                            .startResolutionForResult(this, RC_SIGN_IN);
//                } catch (SendIntentException e) {
//                    mIntentInProgress = false;
//                    mGoogleApiClient.connect();
//                }
//            }
//        } catch (Exception e) {
//        }
//    }
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        if (!result.hasResolution()) {
//            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
//                    0).show();
//            return;
//        }
//        if (!mIntentInProgress) {
//            mConnectionResult = result;
//            if (mSignInClicked) {
//                resolveSignInError();
//            }
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle arg0) {
//        Log.e("Conn", "Conn");
//        mSignInClicked = false;
//        new GetGoogleCalendarEvents().execute();
//        try{
//            progressDialog.dismiss();
//        }catch(Exception e){}
//    }
//    @Override
//    public void onConnectionSuspended(int arg0) {
//        //mGoogleApiClient.connect();
//    }
//
//    public String getProfileInformation() {
//        String name = null;
//        try {
//            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//                com.google.android.gms.plus.model.people.Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//                name= currentPerson.getId();
//                Log.e("Name","//"+name);
//            }
//            mGoogleApiClient.disconnect();
//            mSignInClicked = false;
//            mIntentInProgress = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return name;
//    }

//    class GetGoogleCalendarEvents extends AsyncTask<Void, Void, String> {
//        String ok  = getProfileInformation();
//        String url = "https://www.googleapis.com/calendar/v3/users/me/calendarList?userId="+ok;
//        String sResponse;
//
//        // private ProgressDialog dialog;
//        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
//
//        @Override
//        protected void onPreExecute() {
//            dialog.setMessage("Please wait...");
//            dialog.show();
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            try {
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpGet httppost = new HttpGet(url);
//                HttpResponse response = httpclient.execute(httppost);
//                BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(
//                                response.getEntity().getContent(), "UTF-8"));
//                Log.e("Request", "executing request " + httppost.getRequestLine());
//                sResponse = reader.readLine();
//
//                return sResponse;
//            } catch (Exception e) {
//                // something went wrong. connection with the server error
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            this.dialog.dismiss();
//            Log.e("result", result);
//            if (this.sResponse != null) {
//
//            }
//        }
//    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode,responseCode, intent);

    }
    private void toggleRightDrawer() {
        if (drawerLayout.isDrawerVisible(Gravity.RIGHT)) {
            invalidateOptionsMenu();
            drawerLayout.closeDrawer(Gravity.RIGHT);
            getSupportActionBar().setTitle("Todo's");
        } else {
            invalidateOptionsMenu();

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


        // ***** LeftMenudrawer Mange Account feld**********//
        initTabFragment();
        if (App.prefs.getUserId() == -1) {

            aq.id(R.id.manage_img).image(R.drawable.verify_number);
            aq.id(R.id.manage_text).text("Verify your Number");
        } else {

            aq.id(R.id.manage_img).image(R.drawable.manage_account);
            aq.id(R.id.manage_text).text("Manage Account");
            aq.id(R.id.username).text(App.prefs.getUserName());
            if(!App.prefs.getUserProfileUri().isEmpty()){
//                aq.id(imageEvent).image(App.prefs.getUserProfileUri());
//                aq.id(R.id.profile_image).background(R.drawable.round_photobutton).visible();
                aq.id(R.id.user_number).text(App.prefs.getUserNumber());
                aq.id(R.id.imageView1).text(App.prefs.getInitials());
            }else{
                aq.id(R.id.imageView1).text(App.prefs.getInitials());
            }
//
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

        // ***** Initializing Today, tomorrow, upcoming array**********//
        Today = new ArrayList<>();
        Tomorrow = new ArrayList<>();
        Upcoming = new ArrayList<>();

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
        for (int i = 0; i < TaskData.getInstance().result.todos.size(); i++) {
            if (TaskData.getInstance().result.todos.get(i).start_date != null) {
                try {
                    date = sdf
                            .parse(TaskData.getInstance().result.todos.get(i).start_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (date != null) {
                    if (date.equals(today)) {
                        Today.add((TaskData.getInstance().result.todos.get(i)));
                    } else if (date.equals(tomorrow)) {
                        Tomorrow.add((TaskData.getInstance().result.todos.get(i)));
                    } else if (date.after(tomorrow)) {
                        Upcoming.add((TaskData.getInstance().result.todos.get(i)));
                    }
                }
            }
        }


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


        // Menu Drawer on click change items

        aq.id(R.id.settings).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Fragment fr = new Setting();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fr);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        aq.id(R.id.manage).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Fragment fr = new Accounts();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fr);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        aq.id(R.id.todo_layout).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
//
                Fragment fr = new Calender();
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction(); //
                transaction.setCustomAnimations(R.anim.slide_in,
                        R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
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


        aq.id(R.id.add_task_button).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container, AddTaskFragment.newInstance(pager_number, false, 0))
                        .commit();
            }
        });
        aq.id(R.id.add_event_button).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container, AddEventFragment.newInstance(pager_number,false, 0))
                        .commit();
            }
        });
//        aq.id(R.id.add_schedule_button).clicked(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                getSupportFragmentManager()
//                        .beginTransaction().addToBackStack(null)
//                        .replace(R.id.container, AddScheduleFragment.newInstance(pager_number,false, 0))
//                        .commit();
//            }
//        });
//        aq.id(R.id.add_appointment_button).clicked(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .addToBackStack(null)
//                        .replace(R.id.container, AddAppointmentFragment.newInstance(pager_number, false, 0))
//                        .commit();
//            }
//        });
        aq.id(R.id.add_project_button).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container, AddProjectFragment.newInstance(pager_number, 0))
                        .commit();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
            refreshMenu();
            SharedPreferences commentPrefs = getSharedPreferences("Comment", 0);
            SharedPreferences.Editor editor = commentPrefs.edit();
            editor.clear();
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

    private void initTabFragment(){
        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
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
        tabs.setAllCaps(false);
        tabs.setTypeface(null, Typeface.NORMAL);
        tabs.setViewPager(pager);
    }
    public class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
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
            Fragment fragment = TaskListFragment
                    .newInstance(position);

            return fragment;
        }

    }



    private void initLabelsFragment(){

        pager = (ViewPager) findViewById(R.id.pager);
        labelAdapter = new LabelPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(labelAdapter);

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
        tabs.setAllCaps(false);
        tabs.setTypeface(null, Typeface.NORMAL);
        tabs.setViewPager(pager);
    }

    public class LabelPagerAdapter extends FragmentPagerAdapter {

        public LabelPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            Log.e("name",TaskData.getInstance().labels.get(position));
//            if(TaskData.getInstance().labels.get(position) != null)
//                return TaskData.getInstance().labels.get(position);
//            else
                return "No Label";
        }

        @Override
        public Fragment getItem(int position) {
            return TaskByLabelFragment.newInstance(position, TaskData.getInstance().labels.get(position), TaskData.getInstance().labels.size());
        }

    }



    public void db_initialize() {
        tododao = App.daoSession.getToDoDao();
        labeldao = App.daoSession.getLabelDao();
    }



    // ****************** Notification List Adapter*********

    public class Notify_adapter extends BaseAdapter {

        Context context;

        public Notify_adapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if(NotificationData.getInstance().result.size() > 0)
                aq.id(R.id.no_notification).visibility(View.GONE);
            return NotificationData.getInstance().result.size();
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

            holder.message.setText(NotificationData.getInstance().result.get(position).first_name+" "
                    +NotificationData.getInstance().result.get(position).last_name+" "
                    +NotificationData.getInstance().result.get(position).message
                    +NotificationData.getInstance().result.get(position).todo_title);

            holder.time.setText(NotificationData.getInstance().result.get(position).date_created);

            return view;
        }

        class Holder {
            TextView message, time;
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}
