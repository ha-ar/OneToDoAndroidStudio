//package com.vector.guru99;
//
//import java.util.Arrays;
//import java.util.List;
//
//import net.simonvt.menudrawer.MenuDrawer;
//
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.IntentSender.SendIntentException;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v4.content.LocalBroadcastManager;
//import android.text.Html;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.androidquery.AQuery;
//import com.androidquery.callback.AjaxCallback;
//import com.androidquery.callback.AjaxStatus;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
//import com.facebook.widget.LoginButton.UserInfoChangedCallback;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.SignInButton;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.plus.Plus;
//import com.google.android.gms.plus.Plus.PlusOptions;
//import com.google.android.gms.plus.model.people.Person;
//import com.google.gson.Gson;
//import com.vector.guru99.models.SignINUserModel;
//import com.vector.guru99.models.SignUpSharedPreferncesModel;
//
//public class LoginUser extends Activity implements ConnectionCallbacks,
//		OnConnectionFailedListener {
//	
//	public static final String MyPREFERENCES = "MyPrefs";
//	private static final String TAG = "Login";
//	public static final String Name = "nameKey";
//	TextView textView;
//	BroadcastReceiver receiver = null;
//	SharedPreferences.Editor pref;
//	private UiLifecycleHelper uiHelper;
//	private GraphUser user;
//	MenuDrawer mDrawer;
//	ProgressDialog progressDialog;
//	Bundle bundle = new Bundle();
//	final List<String> Permissions = Arrays.asList("public_profile", "email",
//			"user_likes", "user_status");
//	public static final int RC_SIGN_IN = 0;
//	public static final int PROFILE_PIC_SIZE = 400;
//	public static GoogleApiClient mGoogleApiClient;
//	public boolean mIntentInProgress;
//	public boolean mSignInClicked;
//	public ConnectionResult mConnectionResult;
//	static SignInButton btnSignIn;
//	String personName;
//	String personPhotoUrl;
//	String personGooglePlusProfile;
//	String email,type,firstName,secondName;AQuery aq;
//	private Session.StatusCallback callback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state,
//				Exception exception) {
//			onSessionStateChange(session, state, exception);
//		}
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		uiHelper = new UiLifecycleHelper(LoginUser.this, callback);
//		uiHelper.onCreate(savedInstanceState);
//		setContentView(R.layout.login_user); 
//		aq = new AQuery(LoginUser.this); 
//		final TextView tx = (TextView) findViewById(R.id.logo_description);
//		Button signInEmail = (Button) findViewById(R.id.signin_email); 
//		Button signUpEmail = (Button) findViewById(R.id.signup);
//		String str = "Read ,learn and educate anytime anywhere with <b>guru99</b> for free";
//		tx.setText(Html.fromHtml(str));
//		String currentUserLogin;
//		currentUserLogin = SignUpSharedPreferncesModel.getPreferences(getApplicationContext(), "ACTYPE");
//		 IntentFilter intentFilter = new IntentFilter();
//		    intentFilter.addAction("com.vector.guru99");
//		    LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
//		        @Override
//		        public void onReceive(Context context, Intent intent) {
//		            Log.d("onReceive","Logout in progress");
//		            //At this point we should start the login activity and finish this one.
//		            finish();
//		        }
//		    }, intentFilter);
////		IntentFilter intentFilter = new IntentFilter();
////		intentFilter.addAction("com.vector.guru99");
////		BroadcastReceiver receiver = new BroadcastReceiver() {
////
////			@Override
////			public void onReceive(Context context, Intent intent) {
////				Log.d("LoginUser", "finish");
////				finish();
////			}
////		};
////		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
////				intentFilter);
//		Log.d("Login",currentUserLogin );
//		if(currentUserLogin.toString().equals("1") || currentUserLogin.toString().equals("2") || currentUserLogin.toString().equals("3"))
//		{
//			Intent intent = new Intent(LoginUser.this,
//					BaseActivity.class);
//			LoginUser.this.finish();
//			startActivity(intent);
//		}
//		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB)
//		{
//			signUpEmail.setBackgroundResource(R.drawable.version_left_selector);	
//			signInEmail.setBackgroundResource(R.drawable.version_right_selector);
//			signInEmail.setTextSize(15);
//			signUpEmail.setTextSize(15);
//		} 
//		else
//		{
//			signUpEmail.setBackgroundResource(R.drawable.left_selector);	
//			signInEmail.setBackgroundResource(R.drawable.right_selector);
//			signInEmail.setTextSize(17);
//			signUpEmail.setTextSize(17);
//		}
//		signUpEmail.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(LoginUser.this,
//						Signup_User.class);
//				Bundle bundle = new Bundle();
//				bundle.putInt("EDIT", 0);
//				intent.putExtras(bundle);
//				startActivity(intent);
//			}
//		});
//		signInEmail.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(LoginUser.this,
//						UserSignInEmail.class);
//				startActivity(intent);
//				LoginUser.this.finish();
//			}
//		});
//		// /////////////////////////////////////////
//		btnSignIn = (SignInButton) findViewById(R.id.g_plus);
//		btnSignIn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (!mGoogleApiClient.isConnected())
//				{
//					g_plus_LogIn();					
//				}
//			}
//		});
//		setGooglePlusButtonText(btnSignIn, "Sign in with Google");
//		PlusOptions plus = new PlusOptions.Builder().build();
//		mGoogleApiClient = new GoogleApiClient.Builder(this)
//				.addConnectionCallbacks(this)
//				.addOnConnectionFailedListener(this).addApi(Plus.API, plus)
//				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
//		// //////////////////////////////////////////
//		pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
//		LoginButton fb = (LoginButton) findViewById(R.id.fb_button);
//		fb.setReadPermissions(Permissions);
//
//		fb.setUserInfoChangedCallback(new UserInfoChangedCallback() {
//			@Override
//			public void onUserInfoFetched(GraphUser user) {
//				// TODO Auto-generated method stub
//				
//				LoginUser.this.user = user;
//				Session session = Session.getActiveSession();
//				boolean button = (session != null && session.isOpened());
//				if (button && user != null) {
//					type= "2";
//					firstName = user.getFirstName().toString();
//					secondName = user.getLastName().toString();
//					String FBUserName;
//					FBUserName = user.getFirstName().toString();
//					FBUserName += " ";
//					FBUserName += user.getLastName().toString();
//					String UserId = user.getId()
//							.toString();
//					SignUpSharedPreferncesModel.setPreferences(
//							getApplicationContext(),"NEWFBUSERID", UserId);
//					SignUpSharedPreferncesModel.setPreferences(
//							getApplicationContext(),"FBFIRRSTNAME", user.getFirstName()
//							.toString());
//					SignUpSharedPreferncesModel.setPreferences(
//							getApplicationContext(),"FBUSERNAME", FBUserName);
//					email = user.asMap().get("email").toString();
//					Log.e("Email", email);
//					SignUpSharedPreferncesModel.setPreferences(
//							getApplicationContext(),"FBEMAIL", user.asMap().get("email").toString());
//					
//					String imageURL = ("https://graph.facebook.com/"
//							+ user.getId() + "/picture?type=large");
//					SignUpSharedPreferncesModel.setPreferences(
//							getApplicationContext(),"FBIMAGEURL", imageURL);
//					Log.e("FBID", UserId);
//					
//						CheckRegistered();			
//					
//					
//				}
//				
//			}
//		});
//	}
//
//	private void onSessionStateChange(Session session, SessionState state,
//			Exception exception) {
//		if (state.isOpened()) {
//			Log.i(TAG, "Logged in...");
//		} else if (state.isClosed()) {
//			Log.i(TAG, "Logged out...");
//		}
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		uiHelper.onResume();
//		Session session = Session.getActiveSession();
//		if (session != null && (session.isOpened() || session.isClosed())) {
//			onSessionStateChange(session, session.getState(), null);
//		}
//		uiHelper.onResume();
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int responseCode,
//			Intent intent) {
//		Session.getActiveSession().onActivityResult(this, requestCode,
//				responseCode, intent);
//		if (requestCode == RC_SIGN_IN) {
//			if (responseCode != RESULT_OK) {
//				mSignInClicked = false;
//			}
//			mIntentInProgress = false;
//			if (!mGoogleApiClient.isConnecting()) {
//				mGoogleApiClient.connect();
//			}
//		}
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		uiHelper.onPause();
//	}
//
//	@Override
//	public void onDestroy() {
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//		receiver = null;
//		super.onDestroy();
//		uiHelper.onDestroy();		
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		uiHelper.onSaveInstanceState(outState);
//	}
//
//	// ///////////////////////////////////////////////////
//	public void g_plus_LogIn() {
//		if (!mGoogleApiClient.isConnecting()) {
//			progressDialog = new ProgressDialog(
//					LoginUser.this);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			progressDialog.setMessage("Please wait. . .");
//			progressDialog.setIndeterminate(true);
//			progressDialog.setCancelable(true);
//			progressDialog.show();
//			mSignInClicked = true;
//			resolveSignInError();
//		}
//	}
//
//	public static void signOutFromGplus() {
//		if (mGoogleApiClient.isConnected()) {			
//			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//			mGoogleApiClient.disconnect();
//			updateUI(false);
//		}
//	}
//
//	private void resolveSignInError() {
//		try {
//			if (mConnectionResult.hasResolution()) {
//				try {
//					mIntentInProgress = true;
//					mConnectionResult
//							.startResolutionForResult(this, RC_SIGN_IN);
//				} catch (SendIntentException e) {
//					mIntentInProgress = false;
//					mGoogleApiClient.connect();
//				}
//			}
//		} catch (Exception e) {
//		}
//	}
//
//	@Override
//	public void onConnectionFailed(ConnectionResult result) {
//		if (!result.hasResolution()) {
//			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
//					0).show();
//			return;
//		}
//		if (!mIntentInProgress) {
//			mConnectionResult = result;
//			if (mSignInClicked) {
//				resolveSignInError();
//			}
//		}
//	}
//
//	protected void onStart() {
//		super.onStart();
//		mGoogleApiClient.connect();
//	}
//
//	protected void onStop() {
//		super.onStop();
////		if (mGoogleApiClient.isConnected()) {
////			mGoogleApiClient.disconnect();
////		}
//	}
//
//	@Override
//	public void onConnected(Bundle arg0) {
//		mSignInClicked = false;	
//
//		getProfileInformation();
//	
//			CheckRegistered();			
//		
//		try{
//		progressDialog.dismiss();
//		}catch(Exception e){}
//		//updateUI(true);
//
//	}
//	@Override
//	public void onBackPressed() {
//		// if (apSmartWall != null) {
//		// apSmartWall.startSmartWallAd();
//		// }
//		new AlertDialog.Builder(this)
//				.setMessage("Are you sure you want to exit?")
//				.setCancelable(false)
//				.setPositiveButton("Yes",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								LoginUser.this.finish();
//								
//							}
//						}).setNegativeButton("No", null).show();
//		// super.onBackPressed();
//	}
//	private static void updateUI(boolean isSignedIn) {
//		if (isSignedIn) {
//			setGooglePlusButtonText(btnSignIn, "Sign out from Google");
//		} else {
//			setGooglePlusButtonText(btnSignIn, "Sign in with Google");
//		}
//	}
//
//	protected static void setGooglePlusButtonText(SignInButton signInButton,
//			String buttonText) {
//		for (int i = 0; i < signInButton.getChildCount(); i++) {
//			View v = signInButton.getChildAt(i);
//			if (v instanceof TextView) {
//				TextView mTextView = (TextView) v;
//				mTextView.setTextSize(16);
//				mTextView.setText(buttonText);
//				return;
//			}
//		}
//	}
//
//	@Override
//	public void onConnectionSuspended(int arg0) {
//		mGoogleApiClient.connect();
//		updateUI(false);
//	}
//
//	// /////////////////////////////////////////////////////////////////////
//	public void getProfileInformation() {
//		try {			
//			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//				personName = currentPerson.getDisplayName();
//				firstName = currentPerson.getDisplayName().toString();
//				secondName = currentPerson.getDisplayName().toString();
//				personPhotoUrl = currentPerson.getImage().getUrl();
//				String gmailUserId = currentPerson.get.toString();
//				personGooglePlusProfile = currentPerson.getUrl();
//				email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//				type = "3";
//				Log.w(TAG, "Name: " + personName + ", plusProfile: "
//						+ personGooglePlusProfile + ", email: " + email
//						+ ", Image: " + personPhotoUrl+",ID:"+gmailUserId);
//
//				// by default the profile url gives 50x50 px image only
//				// we can replace the value with whatever dimension we want by
//				// replacing sz=X
//				personPhotoUrl = personPhotoUrl.substring(0,
//						personPhotoUrl.length() - 2)
//						+ PROFILE_PIC_SIZE;
//				SignUpSharedPreferncesModel.setPreferences(
//						getApplicationContext(),"PersonName", personName);
//				SignUpSharedPreferncesModel.setPreferences(
//						getApplicationContext(),"G_EMAIL", email);
//				SignUpSharedPreferncesModel.setPreferences(
//						getApplicationContext(),"PhotoUrl", personPhotoUrl);
//				
//				SignUpSharedPreferncesModel.setPreferences(
//						getApplicationContext(),"NEWGMAILUSERID",gmailUserId );					
//				
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	public void CheckRegistered()
//	{	
//		String url = "http://api.guru99.com/getRegistration.php?Id="+email+"&PassToken=&Type="+type;
//		Log.e("URLL", url);
//		aq.ajax(url, JSONObject.class,new AjaxCallback<JSONObject>() {
//					@Override
//					public void callback(String url, JSONObject json,
//							AjaxStatus status) {								
//
//						if (json != null
//								&& json.toString().contains("Success")) {
//							Log.v("JSON", json.toString());
//							Toast.makeText(getApplicationContext(),
//									"You Login Successfully!",
//									Toast.LENGTH_SHORT).show();
//							SignUpSharedPreferncesModel.setPreferences(
//									getApplicationContext(),
//									"ACTYPE", type);
//							Gson gson = new Gson();
//			            	SignINUserModel model = new SignINUserModel();
//			            	model = gson.fromJson(json.toString(), SignINUserModel.class);
//			            	SignINUserModel.getInstance().setList(model);
//			            	//Log.e("JSON MODEL",SignINUserModel.getInstance().result.User_details.get(0).UserId);
//			            	String id =SignINUserModel.getInstance().result.User_details.get(0).UserId;
//							SignUpSharedPreferncesModel.setPreferences(
//									getApplicationContext(),
//									"NEWUSERID", id);
//
//							Intent intent = new Intent(LoginUser.this,
//									BaseActivity.class);				
//							LoginUser.this.finish();				
//							startActivity(intent);
//						} else {
//							FirstRegisteredAndLogin();
//						}
//					}
//				});
//        }
//	public void FirstRegisteredAndLogin(){
//		String url = "http://api.guru99.com/pushRegistration.php?FirstName="
//				+ firstName.toString()
//				+ "&LastName="
//				+ secondName.toString()
//				+ "&Id="
//				+ email
//				+ "&PassToken="
//				+ "" + "&Type="+type;
//		Log.d("url", url);
//		aq.ajax(url, JSONObject.class,
//				new AjaxCallback<JSONObject>() {
//					@Override
//					public void callback(String url, JSONObject json,
//							AjaxStatus status) {
//
//						if (json != null
//								&& json.toString().contains("Success")) {
//							Log.v("JSONRegistered", json.toString());
//							CheckRegistered();
//						} 
//					}
//				});
//		
//	}
//}