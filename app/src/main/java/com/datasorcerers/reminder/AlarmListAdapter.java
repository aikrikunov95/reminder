package com.datasorcerers.reminder;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder> {
    private SortedList<Alarm> alarms;
    private ViewHolder.IViewHolderClick listener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView note, date;
        private IViewHolderClick clickListener;

        public ViewHolder(View v, IViewHolderClick listener) {
            super(v);
            clickListener = listener;
            note = (TextView) v.findViewById(R.id.alarm_note);
            date = (TextView) v.findViewById(R.id.alarm_date);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.showEditDialog(v, getLayoutPosition());
        }

        public static interface IViewHolderClick {
            public void showEditDialog(View caller, int pos);
        }

    }

    public AlarmListAdapter(List<Alarm> alarms, ViewHolder.IViewHolderClick listener) {
        this.listener = listener;
        this.alarms = new SortedList<>(Alarm.class, new SortedList.Callback<Alarm>() {
            @Override
            public int compare(Alarm o1, Alarm o2) {
                return (int) (o1.getDatetime() - o2.getDatetime());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Alarm oldItem, Alarm newItem) {
                return areItemsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(Alarm item1, Alarm item2) {
                boolean notesSame = item1.getNote().equals(item2.getNote());
                boolean datesSame = item1.getDatetime() == item2.getDatetime();
                return notesSame && datesSame;
            }
        });
        this.alarms.addAll(alarms);
    }

    @Override
    public AlarmListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false);

        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.note.setText(alarms.get(position).getNote());
        holder.date.setText(formatDate(alarms.get(position).getDatetime()));
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    /*
    * alarmlist helper methods
    */

    public Alarm get(int position) {
        return alarms.get(position);
    }

    public int add(Alarm item) {
        return alarms.add(item);
    }

    public int indexOf(Alarm item) {
        return alarms.indexOf(item);
    }

    public void updateItemAt(int index, Alarm item) {
        alarms.updateItemAt(index, item);
    }

    public void addAll(List<Alarm> items) {
        alarms.beginBatchedUpdates();
        for (Alarm item : items) {
            alarms.add(item);
        }
        alarms.endBatchedUpdates();
    }

    public boolean remove(Alarm item) {
        return alarms.remove(item);
    }

    public Alarm removeItemAt(int index) {
        return alarms.removeItemAt(index);
    }

    public void clear() {
        alarms.beginBatchedUpdates();
        while (alarms.size() > 0) {
            alarms.removeItemAt(alarms.size() - 1);
        }
        alarms.endBatchedUpdates();
    }

    private String formatDate(long data) {
        DateTime dt = new DateTime(data);
        return dt.toString();
    }
}
