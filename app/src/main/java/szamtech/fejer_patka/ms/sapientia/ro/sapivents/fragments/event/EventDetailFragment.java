package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;

/**
 * Fragment for listing an event's details
 */
public class EventDetailFragment extends Fragment {

    @BindView(R.id.event_detail_name) TextView eventName;
    @BindView(R.id.event_detail_desc) TextView eventDescription;
    @BindView(R.id.event_detail_date) TextView eventDate;
    @BindView(R.id.event_detail_location) TextView eventLocation;
    @BindView(R.id.event_detail_image) ImageView eventImageView;
    @BindView(R.id.event_detail_prev_image_button) ImageButton eventPrevImage;
    @BindView(R.id.event_detail_next_image_button) ImageButton eventNextImage;
    @BindView(R.id.event_detail_attendants_number) TextView eventAttendantsNumber;
    @BindView(R.id.event_detail_progressBar) ProgressBar mProgressBar;
    @BindView(R.id.event_detail_attend_button) Button attendButton;

    private static final String TAG = "EventDetailFragment";
    private static Event sEvent;
    private int imageIndex = 0;
    private DatabaseReference mDatabase;

    public EventDetailFragment() {
        // Required empty public constructor
    }

    public static EventDetailFragment newInstance(Event event){
        EventDetailFragment eventDetailFragment = new EventDetailFragment();
        sEvent = event;
        return eventDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference("events");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        ButterKnife.bind(this,view);

        if(sEvent != null){
            eventName.setText(sEvent.getTitle());
            eventDescription.setText(sEvent.getDescription());
            eventDate.setText(sEvent.getEventDate().toString());
            eventLocation.setText(sEvent.getLocation());
            eventAttendantsNumber.setText(Integer.toString(sEvent.getAttendants()));
            if(!sEvent.getImages().isEmpty()) {
                Glide.with(getContext())
                        .load(sEvent.getImages().get(0))
                        .listener(new RequestListener<Drawable>() {

                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                          }
                        )
                        .apply(new RequestOptions().centerCrop())
                        .into(eventImageView);
            }
        }
        return view;
    }

    /**
     * OnClick for the nextImage button
     * Loads the next image from the event's images ArrayList
     * @param v Button's view
     */
    @OnClick(R.id.event_detail_prev_image_button) void nextImage(View v){
        mProgressBar.setVisibility(View.VISIBLE);
        if(!sEvent.getImages().isEmpty()){
            ++imageIndex;
            imageIndex = imageIndex % sEvent.getImages().size();
            Glide.with(getActivity())
                    .load(sEvent.getImages().get(imageIndex))
                    .listener(new RequestListener<Drawable>() {

                                  @Override
                                  public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                      mProgressBar.setVisibility(View.GONE);
                                      return false;
                                  }

                              }
                    )
                    .apply(new RequestOptions().centerCrop())
                    .into(eventImageView);
        }
    }
    /**
     * OnClick for the prevImage button
     * Loads the previous image from the event's images ArrayList
     * @param v Button's view
     */
    @OnClick(R.id.event_detail_next_image_button) void prevImage(View v){
        mProgressBar.setVisibility(View.VISIBLE);
        if(!sEvent.getImages().isEmpty()){
            if(imageIndex == 0){
                imageIndex = sEvent.getImages().size()-1;
            }else{
                --imageIndex;
            }
            Glide.with(getActivity())
                    .load(sEvent.getImages().get(imageIndex))
                    .listener(new RequestListener<Drawable>() {

                                  @Override
                                  public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                      mProgressBar.setVisibility(View.GONE);
                                      return false;
                                  }

                              }
                    )
                    .apply(new RequestOptions().centerCrop())
                    .into(eventImageView);
        }
    }

    @OnClick(R.id.event_detail_attend_button) void attendToEvent(View v){
        final String btnTitle = attendButton.getText().toString();

        DatabaseReference getNumberOfAtendants = mDatabase.child(sEvent.getKey() + "/attendants");

        getNumberOfAtendants.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numberOfAttendants = dataSnapshot.getValue(Integer.class);

                if(btnTitle.equalsIgnoreCase("attend")){
                    attendButton.setText("Absent");
                    ++numberOfAttendants;
                }
                else{
                    attendButton.setText("Attend");
                    --numberOfAttendants;
                }

                changeEventAttendantsNumber(sEvent.getKey(), numberOfAttendants, btnTitle);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void changeEventAttendantsNumber(String key, final int actualNumber, final String state){

        mDatabase.child(key + "/attendants").setValue(actualNumber)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        eventAttendantsNumber.setText(String.valueOf(actualNumber));
                        Toast.makeText(getActivity(), "Event attendance set to: " +
                               state + "!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
