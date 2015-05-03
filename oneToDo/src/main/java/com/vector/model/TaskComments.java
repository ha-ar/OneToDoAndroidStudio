package com.vector.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hasanali on 31/03/15.
 */
public class TaskComments {

    public static TaskComments task = null;


    public static TaskComments getInstance() {
        if (task == null) {
            task = new TaskComments();
        }
        return task;
    }

    public void setObj(TaskComments obj) {
        task = obj;
    }

    public boolean error;

    @SerializedName("task")
    public ArrayList<Comment> comments = new ArrayList<>();

    public class Comment{
        public String comment;
        public String name;
        public String time;
    }

    public void addComment(String comment, String name, String time){
        Comment mComm = new Comment();
        mComm.comment = comment;
        mComm.name = name;
        mComm.time = time;
        comments.add(mComm);
    }

}
