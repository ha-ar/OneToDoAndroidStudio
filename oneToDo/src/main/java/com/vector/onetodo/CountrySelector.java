package com.vector.onetodo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.vector.model.ContactsData;
import com.vector.onetodo.db.gen.Assign;
import com.vector.onetodo.db.gen.AssignDao;
import com.vector.onetodo.utils.Constants;
import com.vector.onetodo.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountrySelector extends Fragment {

	private AQuery aq;
	private TextView skip;
	private Boolean message;
	private AlertDialog alert;
	private TextView confirm, save;
	private int position = 0;
	public static View view;
	private InputMethodManager imm;
	@NotNull
    private ArrayList<String> contactsList = new ArrayList<String>();
	private AssignDao assignDao;
	@Nullable
    String phoneNumber = null;

	@Override
	public void onResume() {
		super.onResume();
		if (SplashScreen.country != null) {
			aq.id(R.id.country).getEditText().setText(SplashScreen.code);
			aq.id(R.id.country).getEditText()
					.setSelection(SplashScreen.code.length());
			aq.id(R.id.spinner1).text(SplashScreen.country);
			aq.id(R.id.country).getEditText().setEnabled(true);
			aq.id(R.id.country).getEditText().requestFocus();
			if (imm != null) {
				imm.showSoftInput(aq.id(R.id.country).getEditText(),
						InputMethodManager.SHOW_IMPLICIT);
			}
			position = 1;
		}
	}

	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.about, container, false);
		aq = new AQuery(getActivity(), view);
		getActivity();
		imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// ******* Phone contact , name list

		Constants.Name = new ArrayList<String>();
		Constants.Contact = new ArrayList<String>();

		contactsList = Utils.getContactsList(getActivity());
		// addContacts();
		// new Phone_contact().execute();

		String html = "ONE" + "<br />" + "todo";
		aq.id(R.id.title).text(Html.fromHtml(html));

		skip = (TextView) getActivity().findViewById(R.id.loginSkip);

		View vie = getActivity().getLayoutInflater().inflate(R.layout.skip,
				null, false);
		AlertDialog.Builder builderLabel = new AlertDialog.Builder(
				getActivity());
		builderLabel.setView(vie);
		alert = builderLabel.create();
		confirm = (TextView) vie.findViewById(R.id.skip_confirm);
		save = (TextView) vie.findViewById(R.id.skip_save);

		aq.id(R.id.spinner1).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Fragment fr = new Country();
				FragmentTransaction trans = getFragmentManager()
						.beginTransaction();
				trans.replace(R.id.container, fr);
				trans.addToBackStack("COUNTRY");
				trans.commit();
			}
		});

		aq.id(R.id.save_profile).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				App.prefs.setUserName(aq.id(R.id.user_name).getText()
						.toString());
				if (!(aq.id(R.id.country).getText().length() < 4
						|| aq.id(R.id.country).getText().equals("") || position == 0)) {
					if (App.prefs.getGcmid() != null) {
						registerUser();
						contactsList = Utils.getContactsList(getActivity());
						Toast.makeText(getActivity(), "Syncing contacts",
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(getActivity(), "Please wait...",
								Toast.LENGTH_LONG).show();
						SplashScreen.registerInBackground(getActivity());
					}
				} else {
					Toast.makeText(getActivity(),
							"Enter correct number and select country",
							Toast.LENGTH_LONG).show();
					aq.id(R.id.country).getEditText().requestFocus();
				}

			}
		});
		skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alert.show();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alert.dismiss();
			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alert.dismiss();
				showUserDetailsActivity();
			}
		});

		if (!Utils.getUserName(getActivity()).isEmpty())
			aq.id(R.id.user_name).text(Utils.getUserName(getActivity()));

	}

	private void showUserDetailsActivity() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
		Intent intent = new Intent();
		intent.setClass(getActivity(), MainActivity.class);
		getActivity().startActivity(intent);
		getActivity().finish();
		getActivity()
				.overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
	}

	private void registerUser() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("user[email]", System.nanoTime() + "");
		params.put("user[password]", "");
		params.put("user[gcm_id]", App.prefs.getGcmid());
		params.put("user[registration_type]", "not using yet");
		params.put("user[registration_type_id]", "not using yet");
		params.put(
				"user[device_type_id]",
				Secure.getString(getActivity().getContentResolver(),
						Secure.ANDROID_ID).toString());

		params.put("user[device_type]", Build.MODEL + "");
		params.put("user[mobile_no]", aq.id(R.id.country).getText().toString());
		params.put("user[country]", SplashScreen.country);
		params.put(
				"user[date_created]",
				Utils.getCurrentYear(1) + "-" + Utils.getCurrentMonthDigit(1)
						+ "-" + Utils.getCurrentDayDigit(1) + " "
						+ Utils.getCurrentHours() + ":"
						+ Utils.getCurrentMins() + ":00");

		String[] name = App.prefs.getUserName().split(" ");// Utils.getUserName(getActivity()).split(" ");
		App.prefs.setInitials(name[0].substring(0, 1).toUpperCase()
				+ name[1].substring(0, 1).toUpperCase());
		params.put("user_profile[first_name]", name[0]);
		params.put("user_profile[last_name]", name[1]);
		params.put("user_profile[gender]", "male");
		params.put("user_profile[birthday]", "27");
		params.put("user_profile[profile_image]", "//comingsoon");

		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage("Registering...Please wait.");
		aq.progress(dialog).ajax(
				"http://api.heuristix.net/one_todo/v1/user/register", params,
				JSONObject.class, new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, @NotNull JSONObject json,
							AjaxStatus status) {
						int id = -1;
						try {

							JSONObject obj1 = new JSONObject(json.toString());
							message = obj1.getBoolean("error");
							id = obj1.getInt("result");

						} catch (Exception e) {
						}
						if (id != -1) {
							Constants.user_id = id;
							App.prefs.setUserId(id);
							addContacts();
						}
						Log.v("Response", json.toString());

					}
				});
	}

	private void addContacts() {
		HttpEntity entity = null;
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("user_id", String.valueOf(App.prefs
				.getUserId())));
		for (int i = 0; i < contactsList.size(); i++) {
			pairs.add(new BasicNameValuePair("contacts[" + i + "]",
					contactsList.get(i)));
		}
		try {
			entity = new UrlEncodedFormEntity(pairs, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Map<String, java.lang.Object> params = new HashMap<String, java.lang.Object>();
		params.put(AQuery.POST_ENTITY, entity);
		aq.ajax("http://api.heuristix.net/one_todo/v1/user/addContacts",
				params, JSONObject.class, new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, @NotNull JSONObject json,
							AjaxStatus status) {
						try {
							if (!json.getBoolean("error")
									&& json.getBoolean("result")) {
								Toast.makeText(getActivity(),
										"Contacts Synced!", Toast.LENGTH_SHORT)
										.show();
								getAssignAbleFriendsList();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	private void getAssignAbleFriendsList() {
		aq.ajax("http://api.heuristix.net/one_todo/v1/contacts/"
				+ App.prefs.getUserId(), String.class,
				new AjaxCallback<String>() {
					@Override
					public void callback(String url, @Nullable String json,
							AjaxStatus status) {
						if (json != null) {
							Gson gson = new Gson();
							ContactsData obj = new ContactsData();
							obj = gson.fromJson(json.toString(),
									ContactsData.class);
							ContactsData.getInstance().setList(obj);
							// Log.v("Contact",
							// ContactsData.getInstance().contactsList
							// .get(0).id);
							populateAssignAbleFriends();
							showUserDetailsActivity();
						}
					}
				});
	}

	private void populateAssignAbleFriends() {
		assignDao = App.daoSession.getAssignDao();
		assignDao.deleteAll();
		for (int i = 0; i < ContactsData.getInstance().contactsList.size(); i++) {
			String initials = ContactsData.getInstance().contactsList.get(i).firstName
					.substring(0, 1).toUpperCase()
					+ ContactsData.getInstance().contactsList.get(i).lastName
							.substring(0, 1).toUpperCase();
			Assign assign = new Assign(
					null,
					ContactsData.getInstance().contactsList.get(i).firstName
							+ " "
							+ ContactsData.getInstance().contactsList.get(i).lastName,
					initials,
					ContactsData.getInstance().contactsList.get(i).number, Long
							.valueOf(ContactsData.getInstance().contactsList
									.get(i).id));
			assignDao.insert(assign);
		}

	}
	// public class getPhoneContacts extends AsyncTask<Void, Void, Void> {
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// super.onPostExecute(result);
	// if (Constants.Name.size() > 0)
	// Toast.makeText(getActivity(), Constants.Name.get(0),
	// Toast.LENGTH_LONG);
	// else
	// Toast.makeText(getActivity(), "Not", Toast.LENGTH_LONG);
	//
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	//
	// cursor = getActivity()
	// .getContentResolver()
	// .query(ContactsContract.Contacts.CONTENT_URI,
	// null,
	// ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1",
	// null,
	// "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME
	// + ") ASC");
	// }
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// if (cursor.getCount() > 0) {
	// while (cursor.moveToNext()) {
	// int hasPhoneNumber = Integer
	// .parseInt(cursor.getString(cursor
	// .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
	// if (hasPhoneNumber > 0) {
	// Constants.Name
	// .add(cursor.getString(cursor
	// .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
	// // Query and loop for every phone number of the contact
	// Cursor phoneCursor = getActivity()
	// .getContentResolver()
	//
	// .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	// null,
	// ContactsContract.CommonDataKinds.Phone.CONTACT_ID
	// + " = ?",
	// new String[] { cursor.getString(cursor
	// .getColumnIndex(ContactsContract.Contacts._ID)) },
	// null);
	//
	// phoneCursor.moveToNext();
	// phoneNumber = phoneCursor
	// .getString(phoneCursor
	// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	// Constants.Contact.add(phoneNumber);
	//
	// phoneCursor.close();
	// }
	// }
	// }
	//
	// return null;
	// }
	// }
}
