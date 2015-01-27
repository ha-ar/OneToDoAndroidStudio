package com.vector.onetodo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class Country extends Fragment {

	private ListView lv;
	private CountriesListAdapter adapter;
	private ActionBar actionBar;
	private List<String> countriesList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.country, container, false);

		lv = (ListView) view.findViewById(R.id.country_list);
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
		if (toolbar != null)
			((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);

		actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		actionBar.setTitle("Select Country");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		String[] recourseList = getResources().getStringArray(
				R.array.CountryCodes);
		countriesList = new ArrayList<String>(Arrays.asList(recourseList));
		adapter = new CountriesListAdapter(getActivity(), countriesList);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				SplashScreen.country = ((TextView) view
						.findViewById(R.id.country_name)).getText().toString();
				SplashScreen.code = ((TextView) view
						.findViewById(R.id.country_code)).getText().toString();
				App.prefs.setCountryCode(SplashScreen.code);
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
	}

	SearchView search;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.base, menu);

		SearchManager manager = (SearchManager) getActivity().getSystemService(
				Context.SEARCH_SERVICE);
		search = (SearchView) menu.findItem(R.id.action_search).getActionView();
		search.setSearchableInfo(manager.getSearchableInfo(getActivity()
				.getComponentName()));
		search.setOnSearchClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ActionBarActivity) getActivity()).getSupportActionBar()
						.setDisplayHomeAsUpEnabled(true);
				((ActionBarActivity) getActivity()).getSupportActionBar()
						.setDisplayShowHomeEnabled(true);
			}
		});
		search.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String query) {
				adapter.getFilter().filter(query);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return false;
			}

		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (search.isIconified())
				getActivity().getSupportFragmentManager().popBackStack();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
