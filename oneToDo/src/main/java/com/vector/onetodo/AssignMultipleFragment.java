package com.vector.onetodo;

import android.content.Context;
import android.os.Bundle;
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

import com.vector.model.ContactsData;
import com.vector.onetodo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AssignMultipleFragment extends ProjectsTabHolder {

	private ListView listView;
    private List<ContactsData.Contacts> contactsList = new ArrayList<>();
	private ImageView img;

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

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.invitation_list, container, false);
		listView = (ListView) view.findViewById(R.id.invitation_list_view);
		img = (ImageView) getActivity().findViewById(R.id.assign_add);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        int position = getArguments().getInt("position");
        ContactsAdapter adapter = new ContactsAdapter(getActivity());
        listView.setAdapter(adapter);
		contactsList = ContactsData.getInstance().contactsList;
		listView.setAdapter(new ContactsAdapter(getActivity()));

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				toggleSelection(view.findViewById(R.id.item_image), position);
				if(AddEventFragment.selectedInvitees.size() > 0){
					img.setAlpha((float) 1);
				}
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
	
	private void toggleSelection(View view, int position){
		if(view.getVisibility() == View.GONE){
			view.setVisibility(View.VISIBLE);
			AddEventFragment.selectedInvitees.add(String.valueOf(contactsList.get(position).id));
		}
		else{
			view.setVisibility(View.GONE);
			if(AddEventFragment.selectedInvitees.contains(String.valueOf(contactsList.get(position).id))){
				AddEventFragment.selectedInvitees.remove(String.valueOf(contactsList.get(position).id));
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
			holder.title.setText(contactsList.get(position).firstName+" "+contactsList.get(position).lastName);
            holder.icon.setText(Utils.getInitials(contactsList.get(position).firstName,contactsList.get(position).lastName));
            holder.number.setText(contactsList.get(position).number);
			return view;
		}
	}

	class Holder {
		TextView title, number, time, icon;
	}

	@Override
	public void adjustScroll(int scrollHeight) {
		
	}
}