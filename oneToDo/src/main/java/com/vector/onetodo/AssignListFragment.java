package com.vector.onetodo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.vector.model.ContactsData;
import com.vector.onetodo.db.gen.Assign;
import com.vector.onetodo.db.gen.AssignDao;
import com.vector.onetodo.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignListFragment extends ProjectsTabHolder {

	private ListView listView;
	private int position;
    private RelativeLayout last;
    private List<Assign> contactsList = new ArrayList<Assign>();
	private ImageView img;
    private AQuery aq;

	public static AssignListFragment newInstance(int position) {
		AssignListFragment myFragment = new AssignListFragment();
		Bundle args = new Bundle();
		args.putInt("position", position);
		myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.invitation_list, container, false);
		listView = (ListView) view.findViewById(R.id.invitation_list_view);
		img = (ImageView) getActivity().findViewById(R.id.assign_add);
		String date_string;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i <= 2; i++) {
			date_string = Utils.getCurrentYear(i) + "-"
					+ (Utils.getCurrentMonthDigit(i) + 1) + "-"
					+ Utils.getCurrentDayDigit(i);
			try {
				Date mDate = sdf.parse(date_string);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
        aq = new AQuery(getActivity(),getView());
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		position = getArguments().getInt("position");
        getContacts();
		setadapter(getActivity(), position);
        AssignDao dao = App.daoSession.getAssignDao();
		contactsList = dao.loadAll();
		listView.setAdapter(new ContactsAdapter(getActivity()));

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View container,
					int position, long id) {
				LinearLayout linearLayoutParent = (LinearLayout) container;
				RelativeLayout linearLayoutChild = (RelativeLayout) linearLayoutParent
						.getChildAt(1);
				if (last != null) {
					ImageView tvCount1 = (ImageView) last.getChildAt(2);
					tvCount1.setVisibility(View.GONE);
				}
				last = linearLayoutChild;
				ImageView tvCountry = (ImageView) linearLayoutChild
						.getChildAt(2);
				tvCountry.setVisibility(View.VISIBLE);
				img.setAlpha((float) 1);
				AddTaskFragment.assignedSelectedID = String.valueOf(contactsList.get(position).getFriends_id());
				AddTaskFragment.assignedSelectedName = contactsList.get(position).getName();
				AddTaskFragment.updateAssign(contactsList.get(position).getName());
			}
		});

		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (img.getAlpha() == 1) {
					getActivity().getSupportFragmentManager().popBackStack();
				}
			}
		});

	}

	public class ContactsAdapter extends BaseAdapter {

		Context context;

		public ContactsAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return contactsList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int ID) {
			return ID;
		}

		@Override
		public View getView(int position, View view1, ViewGroup parent) {
			View view = view1;
			Holder holder = null;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.add_task_assign_item, parent,
						false);
				holder = new Holder();
				holder.title = (TextView) view.findViewById(R.id.assign_name);
                holder.icon = (TextView) view.findViewById(R.id.assign_image);
				holder.number = (TextView) view
						.findViewById(R.id.assign_contact);
				view.setTag(holder);
			} else {
				holder = (Holder) view.getTag();
			}
			holder.title.setText(contactsList.get(position).getName());
            holder.icon.setText(contactsList.get(position).getInitials());
            holder.number.setText(contactsList.get(position).getNumber());
			// holder.title.setText(contactsList.get(position).get);
//            Utils.getInitials();
			return view;
		}
	}

	class Holder {
		TextView title, number, time, icon;
	}

	private void setadapter(Context context, int position) {
		ContactsAdapter adapter = new ContactsAdapter(getActivity());
		listView.setAdapter(adapter);
	}

	@Override
	public void adjustScroll(int scrollHeight) {
		
	}

    private void getContacts() {
        HttpEntity entity = null;
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        ArrayList<String> contacts = Utils.getContactsList(getActivity());
        for (int i = 0; i < contactsList.size(); i++) {
            pairs.add(new BasicNameValuePair("array[]",
                    contacts.get(i)));
        }
        try {
            entity = new UrlEncodedFormEntity(pairs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, java.lang.Object> params = new HashMap<>();
        params.put(AQuery.POST_ENTITY, entity);
        aq.ajax("http://api.heuristix.net/one_todo/v1/user/getContacts",
                params, String.class, new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String json,
                                         AjaxStatus status) {
                        try {
                            Log.e("status", status.getError());
                            Log.e("status", status.getMessage());
                            Log.e("contacts", json.toString());
                            Gson gson = new Gson();
                            ContactsData obj = new ContactsData();
                            obj = gson.fromJson(json,
                                    ContactsData.class);
                            ContactsData.getInstance().setList(obj);
//                            if (!json.getBoolean("error")
//                                    && json.getBoolean("result")) {
//                                Toast.makeText(getActivity(),
//                                        "Contacts Synced!", Toast.LENGTH_SHORT)
//                                        .show();
////                                getAssignAbleFriendsList();
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                });

    }
}