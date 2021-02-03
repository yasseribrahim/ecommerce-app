package com.ecommerce.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.ecommerce.app.R;
import com.ecommerce.app.custom.CustomViewPager;
import com.ecommerce.app.fragments.CartFragment;
import com.ecommerce.app.fragments.CategoryFragment;
import com.ecommerce.app.fragments.OrdersFragment;
import com.ecommerce.app.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    private Toolbar toolbar;
    private CustomViewPager pager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private BottomNavigationView navigation;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);
        setActionBarTitle("Categories");

        auth = FirebaseAuth.getInstance();

        navigation = findViewById(R.id.bottom_navigation);
        pager = findViewById(R.id.pager);

        pager.addOnPageChangeListener(this);
        navigation.setOnNavigationItemSelectedListener(this);
        loadFragments();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation_home view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_cat:
                pager.setCurrentItem(0);
                return true;
            case R.id.nav_search:
                pager.setCurrentItem(1);
                return true;
            case R.id.nav_cart:
                pager.setCurrentItem(2);
                return true;
            case R.id.nav_order:
                pager.setCurrentItem(3);
                return true;
        }
        return false;
    }

    protected void setupSupportedActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolBarShadowStyle);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    protected void setActionBarTitle(int titleId) {
        getSupportActionBar().setTitle(titleId);
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chart:
                startActivity(new Intent(this, OrdersHistoryActivity.class));
                break;
            case R.id.action_logout:
                auth.signOut();
                startActivity(new Intent(this, SplashActivity.class));
                finishAffinity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragments() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.titles.clear();
        sectionsPagerAdapter.fragments.clear();
        sectionsPagerAdapter.notifyDataSetChanged();

        sectionsPagerAdapter.addFrag(CategoryFragment.newInstance(), getString(R.string.str_category));
        sectionsPagerAdapter.addFrag(SearchFragment.newInstance(), getString(R.string.str_search));
        sectionsPagerAdapter.addFrag(CartFragment.newInstance(), getString(R.string.str_cart));
        sectionsPagerAdapter.addFrag(OrdersFragment.newInstance(), getString(R.string.str_orders));

        pager.setOffscreenPageLimit(3);
        pager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setActionBarTitle(sectionsPagerAdapter.titles.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments;
        private final List<String> titles;

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
            clearFragments(manager);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFrag(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        void clearFragments(FragmentManager manager) {
            List<Fragment> fragments = manager.getFragments();
            if (fragments != null) {
                FragmentTransaction transaction = manager.beginTransaction();
                for (Fragment fragment : fragments) {
                }
                transaction.commitAllowingStateLoss();
            }
        }
    }
}
