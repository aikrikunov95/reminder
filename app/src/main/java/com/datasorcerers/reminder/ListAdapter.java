package com.datasorcerers.reminder;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private SortedList<Alarm> mAlarms;
    private ViewHolder.AlarmClickListener mClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView note, date;
        private AlarmClickListener clickListener;

        public ViewHolder(View v, AlarmClickListener listener) {
            super(v);
            clickListener = listener;
            note = (TextView) v.findViewById(R.id.alarm_note);
            date = (TextView) v.findViewById(R.id.alarm_date);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onAlarmClick(v, getLayoutPosition());
        }

        public static interface AlarmClickListener {
            public void onAlarmClick(View caller, int pos);
        }
    }

    public ListAdapter(List<Alarm> alarms, ViewHolder.AlarmClickListener listener) {
        this.mClickListener = listener;
        this.mAlarms = new SortedList<>(Alarm.class, new SortedList.Callback<Alarm>() {
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
                /*boolean idsAreSame = oldItem.getId() == newItem.getId();
                boolean notesAreSame = oldItem.getNote().equals(newItem.getNote());
                boolean datesAreSame = oldItem.getDatetime() == newItem.getDatetime();
                return idsAreSame && notesAreSame && datesAreSame;*/
                return areItemsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areItemsTheSame(Alarm item1, Alarm item2) {
                return item1.getId() == item2.getId();
            }
        });
        this.mAlarms.addAll(alarms);
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(v, mClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Alarm alarm = mAlarms.get(position);
        if (!alarm.isNotified()) {
            holder.note.setText(alarm.getNote());
        } else {
            String text = alarm.getNote() + " missed";
            holder.note.setText(text);
        }
        holder.date.setText(new DateTime(alarm.getDatetime()).toString());
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    public SortedList<Alarm> getmAlarms() {
        return mAlarms;
    }
}
