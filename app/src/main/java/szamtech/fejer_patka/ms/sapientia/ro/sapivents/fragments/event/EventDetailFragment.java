package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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

    private static Event sEvent;
    private int imageIndex = 0;

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
            eventDate.setText(sEvent.getDate().toString());
            eventLocation.setText(sEvent.getLocation());
            if(sEvent.getAttendants() != null){
                eventAttendantsNumber.setText(0 + "");
                if(!sEvent.getAttendants().isEmpty()){
                    eventAttendantsNumber.setText(sEvent.getAttendants().size()+"");
                }
            }
            if(!sEvent.getImages().isEmpty()) {
                Glide.with(getContext())
                        .load(sEvent.getImages().get(0))
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
        if(!sEvent.getImages().isEmpty()){
            ++imageIndex;
            imageIndex = imageIndex % sEvent.getImages().size();
            Glide.with(getActivity())
                    .load(sEvent.getImages().get(imageIndex))
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
        if(!sEvent.getImages().isEmpty()){
            if(imageIndex == 0){
                imageIndex = sEvent.getImages().size()-1;
            }else{
                --imageIndex;
            }
            Glide.with(getActivity())
                    .load(sEvent.getImages().get(imageIndex))
                    .apply(new RequestOptions().centerCrop())
                    .into(eventImageView);
        }
    }
}
