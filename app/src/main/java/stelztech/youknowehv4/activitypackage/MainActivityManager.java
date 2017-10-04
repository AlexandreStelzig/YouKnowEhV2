package stelztech.youknowehv4.activitypackage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.fragmentpackage.AboutFragment;
import stelztech.youknowehv4.fragmentpackage.CardListFragment;
import stelztech.youknowehv4.fragmentpackage.DeckListFragment;
import stelztech.youknowehv4.fragmentpackage.QuizFragment;
import stelztech.youknowehv4.fragmentpackage.ReviewFragment;
import stelztech.youknowehv4.fragmentpackage.ProfileFragment;
import stelztech.youknowehv4.fragmentpackage.SettingsFragment;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.CardInfoToolbarManager;
import stelztech.youknowehv4.manager.CardToolbarManager;
import stelztech.youknowehv4.manager.ExportImportManager;
import stelztech.youknowehv4.manager.SortingStateManager;

public class MainActivityManager extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // fragments
//    private List<Fragment> fragmentList;
    private DeckListFragment mDeckListFragment;
    private CardListFragment mCardListFragment;
    private ReviewFragment mReviewFragment;
    private SettingsFragment mSettingsFragment;
    private AboutFragment mAboutFragment;
    private ProfileFragment mProfileFragment;
    private QuizFragment mQuizFragment;
    private boolean mViewIsAtHome;

    private Fragment previousFragment;
    private Fragment currentFragment;

    private boolean goBackToDecks;

    private DrawerLayout drawer;

    // components
    private NavigationView navigationView;

    private final int INT_NULL = -1;

    private boolean backToPreviousActivity = false;
    private String lastWordInfoSeen = "";

    // Activity results
    public final static int CARD_RESULT = 1;
    public final static int EXPORT_RESULT = 2;
    public final static int RESULT_ANIMATION_RIGHT_TO_LEFT = 3;
    public final static int EXPORT_RESULT_ALL = 4;

    // set fragment after drawer close
    private int mFragmentToSet = INT_NULL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_menu);
        setSupportActionBar(toolbar);

        // init
        mViewIsAtHome = true;
        goBackToDecks = false;

        // action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FrameLayout frame = (FrameLayout) findViewById(R.id.content_frame);

        ActionButtonManager actionButtonManager = ActionButtonManager.getInstance();
        actionButtonManager.setContext(this);
        actionButtonManager.setState(ActionButtonManager.ActionButtonState.GONE, this);

        CardToolbarManager.getInstance().setContext(this);
        CardInfoToolbarManager.getInstance().setContext(this);

        Helper helper = Helper.getInstance();
        helper.setContext(this);

        SortingStateManager sortingStateManager = SortingStateManager.getInstance();
        sortingStateManager.setContext(this);

        // drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

        };
        drawer.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        // menu
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // init fragments
        mDeckListFragment = new DeckListFragment();
        mCardListFragment = new CardListFragment();
        mReviewFragment = new ReviewFragment();
        mSettingsFragment = new SettingsFragment();
        mAboutFragment = new AboutFragment();
        mProfileFragment = new ProfileFragment();
        mQuizFragment = new QuizFragment();

        // default page
        displayFragment(R.id.review);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayFragment(item.getItemId());
                goBackToDecks = false; // if clicked from the drawer, dont go back to deck from cards
                backToPreviousActivity = false;

                if (item.getItemId() == R.id.deck_list)
                    mDeckListFragment.setScrollToTop(true);

            }
        }, 150);

        return true;
    }

    public void createDeck() {
        mDeckListFragment.createDeck();
    }


    public void displayFragment(int fragmentId) {


        currentFragment = null;
        String title = getString(R.string.app_name);
        String subtitle = "";
        DatabaseManager dbManager = DatabaseManager.getInstance(MainActivityManager.this);

        switch (fragmentId) {
            case R.id.review:
                currentFragment = mReviewFragment;
                title = "Review";
                subtitle = dbManager.getActiveProfile().getProfileName();
                break;
            case R.id.card_list:
                currentFragment = mCardListFragment;
//                mCardListFragment.setToSelectDeckId("-1");
                title = "";
                break;
            case R.id.deck_list:
                currentFragment = mDeckListFragment;
                title = "Deck List";
                break;
            case R.id.settings:
                currentFragment = mSettingsFragment;
                title = "Settings";
                break;
            case R.id.about:
                currentFragment = mAboutFragment;
                title = "About";
                break;
            case R.id.profile:
                currentFragment = mProfileFragment;
                title = "Profile";
                break;
            case R.id.quiz:
                currentFragment = mQuizFragment;
                title = "Quiz";
                subtitle = dbManager.getActiveProfile().getProfileName();
                break;
            case R.id.quiz_history:
                title = "Quiz History";
                subtitle = dbManager.getActiveProfile().getProfileName();
                break;
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setSubtitle(subtitle);
        }

        // animate the fragment switch
        if (currentFragment != null) {
            if (!currentFragment.isVisible()) {

                replaceFragmentWithAnimation(currentFragment, "" + currentFragment);

            }
        }


        // set hardware back button boolean
        if (fragmentId == R.id.review) {
            mViewIsAtHome = true;
        } else {
            mViewIsAtHome = false;
        }

        // if previous fragment was deck and the next one is card, set back button functionality
        if (previousFragment != null &&
                previousFragment.equals(mDeckListFragment) && currentFragment.equals(mCardListFragment)) {
            goBackToDecks = true;
        }

        previousFragment = currentFragment;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        navigationView.setCheckedItem(fragmentId);

    }

    //
    public void displayDeckInfo(String deckId) {
        displayFragment(R.id.card_list);
        mCardListFragment.setToSelectDeckId(deckId);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.card_list);
    }


    @Override
    public void onBackPressed() {
        // set hardware back button logic
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (backToPreviousActivity) {
            startActivityViewCard(lastWordInfoSeen);
        } else {

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {

                if (!mCardListFragment.isLoading() && !mDeckListFragment.isLoading() && !mReviewFragment.isLoading()) {

                    if (currentFragment.equals(mDeckListFragment) && mDeckListFragment.isDeckOrdering()) {
                        mDeckListFragment.actionDone();
                    } else if (currentFragment.equals(mCardListFragment) &&
                            (mCardListFragment.getCurrentState().equals(CardListFragment.CardListState.PRACTICE_TOGGLE)
                                    || (mCardListFragment.getCurrentState().equals(CardListFragment.CardListState.EDIT_DECK)))) {
                        mCardListFragment.cancelState();
                    } else {

                        if (goBackToDecks) {
                            displayFragment(R.id.deck_list);
                            goBackToDecks = false;
                        } else {
                            if (!mViewIsAtHome) { //if the current view is not the News fragment
                                displayFragment(R.id.review);
                                navigationView.setCheckedItem(R.id.review);
                            } else {
                                moveTaskToBack(true);  // if view is a practice, exit app
                            }
                        }

                    }
                }
            }

        }
        backToPreviousActivity = false;
        lastWordInfoSeen = "";
    }

    // transition animation logic
    public void replaceFragmentWithAnimation(Fragment fragment, String tag) {


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragment.equals(mReviewFragment) && previousFragment != null) {
            transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
        } else if (fragment.equals(mCardListFragment)) {
            if (previousFragment != null && previousFragment.equals(mDeckListFragment))
                transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
            else
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        } else if (fragment.equals(mDeckListFragment)) {
            if (previousFragment != null && previousFragment.equals(mReviewFragment)
                    || previousFragment.equals(mCardListFragment))
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            else
                transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
        } else {
            // only the three first fragments have a sliding animation
            transaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        }

