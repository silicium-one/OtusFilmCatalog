package com.silicium.otusfilmcatalog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.IGotoFragmentCallback;
import com.silicium.otusfilmcatalog.logic.model.IOnBackPressedListener;
import com.silicium.otusfilmcatalog.ui.AboutFragment;
import com.silicium.otusfilmcatalog.ui.AddFragment;
import com.silicium.otusfilmcatalog.ui.DetailFragment;
import com.silicium.otusfilmcatalog.ui.MainFragment;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class RootActivity extends AppCompatActivity implements IGotoFragmentCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.FRAGMENT_TAG);
        if (fragment == null)
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root_fragment, new MainFragment(), MainFragment.FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof FragmentWithCallback) {
            ((FragmentWithCallback) fragment).setGotoFragmentCallback(this);
        }
    }

    @Override
    public void GotoDetailFragment(String filmID) {
        if (isPortrait()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_fragment, DetailFragment.newInstance(filmID), DetailFragment.FRAGMENT_TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adds_fragment, DetailFragment.newInstance(filmID), DetailFragment.FRAGMENT_TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void GotoAddFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_fragment, new AddFragment(), AddFragment.FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void GotoMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // В случае перехода к главному фрагменту никаких скачков по кнопке "Назад" быть не должно
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        fragmentManager.beginTransaction()
                .replace(R.id.root_fragment, new MainFragment(), MainFragment.FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void GotoAboutFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_fragment, new AboutFragment(), AboutFragment.FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getTopFragmentOrNull();

        if (fragment instanceof IOnBackPressedListener && ((IOnBackPressedListener) fragment).onBackPressed())
            return;
        super.onBackPressed();
    }

    private Fragment getTopFragmentOrNull() {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getFragments().size();
        if (count > 0)
            fragment = fragmentManager.getFragments().get(count-1);
        return fragment;
    }

    private boolean isPortrait() {
        return getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;
    }
}
