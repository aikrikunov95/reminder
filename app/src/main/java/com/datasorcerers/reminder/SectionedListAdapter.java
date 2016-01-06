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

public class SectionedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private static final int SECTION_TYPE = 0;

    private boolean isValid = true;
    private int sectionResourceId;
    private int textResourceId;
    private LayoutInflater layoutInflater;
    private ListAdapter baseAdapter;
    private SparseArray<Section> sections = new SparseArray<Section>();

    public SectionedListAdapter(Context context, int sectionResourceId, int textResourceId,
                                RecyclerView.Adapter baseAdapter) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sectionResourceId = sectionResourceId;
        this.textResourceId = textResourceId;
        this.baseAdapter = (ListAdapter) baseAdapter;
        mContext = context;

        this.baseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                isValid = SectionedListAdapter.this.baseAdapter.getItemCount() > 0;
                setSections();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                isValid = SectionedListAdapter.this.baseAdapter.getItemCount() > 0;
                setSections();
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                isValid = SectionedListAdapter.this.baseAdapter.getItemCount() > 0;
                setSections();
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                isValid = SectionedListAdapter.this.baseAdapter.getItemCount() > 0;
                setSections();
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }


    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public SectionViewHolder(View view,int mTextResourceid) {
            super(view);
            title = (TextView) view.findViewById(mTextResourceid);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(sectionResourceId, parent, false);
            return new SectionViewHolder(view, textResourceId);
        }else{
            return baseAdapter.onCreateViewHolder(parent, typeView-1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((SectionViewHolder)sectionViewHolder).title.setText(sections.get(position).title);
        }else{
            baseAdapter.onBindViewHolder((ListAdapter.ViewHolder) sectionViewHolder, sectionedPositionToPosition(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : baseAdapter.getItemViewType(sectionedPositionToPosition(position))+1 ;
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
        this.sections.clear();

        SortedList<Alarm> alarms = baseAdapter.getAll();
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
            this.sections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < sections.size(); i++) {
            if (sections.valueAt(i).firstPosition > position) {
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
        for (int i = 0; i < sections.size(); i++) {
            if (sections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return sections.get(position) != null;
    }


    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - sections.indexOfKey(position)
                : baseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (isValid ? baseAdapter.getItemCount() + sections.size() : 0);
    }

}

