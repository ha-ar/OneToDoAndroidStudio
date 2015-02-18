package com.vector.onetodo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.androidquery.AQuery;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Utils;

import net.appkraft.parallax.ParallaxScrollView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.QueryBuilder;

public class TaskView extends Fragment {

	AQuery aq, aqd, aq_menu;
	AlertDialog alert;
	ParallaxScrollView parallax;
	ImageView image;
	private PopupWindow popupWindowTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.viewtask, container, false);
		parallax = (ParallaxScrollView) view.findViewById(R.id.scrollView123);
		image = (ImageView) view.findViewById(R.id.imageView1123);
		aq = new AQuery(getActivity(), view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		int id = getArguments().getInt("id");
        QueryBuilder qb = App.daoSession.getToDoDao().queryBuilder();
        qb.where(ToDoDao.Properties.Id.eq(id));
        List<ToDo> data = qb.list();
		if (data.get(0).getTitle() != null)
			aq.id(R.id.title).text(
                    data.get(0).getTitle());

		if (data.get(0).getRepeat() != null)
			aq.id(R.id.repeat).text(
                    data.get(0).getRepeat().toString());
//
//		String strDate = data.get(0).getStart_date().toString();
		Calendar calendar = Calendar.getInstance();
//		SimpleDateFormat dateFormat = new SimpleDateFormat(
//				"yyyy-MM-dd HH:mm:ss");
//		Date date = null;
//		try {
//			date = dateFormat.parse(strDate);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		calendar.setTimeInMillis(data.get(0).getStart_date());
		String strDate = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
				Locale.US)
				+ " "
				+ calendar.get(Calendar.DAY_OF_MONTH)
				+ " "
				+ calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
						Locale.US) + " " + calendar.get(Calendar.YEAR);
		aq.id(R.id.time).text(strDate);

		aq.id(R.id.backview).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
		if (data.get(0).getLocation() != null)
			aq.id(R.id.location).text(strDate);
	 
		aq.id(R.id.location).text("Remind before ");

	 
		final View view1 = getActivity().getLayoutInflater().inflate(
				R.layout.landing_menu, null, false);

		aq_menu = new AQuery(getActivity(), view1);
		aq_menu.id(R.id.menu_item1).text("RSVP");
		aq_menu.id(R.id.menu_item2).visibility(View.GONE);
		aq_menu.id(R.id.menu_item3).visibility(View.GONE);
		popupWindowTask = new PopupWindow(view1, Utils.getDpValue(200,
				getActivity()), WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindowTask.setBackgroundDrawable(getResources().getDrawable(
				android.R.drawable.dialog_holo_light_frame));
		popupWindowTask.setOutsideTouchable(true);

		View dialog = getActivity().getLayoutInflater().inflate(R.layout.rsvp,
				null, false);
		aqd = new AQuery(dialog);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(dialog);
		alert = builder.create();

		aqd.id(R.id.cancel).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alert.dismiss();
			}
		});
		aqd.id(R.id.ok).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alert.dismiss();
			}
		});

		aq_menu.id(R.id.menu_item1).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindowTask.dismiss();
				alert.show();
			}
		});

		aq.id(R.id.imageView4).clicked(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (popupWindowTask.isShowing())
					popupWindowTask.dismiss();
				else { 
					popupWindowTask.showAsDropDown(aq.id(R.id.imageView4)
							.getView(), 5, 10);
				}
			}
		});

		parallax.setImageViewToParallax(image);
 
	}

}
