package szamtech.fejer_patka.ms.sapientia.ro.sapivents;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.DateTime;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.User;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventAddEditFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventListFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user.UserProfileViewFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user.UserSignInFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventPrefUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";

    @BindView(R.id.bottom_nav) BottomNavigationView mBottomNavigationView;
    @BindView(R.id.fragment_place) FrameLayout mFragmentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        //Bottom navigation

        prepareData();
        mBottomNavigationView.setSelectedItemId(R.id.menu_home);

        //If the user is not authenticated, call this sign in fragment
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            //Create the UserSignInFragment
            UserSignInFragment userSignInFragment = new UserSignInFragment();
            FragmentNavigationUtil.addFragmentToScreen(this, userSignInFragment, R.id.fragment_place, FragmentNavigationUtil.HOME_SCREEN);
            mBottomNavigationView.setVisibility(View.INVISIBLE);
        }else{
            int bottomPaddingInPixels = (int) getResources().getDimension(R.dimen.bottom_nav_height);
            int topPaddingInPixels = (int) getResources().getDimension(R.dimen.logo_height);
            //Set the padding to the R.id.fragment_place FrameLayout
            mFragmentPlace.setPadding(0,topPaddingInPixels,0,bottomPaddingInPixels);
            //Make the bottom navigation visible, and the userSignInButton invisible
            mBottomNavigationView.setVisibility(View.VISIBLE);

            EventListFragment listFragment = new EventListFragment();
            FragmentNavigationUtil.screenSelected(
                    MainActivity.this,
                    listFragment,
                    R.id.fragment_place,
                    FragmentNavigationUtil.HOME_SCREEN
            );
        }

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    //TODO: remove this once Firebase is integrated to the project
    /**
     * Saves a list of generated Event objects to SharedPreferences using the EventPrefUtil class
     */
    private void prepareData(){
        ArrayList<String> images = new ArrayList<>();
        images.add("https://upload.wikimedia.org/wikipedia/commons/thumb/6/67/Inside_the_Batad_rice_terraces.jpg/220px-Inside_the_Batad_rice_terraces.jpg");
        images.add("https://images.pexels.com/photos/814499/pexels-photo-814499.jpeg?auto=compress&cs=tinysrgb&h=350");
        images.add("https://images.pexels.com/photos/414171/pexels-photo-414171.jpeg?auto=compress&cs=tinysrgb&h=350");
        User author = new User("Pityiri","Palko");
        ArrayList<String> attendants = new ArrayList<String>();
        for(int i=0; i<50; ++i){
            attendants.add("userId" + i);
        }

        EventPrefUtil.clearAll(this);
        DateTime dateTime = new DateTime(2018,11,24,21,48);
        for(int i = 0; i < 10; ++i){
            Event event = new Event("Event" + i, getResources().getString(R.string.placeholder_text),dateTime,"itt",images,"author",attendants,true);
            EventPrefUtil.saveEvent(this, event.getId() + "", event);
        }
    }


    /**
     * Callback method for bottom navigation
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int selectedId = mBottomNavigationView.getSelectedItemId();

        //If the user clicks on the currently selected element, then don't load any fragments
        if(selectedId != item.getItemId()){
            switch(item.getItemId()){
                case R.id.menu_account:
                    UserProfileViewFragment accountFragment = new UserProfileViewFragment();
                    FragmentNavigationUtil.screenSelected(
                            MainActivity.this,
                            accountFragment,
                            R.id.fragment_place,
                            FragmentNavigationUtil.ACCOUNT_SCREEN
                    );
                    return true;
                case R.id.menu_home:
                    EventListFragment listFragment = new EventListFragment();
                    FragmentNavigationUtil.screenSelected(
                            MainActivity.this,
                            listFragment,
                            R.id.fragment_place,
                            FragmentNavigationUtil.HOME_SCREEN
                    );
                    return true;
                case R.id.menu_add:
                    EventAddEditFragment fragment = EventAddEditFragment.newInstanceForAdding();
                    FragmentNavigationUtil.screenSelected(
                            MainActivity.this,
                            fragment,
                            R.id.fragment_place,
                            FragmentNavigationUtil.ADD_SCREEN
                    );
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //FragmentNavigationUtil.popFragment returns false if there is only one fragment in the stack
        //In this case, a back press exits the application
        if(!FragmentNavigationUtil.popFragment(this,R.id.fragment_place)){
            this.finishAffinity();
            Log.v(TAG,"Only fragment");
        }
    }
}
