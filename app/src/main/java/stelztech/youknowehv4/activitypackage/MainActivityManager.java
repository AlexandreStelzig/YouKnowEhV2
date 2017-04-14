package stelztech.youknowehv4.activitypackage;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.FrameLayout;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.fragmentpackage.AboutFragment;
import stelztech.youknowehv4.fragmentpackage.AccountFragment;
import stelztech.youknowehv4.fragmentpackage.CardListFragment;
import stelztech.youknowehv4.fragmentpackage.DeckListFragment;
import stelztech.youknowehv4.fragmentpackage.PracticeFragment;
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
    private boolean mViewIsAtHome;

    private final int INT_NULL = -1;

    private boolean backToPreviousActivity = false;
    private String lastWordInfoSeen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_menu);
        setSupportActionBar(toolbar);

        // init
        mViewIsAtHome = true;

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            public void onDrawerSlide(View drawerView, float slideOffset)
//            {
//                super.onDrawerSlide(drawerView, slideOffset);
//                float moveFactor = (drawer.getWidth() * slideOffset);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//                {
//                    frame.setTranslationX(moveFactor);
//                }
//                else
//                {
//                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
//                    anim.setDuration(0);
//                    anim.setFillAfter(true);
//                    frame.startAnimation(anim);
//
//                    lastTranslate = moveFactor;
//                }
//            }
        };
        drawer.addDrawerListener(toggle);


        toggle.syncState();

        // menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.practice);

        // init fragments
        mDeckListFragment = new DeckListFragment();
        mCardListFragment = new CardListFragment();

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayFragment(item.getItemId());
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
                fragment = new PracticeFragment();
                title = "Practice";
                break;
            case R.id.card_list:
                fragment = mCardListFragment;
                mCardListFragment.setToSelectDeckId("-1");
                title = "";
                break;
            case R.id.deck_list:
                fragment = mDeckListFragment;
                title = "Deck List";
                break;
            case R.id.settings:
                fragment = new SettingsFragment();
                title = "Settings";
                break;
            case R.id.about:
                fragment = new AboutFragment();
                title = "About";
                break;
            case R.id.account:
                fragment = new AccountFragment();
                title = "Account";
                break;
        }

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

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
                if (!mViewIsAtHome) { //if the current view is not the News fragment
                    displayFragment(R.id.practice);
                } else {
                    moveTaskToBack(true);  //If view is in News fragment, exit application
                }
            }

        }
        backToPreviousActivity = false;
        lastWordInfoSeen = "";
    }

    // transition animation
    public void replaceFragmentWithAnimation(android.support.v4.app.Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);


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
        this.startActivityForResult(i, 1);
    }

    // activity to view a new card
    public void startActivityViewCard(String cardId) {
        lastWordInfoSeen = cardId;
        Intent i = new Intent(this, CardInfoActivity.class);
        i.putExtra("initialState", CardInfoActivity.CardInfoState.VIEW);
        i.putExtra("cardId", cardId);
        this.startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO add if statements


        if (data != null && data.getStringExtra("deckIdReturn") != null) {
            mCardListFragment.setToSelectDeckId(data.getStringExtra("deckIdReturn"));
            backToPreviousActivity = true;
            mCardListFragment.setSpinnerSelected();
        }
        mCardListFragment.populateListView(getCurrentDeckIdSelected());


    }

}
