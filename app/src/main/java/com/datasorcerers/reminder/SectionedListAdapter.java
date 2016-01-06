package com.datasorcerers.reminder;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SectionedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private int mSectionResourceId;
    private int mTextResourceId;
    private LayoutInflater mLayoutInflater;
    private ListAdapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<>();

    public SectionedListAdapter(Context context, int sectionResourceId, int textResourceId,
                                List<Alarm> alarms, ListAdapter.ViewHolder.AlarmClickListener alarmClickListener) {

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mSectionResourceId = sectionResourceId;
        this.mTextResourceId = textResourceId;
        this.mBaseAdapter = new ListAdapter(alarms, alarmClickListener);
        mContext = context;

        this.mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = SectionedListAdapter.this.mBaseAdapter.getItemCount() > 0;
                setSections();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = SectionedListAdapter.this.mBaseAdapter.getItemCount() > 0;
                setSections();
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = SectionedListAdapter.this.mBaseAdapter.getItemCount() > 0;
                setSections();
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = SectionedListAdapter.this.mBaseAdapter.getItemCount() > 0;
                setSections();
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }


    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public SectionViewHolder(View view,int textResourceId) {
            super(view);
            title = (TextView) view.findViewById(textResourceId);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false);
            return new SectionViewHolder(view, mTextResourceId);
        }else{
            return mBaseAdapter.onCreateViewHolder(parent, typeView-1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((SectionViewHolder)sectionViewHolder).title.setText(mSections.get(position).title);
        }else{
            mBaseAdapter.onBindViewHolder((ListAdapter.ViewHolder) sectionViewHolder, sectionedPositionToPosition(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position))+1 ;
    }


    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;

        public Section(int firstPosition, CharSequence title) {
            this.firstPosition = firstPosition;
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }
    }


    public void setSections() {
        this.mSections.clear();

        SortedList<Alarm> alarms = mBaseAdapter.getAlarms();
        ArrayList<DateTime> alarmDates = new ArrayList<>();
        ArrayList<Section> sectionsList = new ArrayList<>();
        for (int i = 0; i < alarms.size(); i++) {
            Alarm a = alarms.get(i);
            DateTime date = new DateTime(a.getDatetime())
                    .withHourOfDay(0)
                    .withMinuteOfHour(0)
                    .withSecondOfMinute(0)
                    .withMillisOfSecond(0);
            if (!alarmDates.contains(date)) {
                alarmDates.add(date);
                sectionsList.add(new Section(i, date.toString()));
            }
        }

        Collections.sort(sectionsList, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sectionsList) {
            section.sectionedPosition = section.firstPosition + offset;
            this.mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }


    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }
    
    /*
    * alarm list helper methods
    */

    public Alarm get(int position) {
        return mBaseAdapter.getAlarms().get(position);
    }

    public SortedList<Alarm> getAlarms() {
        return mBaseAdapter.getAlarms();
    }

    public int add(Alarm item) {
        return mBaseAdapter.getAlarms().add(item);
    }

    public int indexOf(Alarm item) {
        return mBaseAdapter.getAlarms().indexOf(item);
    }

    public void updateItemAt(int index, Alarm item) {
        mBaseAdapter.getAlarms().updateItemAt(index, item);
    }

    public int indexOfId(int id) {
        for (int i = 0; i < mBaseAdapter.getAlarms().size(); i++) {
            if (id == mBaseAdapter.getAlarms().get(i).getId()) {
                return indexOf(mBaseAdapter.getAlarms().get(i));
            }
        }
        return -1;
    }

    public void updateItem(Alarm alarm) {
        updateItemAt(indexOfId(alarm.getId()), alarm);

    }

    public void addAll(List<Alarm> items) {
        mBaseAdapter.getAlarms().beginBatchedUpdates();
        for (Alarm item : items) {
            mBaseAdapter.getAlarms().add(item);
        }
        mBaseAdapter.getAlarms().endBatchedUpdates();
    }

    public boolean remove(Alarm item) {
        int id = indexOfId(item.getId());
        if (id >= 0) {
            removeItemAt(id);
            return true;
        } else {
            return false;
        }
    }

    public Alarm removeItemAt(int index) {
        return mBaseAdapter.getAlarms().removeItemAt(index);
    }

    public void clear() {
        mBaseAdapter.getAlarms().beginBatchedUpdates();
        while (mBaseAdapter.getAlarms().size() > 0) {
            mBaseAdapter.getAlarms().removeItemAt(mBaseAdapter.getAlarms().size() - 1);
        }
        mBaseAdapter.getAlarms().endBatchedUpdates();
    }

    public void notifyAlarmsDataSetChanged() {
        mBaseAdapter.notifyDataSetChanged();
    }

}

