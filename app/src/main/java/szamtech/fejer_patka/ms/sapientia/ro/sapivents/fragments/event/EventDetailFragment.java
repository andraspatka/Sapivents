package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;

/**
 * Fragment for listing an event's details
 */
public class EventDetailFragment extends Fragment {

    private static Event sEvent;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        TextView eventName = (TextView) view.findViewById(R.id.event_detail_name);
        TextView eventDesc = (TextView) view.findViewById(R.id.event_detail_desc);
        ImageView eventImage = (ImageView) view.findViewById(R.id.event_detail_image);
        eventName.setText(sEvent.getTitle());
        eventDesc.setText(sEvent.getDescription());
        Glide.with(getContext())
                .load(sEvent.getImages().get(0))
                .into(eventImage);

        return view;
    }
}
