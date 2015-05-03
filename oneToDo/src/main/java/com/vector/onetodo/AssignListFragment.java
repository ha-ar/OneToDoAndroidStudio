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

public class AssignListFragment extends ProjectsTabHolder {

    private ListView listView;
    private RelativeLayout last;
    private ImageView img;
    private AQuery aq;
    private ContactsAdapter adapter;

    public static AssignListFragment newInstance(int position) {
        AssignListFragment myFragment = new AssignListFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        myFragment.setArguments(args);
        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.invitation_list, container, false);
        listView = (ListView) view.findViewById(R.id.invitation_list_view);
        img = (ImageView) getActivity().findViewById(R.id.assign_add);
        aq = new AQuery(getActivity(),getView());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getContacts();
        adapter = new ContactsAdapter(getActivity());
        listView.setAdapter(adapter);
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
                AddTaskFragment.assignedSelectedID = ContactsData.getInstance().contactsList.get(position).id;
                AddTaskFragment.updateAssign(ContactsData.getInstance().contactsList.get(position).firstName+" "+ContactsData.getInstance().contactsList.get(position).lastName);
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
            // holder.title.setText(contactsList.get(position).get);
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