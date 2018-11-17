package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventPrefUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventsAdapter;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

/**
 * Fragment for listing the Events
 */
public class EventListFragment extends Fragment implements EventsAdapter.EventListItemOnClickInterface {
    private List<Event> mEvents = new ArrayList<>();
    private EventsAdapter mEventsAdapter;

    private static final String TAG = "EventListFragment";
    @BindView(R.id.event_list_recycler_view) RecyclerView recyclerView;

    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        ButterKnife.bind(this, view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        //TODO: Create custom ItemDecorator class
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        updateData();

        return view;
    }

    /**
     * Gets data from sharedPreferences
     * TODO: optimize this, if possible
     */
    private void updateData(){
        //Get the data from SharedPreferences
        mEvents = EventPrefUtil.getAllValues(getActivity());
        //Create the adapter
        mEventsAdapter = new EventsAdapter(mEvents, getContext(), this);
        //Set the adapter to the recyclerview
        recyclerView.setAdapter(mEventsAdapter);
        Log.d(TAG, mEvents.toString());
        //Notify the adapter that the dataset has been refreshed
        mEventsAdapter.notifyDataSetChanged();
    }

    /**
     * Implemented method of
     * {@link szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventsAdapter.EventListItemOnClickInterface}
     * Gets called when a list item is clicked
     * @param event the event which got clicked
     */
    @Override
    public void onClickEventItem(Event event) {
        EventDetailFragment eventDetailFragment = EventDetailFragment.newInstance(event);
        FragmentNavigationUtil.addFragmentOnTop(
                getContext(),
                eventDetailFragment,
                R.id.fragment_place
        );
    }
}
