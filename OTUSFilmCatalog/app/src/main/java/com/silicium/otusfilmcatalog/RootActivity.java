package com.silicium.otusfilmcatalog;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.IGotoFragmentCallback;
import com.silicium.otusfilmcatalog.logic.model.IOnBackPressedListener;
import com.silicium.otusfilmcatalog.ui.AboutFragment;
import com.silicium.otusfilmcatalog.ui.AddFragment;
import com.silicium.otusfilmcatalog.ui.DetailFragment;
import com.silicium.otusfilmcatalog.ui.MainFragment;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class RootActivity extends AppCompatActivity implements IGotoFragmentCallback, FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        Fragment topFragment = getTopFragmentOrNull();
        if (topFragment == null) // первый запуск
            GotoMainFragment();
        else
            CheckContainerSizes();

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        FilmDescriptionStorage.getInstance().updateWidgetView();
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

        CheckContainerSizes();
    }

    @Override
    public void GotoDetailFragment(String filmID) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adds_fragment, DetailFragment.newInstance(filmID), DetailFragment.FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
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

    @Override
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

    @Override
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

    private void CheckContainerSizes() {
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
}
