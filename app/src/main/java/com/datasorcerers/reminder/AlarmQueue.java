package com.datasorcerers.reminder;

import android.content.Intent;

import java.util.LinkedList;
import java.util.Queue;

public class AlarmQueue {
    private static AlarmQueue instance;
    private Queue<Intent> intentQueue;

    private AlarmQueue () {
    }

    public static AlarmQueue getInstance() {
        if (null == instance) {
            instance = new AlarmQueue();
            instance.intentQueue = new LinkedList<>();
        }
        return instance;
    }

    public boolean isEmpty() {
        return intentQueue.isEmpty();
    }

    public void add(Intent i) {
        intentQueue.add(i);
    }

    public Intent poll() {
        return intentQueue.poll();
    }

    public Intent peek() {
        return intentQueue.peek();
    }
}
