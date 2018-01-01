package stelztech.youknowehv4.activities;

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
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.utilities.Helper;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-05-09.
 */

public class ArchivedActivity extends AppCompatActivity {

    // list
    private List<Card> cardList;
    private List<Card> allCardsListSearch;
    private CustomListAdapter customListAdapter;
    private ListView listView;
    private TextView placeholderTextView;
    private TextView orientation;
    private TextView nbCards;
    private TextView nbCardsPractice;


    // loading indicator
    private ProgressBar progressBar;


    // reverse
    private boolean isReverseOrder = false;

    private String orientationQuestionAnswer;
    private String orientationAnswerQuestion;
    private String questionLabel;
    private String answerLabel;

    private int indexSelected;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeManager.getInstance().getCurrentAppThemeValue());

        overridePendingTransition(R.anim.enter, R.anim.exit);

        setContentView(R.layout.fragment_list);

        getSupportActionBar().setTitle("Deleted Cards");

        // init components
        progressBar = (ProgressBar) findViewById(R.id.list_loading_indicator);
        indexSelected = -1;
        listView = (ListView) findViewById(R.id.listview);
        placeholderTextView = (TextView) findViewById(R.id.list_text);
        progressBar = (ProgressBar) findViewById(R.id.list_loading_indicator);

        placeholderTextView.setText("Empty");

        // header init
        orientation = (TextView) findViewById(R.id.listview_card_orientation);
        nbCards = (TextView) findViewById(R.id.listview_number_cards);
        nbCardsPractice = (TextView) findViewById(R.id.listview_number_cards_practice);

        nbCardsPractice.setVisibility(View.GONE);


        Profile currentProfile = Database.mUserDao.fetchActiveProfile();
        questionLabel = currentProfile.getFrontLabel();
        answerLabel = currentProfile.getBackLabel();

        orientationQuestionAnswer = questionLabel + " - " + answerLabel;
        orientationAnswerQuestion = answerLabel + " - " + questionLabel;

        registerForContextMenu(listView);

        // set back button instead of drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setOrientationText();
        populateListView();
    }

    private void populateListView() {

        progressBar.setRotation((float) (Math.random() * 360));
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        if (cardList != null) {
            cardList.clear();
        }

        cardList = Database.mCardDao.fetchArchivedCards();
        customListAdapter = new CustomListAdapter(this);

        if (!cardList.isEmpty()) {
            placeholderTextView.setVisibility(View.GONE);

            listView.setAdapter(customListAdapter);
            listView.smoothScrollToPosition(0);

            registerForContextMenu(listView);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }, (long) (Math.random() * 250) + 400);


        } else {
            placeholderTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        setNumberCardText();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        if (v.getId() == R.id.listview) {
            menu.setHeaderTitle(cardList.get(indexSelected).getQuestion() + " / " +
                    cardList.get(indexSelected).getAnswer());
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.hold_menu_archived, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quick_info_archived:
                showQuickInfoCard();
                return true;
            case R.id.unarchived_card:
                Database.mCardDao.toggleArchiveCard(cardList.get(indexSelected).getCardId());
                cardList.remove(indexSelected);
                if (cardList.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    placeholderTextView.setVisibility(View.VISIBLE);
                }
                customListAdapter.notifyDataSetChanged();
                setNumberCardText();
                Toast.makeText(this, "Card restored", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.permanently_delete_card:
                if (!cardList.isEmpty()) {
                    displayPermDeleteConfirmation(cardList.get(indexSelected).getCardId());
                } else {
                    Toast.makeText(this, "No cards", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                indexSelected = -1;
                return super.onContextItemSelected(item);
        }
    }

    private void setNumberCardText() {
        int numberOfCards = cardList.size();
        String message;
        if (numberOfCards == 0)
            message = "No Cards";
        else if (numberOfCards == 1)
            message = "1 Card";
        else
            message = numberOfCards + " Cards";
        nbCards.setText(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_archived, menu);
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

            case R.id.action_sort_archived:
                sortDialog().show();
                return true;

            case R.id.action_reverse_archived:

                isReverseOrder = !isReverseOrder;
                setOrientationText();
                Toast.makeText(this, "Orientation: " + orientation.getText().toString(), Toast.LENGTH_SHORT).show();
                customListAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_clear_archived:
                if (cardList.isEmpty()) {
                    Toast.makeText(this, "No deleted cards", Toast.LENGTH_SHORT).show();
                } else{
                    displayClearAllConfirmation();
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setOrientationText() {
        if (isReverseOrder) {
            orientation.setText(orientationAnswerQuestion);
        } else {
            orientation.setText(orientationQuestionAnswer);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        CardInfoToolbarManager.getInstance().setState(currentState, menu);
        return true;
    }


    // confirmation dialog
    private void displayClearAllConfirmation() {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure you want to permanently delete all archived cards?\n" +
                "All archived cards will be lost forever.");
        alertDialog.setTitle("!!! Permanently delete all cards !!!");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                for (int counter = 0; counter < cardList.size(); counter++) {
                    Database.mCardDao.deleteCard(cardList.get(counter).getCardId());
                }
                populateListView();
                Toast.makeText(ArchivedActivity.this, "All deleted cards permanently deleted", Toast.LENGTH_SHORT).show();
            }
        });
        // Setting cancel Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }


    // confirmation dialog
    private void displayPermDeleteConfirmation(final int cardId) {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure you want to permanently delete this card?\n" +
                "The card will be lost forever.");
        alertDialog.setTitle("!!! Permanently delete card !!!");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String question = cardList.get(indexSelected).getQuestion();
                String answer = cardList.get(indexSelected).getAnswer();
                Database.mCardDao.deleteCard(cardId);
                cardList.remove(indexSelected);
                if (cardList.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    placeholderTextView.setVisibility(View.VISIBLE);
                }
                customListAdapter.notifyDataSetChanged();
                setNumberCardText();

                Toast.makeText(ArchivedActivity.this, "\"" + question + " / " + answer + "\" permanently deleted", Toast.LENGTH_SHORT).show();
            }
        });
        // Setting cancel Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    // sort dialog
    private AlertDialog sortDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Profile currentProfile = Database.mUserDao.fetchActiveProfile();

        String[] sortingOptions = getResources().getStringArray(R.array.sort_options);
        sortingOptions[0] = currentProfile.getFrontLabel() + " (A-Z)";
        sortingOptions[1] = currentProfile.getFrontLabel() + " (Z-A)";
        sortingOptions[2] = currentProfile.getBackLabel() + " (A-Z)";
        sortingOptions[3] = currentProfile.getBackLabel() + " (Z-A)";

        final String[] sortingChoices = sortingOptions;
        builder.setTitle("Sort by");



        builder.setSingleChoiceItems(sortingChoices, SortingStateManager.getInstance().getSelectedPosition(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SortingStateManager sortingStateManager = SortingStateManager.getInstance();
                        sortingStateManager.changeStateByPosition(which);
                        cardList = Database.mCardDao.fetchArchivedCards();
                        customListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        listView.smoothScrollToPosition(0);

                        int sortingPosition = sortingStateManager.getSelectedPosition();
                        Toast.makeText(ArchivedActivity.this, "Sort by: " + sortingChoices[sortingPosition], Toast.LENGTH_SHORT).show();
                    }
                });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = builder.create();
        Helper.getInstance().hideKeyboard(this);


        return alert;
    }

    // helper

    private void showQuickInfoCard() {
        Card card = cardList.get(indexSelected);
        String cardQuestion = questionLabel + ": " + card.getQuestion();
        String cardAnswer = answerLabel + ": " + card.getAnswer();
        String cardDateCreated = "Date created: " + card.getDateCreated();
        String cardDateModified = "Date modified: " + card.getDateModified();

        String message = cardQuestion + "\n" + cardAnswer + "\n"
                + cardDateCreated + "\n" + cardDateModified;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton("done", null).show();
    }

    ////// CUSTOM ADAPTER //////

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
            return cardList.size();
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
            TextView questionHolder;
            TextView answerHolder;
            CheckBox checkBox;
            LinearLayout checkboxLayout;
            LinearLayout cardLayout;
            LinearLayout cardOptionLayout;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final Holder holder = new Holder();
            final View rowView;
            rowView = inflater.inflate(R.layout.custom_card_item, null);


            holder.questionHolder = (TextView) rowView.findViewById(R.id.custom_card_item_question);
            holder.answerHolder = (TextView) rowView.findViewById(R.id.custom_card_item_answer);
            holder.checkBox = (CheckBox) rowView.findViewById(R.id.custom_card_item_checkbox);
            holder.checkboxLayout = (LinearLayout) rowView.findViewById(R.id.checkbox_layout);
            holder.cardLayout = (LinearLayout) rowView.findViewById(R.id.custom_card_item_layout);
            holder.cardOptionLayout = (LinearLayout) rowView.findViewById(R.id.custom_card_option_layout);

            holder.checkboxLayout.setVisibility(View.GONE);

            ((LinearLayout) rowView.findViewById(R.id.custom_card_item_nb_decks_layout)).setVisibility(View.GONE);

            if (!isReverseOrder) {
                holder.questionHolder.setText(cardList.get(position).getQuestion());
                holder.answerHolder.setText(cardList.get(position).getAnswer());
            } else {
                holder.answerHolder.setText(cardList.get(position).getQuestion());
                holder.questionHolder.setText(cardList.get(position).getAnswer());
            }


            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    indexSelected = position;
                    openContextMenu(listView);
                    return true;
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    indexSelected = position;
                    openContextMenu(listView);
                }
            });

            holder.cardOptionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    indexSelected = position;
                    openContextMenu(listView);
                }
            });

            // ripple background
            holder.cardLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_normal));
            rowView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                        holder.cardLayout.getBackground().setHotspot(event.getX(), event.getY());
                    }
                    return false;
                }
            });

            return rowView;

        }

    }

}
