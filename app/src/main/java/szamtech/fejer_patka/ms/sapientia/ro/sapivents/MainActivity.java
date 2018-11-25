package szamtech.fejer_patka.ms.sapientia.ro.sapivents;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventAddEditFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventListFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user.UserProfileEditViewFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user.UserSignInFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.Constants;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.EventPrefUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";

    @BindView(R.id.bottom_nav) BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        //Bottom navigation
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        prepareData();
        mBottomNavigationView.setVisibility(View.INVISIBLE);

        UserSignInFragment userSignInFragment = new UserSignInFragment();
        FragmentNavigationUtil.addFragmentOnTop(this, userSignInFragment, R.id.fragment_place_signin);

    }
    //TODO: remove this once Firebase is integrated to the project
    /**
     * Saves a list of generated Event objects to SharedPreferences using the EventPrefUtil class
     */
    private void prepareData(){
        for(int i = 0; i < 10; ++i){
            Event event = new Event("Event" + i, getResources().getString(R.string.placeholder_text));
            EventPrefUtil.saveEvent(this, event.getId() + "", event);
            Log.v(TAG,event.getName() + " " + event.getId());
        }
    }


    /**
     * Callback method for bottom navigation
     * WORK IN PROGRESS
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_account:
                        /*if(FragmentNavigationUtil.screenIsEmpty(Constants.ACCOUNT_SCREEN)){
                            UserProfileEditViewFragment accountFragment = new UserProfileEditViewFragment();
                            FragmentNavigationUtil.addFragment(
                                    MainActivity.this,
                                    Constants.ACCOUNT_SCREEN,
                                    accountFragment,
                                    R.id.fragment_place,
                                    true
                            );
                        }else{
                            FragmentNavigationUtil.loadStack(
                                    MainActivity.this,
                                    Constants.ACCOUNT_SCREEN,
                                    R.id.fragment_place);
                        }*/
                UserProfileEditViewFragment accountFragment = new UserProfileEditViewFragment();
                FragmentNavigationUtil.onTabSelected(
                        MainActivity.this,
                        Constants.ACCOUNT_SCREEN,
                        accountFragment,
                        R.id.fragment_place
                );
                return true;
            case R.id.menu_home:
                        /*if(FragmentNavigationUtil.screenIsEmpty(Constants.HOME_SCREEN)){
                            EventListFragment listFragment = new EventListFragment();
                            FragmentNavigationUtil.addFragment(
                                    MainActivity.this,
                                    Constants.HOME_SCREEN,
                                    listFragment,
                                    R.id.fragment_place,
                                    true
                            );
                        }else{
                            FragmentNavigationUtil.loadStack(
                                    MainActivity.this,
                                    Constants.HOME_SCREEN,
                                    R.id.fragment_place);
                        }*/
                EventListFragment listFragment = new EventListFragment();
                FragmentNavigationUtil.onTabSelected(
                        MainActivity.this,
                        Constants.HOME_SCREEN,
                        listFragment,
                        R.id.fragment_place
                );
                return true;
            case R.id.menu_add:
                        /*EventAddEditFragment fragment = new EventAddEditFragment();
                        FragmentNavigationUtil.addFragment(
                                MainActivity.this,
                                Constants.ADD_SCREEN,
                                fragment,
                                R.id.fragment_place,
                                true
                        );*/
                EventAddEditFragment fragment = EventAddEditFragment.newInstanceForAdding();
                FragmentNavigationUtil.onTabSelected(
                        MainActivity.this,
                        Constants.ADD_SCREEN,
                        fragment,
                        R.id.fragment_place
                );
                return true;
        }
        return false;
    }
}
