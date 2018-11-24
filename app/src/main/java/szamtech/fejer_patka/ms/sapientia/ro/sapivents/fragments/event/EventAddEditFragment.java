package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventPrefUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for adding/editing event details
 * TODO: Finish this
 */
public class EventAddEditFragment extends Fragment {
    private static final String TAG = "EventAddEditFragment";

    //This field is set to true, if the fragment was instantiated for editing
    //and false otherwise
    private static boolean sIsOpenForEditing;
    private static Event sEvent;

    @BindView(R.id.event_edit_add_save) Button eventSaveButton;
    @BindView(R.id.event_edit_add_change_image) Button eventChangeImage;
    @BindView(R.id.event_edit_add_name) EditText eventNameEditText;
    @BindView(R.id.event_edit_add_desc) EditText eventDescEditText;
    @BindView(R.id.event_edit_add_image) ImageView eventImageView;

    public EventAddEditFragment() {
        // Required empty public constructor
    }

    /**
     * Call this instantiator if you want to instantiate the fragment for editing
     * @param event the event to be edited
     * @return an EventAddEditFragment instance
     */
    public static EventAddEditFragment newInstanceForEditing(Event event) {
        sIsOpenForEditing = true;
        sEvent = event;
        return new EventAddEditFragment();
    }

    /**
     * Call this instantiator if you want to instantiate the fragment for adding
     * @return an EventAddEditFragment instance
     */
    public static EventAddEditFragment newInstanceForAdding(){
        sIsOpenForEditing = false;
        sEvent = new Event("placeholder", "placeholder");
        return new EventAddEditFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_add_edit, container, false);
        ButterKnife.bind(this, view);
        //If the fragment is opened for editing, then fill the EditText and ImageView views with
        //the event's details
        if(sIsOpenForEditing){
            eventNameEditText.setText(sEvent.getTitle());
            eventDescEditText.setText(sEvent.getDescription());
            Glide.with(getActivity())
                    .load(sEvent.getImages().get(0))
                    .apply(new RequestOptions().centerCrop())
                    .into(eventImageView);
        }

        return view;
    }

    /**
     * Save the event, whether it was opened for editing or adding
     * @param v
     */
    @OnClick(R.id.event_edit_add_save) void saveEvent(View v){
        //Set the name and description fields from the EditTexts
        sEvent.setTitle(eventNameEditText.getText().toString());
        sEvent.setDescription(eventDescEditText.getText().toString());
        Log.v(TAG, sEvent.toString());
        //Save the event
        EventPrefUtil.saveEvent(getActivity(),sEvent.getId()+"",sEvent);
        //Remove this fragment from the backstack
        FragmentNavigationUtil.back(getActivity(), 0);
    }

    //TODO: Figure this out
    private final int SELECT_PHOTO = 1;
    /**
     * Change the event's image
     * @param v
     */
    @OnClick(R.id.event_edit_add_change_image) void changeImage(View v){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    /**
     * Callback
     * @param requestCode
     * @param resultCode
     * @param imageReturnedIntent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    final Uri imageUri = imageReturnedIntent.getData();
                    //Set the URI to the event's image field
                    if(imageUri != null){
                        sEvent.addImage(imageUri.toString());
                        Log.v(TAG, imageUri.toString());
                    }
                    //Load the image
                    Glide.with(getActivity())
                            .load(sEvent.getImages().get(0))
                            .apply(new RequestOptions().centerCrop())
                            .into(eventImageView);


                }
                break;
        }
    }
}
