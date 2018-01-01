package stelztech.youknowehv4.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.components.CustomProgressDialog;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.fragments.FragmentCommon;
import stelztech.youknowehv4.fragments.about.AboutFragment;
import stelztech.youknowehv4.fragments.card.CardListFragment;
import stelztech.youknowehv4.fragments.deck.DeckListFragment;
import stelztech.youknowehv4.fragments.profile.ProfileFragment;
import stelztech.youknowehv4.fragments.quiz.QuizFragment;
import stelztech.youknowehv4.fragments.statistics.StatisticsFragment;
import stelztech.youknowehv4.fragments.review.ReviewFragment;
import stelztech.youknowehv4.fragments.profile.Old_ProfileFragment;
import stelztech.youknowehv4.utilities.CardUtilities;
import stelztech.youknowehv4.utilities.Helper;
import stelztech.youknowehv4.manager.FloatingActionButtonManager;
import stelztech.youknowehv4.manager.CardInfoToolbarManager;
import stelztech.youknowehv4.manager.CardToolbarManager;
import stelztech.youknowehv4.manager.ExportImportManager;
import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.manager.ThemeManager;

public class MainActivityManager extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // fragments
//    private List<Fragment> fragmentList;
    private DeckListFragment mDeckListFragment;
    private CardListFragment mCardListFragment;
    private ReviewFragment mReviewFragment;
    private AboutFragment mAboutFragment;
    private ProfileFragment mProfileFragment;
    private QuizFragment mQuizFragment;
    private StatisticsFragment mStatisticsFragment;
    private boolean mViewIsAtHome;

    private FragmentCommon previousFragment;
    private FragmentCommon currentFragment;

    private boolean goBackToDecks;

    private DrawerLayout drawer;

    // components
    private NavigationView navigationView;

    private final int INT_NULL = -1;

    private boolean backToPreviousActivity = false;
    private long lastWordInfoSeen = -1;

    // Activity results
    public final static int CARD_RESULT = 1;
    public final static int EXPORT_RESULT = 2;
    public final static int RESULT_ANIMATION_RIGHT_TO_LEFT = 3;
    public final static int EXPORT_RESULT_ALL = 4;
    public final static int RESULT_QUIZ_END = 5;
    public final static int PROFILE_UPDATED = 6;

    // set fragment after drawer close
    private int mFragmentToSet = INT_NULL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeManager.getInstance().getCurrentAppThemeNoActionBarValue());

        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_menu);
        setSupportActionBar(toolbar);

        // init
        mViewIsAtHome = true;
        goBackToDecks = false;

        // action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FrameLayout frame = (FrameLayout) findViewById(R.id.content_frame);

        FloatingActionButtonManager floatingActionButtonManager = FloatingActionButtonManager.getInstance();
        floatingActionButtonManager.setState(FloatingActionButtonManager.ActionButtonState.GONE, this);

        SortingStateManager.getInstance().setContext(this);

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
        mReviewFragment = new ReviewFragment(1, false);
        mCardListFragment = new CardListFragment(2, false);
        mDeckListFragment = new DeckListFragment(3, false);
        mQuizFragment = new QuizFragment(4, false);
        mStatisticsFragment = new StatisticsFragment(5, false);
        mProfileFragment = new ProfileFragment(6, true);
        mAboutFragment = new AboutFragment(7, true);

        Intent intent = getIntent();
        boolean loadProfilePage = intent.getBooleanExtra("StartOnProfileFragment", false);

        if (loadProfilePage) {
            displayFragment(R.id.profile);
        } else {
            // default page
            displayFragment(R.id.review);
        }


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

        switch (fragmentId) {
            case R.id.review:
                currentFragment = mReviewFragment;
                title = "Review";
                subtitle = Database.mUserDao.fetchActiveProfile().getProfileName();
                break;
            case R.id.card_list:
                currentFragment = mCardListFragment;
//                mCardListFragment.setToSelectDeckId("-1");
                title = "";
                break;
            case R.id.deck_list:
                currentFragment = mDeckListFragment;
                title = "Deck List";
                subtitle = Database.mUserDao.fetchActiveProfile().getProfileName();
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
                subtitle = Database.mUserDao.fetchActiveProfile().getProfileName();
                break;
            case R.id.statistics:
                currentFragment = mStatisticsFragment;
                title = "Statistics";
                subtitle = Database.mUserDao.fetchActiveProfile().getProfileName();
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
    public void displayDeckInfo(long deckId) {
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
        lastWordInfoSeen = -1;
    }

    // transition animation logic
    public void replaceFragmentWithAnimation(FragmentCommon fragment, String tag) {


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (previousFragment != null) {
            if (fragment.isAnimationFade()) {
                transaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            } else if (previousFragment.getAnimationLayoutPosition() < fragment.getAnimationLayoutPosition()) {
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        } else {
            transaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        }

        transaction.replace(R.id.content_frame, fragment, tag);
        transaction.commit();
    }

    // returns the currently selected deck in card list
    public long getCurrentDeckIdSelected() {
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
    public void startActivityViewCard(long cardId) {
        lastWordInfoSeen = cardId;
        Intent i = new Intent(this, CardInfoActivity.class);
        i.putExtra("initialState", CardInfoActivity.CardInfoState.VIEW);
        i.putExtra("cardId", cardId);
        this.startActivityForResult(i, MainActivityManager.CARD_RESULT);
    }

    public void startActivityEditCard(long cardId) {
        lastWordInfoSeen = cardId;
        Intent i = new Intent(this, CardInfoActivity.class);
        i.putExtra("initialState", CardInfoActivity.CardInfoState.EDIT);
        i.putExtra("cardId", cardId);
        this.startActivityForResult(i, MainActivityManager.CARD_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        //TODO add if statements

        if (requestCode == CARD_RESULT) {
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

            if (data != null && data.getStringExtra("deckIdReturn") != null) {
                mCardListFragment.setToSelectDeckId(data.getLongExtra("deckIdReturn", 1L));
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
                ExportImportManager.readCSV(this, selectedDocument, null);
            }
        } else if (requestCode == RESULT_ANIMATION_RIGHT_TO_LEFT) {
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (requestCode == EXPORT_RESULT_ALL) {
            if (resultCode == RESULT_OK) {

                final CustomProgressDialog customProgressDialog = new CustomProgressDialog("Importing Decks", 100, MainActivityManager.this, MainActivityManager.this) {
                    @Override
                    public void loadInformation() {
                        Uri uri = data.getData();
                        ExportImportManager.readAllCSV(MainActivityManager.this, uri, this);
                        CardUtilities.mergeDuplicates(this, -1);
                    }

                    @Override
                    public void informationLoaded() {

                    }
                };
                customProgressDialog.startDialog();

            }
        } else if (requestCode == RESULT_QUIZ_END) {
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

            mQuizFragment.onQuizFinishResult();


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
