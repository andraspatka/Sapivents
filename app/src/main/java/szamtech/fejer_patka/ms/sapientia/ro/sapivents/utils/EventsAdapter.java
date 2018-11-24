package szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.Event;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder>{
    private List<Event> mEventsList;
    private Context mContext;

    //Instance of the interface. Used for communicating with EventListFragment
    private EventListItemOnClickInterface mEventListItemOnClickInterface;

    private static final String TAG = "EventsAdapter";

    public EventsAdapter(List<Event> events, Context context, EventListItemOnClickInterface eventListItemOnClickInterface){
        this.mEventsList = events;
        this.mContext = context;
        this.mEventListItemOnClickInterface = eventListItemOnClickInterface;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        final EventsAdapter.EventsViewHolder holder = new EventsViewHolder(itemView);
        //OnClickListener is set in the onCreateViewHolder rather than in the onBindViewHolder
        //so the listener isn't bound multiple times unnecessarily
        //Set onClickListener for the whole itemView
        //holder.delete.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventListItemOnClickInterface.onClickEventItem(mEventsList.get(holder.getAdapterPosition()));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mEventListItemOnClickInterface.onLongClickEventItem(mEventsList.get(holder.getAdapterPosition()));
                return true;
            }
        });
        //Set onClickListener for only the delete icon
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventListItemOnClickInterface.onClickDeleteEvent(mEventsList.get(holder.getAdapterPosition()));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(EventsAdapter.EventsViewHolder holder, int position) {
        Event event = mEventsList.get(position);
        holder.name.setText(event.getTitle());
        //The description gets trimmed, only the first 100 characters are displayed
        if(event.getDescription().length() > 100){
            holder.desc.setText(event.getDescription().substring(0, 100) + "...");
        }else{
            holder.desc.setText(event.getDescription());
        }


        //RequestOptions centerCrop() option makes the image fit the imageview fully
        Glide.with(mContext)
                .load(event.getImages().get(0))
                .apply(new RequestOptions().centerCrop())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mEventsList.size();
    }

    /**
     * EventViewHolder class, used for the individual views inside a recyclerview
     * When the model is updated, this might have to be updated as well
     */
    class EventsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.event_name) TextView name;
        @BindView(R.id.event_desc) TextView desc;
        @BindView(R.id.event_image) ImageView image;
        @BindView(R.id.event_delete) ImageButton delete;

        EventsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * This interface is used for passing listeners to the EditListFragment
     * (Or any other fragment/activity which contains this adapter)
     */
    public interface EventListItemOnClickInterface {
        void onClickEventItem(Event event);
        void onLongClickEventItem(Event event);
        void onClickDeleteEvent(Event event);
    }
}
