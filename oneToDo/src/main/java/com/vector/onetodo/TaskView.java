package com.vector.onetodo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.androidquery.AQuery;
import com.vector.onetodo.utils.Utils;

import net.appkraft.parallax.ParallaxScrollView;

public class TaskView extends Fragment {

    AQuery aq, aqd, aq_menu;
    AlertDialog alert;
    ParallaxScrollView parallax;
    ImageView image;
    private PopupWindow popupWindowTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewtask, container, false);
        parallax = (ParallaxScrollView) view.findViewById(R.id.scrollView123);
        image = (ImageView) view.findViewById(R.id.imageView1123);
        aq = new AQuery(getActivity(), view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View view1 = getActivity().getLayoutInflater().inflate(
                R.layout.landing_menu, null, false);
//        ShowTaskViewData();
        aq_menu = new AQuery(getActivity(), view1);
        aq_menu.id(R.id.menu_item1).text("RSVP");
        aq_menu.id(R.id.menu_item2).visibility(View.GONE);
        aq_menu.id(R.id.menu_item3).visibility(View.GONE);
        popupWindowTask = new PopupWindow(view1, Utils.getDpValue(200,
                getActivity()), WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindowTask.setBackgroundDrawable(getResources().getDrawable(
                android.R.drawable.dialog_holo_light_frame));
        popupWindowTask.setOutsideTouchable(true);

        View dialog = getActivity().getLayoutInflater().inflate(R.layout.rsvp,
                null, false);
        aqd = new AQuery(dialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialog);
        alert = builder.create();

        aqd.id(R.id.cancel).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alert.dismiss();
            }
        });
        aqd.id(R.id.ok).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alert.dismiss();
            }
        });

        aq_menu.id(R.id.menu_item1).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindowTask.dismiss();
                alert.show();
            }
        });
        aq.id(R.id.backview).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        aq.id(R.id.imageView4).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (popupWindowTask.isShowing())
                    popupWindowTask.dismiss();
                else {
                    popupWindowTask.showAsDropDown(aq.id(R.id.imageView4)
                            .getView(), 5, 10);
                }
            }
        });

        parallax.setImageViewToParallax(image);

    }
    static int p;
//    private static ArrayList<ItemDetails> GetSearchResults() {
//        ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();
//        for (p = 0; p < 8; p++) {
//            ItemDetails item_details = new ItemDetails();
//            item_details.setName("ok");
//            item_details.setImage("ok");
//            results.add(item_details);
//        }
//        return results;
//    }
//    public void ShowTaskViewData(){
//        ArrayList<ItemDetails> items = GetSearchResults();
//        aq.id(R.id.invitee_list).adapter(new InviteeAdapter(getActivity(), items));
//        int Position = getArguments().getInt("pos");
//        ToDoDao toDoDao = App.daoSession.getToDoDao();
//        RepeatDao repeatDao = App.daoSession.getRepeatDao();
//        ReminderDao reminderDao = App.daoSession.getReminderDao();
//        List<ToDo> todotemp = toDoDao.queryBuilder().list();
//        List<Repeat> repeattemp = repeatDao.queryBuilder().list();
//        List<Reminder> remindertemp = reminderDao.queryBuilder().list();
//        if(todotemp.size() !=0) {
//            for (int loop=Position;loop<todotemp.size();) {
//                aq.id(R.id.title).text(todotemp.get(loop).getTitle());
//                aq.id(R.id.repeat).text(repeattemp.get(loop).getRepeat_interval());
//                String strDate = String.valueOf(todotemp.get(loop).getStart_date());
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(Long.valueOf(strDate));
//                strDate = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
//                        Locale.US)
//                        + " "
//                        + calendar.get(Calendar.DAY_OF_MONTH)
//                        + " "
//                        + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
//                        Locale.US) + " " + calendar.get(Calendar.YEAR);
//                aq.id(R.id.time).text(strDate);
//                aq.id(R.id.location).text(todotemp.get(loop).getLocation());
//                aq.id(R.id.reminder).text("Remind before "+Integer.parseInt(remindertemp.get(loop).getTime().toString())/60000+" mins");
//                aq.id(R.id.repeat_view).text(repeattemp.get(loop).getRepeat_interval());
//                aq.id(R.id.notes).text(todotemp.get(loop).getNotes());
//                break;
//            }
//        }
//    }
}
