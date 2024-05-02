package com.example.cs4084_project;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0,0,0,0);
    }

    HomeFragment homeFragment = new HomeFragment();
    ExploreFragment exploreFragment = new ExploreFragment();
    CoffeeFragment coffeeFragment = new CoffeeFragment();
    AccountFragment accountFragment = new AccountFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getVisibleFragment();
        int itemId = item.getItemId();
        if (itemId == R.id.home) {
            fr.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            fr.replace(R.id.flFragment, homeFragment).commit();
            return true;
        } else if (itemId == R.id.explore) {
            if (currentFragment instanceof HomeFragment) {
                fr.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                fr.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            fr.replace(R.id.flFragment, exploreFragment).commit();
            return true;
        } else if (itemId == R.id.coffee) {
            if (currentFragment instanceof AccountFragment) {
                fr.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                fr.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            fr.replace(R.id.flFragment, coffeeFragment).commit();
            return true;
        } else if (itemId == R.id.account) {
            fr.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fr.replace(R.id.flFragment, accountFragment).commit();
            return true;
        }
        return false;
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment f : fragments) {
            if (f != null && f.isVisible()) {
                return f;
            }
        }
        return null;
    }
}