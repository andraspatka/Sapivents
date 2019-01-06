package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventAddEditFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventsAdapter;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

public class UserEventListFragment extends Fragment implements EventsAdapter.EventListItemOnClickInterface {
    private List<Event> mEvents = new ArrayList<>();
    private EventsAdapter mEventsAdapter;

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;

    private static final String TAG = "EventListFragment";
    @BindView(R.id.user_event_list_recycler_view) RecyclerView recyclerView;

    public UserEventListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_event_list, container, false);

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

        final String actualPhoneNumber = mFirebaseUser.getPhoneNumber();
        Log.v(TAG, "phone:" + actualPhoneNumber);
        Query listEventQuery = mDatabase.child("events").orderByChild("author").equalTo(actualPhoneNumber);
        listEventQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, "onDataChange()");
                mEvents = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Event actualEvent = postSnapshot.getValue(Event.class);
                    mEvents.add(actualEvent);
                    Log.v(TAG, actualEvent.getTitle());

                }
                mEventsAdapter = new EventsAdapter(mEvents, getContext(), UserEventListFragment.this);
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
    public void onClickEventItem(final Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle(R.string.pick_color)
        String options[] ={"Edit", "Delete"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                switch(which){
                    case 0:
                        EventAddEditFragment eventAddEditFragment = EventAddEditFragment.newInstanceForEditing(event);
                        FragmentNavigationUtil.addFragmentToScreen(
                                getContext(),
                                eventAddEditFragment,
                                R.id.fragment_place,
                                FragmentNavigationUtil.ACCOUNT_SCREEN);
                        break;
                    case 1:

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setTitle("Deleting event...");
                        builder.setMessage("Are you sure you want to delete event: " + event.getTitle() + "?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                deleteEventFromDatabase(event.getKey());
                                updateData();
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                        break;
                }
            }
        });
        builder.show();
    }

    public void deleteEventFromDatabase(String eventId){
        DatabaseReference ref = mDatabase.child("events/" + eventId);
        Task task = ref.removeValue();
        task.addOnCompleteListener(getActivity(), new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Error deleting event, please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
