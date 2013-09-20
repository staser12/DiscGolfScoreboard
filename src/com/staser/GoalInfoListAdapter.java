package com.staser;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


public class GoalInfoListAdapter extends BaseAdapter implements OnClickListener {
	
	private Context mContext;
	private List<GoalListItem> mListGoalItems;
    
    public GoalInfoListAdapter(Context context, List<GoalListItem> listGoalItems) {
        mContext = context;
        mListGoalItems = listGoalItems;
    }

    public int getCount() {
        return mListGoalItems.size();
    }

    public Object getItem(int position) {
        return mListGoalItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public List<GoalListItem> getList() {
        return mListGoalItems;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
    	GoalListItem entry = mListGoalItems.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.goalinfo_listitem, null);
        }
                
        TextView tvPlayerName = (TextView) convertView.findViewById(R.id.goalinfo_playername);
        tvPlayerName.setText(entry.getPlayerName());

        TextView tvPlayerScore = (TextView) convertView.findViewById(R.id.goalinfo_playerscore);
        tvPlayerScore.setText(Integer.toString( entry.getPlayerScore()));  
        
        Button btnMinus = (Button) convertView.findViewById(R.id.goal_info_listitem_btn_minus);
        btnMinus.setFocusableInTouchMode(false);
        btnMinus.setFocusable(false);
        btnMinus.setOnClickListener(this);
        btnMinus.setTag(entry);
        
        Button btnPlus = (Button) convertView.findViewById(R.id.goal_info_listitem_btn_plus);
        btnPlus.setFocusableInTouchMode(false);
        btnPlus.setFocusable(false);
        btnPlus.setOnClickListener(this);
        btnPlus.setTag(entry);
        
        return convertView;
    }  
    
    @Override
    public void onClick(View view) {
    	GoalListItem entry = ( GoalListItem ) view.getTag();
    	int viewId = view.getId(); 
    	
    	switch( viewId ) {    	
    		case R.id.goal_info_listitem_btn_minus:
    			entry.decrementScore();
    			break;
    		case R.id.goal_info_listitem_btn_plus:
    			entry.incrementScore();
    			break;
    		default:
    			break;
    	}  	
    	
        notifyDataSetChanged();
    }
}
