package com.business.card.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.business.card.R;
import com.business.card.fragments.CardLayoutFragment;
import com.business.card.util.Util;

public class SelectLayoutActivity extends ActionBarActivity {

    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;

    private int currentPage;

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;

            getSupportActionBar().setSubtitle(getString(R.string.x_of_y, (currentPage + 1), pagerAdapter.getCount()));

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(Util.getColorByCardLayoutNo(currentPage + 1))));

//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String selectedLayout = getIntent().getExtras().getString(AddEditCardActivity.LAYOUT_EXTRA_KEY);

        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setOnPageChangeListener(pageChangeListener);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.setCurrentItem(Integer.parseInt(selectedLayout) - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // the top left back button was clicked
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.action_logout:
                (new Util()).displayConfirmLogoutDialog(this);

                return true;
            case R.id.action_accept:
                Intent returnIntent = new Intent();
                returnIntent.putExtra(AddEditCardActivity.LAYOUT_EXTRA_KEY, String.valueOf(currentPage + 1));
                setResult(RESULT_OK, returnIntent);
                finish();
                return true;
            case R.id.action_settings:
                // start the settings activity
                Intent settingsINtent = new Intent(this, SettingsActivity.class);
                startActivity(settingsINtent);

                break;
            default:
                break;
        }

        return true;
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Bundle bundle = new Bundle();
            CardLayoutFragment cardLayoutFragment = new CardLayoutFragment();

            switch (pos) {
                case 0:
                    bundle.putInt(CardLayoutFragment.LAYOUT_KEY, R.layout.card_layout_1);
                    cardLayoutFragment.setArguments(bundle);
                    break;
                case 1:
                    bundle.putInt(CardLayoutFragment.LAYOUT_KEY, R.layout.card_layout_2);
                    cardLayoutFragment.setArguments(bundle);
                    break;
                case 2:
                    bundle.putInt(CardLayoutFragment.LAYOUT_KEY, R.layout.card_layout_3);
                    cardLayoutFragment.setArguments(bundle);
                    break;
                case 3:
                    bundle.putInt(CardLayoutFragment.LAYOUT_KEY, R.layout.card_layout_4);
                    cardLayoutFragment.setArguments(bundle);
                    break;
                case 4:
                    bundle.putInt(CardLayoutFragment.LAYOUT_KEY, R.layout.card_layout_5);
                    cardLayoutFragment.setArguments(bundle);
                    break;
                case 5:
                    bundle.putInt(CardLayoutFragment.LAYOUT_KEY, R.layout.card_layout_6);
                    cardLayoutFragment.setArguments(bundle);
                    break;
                default:
                    return null;
            }

            return cardLayoutFragment;
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(R.string.select_card_layout);
        }
    }
}
