package com.vector.onetodo;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vector.onetodo.db.gen.ToDo;

public abstract class LazyListAdapter<T extends ToDo> extends BaseAdapter {
	
    protected boolean dataValid;
    protected List<ToDo> lazyList;
    protected Context context;

    public LazyListAdapter(Context context, List<ToDo> lazyList) {
    	this.lazyList = lazyList;
        this.dataValid = lazyList != null;
        this.context = context;
    }

    /**
     * Returns the list.
     * @return the list.
     */
    public List<ToDo> getLazyList() {
        return lazyList;
    }
    /**
     * @see android.widget.ListAdapter#getCount()
     */
    @Override
    public int getCount() {
        if (dataValid && lazyList != null) {
            return lazyList.size();
        } else {
            return 0;
        }
    }
    
    /**
     * @see android.widget.ListAdapter#getItem(int)
     */
    @Override
    public ToDo getItem(int position) {
        if (dataValid && lazyList != null) {
            return lazyList.get(position);
        } else {
            return null;
        }
    }
    /**
     * @see android.widget.ListAdapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        if (dataValid && lazyList != null) {
        	ToDo item = lazyList.get(position);
            if (item != null) {
            	return item.getId();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    @Override
    public boolean hasStableIds() {
        return true;
    }
    /**
     * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        if (!dataValid) {
            throw new IllegalStateException("this should only be called when lazylist is populated");
        }
        
        ToDo item = lazyList.get(position);
        if (item == null) {
            throw new IllegalStateException("Item at position " + position + " is null");
        }
        
        View v;
        if (convertView == null) {
            v = newView(context, item, parent);
        } else {
            v = convertView;
        }
        bindView(v, context, item);
        return v;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
    	
        if (dataValid) {
        	ToDo item = lazyList.get(position);
            View v;
            if (convertView == null) {
                v = newDropDownView(context, item, parent);
            } else {
                v = convertView;
            }
            bindView(v, context, item);
            return v;
        } else {
            return null;
        }
    }
    
    /**
     * Makes a new view to hold the data contained in the item.
     * @param context Interface to application's global information
     * @param item  The object that contains the data
     * @param parent The parent to which the new view is attached to
     * @return the newly created view.
     */
    public abstract View newView(Context context, ToDo item, ViewGroup parent);
    /**
     * Makes a new drop down view to hold the data contained in the item.
     * @param context Interface to application's global information
     * @param item  The object that contains the data
     * @param parent The parent to which the new view is attached to
     * @return the newly created view.
     */
    public View newDropDownView(Context context, ToDo item, ViewGroup parent) {
        return newView(context, item, parent);
    }
    /**
     * Bind an existing view to the data data contained in the item.
     * @param view Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor The object that contains the data
     */
    public abstract void bindView(View view, Context context, ToDo item);



}