//        int positionPrevious = 0;
//        int positionCurrent = 0;
//        for(int i = 0; i < fragmentList.size(); i++){
//            if(fragmentList.get(i).equals(fragment))
//                positionCurrent = i;
//            else if(fragmentList.get(i).equals(previousFragment))
//                positionPrevious = i;
//        }
//
//        if(positionCurrent < positionPrevious)
//            transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
//        else
//            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);


        transaction.replace(R.id.content_frame, fragment, tag);
        transaction.commit();
    }

    // returns the currently selected deck in card list
    public String getCurrentDeckIdSelected() {
        return mCardListFragment.getCurrentDeckIdSelected();
    }

    // activity to create a new card
    public void startActivityNewCard() {
        if (!mCardListFragment.isLoading()) {
            Intent i = new Intent(this, CardInfoActivity.class);
            i.putExtra("initialDeckId", getCurrentDeckIdSelected());
            i.putExtra("initialState", CardInfoActivity.CardInfoState.NEW);
            this.startActivityForResult(i, MainActivityManager.CARD_RESULT);
        }
    }

    // activity to view a new card
    public void startActivityViewCard(String cardId) {
        lastWordInfoSeen = cardId;
        Intent i = new Intent(this, CardInfoActivity.class);
        i.putExtra("initialState", CardInfoActivity.CardInfoState.VIEW);
        i.putExtra("cardId", cardId);
        this.startActivityForResult(i, MainActivityManager.CARD_RESULT);
    }

    public void startActivityEditCard(String cardId) {
        lastWordInfoSeen = cardId;
        Intent i = new Intent(this, CardInfoActivity.class);
        i.putExtra("initialState", CardInfoActivity.CardInfoState.EDIT);
        i.putExtra("cardId", cardId);
        this.startActivityForResult(i, MainActivityManager.CARD_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO add if statements

        if (requestCode == CARD_RESULT) {
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

            if (data != null && data.getStringExtra("deckIdReturn") != null) {
                mCardListFragment.setToSelectDeckId(data.getStringExtra("deckIdReturn"));
                backToPreviousActivity = true;
                mCardListFragment.setScrollToTop(false);
                mCardListFragment.setSpinnerSelected();

//                mCardListFragment.populateListView(getCurrentDeckIdSelected());
            }
            if (mCardListFragment.getCurrentState() == CardListFragment.CardListState.SEARCH) {
                mCardListFragment.populateSpinner();
                mCardListFragment.populateSearchListView(mCardListFragment.getSearchView().getQuery().toString());
            } else {
                mCardListFragment.setScrollToTop(false);
                mCardListFragment.populateSpinner();

//                mCardListFragment.populateListView(getCurrentDeckIdSelected());
            }

        } else if (requestCode == EXPORT_RESULT) {
            if (resultCode == RESULT_OK) {
                Uri selectedDocument = data.getData();
                ExportImportManager.readCSV(this, selectedDocument);
            }
        } else if (requestCode == RESULT_ANIMATION_RIGHT_TO_LEFT) {
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (requestCode == EXPORT_RESULT_ALL) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                ExportImportManager.readAllCSV(MainActivityManager.this, uri);


            }
        }

    }

    public void enableDrawerSwipe(boolean isSwippable) {
        if (isSwippable) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public boolean isGoBackToDecks() {
        return goBackToDecks;
    }

    public void setGoBackToDecks(boolean goBackToDecks) {
        this.goBackToDecks = goBackToDecks;
    }

    public void resetFragmentPractice() {
        mReviewFragment.resetFragment();
    }
}
