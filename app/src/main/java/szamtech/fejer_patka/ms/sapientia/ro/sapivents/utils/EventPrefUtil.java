package szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;

/**
 * Util class for saving and getting event objects from SharedPreferences
 * singleton class
 */
public class EventPrefUtil {

    private EventPrefUtil(){}

    private static final String TAG = "EventPrefUtil";

    //Global access point
    public static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Save an event to SharedPreferences
     * In order to save it to SharedPreferences, the Event gets serialized to JSON
     * @param context activity context
     * @param key the event's key
     * @param event the event object to be saved
     */
    public static void saveEvent(Context context, String key, Event event) {
        SharedPreferences.Editor editor = getPreferences(context).edit();

        Gson gson = new Gson();
        String json = gson.toJson(event);
        editor.putString(key, json);
        editor.apply();
    }

    /**
     * Gets a contact from SharedPreferences
     * @param context application context
     * @return the event at {@param key}
     */
    public static Event getEvent(Context context, String key) {
        Gson gson = new Gson();
        String json = getPreferences(context).getString(key,"");

        return gson.fromJson(json, Event.class);
    }

    /**
     * Removes a contact from the SharedPreferences
     * @param context Activity's context
     * Event object with @param key gets removed
     */
    public static void removeEvent(Context context, String key) {
        getPreferences(context)
                .edit()
                .remove(key)
                .apply();
    }

    /**
     * Clears everything from SharedPreferences
     * @param context Activity's context
     */
    public static void clearAll(Context context){
        getPreferences(context)
                .edit()
                .clear()
                .apply();
    }

    /**
     * Gets all the values from SharedPreferences
     * Does it in an asynchronous manner, using an AsyncTaskRunner
     * @param context
     * @return a list of all of the events
     */
    public static List<Event> getAllValues(Context context){
        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner(context);
        asyncTaskRunner.execute();
        List<Event> eventList = new ArrayList<>();

        try {
            eventList = asyncTaskRunner.get();
            Collections.sort(eventList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return  eventList;
    }

    /**
     * AsyncTask for parsing JSONs in the background
     */
    private static class AsyncTaskRunner extends AsyncTask<String, String, List<Event>> {
        @SuppressLint("StaticFieldLeak")
        private Context mContext;

        AsyncTaskRunner(Context context){
            this.mContext = context;
        }

        @Override
        protected List<Event> doInBackground(String... params) {
            return getAllValues(mContext);
        }

        private List<Event> getAllValues(Context context) {
            Map<String, ?> contacts = getPreferences(context).getAll();
            List<Event> eventList = new ArrayList<>();
            for ( String key : contacts.keySet() ) {
                eventList.add(getEvent(context, key));
            }
            Log.d(TAG, eventList.toString());
            return eventList;
        }
    }
}
