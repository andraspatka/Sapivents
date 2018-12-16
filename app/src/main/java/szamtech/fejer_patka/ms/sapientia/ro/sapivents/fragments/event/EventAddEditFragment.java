package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.DateTime;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventPrefUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for adding/editing event details
 */
public class EventAddEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "EventAddEditFragment";

    //This field is set to true, if the fragment was instantiated for editing
    //and false otherwise
    private static boolean sIsOpenForEditing;
    private static Event sEvent;

    @BindView(R.id.event_edit_add_save_button) Button eventSaveButton;
    @BindView(R.id.event_edit_add_change_image_button) Button eventChangeImage;
    @BindView(R.id.event_edit_add_name_edit_text) EditText eventNameEditText;
    @BindView(R.id.event_edit_add_desc_edit_text) EditText eventDescEditText;
    @BindView(R.id.event_edit_add_image) ImageView eventImageView;
    @BindView(R.id.event_edit_add_next_image_button) ImageButton nextImage;
    @BindView(R.id.event_edit_add_prev_image_button) ImageButton prevImages;
    @BindView(R.id.event_edit_add_date_edit_text) EditText eventDateEditText;
    @BindView(R.id.event_edit_add_location_edit_text) EditText eventLocationEditText;
    @BindView(R.id.event_edit_add_published) ImageButton eventPublishedButton;

    private int imageIndex = 0;

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
            eventDateEditText.setText(sEvent.getDate().toString());
            eventLocationEditText.setText(sEvent.getLocation());
            if(!sEvent.getImages().isEmpty()){
                Glide.with(getActivity())
                        .load(sEvent.getImages().get(0))
                        .apply(new RequestOptions().centerCrop().override(132,132))
                        .into(eventImageView);
                eventPublishedButton.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    /**
     * Save the event, whether it was opened for editing or adding
     * @param v button's view
     */
    @OnClick(R.id.event_edit_add_save_button) void saveEvent(View v){

        Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        if(eventNameEditText.getText().toString().equals("")){
            toast.setText("Please add a title to the event");
            toast.show();
        }else if(eventDescEditText.getText().toString().equals("")) {
            toast.setText("Please add a description to the event");
            toast.show();
        }else if(eventDateEditText.getText().toString().equals("")) {
            toast.setText("Please set a date and time to the event");
            toast.show();
        }else if(eventLocationEditText.getText().toString().equals("")){
            toast.setText("Please add a location to the event");
            toast.show();
        }else{
            //Set the name, description, location and date fields from the EditTexts
            sEvent.setTitle(eventNameEditText.getText().toString());
            sEvent.setDescription(eventDescEditText.getText().toString());
            sEvent.setLocation(eventLocationEditText.getText().toString());
            Log.v(TAG, eventDateEditText.getText().toString());
            sEvent.setDate(new DateTime(eventDateEditText.getText().toString()));
            //The published state is true by default for new Events
            if(!sIsOpenForEditing){
                sEvent.setPublished(true);
            }
            Log.v(TAG, sEvent.toString());
            //Save the event
            EventPrefUtil.saveEvent(getActivity(),sEvent.getId()+"",sEvent);
            //If it's open for editing, then we are in the same navigation tab,
            //so FragmentNavigationUtil.popFragment method is appropriate
            if(sIsOpenForEditing){
                FragmentNavigationUtil.popFragment(getActivity(), R.id.fragment_place);
            }
            //Get the BottomNavigationView and set the appropriate icon as selected
            BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
            bottomNavigationView.setSelectedItemId(R.id.menu_home);

            //Clear input fields
            eventNameEditText.setText("");
            eventDescEditText.setText("");
            eventDateEditText.setText("");
            eventLocationEditText.setText("");
        }
    }



    /**
     * OnClick for the nextImage button
     * Loads the next image from the event's images ArrayList
     * @param v Button's view
     */
    @OnClick(R.id.event_edit_add_next_image_button) void nextImage(View v){
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
    @OnClick(R.id.event_edit_add_prev_image_button) void prevImage(View v){
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

    /**
     * OnClick callback to the Date EditText
     * Picks a Date using the built-in DatePickerDialog
     * @param v
     */
    @OnClick(R.id.event_edit_add_date_edit_text) void pickDate(View v){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.show();
    }

    /**
     * This function gets called after a Date was picked
     * Calls the TimePickerDialog
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        sEvent.setDate(new DateTime(year,month,dayOfMonth,0,0));

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog timePickerDialog =  new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();

    }

    /**
     * Sets the hour and minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        sEvent.getDate().setHour(hourOfDay);
        sEvent.getDate().setMinutes(minute);
        eventDateEditText.setText(sEvent.getDate().toString());
    }

    /**
     * Toggle published state
     */
    @OnClick(R.id.event_edit_add_published) void togglePublished(View v){
        if(sEvent.isPublished()){
            eventPublishedButton.setImageResource(R.drawable.ic_visibility_off_white_24dp);
            sEvent.setPublished(false);
        }else{
            eventPublishedButton.setImageResource(R.drawable.ic_visibility_white_24dp);
            sEvent.setPublished(true);
        }
    }

    //TODO: Figure this out
    private final int SELECT_PHOTO = 1;
    /**
     * Change the event's image
     * @param v
     */
    @OnClick(R.id.event_edit_add_change_image_button) void changeImage(View v){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }
    /**
     * Callback
     * @param requestCode
     * @param resultCode
     * @param imagesReturnedIntent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imagesReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imagesReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    ClipData clipData = imagesReturnedIntent.getClipData();
                    if(clipData != null){
                        sEvent.clearImages();
                        for(int i=0;i<clipData.getItemCount();++i){
                            ClipData.Item item = clipData.getItemAt(i);
                            //If the item is not null, add the Uri to the event image list
                            if(item != null){
                                sEvent.getImages().add(item.getUri().toString());
                            }
                        }
                        //Load the image
                        if(!sEvent.getImages().isEmpty()){
                            Glide.with(getActivity())
                                    .load(sEvent.getImages().get(0))
                                    .apply(new RequestOptions().centerCrop())
                                    .into(eventImageView);
                            imageIndex=0;
                        }
                    }
                }
                break;
        }
    }
}
