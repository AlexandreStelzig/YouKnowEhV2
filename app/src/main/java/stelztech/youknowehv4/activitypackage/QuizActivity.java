package stelztech.youknowehv4.activitypackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Profile;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizActivity extends AppCompatActivity {


    // database
    private DatabaseManager dbManager;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.enter, R.anim.exit);

        setContentView(R.layout.activity_quiz);

        getSupportActionBar().setTitle("Quiz");


        dbManager = DatabaseManager.getInstance(this);

        // set back button instead of drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_quiz, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id) {

            // back button
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
