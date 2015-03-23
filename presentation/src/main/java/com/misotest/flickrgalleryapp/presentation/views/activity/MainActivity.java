package com.misotest.flickrgalleryapp.presentation.views.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.misotest.flickrgalleryapp.R;
import com.misotest.flickrgalleryapp.presentation.utils.FragmentHelper;
import com.misotest.flickrgalleryapp.presentation.views.fragments.PhotoGridFragment;

/**
 * Main activity in fragment oriented app
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentHelper.prepareAndShowFragment(this, R.id.fragment_container,
                new PhotoGridFragment(), false, PhotoGridFragment.TAG);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
