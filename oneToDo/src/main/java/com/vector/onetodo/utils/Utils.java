package com.vector.onetodo.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.vector.onetodo.App;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Utils {
	public static final Calendar currentDateCal = Calendar
			.getInstance(Locale.US);

	private static final Calendar tempCal = Calendar.getInstance(Locale.US);

    public static int getPxFromDp(Context context, int val) {
		Resources r = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				val, r.getDisplayMetrics());
	}

	/*
	 * @param days to add in current date
	 */
	public static int getCurrentDate(int day) {
		tempCal.setTime(currentDateCal.getTime());
		tempCal.add(Calendar.DATE, day);
		return tempCal.get(Calendar.DATE);
	}

	public static String getCurrentDay(int day, int isLong) {
		tempCal.setTime(currentDateCal.getTime());
		tempCal.add(Calendar.DATE, day);
		return tempCal.getDisplayName(Calendar.DAY_OF_WEEK, isLong, Locale.US);
	}

	public static int getCurrentDayDigit(int day) {
		tempCal.setTime(currentDateCal.getTime());
		tempCal.add(Calendar.DATE, day);
		return tempCal.get(Calendar.DAY_OF_MONTH);
	}

	public static String getCurrentMonth(int day, int isLong) {
		tempCal.setTime(currentDateCal.getTime());
		tempCal.add(Calendar.DATE, day);
		return tempCal.getDisplayName(Calendar.MONTH, isLong, Locale.US);
	}

	public static int getCurrentMonthDigit(int day) {
		tempCal.setTime(currentDateCal.getTime());
		tempCal.add(Calendar.DATE, day);
		return tempCal.get(Calendar.MONTH);
	}

	public static int getCurrentYear(int day) {
		tempCal.setTime(currentDateCal.getTime());
		tempCal.add(Calendar.DATE, day);
		return tempCal.get(Calendar.YEAR);
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static int getCurrentHours() {
		return currentDateCal.get(Calendar.HOUR);
	}

	public static int getCurrentMins() {
		return currentDateCal.get(Calendar.MINUTE);
	}

	public static int getCurrentAmPm() {
		return currentDateCal.get(Calendar.AM_PM);
	}

	public static String getDateFormatted(long milliSeconds) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		tempCal.setTimeInMillis(milliSeconds);
		return formatter.format(tempCal.getTime());
	}

	public static String getTimeFormatted(long milliSeconds) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		tempCal.setTimeInMillis(milliSeconds);
		return formatter.format(tempCal.getTime());
	}

	public static void clearAllFields(ViewGroup viewGroup) {
		for (int i = 0, count = viewGroup.getChildCount(); i < count; ++i) {
			ViewGroup nestedChild = (ViewGroup) viewGroup.getChildAt(i);
			for (int j = 0; j <= nestedChild.getChildCount(); j++) {
				View view = nestedChild.getChildAt(j);
				if (view instanceof EditText) {
					((EditText) view).setText("");
				}
			}
		}
	}

	public static void hidKeyboard(Activity act) {
		InputMethodManager inputManager = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View v = act.getCurrentFocus();
		if (v == null)
			return;

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static void showKeyboard(Activity act) {
		act.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	public static int getDpValue(int val, Context ctx) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				val, ctx.getResources().getDisplayMetrics());
	}

	public static String getDayOfMonthSuffix(final int n) {
		if (n >= 11 && n <= 13) {
			return "th";
		}
		switch (n % 10) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}

	public static Bitmap getBitmap(Uri image, Context ctx,
			ContentResolver mContentResolver) {

		Uri uri = image;
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 12000; // 1.2MP
			in = mContentResolver.openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			Log.d("TAG", "scale = " + scale + ", orig-width: " + o.outWidth
					+ ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in = mContentResolver.openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				Log.d("TAG", "1th scale operation dimenions - width: " + width
						+ ", height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}
			in.close();

			Log.d("TAG", "bitmap size - width: " + b.getWidth() + ", height: "
					+ b.getHeight());
			return b;
		} catch (IOException e) {
			Log.e("TAG", e.getMessage(), e);
			return null;
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, (int) (bitmap.getHeight() * 0.9),
				(bitmap.getHeight()));
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static int convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return (int) dp;
	}

	public static int convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	public static void RobotoMedium(Context context, TextView view) {
		view.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"Roboto-Medium.ttf"));
	}

	public static void RobotoRegular(Context context, TextView view) {
		view.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"Roboto-Regular.ttf"));
	}

	public static String getUserName(Context ctx) {
		Cursor c = ctx.getContentResolver().query(
				ContactsContract.Profile.CONTENT_URI, null, null, null, null);
		c.moveToFirst();
		String name = "Guest User";
		try {
			name = c.getString(c.getColumnIndex("display_name"));
		} catch (CursorIndexOutOfBoundsException cioe) {
			name = "Guest User";
		}
		c.close();
		return name;
	}

	public static ArrayList<String> getContactsList(Context ctx) {
		ArrayList<String> contactsList = new ArrayList<String>();
		Cursor phones = ctx.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			if (!phoneNumber.contains("+")) {
				phoneNumber = App.prefs.getCountryCode()
						+ phoneNumber.substring(1, phoneNumber.length());
			}
			Log.e(name, phoneNumber);
			contactsList.add(phoneNumber.replace(" ",""));
		}
		phones.close();
		return contactsList;
	}

	public static String getDate(long milliSeconds, String dateFormat) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

    public static String getInitials(String fName, String lName) {
        return fName.substring(0,1).toUpperCase()+""+lName.substring(0,1).toUpperCase();
    }

    public static long milliFromServerDate(String date){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        try {
            cal.setTime(sdf.parse(date));
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getReminderTime(String time){
        String[] splitTime = time.split(" ");
        int days = 0;
        try{
            days = Integer.parseInt(splitTime[0]);
        }catch (NumberFormatException nfe){
            return days;
        }
        int numbers = 1;
        if(splitTime[1].equalsIgnoreCase("mins"))
            numbers = Constants.MIN;
        else if(splitTime[1].equalsIgnoreCase("hours"))
            numbers = Constants.HOUR;
        else if(splitTime[1].equalsIgnoreCase("days"))
            numbers = Constants.DAY;
        else if(splitTime[1].equalsIgnoreCase("weeks"))
            numbers = Constants.WEEK;
        else if(splitTime[1].equalsIgnoreCase("months"))
            numbers = Constants.MONTH;
        else if(splitTime[1].equalsIgnoreCase("years"))
            numbers = Constants.YEAR;

        return days * numbers;


    }

	public static String getImageName(Context ctx, Uri uri){
		String fileName = "";
		String scheme = uri.getScheme();
		if (scheme.equals("file")) {
			fileName = uri.getLastPathSegment();
		}
		else if (scheme.equals("content")) {
			String[] proj = { MediaStore.Images.Media.TITLE };
			Cursor cursor = ctx.getContentResolver().query(uri, proj, null, null, null);
			if (cursor != null && cursor.getCount() != 0) {
				int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
				cursor.moveToFirst();
				fileName = cursor.getString(columnIndex);
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return fileName;
	}

	public static Bitmap getBitmap(Context ctx, Uri fileName){
		Bitmap bm = null;
		try {
			bm = MediaStore.Images.Media.getBitmap(ctx
					.getContentResolver(), fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bm;
	}

	public static byte[] getImageByteArray(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bm != null) {
			bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
		}
		return baos.toByteArray();
	}
}
