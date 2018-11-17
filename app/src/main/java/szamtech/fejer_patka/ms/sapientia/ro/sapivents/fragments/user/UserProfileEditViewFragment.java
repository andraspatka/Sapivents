package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;

/**
 * Fragment for editing, viewing User account information
 * Removed unnecessary boilerplate code
 */
public class UserProfileEditViewFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_user_profile_edit_view, container, false);
    }

}
