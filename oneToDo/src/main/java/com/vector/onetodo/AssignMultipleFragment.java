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
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.vector.model.ContactsData;
import com.vector.onetodo.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignMultipleFragment extends ProjectsTabHolder {

	private ListView listView;
	private ImageView img;
    private AQuery aq;
    private ContactsAdapter adapter;
    public static HashMap<String, String> selectedInvitees = new HashMap<>();

	public static AssignMultipleFragment newInstance(int position) {
		AssignMultipleFragment myFragment = new AssignMultipleFragment();
		Bundle args = new Bundle();
		args.putInt("position", position);
		myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
        selectedInvitees.clear();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.invitation_list, container, false);
		listView = (ListView) view.findViewById(R.id.invitation_list_view);
		img = (ImageView) getActivity().findViewById(R.id.assign_add);
        aq = new AQuery(getActivity(), view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        getContacts();
        adapter = new ContactsAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(new ContactsAdapter(getActivity()));

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				toggleSelection(view.findViewById(R.id.item_image), position);
				if(selectedInvitees.size() > 0){
					img.setAlpha((float) 1);
				}
			}
		});

		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (img.getAlpha() == 1) {
                    AddEventFragment.updateAssign(selectedInvitees);
					getActivity().getSupportFragmentManager().popBackStack();
				}
			}
		});

	}

	private void toggleSelection(View view, int position){
		if(view.getVisibility() == View.GONE){
			view.setVisibility(View.VISIBLE);
			selectedInvitees.put(String.valueOf(ContactsData.getInstance().contactsList.get(position).id), ContactsData.getInstance().contactsList.get(position).firstName+" "+ContactsData.getInstance().contactsList.get(position).lastName);
		}
		else{
			view.setVisibility(View.GONE);
			if(selectedInvitees.containsKey(String.valueOf(ContactsData.getInstance().contactsList.get(position).id))){
				selectedInvitees.remove(String.valueOf(ContactsData.getInstance().contactsList.get(position).id));
			}
		}
	}

    public class ContactsAdapter extends BaseAdapter {

        Context context;

        public ContactsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ContactsData.getInstance().contactsList.size();
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
            Holder holder;
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
            holder.title.setText(ContactsData.getInstance().contactsList.get(position).firstName+" "+ContactsData.getInstance().contactsList.get(position).lastName);
            holder.icon.setText(Utils.getInitials(ContactsData.getInstance().contactsList.get(position).firstName, ContactsData.getInstance().contactsList.get(position).lastName));
            holder.number.setText(ContactsData.getInstance().contactsList.get(position).number);

            return view;
        }
    }

	class Holder {
		TextView title, number, time, icon;
	}

	@Override
	public void adjustScroll(int scrollHeight) {

	}

    private void getContacts() {
        HttpEntity entity = null;
        List<NameValuePair> pairs = new ArrayList<>();
        ArrayList<String> contacts = Utils.getContactsList(getActivity());
        for (int i = 0; i < contacts.size(); i++) {
            pairs.add(new BasicNameValuePair("contacts[]",
                    contacts.get(i)));
        }
        try {
            entity = new UrlEncodedFormEntity(pairs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, java.lang.Object> params = new HashMap<>();
        params.put(AQuery.POST_ENTITY, entity);
        aq.progress(aq.id(R.id.contacts_dialog).getProgressBar()).ajax("http://api.heuristix.net/one_todo/v1/user/getContacts",
                params, String.class, new AjaxCallback<String>() {

                    @Override
                    public void callback(String url, String json,
                                         AjaxStatus status) {
                        try {
                            Log.e("contacts", json);
                            Gson gson = new Gson();
                            ContactsData.getInstance().setList(gson.fromJson(json, ContactsData.class));
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}