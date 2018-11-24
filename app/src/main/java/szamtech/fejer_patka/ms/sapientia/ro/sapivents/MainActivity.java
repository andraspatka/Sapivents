package szamtech.fejer_patka.ms.sapientia.ro.sapivents;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.User;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventAddEditFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventListFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user.UserProfileEditViewFragment;
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
        User author = new User("0123456789","Pityiri","Palko",
                "data:image/png;base64,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");
        ArrayList<User> attendants = new ArrayList<User>();
        EventPrefUtil.clearAll(this);
        for(int i = 0; i < 10; ++i){
            Event event = new Event("Event" + i, getResources().getString(R.string.placeholder_text),2,"itt",images,author,attendants,true);
            EventPrefUtil.saveEvent(this, event.getId() + "", event);
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
