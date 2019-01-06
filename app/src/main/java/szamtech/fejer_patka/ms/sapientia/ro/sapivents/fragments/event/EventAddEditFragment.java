package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.DateTime;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.LoadingDialogUtil;

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
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private int mTotalImageNum;
    private int mImageCount;
    private LoadingDialogUtil mLoadingDialog;

    private DateTime mEventDate = new DateTime();

    final ArrayList<String> s_eventImages = new ArrayList<>();
    final String[] postId = new String[1];
    private boolean isNewImageSelected;

    private int imageIndex = 0;
    //need to be declared final, because it is used in inner class OnProgressListener
    final int[] progressResult = new int[1];

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
    @BindView(R.id.event_edit_progressBar) ProgressBar mProgressBar;

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
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("events");
        mStorage = FirebaseStorage.getInstance();
        isNewImageSelected = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_add_edit, container, false);
        ButterKnife.bind(this, view);
        //If the fragment is opened for editing, then fill the EditText and ImageView views with
        //the event's details
        if(sIsOpenForEditing){
            eventChangeImage.setText("Change Image");
            eventNameEditText.setText(sEvent.getTitle());
            eventDescEditText.setText(sEvent.getDescription());
            eventDateEditText.setText(sEvent.getEventDate().toString());
            eventLocationEditText.setText(sEvent.getLocation());
            if(!sEvent.getImages().isEmpty()){
                mProgressBar.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
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

                        })
                        .apply(new RequestOptions().centerCrop().override(132,132))
                        .into(eventImageView);
                eventPublishedButton.setVisibility(View.VISIBLE);
            }
        }
        else{
            mProgressBar.setVisibility(View.GONE);
            eventChangeImage.setText("Add Image");
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
            sEvent.setAuthor(mFirebaseUser.getPhoneNumber());
            sEvent.setTitle(eventNameEditText.getText().toString());
            sEvent.setDescription(eventDescEditText.getText().toString());
            sEvent.setLocation(eventLocationEditText.getText().toString());
            Log.v(TAG, eventDateEditText.getText().toString());
            sEvent.setEventDate(mEventDate.toString());
            //The published state is true by default for new Events

            if(!sIsOpenForEditing){
                sEvent.setPublished(true);
            }

            uploadEventData();

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

                    })
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
        mProgressBar.setVisibility(View.VISIBLE);
        if(!sEvent.getImages().isEmpty()){
            if(imageIndex == 0){
                imageIndex = sEvent.getImages().size()-1;
            }else{
                --imageIndex;
            }
            if(getActivity() != null){
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

                        })
                        .apply(new RequestOptions().centerCrop())
                        .into(eventImageView);
            }
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

        mEventDate.setYear(year);
        mEventDate.setMonth(month+1);
        mEventDate.setDay(dayOfMonth);

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
        mEventDate.setHour(hourOfDay);
        mEventDate.setMinutes(minute);

        sEvent.setEventDate(mEventDate.toString());
        eventDateEditText.setText(mEventDate.toString());
    }

    /**
     * Toggle published state
     */
    @OnClick(R.id.event_edit_add_published) void togglePublished(View v){
        if(sEvent.isPublished()){
            eventPublishedButton.setImageResource(R.drawable.ic_visibility_off_white_24dp);
            sEvent.setPublished(false);
            setEventPusblishedState(sEvent.getKey(), false);
        }else{
            eventPublishedButton.setImageResource(R.drawable.ic_visibility_white_24dp);
            sEvent.setPublished(true);
            setEventPusblishedState(sEvent.getKey(), true);
        }
    }

    void setEventPusblishedState(String key, final boolean state){

        mDatabase.child(key + "/published").setValue(state)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Event publication state changed to: " + state + "!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error changing event publication state!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //TODO: Figure this out
    private final int SELECT_PHOTO = 1;
    /**
     * Change the event's image
     * @param v
     */
    @OnClick(R.id.event_edit_add_change_image_button) void changeImage(View v){
        isNewImageSelected = true;
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

    void uploadEventData(){

        mImageCount = 0;

        mLoadingDialog = new LoadingDialogUtil(getContext());
        mLoadingDialog.showDialog();

        if(sIsOpenForEditing){

            if(isNewImageSelected){

                uploadImages();

            }
            else{

                uploadEventToDatabase();

            }

        }
        else{

            postId[0] = mDatabase.push().getKey();
            sEvent.setKey(postId[0]);
            uploadImages();
        }

    }

    void checkUploadState(){
        ++mImageCount;

        //if every image is uploaded to storage
        if(mImageCount == mTotalImageNum){
            //setting up uploaded image url's to event
            sEvent.setImages(s_eventImages);

            //uploading event data to database
            uploadEventToDatabase();

        }

    }

    public void uploadEventToDatabase(){

        //checking if fragment is invoked to add or to edit event
        String childId;
        if(sIsOpenForEditing){
            childId = sEvent.getKey();
            sEvent.setEventDate(eventDateEditText.getText().toString());
        }
        else{
            childId = postId[0];
        }

        //uploading new event to database
        mDatabase.child(childId).setValue(sEvent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        //if the images uploaded successfully the we upload the extra data to database
                        //and show to user a  text about the result
                        Log.v(TAG, "Data uploaded");
                        Toast.makeText(getActivity(), "Event successfully uploaded!", Toast.LENGTH_SHORT).show();
                        returnToHomePage();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.v(TAG, "Data uploading failed");
                        Toast.makeText(getActivity(), "Error uploading event, please try again!", Toast.LENGTH_SHORT).show();
                        returnToHomePage();

                    }

                });

    }

    public void returnToHomePage(){

        mLoadingDialog.endDialog();

        //If it's open for editing, then we are in the same navigation tab,
        //so FragmentNavigationUtil.popFragment method is appropriate
        if(sIsOpenForEditing){
            FragmentNavigationUtil.popFragment(getActivity(), R.id.fragment_place);
        }
        //Get the BottomNavigationView and set the appropriate icon as selected
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

    }

    public void uploadImages(){

        final StorageReference storageRef = mStorage.getReference();

        mTotalImageNum = sEvent.getImages().size();
        //aux variable for storing actual progress value
        final int s_progress = 0;

        for(int i = 0; i<sEvent.getImages().size(); ++i){

            final int pos = i;

            StorageReference imageRef = storageRef.child("images/eventImages/" + postId[0] + "/img" + pos);

            UploadTask uploadTask = imageRef.putFile(Uri.parse(sEvent.getImages().get(i)));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Log.v(TAG, "image upload failed " + exception.getMessage());
                    checkUploadState();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Log.v(TAG, "image uploaded");

                    storageRef.child("images/eventImages/" + postId[0] + "/img" + pos).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.v(TAG, "image url onSuccess");
                                    s_eventImages.add(uri.toString());
                                    checkUploadState();
                                }

                            }).addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.v(TAG, "image url onFailure");
                            checkUploadState();
                        }

                    });

                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount()) * (mImageCount + 1) / mTotalImageNum;

                    if(progress > progressResult[0]){

                        progressResult[0] = progress;
                        mLoadingDialog.setDialogText("Upload is " + progress + "% done");

                    }

                }

            });

        }

    }

}
