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
 * Fragment for User sign in
 * Removed unnecessary boilerplate code
 */
public class UserSignInFragment extends Fragment {

    public UserSignInFragment() {
        // Required empty public constructor
    }

    public static UserSignInFragment newInstance() {
        UserSignInFragment fragment = new UserSignInFragment();
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
        return inflater.inflate(R.layout.fragment_user_sign_in, container, false);
    }
}
