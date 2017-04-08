package stelztech.youknowehv4.state;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.ApplicationManager;
import stelztech.youknowehv4.activitypackage.WordInfoActivity;

/**
 * Created by alex on 2017-04-04.
 */

public class ActionButtonStateManager {

    public enum actionButtonState {
        ADD_WORD,
        DECK,
        GONE
    }

    private actionButtonState currentState;

    private static ActionButtonStateManager instance;
    private Context context;

    private ActionButtonStateManager(){
        currentState = actionButtonState.GONE;
    }

    public static ActionButtonStateManager getInstance(){
        if(instance == null){
            instance = new ActionButtonStateManager();
        }
        return instance;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setState(actionButtonState state, final Activity activity) {

        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
            switch (state){
                case DECK:
                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ApplicationManager) activity).createDeck();
                        }
                    });
                    break;
                case ADD_WORD:
                    fab.setVisibility(View.VISIBLE);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar.make(view, "ADD WORD", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Intent i = new Intent(context, WordInfoActivity.class);
                            context.startActivity(i);
                        }
                    });
                    break;
                case GONE:
                    // action button
                    fab.setVisibility(View.GONE);
                    break;
                default:
                    fab.setVisibility(View.GONE);
                    break;
            }
    }
}
