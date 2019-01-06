package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventsAdapter;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

/**
 * Fragment for listing the Events
 */
public class EventListFragment extends Fragment implements EventsAdapter.EventListItemOnClickInterface {

    private List<Event> mEvents = new ArrayList<>();
    private EventsAdapter mEventsAdapter;

    private DatabaseReference mDatabase;

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

    private void updateData(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Query listEventQuery = mDatabase.child("events").orderByChild("eventDate/date");
        listEventQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, "onDataChange()");
                mEvents = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Event actualEvent = postSnapshot.getValue(Event.class);
                    if(actualEvent != null && actualEvent.isPublished()){
                        mEvents.add(actualEvent);
                    }
                }
                mEventsAdapter = new EventsAdapter(mEvents, getContext(), EventListFragment.this);
                recyclerView.setAdapter(mEventsAdapter);
                mEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * Implemented method of
     * {@link szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventsAdapter.EventListItemOnClickInterface}
     * Gets called when a list item is clicked
     * Opens the EventDetailFragment fragment and passes in @param event
     * @param event the event which got clicked
     */
    @Override
    public void onClickEventItem(Event event) {
        EventDetailFragment eventDetailFragment = EventDetailFragment.newInstance(event);
        FragmentNavigationUtil.addFragmentToScreen(
                getContext(),
                eventDetailFragment,
                R.id.fragment_place,
                FragmentNavigationUtil.HOME_SCREEN
        );
    }
}
