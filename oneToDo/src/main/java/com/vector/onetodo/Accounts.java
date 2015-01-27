package com.vector.onetodo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.vector.onetodo.utils.Utils;

public class Accounts extends Fragment {

	AQuery aq, aq_attach, aq_onetodoinfo, aq_buypro, aq_phone, aq_changephone,
			aq_email, aq_changeemail, aq_name;
	private Uri imageUri;
	File photo;
	private static final int TAKE_PICTURE = 1;
	public static final int RESULT_GALLERY = 0;
	CircularImageView imageEvent;
	Dialog onetodoinfo, buypro, onetodopro, phoneinfo, changephone, emailinfo,
			changeemail, nameinfo, select_image;
	private ActionBar actionBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.account, container, false);
		imageEvent = (CircularImageView) view.findViewById(R.id.image_event);
		aq = new AQuery(getActivity(), view);
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
		if (toolbar != null)
			((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
		actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		actionBar.setTitle("Manage Account");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		setFont();
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.plain, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().getSupportFragmentManager().popBackStack();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View dialoglayout6 = inflater.inflate(
				R.layout.account_dialog_attachfrom_gallery_camera, null, false);
		aq_attach = new AQuery(dialoglayout6);
		AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
		builder6.setView(dialoglayout6);
		select_image = builder6.create();

		View dialoglayout7 = inflater.inflate(
				R.layout.account_dialog_getonetodo, null, false);
		aq_onetodoinfo = new AQuery(dialoglayout7);
		AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
		builder7.setView(dialoglayout7);
		onetodoinfo = builder7.create();

		View dialoglayout8 = inflater.inflate(R.layout.account_dialog_phoneno,
				null, false);
		aq_phone = new AQuery(dialoglayout8);
		AlertDialog.Builder builder8 = new AlertDialog.Builder(getActivity());
		builder8.setView(dialoglayout8);
		phoneinfo = builder8.create();

		View dialoglayout9 = inflater.inflate(R.layout.account_dialog_email,
				null, false);
		aq_email = new AQuery(dialoglayout9);
		AlertDialog.Builder builder9 = new AlertDialog.Builder(getActivity());
		builder9.setView(dialoglayout9);
		emailinfo = builder9.create();

		View dialoglayout1 = inflater.inflate(
				R.layout.account_dialog_changename, null, false);
		aq_name = new AQuery(dialoglayout1);
		AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
		builder1.setView(dialoglayout1);
		nameinfo = builder1.create();

		View dialoglayout2 = inflater.inflate(
				R.layout.account_dialog_getbuypro, null, false);
		aq_buypro = new AQuery(dialoglayout2);
		AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
		builder2.setView(dialoglayout2);
		buypro = builder2.create();

		View dialoglayout3 = inflater.inflate(
				R.layout.account_dialog_changeemail, null, false);
		aq_changeemail = new AQuery(dialoglayout3);
		AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
		builder3.setView(dialoglayout3);
		changeemail = builder3.create();

		View dialoglayout4 = inflater.inflate(
				R.layout.account_dialog_change_country_phoneno, null, false);
		aq_changephone = new AQuery(dialoglayout4);
		AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity());
		builder4.setView(dialoglayout4);
		changephone = builder4.create();

		Spinner spinner = aq_changephone.id(R.id.sp_country).getSpinner();
		String[] recourseList = getResources().getStringArray(
				R.array.CountryCodes);
		List<String> countriesList = new ArrayList<String>(
				Arrays.asList(recourseList));
		CountriesListAdapter adapter = new CountriesListAdapter(getActivity(),
				countriesList);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long Id) {
				String code = ((TextView) view.findViewById(R.id.country_code))
						.getText().toString();
				aq_changephone.id(R.id.phoneno).text(code);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
		aq_onetodoinfo.id(R.id.ok_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onetodoinfo.dismiss();
				buypro.show();
			}
		});

		aq_onetodoinfo.id(R.id.cancel_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onetodoinfo.dismiss();
			}
		});
		aq.id(R.id.image_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				select_image.show();
			}
		});
		aq.id(R.id.initials).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				select_image.show();
			}
		});
		aq_attach.id(R.id.from_camera).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {

				select_image.dismiss();
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

				String path = Environment.getExternalStorageDirectory()
						.toString();
				File makeDirectory = new File(path + File.separator + "OneTodo");
				makeDirectory.mkdir();
				photo = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "OneToDo" + File.separator,
						"OneToDo_" + System.currentTimeMillis() + ".JPG");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
				imageUri = Uri.fromFile(photo);
				startActivityForResult(intent, TAKE_PICTURE);
			}
		});
		aq_attach.id(R.id.from_gallery).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {

				select_image.dismiss();
				Intent galleryIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(galleryIntent, RESULT_GALLERY);
			}
		});
		aq.id(R.id.go_pro).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onetodoinfo.show();
			}
		});
		aq.id(R.id.verify_number).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneinfo.show();
			}
		});

		aq_phone.id(R.id.ok_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneinfo.dismiss();
			}
		});

		aq_phone.id(R.id.cancel_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneinfo.dismiss();
				changephone.show();
			}
		});
		aq_changephone.id(R.id.ok_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = aq_changephone.id(R.id.phoneno).getText()
						.toString();
				if (!number.isEmpty() && number.contains("+")
						&& number.length() > 4) {
					aq.id(R.id.user_number).text(number);
					App.prefs.setUserNumber(number);
					changephone.dismiss();
				} else {
					Toast.makeText(getActivity(), "Invalid number!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		aq_changephone.id(R.id.cancel_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changephone.dismiss();
			}
		});
		aq.id(R.id.email_account).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emailinfo.show();
			}
		});
		aq_email.id(R.id.ok_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emailinfo.dismiss();

			}
		});
		aq_email.id(R.id.cancel_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emailinfo.dismiss();
				changeemail.show();
			}
		});
		aq_changeemail.id(R.id.ok_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = aq_changeemail.id(R.id.enteremail).getText().toString();
				if(!email.isEmpty() && email.contains("@")){
					App.prefs.setUserEmail(email);
					changeemail.dismiss();
				}else{
					Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		aq_changeemail.id(R.id.cancel_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeemail.dismiss();
			}
		});

		aq.id(R.id.name_layout).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nameinfo.show();
			}
		});
		aq_name.id(R.id.ok_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String userName = aq_name.id(R.id.entername).getText()
						.toString();
				if (!userName.isEmpty()) {
					nameinfo.dismiss();
					aq.id(R.id.user_name).text(userName);
					App.prefs.setUserName(userName);
				} else {
					Toast.makeText(getActivity(), "Name cannot be empty",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		aq_name.id(R.id.cancel_event).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nameinfo.dismiss();
			}
		});
		aq_changeemail.id(R.id.ok_event).clicked(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeemail.dismiss();
			}
		});
		aq_changeemail.id(R.id.cancel_event).clicked(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeemail.dismiss();
			}
		});
		aq_changephone.id(R.id.ok_event).clicked(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changephone.dismiss();
			}
		});
		aq_changephone.id(R.id.cancel_event).clicked(new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changephone.dismiss();
			}
		});

		aq.id(R.id.initials).text(App.prefs.getInitials());
		aq.id(R.id.user_name).text(App.prefs.getUserName());
		aq.id(R.id.user_number).text(App.prefs.getUserNumber());
		aq.id(R.id.email1).text(App.prefs.getUserEmail());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case TAKE_PICTURE:

			if (resultCode == Activity.RESULT_OK) {
				Picasso.with(getActivity()).load(imageUri).resize(100, 100)
						.into(imageEvent);
				aq.id(R.id.image_event)
						.background(R.drawable.round_photobutton).visible();
				aq.id(R.id.initials).gone();
			}
		case RESULT_GALLERY:
			if (null != data) {
				Uri selectedImage = data.getData();
				Picasso.with(getActivity()).load(selectedImage)
						.resize(100, 100).into(imageEvent);
				aq.id(R.id.image_event)
						.background(R.drawable.round_photobutton).visible();
				aq.id(R.id.initials).gone();
			}
			break;
		default:
			break;
		}
	}

	public void setFont() {
		Utils.RobotoRegular(getActivity(), aq.id(R.id.accounts).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.email).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.mynumber).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.personal).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.name).getTextView());
		Utils.RobotoRegular(getActivity(), aq.id(R.id.display).getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.email1).getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.user_number).getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.user_name).getTextView());
		Utils.RobotoMedium(getActivity(), aq.id(R.id.display1).getTextView());
	}

}
