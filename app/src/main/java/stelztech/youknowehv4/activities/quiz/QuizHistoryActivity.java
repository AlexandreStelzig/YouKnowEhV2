package stelztech.youknowehv4.activities.quiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.components.CustomProgressDialog;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.manager.exportimport.ExportImportManager;
import stelztech.youknowehv4.manager.ThemeManager;
import stelztech.youknowehv4.utilities.DateUtilities;

/**
 * Created by alex on 2017-05-09.
 */

public class QuizHistoryActivity extends AppCompatActivity {


    private ListView listView;
    private ProgressBar progressBar;
    private TextView placerholderTextView;
    private TextView nbQuizLabel;

    private CustomListAdapter customListAdapter;

    private boolean loading;

    private int indexSelected;

    private List<Quiz> quizList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.getInstance().setApplicationTheme(this, true);
        setContentView(R.layout.fragment_list);

        // intro animation
        overridePendingTransition(R.anim.enter, R.anim.exit);

        // set back button instead of drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Quiz History");

        indexSelected = -1;

        listView = (ListView) findViewById(R.id.listview);
        placerholderTextView = (TextView) findViewById(R.id.list_text);
        placerholderTextView.setText("Empty");
        progressBar = (ProgressBar) findViewById(R.id.list_loading_indicator);
        nbQuizLabel = (TextView) findViewById(R.id.listview_number_cards);

        ((TextView) findViewById(R.id.listview_number_cards_practice)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.listview_card_orientation)).setVisibility(View.GONE);

        loading = false;
        quizList = new ArrayList<>();

        customListAdapter = new CustomListAdapter(this);
        listView.setAdapter(customListAdapter);

        populateListView();


    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

