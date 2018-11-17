package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;

/**
 * Fragment for adding/editing event details
 * TODO: Finish this
 */
public class EventAddEditFragment extends Fragment {
    private static final String TAG = "EventAddEditFragment";

    public EventAddEditFragment() {
        // Required empty public constructor
    }

    public static EventAddEditFragment newInstance(String param1, String param2) {
        EventAddEditFragment fragment = new EventAddEditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_add_edit, container, false);
    }
}
