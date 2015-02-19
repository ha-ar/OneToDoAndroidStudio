package com.vector.onetodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vector.model.ItemDetails;

import java.util.ArrayList;

/**
 * Created by hasanali on 3/12/14.
 */
public class InviteeAdapter extends BaseAdapter {

    Context ctx;
    private static ArrayList<ItemDetails> itemDetailsrrayList;
    public InviteeAdapter(Context ctx, ArrayList<ItemDetails> results) {
        super();
        this.ctx = ctx;
        itemDetailsrrayList = new ArrayList<ItemDetails>();
        itemDetailsrrayList.addAll(results);
    }
    public int getCount() {
        return itemDetailsrrayList.size();
    }

    public ItemDetails getItem(int position) {
        return itemDetailsrrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
        LayoutInflater mInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = mInflater.inflate(R.layout.layout_invitee, null);
        holder = new ViewHolder();
        holder.txt_Name = (TextView) v.findViewById(R.id.textView);
        holder.image = (ImageView) v.findViewById(R.id.imageView);
        v.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.image.setBackgroundResource(ok);
        holder.txt_Name.setText(itemDetailsrrayList.get(position).getName());
        return v;
    }
    static class ViewHolder {
        TextView txt_Name;
        ImageView image;
    }
}