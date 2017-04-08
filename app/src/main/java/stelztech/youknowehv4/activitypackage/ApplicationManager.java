package stelztech.youknowehv4.activitypackage;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import stelztech.youknowehv4.fragmentpackage.DeckListFragment;
import stelztech.youknowehv4.fragmentpackage.PracticeFragment;
import stelztech.youknowehv4.fragmentpackage.SettingsFragment;
import stelztech.youknowehv4.fragmentpackage.WordListFragment;
import stelztech.youknowehv4.state.ActionButtonStateManager;
import stelztech.youknowehv4.state.ToolbarStateManager;

public class ApplicationManager extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private String test;

    // fragments
    private DeckListFragment deckListFragment;
    private WordListFragment wordListFragment;
    private boolean viewIsAtHome;

    private float lastTranslate = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init
        viewIsAtHome = true;

        // action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FrameLayout frame = (FrameLayout) findViewById(R.id.content_frame);

        ActionButtonStateManager actionButtonStateManager = ActionButtonStateManager.getInstance();
        actionButtonStateManager.setContext(this);
        actionButtonStateManager.setState(ActionButtonStateManager.actionButtonState.GONE, this);

        ToolbarStateManager toolbarStateManager = ToolbarStateManager.getInstance();
        toolbarStateManager.setContext(this);

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
        deckListFragment = new DeckListFragment();
        wordListFragment = new WordListFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        displayFragment(R.id.practice);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

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
        deckListFragment.createDeck();
    }


    public void displayFragment(int fragmentId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (fragmentId) {
            case R.id.practice:
                fragment = new PracticeFragment();
                title = "Practice";
                break;
            case R.id.word_list:
                fragment = wordListFragment;
                title = "";
                break;
            case R.id.deck_list:
                fragment = deckListFragment;
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

        if (fragmentId == R.id.practice) {
            viewIsAtHome = true;
        } else {
            viewIsAtHome = false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    public void displayDeckInfo(String deckId) {
        displayFragment(R.id.word_list);
        wordListFragment.displayDeckInfo(deckId);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.word_list);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            if (!viewIsAtHome) { //if the current view is not the News fragment
                displayFragment(R.id.practice);
            } else {
                moveTaskToBack(true);  //If view is in News fragment, exit application
            }
        }
    }

    public void replaceFragmentWithAnimation(android.support.v4.app.Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);


        transaction.replace(R.id.content_frame, fragment, tag);
        transaction.commit();
    }

}
