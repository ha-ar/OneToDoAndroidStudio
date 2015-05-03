package com.vector.onetodo;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.vector.model.TaskAdded;
import com.vector.onetodo.interfaces.onTaskAdded;
import com.vector.onetodo.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasanali on 6/03/15.
 */
public class AddToServer extends AsyncTask<String, Integer, Void> {
    HttpClient client;
    HttpPost post = new HttpPost();
    List<NameValuePair> pairs = new ArrayList<>();
    private String title;
    private int titleCheck;
    private String start_date;
    private String notes;
    private String end_date;
    private boolean is_location;
    private String before;
    private String r_time;
    private String r_location;
    private String location_tag;
    private String locationType;
    private boolean repeat_forever;
    private String repeatDate;
    private String location;
    private int MaxId, reminder;
    private List<String> comment = new ArrayList<>();
    private List<String> commentTime = new ArrayList<>();
    private String checklist_data;
    private ArrayList<String> assignedId = new ArrayList<>();
    private String repeat;
    private String label_name;
    private String labelColor;
    private onTaskAdded mCallBack;

    AddToServer(String title, int titleCheck, String start_date, String end_date,
                boolean is_location, String r_location, String location_tag, String locationType, String location,
                String notes, String repeatDate, boolean repeat_forever, int MaxId, List<String> comment,
                List<String> commentTime, String checklist_data, ArrayList<String> assignedID, String repeat, int reminder,
                String label_name, String labelColor, String before, String r_time, onTaskAdded mCallBack){

        this.title = title;
        this.titleCheck = titleCheck;
        this.start_date = start_date;
        this.end_date = end_date;
        this.notes = notes;
        this.is_location = is_location;
        this.before = before;
        this.r_time = r_time;
        this.r_location = r_location;
        this.location_tag = location_tag;
        this.locationType = locationType;
        this.repeat_forever = repeat_forever;
        this.repeatDate = repeatDate;
        this.MaxId = MaxId;
        this.comment = comment;
        this.commentTime = commentTime;
        this.checklist_data = checklist_data;
        this.assignedId = assignedID;
        this.repeat = repeat;
        this.label_name = label_name;
        this.labelColor = labelColor;
        this.reminder = reminder;
        this.location = location;
        this.mCallBack = mCallBack;

    }

    @Override
    protected Void doInBackground(String... params) {
        HttpResponse response = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            response = client.execute(post);
        } catch (IOException e1) {
            e1.printStackTrace();
            this.cancel(true);
        }
        String temp = "";
        try {
            temp = EntityUtils.toString(response.getEntity());
            Log.e("Task Added?", temp);
            Gson gson = new Gson();
            TaskAdded obj = gson.fromJson(temp,
                    TaskAdded.class);
            TaskAdded.getInstance().setObj(obj);
        } catch (org.apache.http.ParseException | IOException | NullPointerException e) {
            e.printStackTrace();
            this.cancel(true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        mCallBack.taskAdded();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        client = new DefaultHttpClient();
        post = new HttpPost("http://api.heuristix.net/one_todo/v1/task/add");
        pairs = new ArrayList<>();

        pairs.add(new BasicNameValuePair("todo[user_id]", App.prefs
                .getUserId() + ""));

        if (titleCheck != -1)
            pairs.add(new BasicNameValuePair("todo[todo_type_id]",
                    titleCheck + ""));

        pairs.add(new BasicNameValuePair("todo[title]", title));
        if (start_date != null)
            pairs.add(new BasicNameValuePair("todo[start_date]", start_date
                    + ""));
        if (end_date != null) {
            pairs.add(new BasicNameValuePair("todo[end_date]", end_date));
        }

        if (notes != null)
            pairs.add(new BasicNameValuePair("todo[notes]", notes));

        if (before != null) {
            if (!is_location) {
                pairs.add(new BasicNameValuePair("todo_reminder[time]",
                        reminder+""));
            } else {
                pairs.add(new BasicNameValuePair("todo_reminder[location]",
                        r_location));

                if (!location_tag.equals("New")) {
                    pairs.add(new BasicNameValuePair(
                            "todo_reminder[location_tag]", location_tag));
                }
                pairs.add(new BasicNameValuePair(
                        "todo_reminder[location_type]", locationType));
            }
        }
        if (repeat != null) {
            pairs.add(new BasicNameValuePair(
                    "todo_repeat[repeat_interval]", repeat));

            if (!repeat_forever)
                pairs.add(new BasicNameValuePair(
                        "todo_repeat[repeat_until]", repeatDate));
        }
        if (location != null) {
            pairs.add(new BasicNameValuePair(
                    "todo[location]", location));
        }

        for (int i = 1; i <= MaxId; i++) {
            pairs.add(new BasicNameValuePair("todo_attachment[" + (i - 1)
                    + "][attachment_path]", App.attach.getString(titleCheck
                    + "path" + i, null)));
        }

        MaxId = 0;
        Log.e("comment size", comment+"");
        if ((comment != null) && (comment.size() > 0) && (commentTime != null)) {
            for (int i = 0; i < comment.size(); i++) {
                pairs.add(new BasicNameValuePair("todo_comment[" + (i - 1)
                        + "][user_id]", Constants.user_id + ""));
                pairs.add(new BasicNameValuePair("todo_comment[" + (i - 1)
                        + "][comment]", comment.get(i)));
                pairs.add(new BasicNameValuePair("todo_comment[" + (i - 1)
                        + "][date_time]", commentTime.get(i)));
            }
        }

        if (checklist_data != null)
            pairs.add(new BasicNameValuePair(
                    "todo_checklist[checklist_data]", checklist_data));

        if (assignedId.size() != 0) {
            if (titleCheck == 2) {
                for (int i = 0; i < assignedId.size(); i++) {
                    pairs.add(new BasicNameValuePair("todo_collaborate["
                            + i + "][assignee_id]", String
                            .valueOf(App.prefs.getUserId())));
                    pairs.add(new BasicNameValuePair("todo_collaborate["
                            + i + "][invitee_id]", assignedId.get(i)));
                }
            }
            else {
                pairs.add(new BasicNameValuePair(
                        "todo_collaborate[assignee_id]", String
                        .valueOf(App.prefs.getUserId())));
                pairs.add(new BasicNameValuePair(
                        "todo_collaborate[invitee_id]", assignedId.get(0)));
            }
        }

        for (int i = 0; i < MaxId; i++) {
            pairs.add(new BasicNameValuePair("todo_attachment[" + i
                    + "][attachment_path]", App.attach.getString(titleCheck
                    + "path" + i, null)));
        }
        pairs.add(new BasicNameValuePair(
                "todo_label[label_name]", label_name));
        pairs.add(new BasicNameValuePair(
                "todo_label[label_color]", labelColor));
        MaxId = 0;

    }
}
