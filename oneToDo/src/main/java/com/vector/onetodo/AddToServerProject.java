package com.vector.onetodo;

import android.os.AsyncTask;
import android.util.Log;

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
public class AddToServerProject extends AsyncTask<String, Integer, Void> {
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
    private int MaxId;
    private List<String> comment = new ArrayList<>();
    private List<String> commentTime = new ArrayList<>();
    private String checklist_data;
    private ArrayList<String> assignedId = new ArrayList<>();
    private String repeat;
    private String label_name;
    private String labelColor;

    AddToServerProject(String title, String start_date, String end_date,
                       ArrayList<String> assignedID,
                       String label_name, String labelColor){

        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.assignedId = assignedID;
        this.label_name = label_name;
        this.labelColor = labelColor;

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
        } catch (org.apache.http.ParseException | IOException | NullPointerException e) {
            e.printStackTrace();
            this.cancel(true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

//        dialog.dismiss();
//        finish();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        client = new DefaultHttpClient();
        post = new HttpPost("http://api.heuristix.net/one_todo/v1/project");
        pairs = new ArrayList<>();

        pairs.add(new BasicNameValuePair("project[user_id]", App.prefs
                .getUserId() + ""));

        pairs.add(new BasicNameValuePair("project[title]", title));
        if (start_date != null)
            pairs.add(new BasicNameValuePair("project[start_date]", start_date
                    + ""));
        if (end_date != null) {
            pairs.add(new BasicNameValuePair("project[end_date]", end_date));
        }

        if (notes != null)
            pairs.add(new BasicNameValuePair("project[notes]", notes));

        pairs.add(new BasicNameValuePair("project[parent]", "0"));

        if(!AddProjectFragment.projectsSubTaskArray.isEmpty()){
            int size = AddProjectFragment.projectsSubTaskArray.size();
            for(int i = 0; i < size; i++){
                pairs.add(new BasicNameValuePair("project_tasks["+i+"][todo][user_id]", String.valueOf(App.prefs.getUserId())));
                pairs.add(new BasicNameValuePair("project_tasks["+i+"][todo][title]", AddProjectFragment.projectsSubTaskArray.get(i).title));
                pairs.add(new BasicNameValuePair("project_tasks["+i+"][todo][start_date]", AddProjectFragment.projectsSubTaskArray.get(i).startDate));
                pairs.add(new BasicNameValuePair("project_tasks["+i+"][todo][end_date]", AddProjectFragment.projectsSubTaskArray.get(i).endDate));
                pairs.add(new BasicNameValuePair("project_tasks["+i+"][todo][priority]", "0"));
                pairs.add(new BasicNameValuePair("project_tasks["+i+"][todo][notes]", AddProjectFragment.projectsSubTaskArray.get(i).notes));
                pairs.add(new BasicNameValuePair("project_invitees[assignee_id]", String.valueOf(App.prefs.getUserId())));
                pairs.add(new BasicNameValuePair("project_invitees[invitee_id]", AddTaskFragment.assignedSelectedID));
                pairs.add(new BasicNameValuePair("project_label[label_name]", AddProjectFragment.projectsSubTaskArray.get(i).labelName));
                pairs.add(new BasicNameValuePair("project_label[label_color]", AddProjectFragment.projectsSubTaskArray.get(i).labelColor));
            }
        }


        if (assignedId.size() != 0) {
            for (int i = 0; i < assignedId.size(); i++) {
                pairs.add(new BasicNameValuePair("todo_collaborate["
                        + i + "][assignee_id]", String
                        .valueOf(App.prefs.getUserId())));
                pairs.add(new BasicNameValuePair("todo_collaborate["
                        + i + "][invitee_id]", assignedId.get(i)));
            }
        }

        for (int i = 0; i < MaxId; i++) {
            pairs.add(new BasicNameValuePair("todo_attachment[" + i
                    + "][attachment_path]", App.attach.getString(titleCheck
                    + "path" + i, null)));
        }

    }
}
