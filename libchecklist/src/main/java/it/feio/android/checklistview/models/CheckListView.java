package it.feio.android.checklistview.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.interfaces.CheckListEventListener;
import it.feio.android.checklistview.interfaces.Constants;

public class CheckListView extends LinearLayout implements Constants, CheckListEventListener {
	
	SharedPreferences shared;
	Editor editor;
	public String listname;
	public Boolean ischeck;
	
	private boolean showDeleteIcon = Constants.SHOW_DELETE_ICON;
	private boolean keepChecked = Constants.KEEP_CHECKED;
	private boolean showChecks = Constants.SHOW_CHECKS;
	private boolean showHintItem = Constants.SHOW_HINT_ITEM;
	private String newEntryHint = "";
	private int moveCheckedOnBottom = Constants.CHECKED_HOLD;
	
	private Context mContext;
	private CheckListChangedListener mCheckListChangedListener;

	
	public CheckListView(Context context) {
		super(context);
		this.mContext = context;
		shared=this.mContext.getSharedPreferences("Checklist", 0);
		editor=shared.edit();
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	/**
	 * Declare if a checked item must be moved on bottom of the list or not
	 * @param moveCheckedOnBottom
	 */
	public void setMoveCheckedOnBottom(int moveCheckedOnBottom) {
		this.moveCheckedOnBottom = moveCheckedOnBottom;
	}

	/**
	 * Set if show or not a delete icon at the end of the line.
	 * Default true.
	 * @param showDeleteIcon True to show icon, false otherwise.
	 */
	public void setShowDeleteIcon(boolean showDeleteIcon) {
		this.showDeleteIcon = showDeleteIcon;
	}
	
	/**
	 * Set if an empty line on bottom of the checklist must be shown or not
	 * @param showHintItem
	 */
	public void setShowHintItem(boolean showHintItem) {
		this.showHintItem= showHintItem;
	}

	/**
	 * Text to be used as hint for the last empty line (hint item)
	 * @param hint
	 */
	public void setNewEntryHint(String hint) {
		setShowHintItem(true);
		this.newEntryHint = hint;
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public void cloneStyles(EditText v) {		
		for (int i = 0; i < getChildCount(); i++) {
			((CheckListViewItem)getChildAt(i)).cloneStyles(v);
		}
	}
		
	
	
	/**
	 * Retrieve the edittext of a child line to be used to copy the typography
	 * @return
	 */
	@Nullable
    public EditText getEditText() {
		EditText res = null;
		CheckListViewItem child = (CheckListViewItem)getChildAt(0);
		if (child != null)
			res = child.getEditText();
		return res;
	}

	
	@Override
	public void onItemChecked(@NotNull CheckListViewItem checked, boolean isChecked) {
		if (isChecked) {
			// If is not selected to HOLD checked items on position then the checked
			// item will be moved on bottom of the list
			if (moveCheckedOnBottom != Constants.CHECKED_HOLD) {
				Log.v(Constants.TAG, "Moving checked on bottom");
				
				CheckListViewItem line;
				for (int i = 0; i < getChildCount(); i++) {
					
					line = ((CheckListViewItem)getChildAt(i));
					if (checked.equals(line)) {
						
						// If it's on last position yet nothing will be done
//						int lastIndex = showHintItem ? getChildCount() -2 : getChildCount() -1;
						int lastIndex = getChildCount() -1;
						if (i == lastIndex) {
							Log.v(Constants.TAG, "Not moving item it's the last one");		
							return;
						}
						
						// Otherwise all items at bottom than the actual will be 
						// cycled until a good position is find.
						Log.v(Constants.TAG, "Moving item at position " + i);
						CheckListViewItem lineAfter;
	
						// The newly checked item will be positioned at last position.
						if (moveCheckedOnBottom == Constants.CHECKED_ON_BOTTOM) {
							removeView(checked);
							addView(checked, lastIndex);
							return;
						}
							
						// Or at the top of checked ones
						if (moveCheckedOnBottom == Constants.CHECKED_ON_TOP_OF_CHECKED) {
							for (int j = lastIndex; j > i ; j--) {
								lineAfter = ((CheckListViewItem)getChildAt(j));
								if (!lineAfter.isChecked()) {
									removeView(checked);
									addView(checked, j);
									return;
								} 
							}
						}
					}
				}
			}
		// Item has been unchecked and have to be (eventually) moved up
		} else {
			if (moveCheckedOnBottom != Constants.CHECKED_HOLD) {
				Log.v(Constants.TAG, "Moving up item");
				
				CheckListViewItem line;
				int position = 0;
				for (int i = 0; i < getChildCount(); i++) {
					line = ((CheckListViewItem)getChildAt(i));
					position = i;
					if (line.isChecked() || line.isHintItem()) break;
				}
				removeView(checked);
				addView(checked, position);
				
			}
		}
		
		// Notify something is changed 
		if (mCheckListChangedListener != null) {
			mCheckListChangedListener.onCheckListChanged();
		}
		
	}

	
	@Override
	public void onNewLineItemEdited(CheckListViewItem checkableLine) {
		addHintItem();
	}
	

	@Override
	public void onEditorActionPerformed(@NotNull CheckListViewItem mCheckListViewItem, int actionId, KeyEvent event) {

		if (actionId != EditorInfo.IME_ACTION_NEXT)
			return;

		EditTextMultiLineNoEnter v = mCheckListViewItem.getEditText();

		// Text lenght, start and end selection points, and other derived flags are retrieved 
		int textLenght = mCheckListViewItem.getText().length();
		int start = v.getSelectionStart();
		int end = v.getSelectionEnd();
		boolean isTextSelected = end != start;
		boolean isTruncating = !isTextSelected && start > 0 && start < textLenght;

		// A check on the view position is done
		int index = indexOfChild(mCheckListViewItem);
		int lastIndex = getChildCount() - 1;
		boolean isLastItem = index == lastIndex;
		CheckListViewItem nextItem = (CheckListViewItem) getChildAt(index + 1);
		
		// If the "next" ime key is pressed being into the hint item of the list the
		// softkeyboard will be hidden and focus assigned out of the checklist items.
		if ( (mCheckListViewItem.isHintItem() || isLastItem) && textLenght == 0) {
			InputMethodManager inputManager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(mCheckListViewItem.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			return;
		}
		
		// If line is empty a newline will not be created but the focus will be moved on bottom.
		// This must happen also if an empty line is already present under the actual one.
		if (textLenght == 0 	// Empty line
			|| (nextItem != null && nextItem.getText().length() == 0 && !isTextSelected && !isTruncating) // Empty item below 
			|| (nextItem != null && !isTextSelected && !isTruncating && start == 0)	// On first characther
			) {
//			focusView(mCheckListViewItem, FOCUS_DOWN);
			nextItem.requestFocus();
			nextItem.getEditText().setSelection(0);
			return;
		}

		// The actual and the new one view contents are generated depending
		// on cursor position
		String text = v.getText().toString();
		String oldViewText = isTextSelected ? text.substring(0, start) + text.substring(end, text.length()) : text
				.substring(0, start);
		String newViewText = isTextSelected ? text.substring(start, end) : text.substring(end, text.length());

		// Actual view content is replaced
		v.setText(oldViewText);

		// A new checkable item is eventually created (optionally with text content)
//		if (newViewText.length() > 0) {
			addItem(newViewText, mCheckListViewItem.isChecked(), index + 1);
//		}

		// The new view is focused
		getChildAt(index + 1).requestFocus();
	}
	
	
	
	/**
	 * Add a new item into the checklist
	 * @param text String to be inserted as item text
	 */
	public void addItem(String text){
		addItem(text, false);
	}
	
	
	
	/**
	 * Add a new item into the checklist
	 * @param text String to be inserted as item text
	 */
	public void addItem(String text, boolean isChecked){
		addItem(text, isChecked, null);
		save(loadMax()+1);
		saved(text, isChecked);
	}
	
	
	/**
	 * Add a new item into the checklist at specific index
	 * @param text String to be inserted as item text
	 */
	public void addItem(String text, boolean isChecked, @Nullable Integer index){
		
		save(loadMax()+1);
		saved(text, isChecked);
		
		CheckListViewItem mCheckListViewItem = new CheckListViewItem(mContext, isChecked, showDeleteIcon);
		mCheckListViewItem.cloneStyles(getEditText());
		mCheckListViewItem.setText(text);
		
		mCheckListViewItem.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mCheckListViewItem.setItemCheckedListener(this);		
		// Set text changed listener if is asked to do this
		if (mCheckListChangedListener != null) {
			mCheckListViewItem.setCheckListChangedListener(this.mCheckListChangedListener);
		}
		if (index != null) {
			addView(mCheckListViewItem, index);
		} else {
			addView(mCheckListViewItem);
		} 
	}
	
	
//	/**
//	 * Add a new item to the checklist 
//	 * @param text String to be inserted as item text
//	 */
//	public void addHintItem(int position){
//		CheckListViewItem mCheckListViewItem = new CheckListViewItem(mContext, false, false);
//		mCheckListViewItem.cloneStyles(getEditText());
//		mCheckListViewItem.setHint(newEntryHint);
//		mCheckListViewItem.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
//		// Set the checkbox initially disabled
//		CheckBox c = mCheckListViewItem.getCheckBox();
//		c.setEnabled(false);
//		mCheckListViewItem.setCheckBox(c);
//		// Attach listener
//		mCheckListViewItem.setItemCheckedListener(this);		
//		// Set text changed listener if is asked to do this
//		if (mCheckListChangedListener != null) {
//			mCheckListViewItem.setCheckListChangedListener(this.mCheckListChangedListener);
//		}
//		// Add view
//		addView(mCheckListViewItem, position);
//	}
	
	
	/**
	 * Add a new item to the checklist 
	 * @param text String to be inserted as item text
	 */
	public void addHintItem(){
		CheckListViewItem mCheckListViewItem = new CheckListViewItem(mContext, false, false);
		//mCheckListViewItem.addicon();
		mCheckListViewItem.cloneStyles(getEditText());
		mCheckListViewItem.setHint(newEntryHint);/*
		mCheckListViewItem.setText(text);
		mCheckListViewItem.*/
		mCheckListViewItem.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		// Set the checkbox initially disabled
		CheckBox c = mCheckListViewItem.getCheckBox();
		c.setEnabled(false);
		mCheckListViewItem.setCheckBox(c);
		// Attach listener
		mCheckListViewItem.setItemCheckedListener(this);		
		// Set text changed listener if is asked to do this
		if (mCheckListChangedListener != null) {
			mCheckListViewItem.setCheckListChangedListener(this.mCheckListChangedListener);
		}
		
		// Defining position (default last, but if checked items behavior is not HOLD if changes)
		int hintItemPosition = getChildCount();
		if (moveCheckedOnBottom != Constants.CHECKED_HOLD) {
			for (int i = 0; i < getChildCount(); i++) {				
				if ( ((CheckListViewItem) getChildAt(i)).isChecked() ) {
					hintItemPosition = i;
					break;
				}
			}
		}
		
		// Add view
		addView(mCheckListViewItem, hintItemPosition);
	}
	




	private void focusView(@NotNull View v, int focusDirection) {
		EditTextMultiLineNoEnter focusableEditText = (EditTextMultiLineNoEnter) v.focusSearch(focusDirection);
		if (focusableEditText != null) {
			focusableEditText.requestFocus();
			focusableEditText.setSelection(focusableEditText.getText().length());
		}		
	}
	
	
	
	
	public void setCheckListChangedListener(CheckListChangedListener mCheckListChangedListener) {
		this.mCheckListChangedListener = mCheckListChangedListener;
	}

//	@Override
//	public void afterTextChanged(Editable s) {}
//	@Override
//	public void beforeTextChanged(CharSequence s, int start, int count,
//			int after) {}
//	@Override
//	public void onTextChanged(CharSequence s, int start, int before, int count) {		
//		// Eventually notify something is changed 
//		if (mCheckListChangedListener != null) {
//			mCheckListChangedListener.onCheckListChanged();
//		}		
//	}

	@Override
	public void onLineDeleted(CheckListViewItem checkableLine) {
		// Eventually notify something is changed 
		mCheckListChangedListener.onCheckListChanged();
	}
	
	public void save(int max){
		editor.putInt("Max", max);
		editor.commit();
	}
	public void saved(String value,Boolean ischecked){
		editor.putString("list"+shared.getInt("Max", 0), value);
		editor.putBoolean("ischecked"+shared.getInt("Max", 0), ischecked);
		editor.commit();
	}
	
	public void load()
	{
		listname=shared.getString("list", null);
		ischeck=shared.getBoolean("ischecked", false);
	}
	public int loadMax(){
		return shared.getInt("Max", 0);
	}

}
