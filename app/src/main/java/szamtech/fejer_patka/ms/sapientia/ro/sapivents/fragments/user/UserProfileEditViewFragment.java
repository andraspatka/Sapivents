package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

/**
 * Fragment for editing, viewing User account information
 * Removed unnecessary boilerplate code
 */
public class UserProfileEditViewFragment extends Fragment {

    @BindView(R.id.user_profile_sign_out_btn) Button signOutButton;
    @BindView(R.id.user_profile_events_btn) Button eventsButton;
    @BindView(R.id.user_profile_phone_no) TextView signedInPhoneNo;
    @BindView(R.id.user_profile_name) TextView signedInName;

    public UserProfileEditViewFragment() {
        // Required empty public constructor
    }

    public static UserProfileEditViewFragment newInstance(String param1, String param2) {
        UserProfileEditViewFragment fragment = new UserProfileEditViewFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_profile_edit_view, container, false);
        ButterKnife.bind(this,view);

        //If a user is logged in
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            signedInPhoneNo.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            signedInName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }else{
            signOutButton.setEnabled(false);
        }

        return view;
    }

    /**
     * Callback method for the signOut button
     * Signs out the firebase user
     * Adds the userSignInFragment as a single fragment
     * @param v
     */
    @OnClick(R.id.user_profile_sign_out_btn) void signOut(View v){
        //Sign out the user
        FirebaseAuth.getInstance().signOut();
        //Add the UserSignInFragment as a single fragment
        UserSignInFragment userSignInFragment = new UserSignInFragment();
        FragmentNavigationUtil.addAsSingleFragment(
                getActivity(),
                userSignInFragment,
                R.id.fragment_place,
                FragmentNavigationUtil.HOME_SCREEN);
        //Set the padding to the R.id.fragment_place FrameLayout
        FrameLayout fragmentPlace = (FrameLayout) getActivity().findViewById(R.id.fragment_place);
        BottomNavigationView bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        fragmentPlace.setPadding(0,0,0,0);
        //Make the bottom navigation invisible
        bottomNav.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.user_profile_events_btn) void listMyEvents(View v){
        UserEventListFragment userEventListFragment = new UserEventListFragment();
        FragmentNavigationUtil.addFragmentToScreen(
                getContext(),
                userEventListFragment,
                R.id.fragment_place,
                FragmentNavigationUtil.ACCOUNT_SCREEN
                );
    }

}
