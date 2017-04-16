package stelztech.youknowehv4.manager;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activitypackage.MainActivityManager;

/**
 * Created by alex on 2017-04-04.
 */

public class ActionButtonManager {

    public enum ActionButtonState {
        ADD_CARD,
        DECK,
        GONE
    }

    private ActionButtonState currentState;

    private static ActionButtonManager instance;
    private Context context;

    private ActionButtonManager() {
        currentState = ActionButtonState.GONE;
    }

    public static ActionButtonManager getInstance() {
        if (instance == null) {
            instance = new ActionButtonManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setState(ActionButtonState state, final Activity activity) {

        FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        switch (state) {
            case DECK:
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivityManager) activity).createDeck();
                    }
                });
                break;
            case ADD_CARD:
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivityManager) activity).startActivityNewCard();
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
