package com.vector.onetodo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class CountriesListAdapter extends BaseAdapter implements Filterable{
	private final Context context;
	private List<String> mOriginalValues;
	private List<String> arrayList; 

	public CountriesListAdapter(Context context, List<String> values) {
		this.context = context;
		this.arrayList = values;
	}
	
	private class ViewHolder {
        TextView textView;
        TextView textViewCode;
        ImageView imageView;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewHolder holder = null;

        if (convertView == null) {
        	holder = new ViewHolder();
        	convertView = inflater.inflate(R.layout.country_list, parent, false);
        	holder.textView = (TextView) convertView.findViewById(R.id.country_name);
    		holder.textViewCode = (TextView) convertView
    				.findViewById(R.id.country_code);
    		holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
        	convertView.setTag(holder);
        }else{
        	 holder = (ViewHolder) convertView.getTag();
        }
		String[] g = arrayList.get(position).split(",");
		holder.textViewCode.setText("+"+ g[0].trim());
		holder.textView.setText(getCountryName(g[1]).trim());
		String pngName = g[1].trim().toLowerCase();
		holder.imageView.setImageResource(context.getResources().getIdentifier(
				"drawable/" + pngName, null, context.getPackageName()));
		return convertView;
	}
	@Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
			@Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                arrayList = (List<String>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<String> FilteredArrList = new ArrayList<String>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<String>(arrayList); // saves the original data in mOriginalValues
                }

                /********
                 * 
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)  
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return  
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                    	String[] name = mOriginalValues.get(i).split(",");
                        String data = getCountryName(name[1]);
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

	public String getCountryName(String ssid) {
		Locale loc = new Locale("", ssid);
		return loc.getDisplayCountry().trim();
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public java.lang.Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
}