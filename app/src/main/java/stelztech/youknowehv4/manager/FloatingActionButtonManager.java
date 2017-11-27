package stelztech.youknowehv4.manager;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;

/**
 * Created by alex on 2017-04-04.
 */

public class FloatingActionButtonManager {

    public enum ActionButtonState {
        ADD_CARD,
        DECK,
        GONE
    }

    private ActionButtonState currentState;

    private static FloatingActionButtonManager instance;
    private Context context;

    private FloatingActionButtonManager() {
        currentState = ActionButtonState.GONE;
    }

    public static FloatingActionButtonManager getInstance() {
        if (instance == null) {
            instance = new FloatingActionButtonManager();
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
                fab.show();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivityManager) activity).createDeck();
                    }
                });
                break;
            case ADD_CARD:
                fab.show();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivityManager) activity).startActivityNewCard();
                    }
                });
                break;
            case GONE:
                // action button
                fab.hide();
                break;
            default:
                fab.setVisibility(View.GONE);
                break;
        }
    }
}
