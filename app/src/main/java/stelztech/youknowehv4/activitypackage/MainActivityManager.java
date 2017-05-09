package stelztech.youknowehv4.activitypackage;

import android.content.Intent;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.fragmentpackage.AboutFragment;
import stelztech.youknowehv4.fragmentpackage.CardListFragment;
import stelztech.youknowehv4.fragmentpackage.DeckListFragment;
import stelztech.youknowehv4.fragmentpackage.PracticeFragment;
import stelztech.youknowehv4.fragmentpackage.ProfileFragment;
import stelztech.youknowehv4.fragmentpackage.SettingsFragment;
import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.manager.ActionButtonManager;
import stelztech.youknowehv4.manager.CardInfoToolbarManager;
import stelztech.youknowehv4.manager.MainMenuToolbarManager;

public class MainActivityManager extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // fragments
    private DeckListFragment mDeckListFragment;
    private CardListFragment mCardListFragment;
    private PracticeFragment mPracticeFragment;
    private SettingsFragment mSettingsFragment;
    private AboutFragment mAboutFragment;
    private ProfileFragment mProfileFragment;
    private boolean mViewIsAtHome;

    private Fragment previousFragment;


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
    public final static int ARCHIVED_RESULT = 3;

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

        MainMenuToolbarManager.getInstance().setContext(this);
        CardInfoToolbarManager.getInstance().setContext(this);

        Helper helper = Helper.getInstance();
        helper.setContext(this);

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
        mPracticeFragment = new PracticeFragment();
        mSettingsFragment = new SettingsFragment();
        mAboutFragment = new AboutFragment();
        mProfileFragment = new ProfileFragment();

        // default page
        displayFragment(R.id.practice);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);

        // search menu option
        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);


                return false;
            }
        });


        return true;
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
            }
        }, 150);

        return true;
    }

    public void createDeck() {
        mDeckListFragment.createDeck();
    }


    public void displayFragment(int fragmentId) {


        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (fragmentId) {
            case R.id.practice:
                fragment = mPracticeFragment;
                title = "Review";
                break;
            case R.id.card_list:
                fragment = mCardListFragment;
//                mCardListFragment.setToSelectDeckId("-1");
                title = "";
                break;
            case R.id.deck_list:
                fragment = mDeckListFragment;
                title = "Deck List";
                break;
            case R.id.settings:
                fragment = mSettingsFragment;
                title = "Settings";
                break;
            case R.id.about:
                fragment = mAboutFragment;
                title = "About";
                break;
            case R.id.profile:
                fragment = mProfileFragment;
                title = "Profile";
                break;
        }

        // animate the fragment switch
        if (fragment != null) {
            if (!fragment.isVisible()) {

                replaceFragmentWithAnimation(fragment, "" + fragment);

            }
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        // set hardware back button boolean
        if (fragmentId == R.id.practice) {
            mViewIsAtHome = true;
        } else {
            mViewIsAtHome = false;
        }

        // if previous fragment was deck and the next one is card, set back button functionality
        if (previousFragment != null &&
                previousFragment.equals(mDeckListFragment) && fragment.equals(mCardListFragment)) {
            goBackToDecks = true;
        }

        previousFragment = fragment;

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

                if (goBackToDecks) {
                    displayFragment(R.id.deck_list);
                    goBackToDecks = false;
                } else {
                    if (!mViewIsAtHome) { //if the current view is not the News fragment
                        displayFragment(R.id.practice);
                        navigationView.setCheckedItem(R.id.practice);
                    } else {
                        moveTaskToBack(true);  // if view is a practice, exit app
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

        if (fragment.equals(mPracticeFragment) && previousFragment != null) {
            transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
        } else if (fragment.equals(mCardListFragment)) {
            if (previousFragment != null && previousFragment.equals(mDeckListFragment))
                transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
            else
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        } else if (fragment.equals(mDeckListFragment)) {
            if (previousFragment != null && previousFragment.equals(mPracticeFragment)
                    || previousFragment.equals(mCardListFragment))
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            else
                transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down);
        } else {
            // only the three first fragments have a sliding animation
            transaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        }


        transaction.replace(R.id.content_frame, fragment, tag);
        transaction.commit();
    }

    // returns the currently selected deck in card list
    public String getCurrentDeckIdSelected() {
        return mCardListFragment.getCurrentDeckIdSelected();
    }

    // activity to create a new card
    public void startActivityNewCard() {
        Intent i = new Intent(this, CardInfoActivity.class);
        i.putExtra("initialDeckId", getCurrentDeckIdSelected());
        i.putExtra("initialState", CardInfoActivity.CardInfoState.NEW);
        this.startActivityForResult(i, MainActivityManager.CARD_RESULT);
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
                mCardListFragment.setSpinnerSelected();
            }
            if(mCardListFragment.getCurrentState() == CardListFragment.CardListState.SEARCH){
                mCardListFragment.populateSearchListView(mCardListFragment.getSearchView().getQuery().toString());
            }else{
                mCardListFragment.populateListView(getCurrentDeckIdSelected());
                mCardListFragment.listViewShow();
            }

        } else if (requestCode == EXPORT_RESULT) {

        }else if (requestCode == ARCHIVED_RESULT){
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
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

}
