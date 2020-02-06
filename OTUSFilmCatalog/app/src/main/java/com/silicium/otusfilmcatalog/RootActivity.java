package com.silicium.otusfilmcatalog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.silicium.otusfilmcatalog.logic.controller.MetricsStorage;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.IGotoFragmentCallback;
import com.silicium.otusfilmcatalog.logic.model.IOnBackPressedListener;
import com.silicium.otusfilmcatalog.ui.AboutFragment;
import com.silicium.otusfilmcatalog.ui.AddFragment;
import com.silicium.otusfilmcatalog.ui.DetailFragment;
import com.silicium.otusfilmcatalog.ui.MainFragment;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class RootActivity extends AppCompatActivity implements IGotoFragmentCallback, FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        Fragment topFragment = getTopFragmentOrNull();
        if (topFragment == null) // первый запуск
            gotoMainFragment();
        else
            checkContainerSizes();

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        MetricsStorage.getMetricNotifier().notifyObserversDataChanged();

        Toolbar toolbar = findViewById(R.id.toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof FragmentWithCallback) {
            ((FragmentWithCallback) fragment).setGotoFragmentCallback(this);
        }
    }

    @Override
    public void onBackStackChanged() {
        // Манипуляции с подсветкой последнего выбранного фильма в горизонтальном режиме
        String filmID = "";
        Fragment topFragment = getTopFragmentOrNull();
        if (topFragment instanceof DetailFragment) {
            filmID = ((DetailFragment) topFragment).getFilmID();
        }

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.FRAGMENT_TAG);
        if (mainFragment != null && !isPortrait()) {
            mainFragment.setSelectedFilmTag(filmID);
        }

        checkContainerSizes();
    }

    @Override
    public void gotoDetailFragment(@NonNull String filmID) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adds_fragment, DetailFragment.newInstance(filmID), DetailFragment.FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoAddFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_fragment, new AddFragment(), AddFragment.FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoMainFragment() {
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

    @Override
    public void gotoAboutFragment() {
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

    @Nullable
    private Fragment getTopFragmentOrNull() {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getFragments().size();
        if (count > 0)
            fragment = fragmentManager.getFragments().get(count - 1);
        return fragment;
    }

    private boolean isPortrait() {
        return getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;
    }

    private void checkContainerSizes() {
        Fragment topFragment = getTopFragmentOrNull();
        FrameLayout root_fragment = findViewById(R.id.root_fragment);
        FrameLayout adds_fragment = findViewById(R.id.adds_fragment);

        if (isPortrait()) { //Манипуляции с тем, какой из контейнеров показывать в вертикальном режиме
            if (topFragment instanceof DetailFragment) {
                root_fragment.setVisibility(FrameLayout.GONE);
                adds_fragment.setVisibility(FrameLayout.VISIBLE);
            } else {
                root_fragment.setVisibility(FrameLayout.VISIBLE);
                adds_fragment.setVisibility(FrameLayout.GONE);
            }
        } else { //Манипуляции с размерами контейнеров в горизонтальном режиме
            if (topFragment instanceof MainFragment || topFragment instanceof DetailFragment) { // Меню слева, детали справа
                root_fragment.setLayoutParams(new ConstraintLayout.LayoutParams((int) getResources().getDimension(R.dimen.chooserSize_landscapeMode), ViewGroup.LayoutParams.MATCH_PARENT));
            } else { // Добавление и About - на весь экран
                root_fragment.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_home)
            gotoMainFragment();
        else if (id == R.id.item_about)
            gotoAboutFragment();
        else if (id == R.id.item_exit) {
            AlertDialog.Builder bld = new AlertDialog.Builder(this);
            DialogInterface.OnClickListener exitDo =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    };

            bld.setMessage(R.string.exitDialogMessage);
            bld.setTitle(R.string.exitDialogTitle);
            bld.setNegativeButton(R.string.exitDialogNegativeAnswer, null);
            bld.setPositiveButton(R.string.exitDialogPositiveAnswer, exitDo);
            bld.setCancelable(false);
            AlertDialog dialog = bld.create();
            dialog.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
