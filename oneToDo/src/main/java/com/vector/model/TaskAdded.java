package com.vector.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hasanali on 31/03/15.
 */
public class TaskAdded {

    public static TaskAdded task = null;


    public static TaskAdded getInstance() {
        if (task == null) {
            task = new TaskAdded();
        }
        return task;
    }

    public void setObj(TaskAdded obj) {
        task = obj;
    }

    public String error;

    @SerializedName("result")
    public int id;

    public String message;
}
