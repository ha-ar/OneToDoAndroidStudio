package com.vector.onetodo;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasanali on 11/03/15.
 */
public class SubTask {

    public String title;
    public String startDate;
    public String endDate;
    public String location;
    public int reminderTime;
    public boolean isLocationBased;
    public String reminderLocation;
    public String repeatInterval;
    public String labelName;
    public String labelColor;
    public String subTask;
    public String notes;
    public List<String> comments = new ArrayList<>();
    public List<String> attachments = new ArrayList<>();
    public View titleView;
    public long repeatTime;

    private class Comment {
        String commentText;
        long commentTime;
    }

    private class Attachment {
        String attachmentName;
        long attachmentTime;
    }
}