//        indexSelected = info.position;

        if (v.getId() == R.id.listview) {
            menu.setHeaderTitle(DateUtilities.convertDateToDisplayString(quizList.get(indexSelected).getDateCreated()));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.hold_menu_quiz_history, menu);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_quiz_history, menu);
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
            case R.id.action_quiz_history_export:
                String dateNow = DateUtilities.getDateNowString();
                ExportImportManager.exportQuizHistoryToEmail(this, DateUtilities.convertDateToDisplayString(DateUtilities.getDateNowString()), quizList);
                return true;
            case R.id.action_quiz_history_import:
                ExportImportManager.importQuizHistory(this, this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_quiz_history_delete:
                Database.mQuizDao.deleteQuiz(quizList.get(indexSelected).getQuizId());
                populateListView();
                return true;
            default:
                indexSelected = -1;
                return super.onContextItemSelected(item);
        }
    }

    private void populateListView() {

        progressBar.setRotation((float) (Math.random() * 360));
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        placerholderTextView.setVisibility(View.GONE);
        nbQuizLabel.setVisibility(View.INVISIBLE);

        new QuizHistoryActivity.LoadData().execute("");
    }

    private void setNumberQuizText() {
        nbQuizLabel.setVisibility(View.VISIBLE);
        int numberOfDecks = quizList.size();
        String message;
        if (numberOfDecks == 0)
            message = "No Quiz History";
        else if (numberOfDecks == 1)
            message = "1 Quiz";
        else
            message = numberOfDecks + " Quizzes";
        nbQuizLabel.setText(message);
    }

    private class CustomListAdapter extends BaseAdapter {
        Context context;

        private LayoutInflater inflater = null;

        public CustomListAdapter(Context context) {
            // TODO Auto-generated constructor stub
            super();
            this.context = context;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return quizList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            TextView quizTypeTextView;
            TextView startDateTextView;
            TextView endDateTextView;
            TextView nbPassedTextView;
            TextView nbFailedTextView;
            TextView timeTextView;
            TextView nbSkippedTextView;
            TextView orientationTextView;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            CustomListAdapter.Holder holder = null;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.custom_quiz_history_item, null);
                holder = new CustomListAdapter.Holder();

                holder.quizTypeTextView = (TextView) convertView.findViewById(R.id.custom_quiz_history_type);
                holder.startDateTextView = (TextView) convertView.findViewById(R.id.custom_quiz_history_start_date);
                holder.endDateTextView = (TextView) convertView.findViewById(R.id.custom_quiz_history_end_date);
                holder.nbPassedTextView = (TextView) convertView.findViewById(R.id.custom_quiz_history_nb_passed);
                holder.nbFailedTextView = (TextView) convertView.findViewById(R.id.custom_quiz_history_nb_failed);
                holder.nbSkippedTextView = (TextView) convertView.findViewById(R.id.custom_quiz_history_nb_skipped);
                holder.timeTextView = (TextView) convertView.findViewById(R.id.custom_quiz_history_time);
                holder.orientationTextView = (TextView) convertView.findViewById(R.id.custom_quiz_history_orientation);


                convertView.setTag(holder);
            } else {
                holder = (CustomListAdapter.Holder) convertView.getTag();
            }

            Quiz quiz = quizList.get(position);

            String quizType = "";

            switch (quiz.getMode()) {

                case READING:
                    quizType = "Reading";
                    break;
                case WRITING:
                    quizType = "Writing";
                    break;
                case MULTIPLE_CHOICE:
                    quizType = "Multiple Choice";
                    break;
            }

            holder.quizTypeTextView.setText(quizType);

            holder.startDateTextView.setText("Start: " + DateUtilities.convertDateToDisplayString(quiz.getDateCreated()));
            holder.endDateTextView.setText("End: " + DateUtilities.convertDateToDisplayString(quiz.getDateFinished()));

            int nbPassed = quiz.getTotalPassed();
            int nbFailed = quiz.getTotalFailed();
            int nbSkipped = quiz.getTotalSkipped();

            holder.nbPassedTextView.setText(nbPassed + " Passed");
            holder.nbFailedTextView.setText(nbFailed + " Failed");
            holder.nbSkippedTextView.setText(nbSkipped + " Skipped");


            Date dateCreated = DateUtilities.stringToDate(quiz.getDateCreated());
            Date dateFinished = DateUtilities.stringToDate(quiz.getDateFinished());

            long diff = 0;
            if (dateFinished != null) {
                diff = dateFinished.getTime() - dateCreated.getTime();
            }
            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 60;

            String timeString;
            if (minutes == 0) {
                timeString = "Time: " + seconds + " seconds";
            } else if (hours == 0) {
                timeString = "Time: " + minutes + " minutes";
            } else if (days == 0) {
                minutes = minutes % 60;
                timeString = "Time: " + hours + "h" + minutes;
            } else {
                hours = hours % 24;
                timeString = "Time: " + days + " days" + hours + " hours";
            }

            holder.timeTextView.setText(timeString);

            Profile activeProfile = Database.mUserDao.fetchActiveProfile();
            String frontLabel = activeProfile.getFrontLabel();
            String backLabel = activeProfile.getBackLabel();

            boolean isReverse = quiz.isReverse();

            String orientationText;
            if (isReverse)
                orientationText = backLabel + " to " + frontLabel;
            else
                orientationText = frontLabel + " to " + backLabel;

            holder.orientationTextView.setText(orientationText);


            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    indexSelected = position;

                    return false;
                }
            });

            return convertView;

        }
    }


    private class LoadData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            loading = true;
            quizList = Database.mQuizDao.fetchAllFinishedQuizzesForActiveProfile();

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!quizList.isEmpty()) {

                placerholderTextView.setVisibility(View.GONE);

                customListAdapter.notifyDataSetChanged();

                registerForContextMenu(listView);

                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

            } else {
                placerholderTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            loading = false;
            setNumberQuizText();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (requestCode == MainActivityManager.IMPORT_QUIZ_HISTORY_RESULT) {
            if (resultCode == RESULT_OK) {


                final CustomProgressDialog customProgressDialog = new CustomProgressDialog("Importing Quiz History", 100, QuizHistoryActivity.this, QuizHistoryActivity.this) {
                    @Override
                    public void loadInformation() {

                        Uri selectedDocument = data.getData();
                        ExportImportManager.readQuizHistoryCSV(QuizHistoryActivity.this, selectedDocument, null);
                    }

                    @Override
                    public void informationLoaded() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateListView();
                            }
                        });
                    }
                };
                customProgressDialog.startDialog();


            }
        }

    }
}