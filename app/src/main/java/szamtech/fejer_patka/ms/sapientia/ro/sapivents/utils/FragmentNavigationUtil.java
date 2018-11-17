package szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.Context;

import java.util.ArrayList;
import java.util.Stack;

/**
 * WORK IN PROGRESS
 * Util class for Fragment navigation
 * Static methods, singleton class
 */
public class FragmentNavigationUtil {
    private static ArrayList<Stack<Fragment>> sFragmentStacks = new ArrayList<>();
    private static final FragmentNavigationUtil SINGLE_INSTANCE = new FragmentNavigationUtil();

    /**
     * Setup, create the stacks
     */
    private FragmentNavigationUtil(){
        for(int i = 0; i< Constants.NUMBER_OF_SCREENS; ++i){
            sFragmentStacks.add(new Stack<Fragment>());
        }
    }
    public static FragmentNavigationUtil getInstance(){
        return SINGLE_INSTANCE;
    }

    /**
     * Adds a fragment on the given screen to the given view
     * @param context Activity's context
     * @param screen The screen where the fragment should be added
     * @param fragment The Fragment to be added
     * @param viewId The view's id in which the fragment should be added
     * @param toBackStack add it to the backstack or not
     * @throws NoSuchScreenException if the screen parameter is invalid
     */
    /*Might not need it
    public static void addFragment(Context context, int screen, Fragment fragment, int viewId, boolean toBackStack) throws NoSuchScreenException{
        if(screen >= Constants.NUMBER_OF_SCREENS){
            throw new NoSuchScreenException(screen);
        }
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        //Do the fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(toBackStack){
            fragmentTransaction.add(viewId, fragment)
                    .addToBackStack(null)
                    .commit();
            sFragmentStacks.get(screen).add(fragment);
        }else{
            fragmentTransaction.add(viewId, fragment)
                    .commit();
        }
    }*/
    /*Might not need it
    public static void loadStack(Context context, int screen, int viewId) throws NoSuchScreenException{
        if(screen >= Constants.NUMBER_OF_SCREENS){
            throw new NoSuchScreenException(screen);
        }
        if(!screenIsEmpty(screen)){
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            //Do the fragment transaction
            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStack();
            }
            for(int i=0; i < sFragmentStacks.get(screen).size(); ++i){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(viewId, sFragmentStacks.get(screen).pop())
                        .addToBackStack(null)
                        .commit();
            }
        }

    }*/

    /**
     * EXPERIMENTAL
     * @param context
     * @param screen
     * @param fragment
     * @param viewId
     */
    public static void onTabSelected(Context context, int screen, Fragment fragment, int viewId) throws NoSuchScreenException{
        if(screen >= Constants.NUMBER_OF_SCREENS){
            throw new NoSuchScreenException(screen);
        }
        // Pop off everything up to and including the current tab
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager.popBackStack(screen, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Add the new tab fragment
        fragmentManager.beginTransaction()
                .replace(viewId, fragment)
                .addToBackStack(screen+"")
                .commit();
    }

    /**
     *  EXPERIMENTAL
     * Add a fragment on top of the current tab
     */
    public static void addFragmentOnTop(Context context, Fragment fragment, int viewId) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(viewId, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Checks to see whether or not the screen has any fragments loaded into it
     * @param screen
     * @return true if the screen's backstack is empty, false if it is not
     * @throws NoSuchScreenException
     */
    /*Might be uneeded
    public static boolean screenIsEmpty(int screen) throws NoSuchScreenException{
        if(screen >= Constants.NUMBER_OF_SCREENS){
            throw new NoSuchScreenException(screen);
        }
        return sFragmentStacks.get(screen).isEmpty();
    }*/

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
