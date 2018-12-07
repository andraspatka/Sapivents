package szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils;

import android.app.Activity;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.Context;

import java.util.ArrayList;
import java.util.Stack;

import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;

/**
 * author: Patka Zsolt-Andras
 * Util class for Fragment navigation
 * Static methods, singleton class
 */
public class FragmentNavigationUtil {
    //Number of bottom navigation elements
    public static final int NUMBER_OF_SCREENS = 3;
    //Bottom navigation element indexes
    public static final int ACCOUNT_SCREEN = 0;
    public static final int HOME_SCREEN = 1;
    public static final int ADD_SCREEN = 2;

    //Holds the fragment stacks for each screen
    private static ArrayList<Stack<Fragment>> sFragmentStacks = new ArrayList<>();
    //Static initializer block, used for initializing sFragmentStacks
    static {
        for(int i = 0; i< FragmentNavigationUtil.NUMBER_OF_SCREENS; ++i){
            sFragmentStacks.add(new Stack<Fragment>());
        }
    }
    /**
     * Private constructor
     */
    private FragmentNavigationUtil(){}

    /**
     * author: Patka Zsolt-Andras
     * @param context the Activity's context
     * @param fragment the given fragment
     * @param viewId the viewId in which the fragment should be placed
     * @param screen the given bottom navigation element
     * @throws NoSuchScreenException if an invalid screen is passed in
     */
    public static void screenSelected(Context context, Fragment fragment, int viewId, int screen) throws NoSuchScreenException{
        if(screen > NUMBER_OF_SCREENS || screen < 0){
            throw new NoSuchScreenException(screen);
        }
        //Get the fragment manager
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        //Clear the backstack
        fragmentManager.popBackStack();

        //If the fragmentStack at the given screen is empty, add the fragment
        if(sFragmentStacks.get(screen).isEmpty()){
            fragmentManager
                    .beginTransaction()
                    .replace(viewId,fragment)
                    .addToBackStack(screen+"")
                    .commit();
            sFragmentStacks.get(screen).add(fragment);
        }else{//Load the fragments from the stack
            for(int i=0; i<sFragmentStacks.get(screen).size(); ++i){
                fragmentManager
                        .beginTransaction()
                        .replace(viewId,sFragmentStacks.get(screen).get(i))
                        .addToBackStack(screen+"")
                        .commit();
            }
        }
    }

    /**
     * The point of this method is to have ONLY one fragment in the backstack
     * Clears the backstack and the stack that corresponds to the given screen
     * @param context Activity's context
     * @param fragment the fragment to be loaded
     * @param viewId the place where to load the fragment to
     * @param screen Screen (Account, Home or Add)
     * @throws NoSuchScreenException If the given screen value is invalid
     */
    public static void addAsSingleFragment(Context context, Fragment fragment, int viewId, int screen) throws NoSuchScreenException{
        if(screen > NUMBER_OF_SCREENS || screen < 0){
            throw new NoSuchScreenException(screen);
        }
        //Get the fragment manager
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        //Clear the backstack
        fragmentManager.popBackStack();
        //Clear the stack corresponding to the given screen
        sFragmentStacks.get(screen).clear();

        fragmentManager
                .beginTransaction()
                .replace(viewId,fragment)
                .addToBackStack(screen+"")
                .commit();
        sFragmentStacks.get(screen).add(fragment);
    }



    /**
     * author: Patka Zsolt-Andras
     * Add a fragment to the given screen
     * @param context Activity's context
     * @param fragment given Fragment
     * @param viewId the viewId in which the fragment should be placed
     * @param screen bottom navigation element
     * @throws NoSuchScreenException if an invalid screen is passed in
     */
    public static void addFragmentToScreen(Context context, Fragment fragment, int viewId, int screen) throws NoSuchScreenException{
        if(screen > NUMBER_OF_SCREENS || screen < 0){
            throw new NoSuchScreenException(screen);
        }
        //Get the bottom navigation element
        BottomNavigationView bottomNavigationView = (BottomNavigationView) ((Activity) context).findViewById(R.id.bottom_nav);
        //Select the appropriate bottom navigation element
        switch(screen){
            case HOME_SCREEN:
                bottomNavigationView.setSelectedItemId(R.id.menu_home);
                break;
            case ACCOUNT_SCREEN:
                bottomNavigationView.setSelectedItemId(R.id.menu_account);
                break;
            case ADD_SCREEN:
                bottomNavigationView.setSelectedItemId(R.id.menu_add);
                break;
        }
        //Add the screen to the stack
        sFragmentStacks.get(screen).add(fragment);
        //Get the FragmentManager
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        //Add the fragment to the backstack, using a tag (tag = screen + "")
        fragmentManager
                .beginTransaction()
                .replace(viewId, fragment)
                .addToBackStack(screen + "")
                .commit();
    }

    /**
     * author: Patka Zsolt-Andras
     * Pop off one fragment
     * @param context Activity's context
     * @param viewId View in which to put the fragment
     * @return false if there is only one fragment remaining in the stack
     */
    public static boolean popFragment(Context context, int viewId){
        //Get the FragmentManager
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        //Get the last backstack entry
        FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
        //Check to see in which screen the last backstack entry was
        String screenStr = backStackEntry.getName();
        int screen = Integer.parseInt(screenStr);
        if(sFragmentStacks.get(screen).size() >= 2){
            sFragmentStacks.get(screen).pop();

            fragmentManager
                    .beginTransaction()
                    .replace(viewId, sFragmentStacks.get(screen).peek())
                    .addToBackStack(screen + "")
                    .commit();
            return true;
        }else return false;
    }

    /**
     * Custom exception class
     * When the user enters an invalid screen number, this exception gets thrown
     */
    private static class NoSuchScreenException extends RuntimeException{
        public NoSuchScreenException(int screen){
            super("Invalid Screen: " + screen);
        }
    }


}
