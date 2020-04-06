package com.wei.ncue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AffairFragment extends Fragment {

    Toolbar toolbar;
    ProgressBar progressBar;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("homeFragment", ".");

        final SharedPreferences prefAccount = getContext().getSharedPreferences("Account", MODE_PRIVATE);
        final SharedPreferences prefPage = getContext().getSharedPreferences("Page", MODE_PRIVATE);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        progressBar = rootView.findViewById(R.id.progressWebview);

        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d("topMenu",item.toString());
                if(item.getItemId()==R.id.logout)
                {
                    prefAccount.edit().clear().apply();
                    WebView webView = new WebView(getContext());
                    webView.setWebViewClient(new WebViewClient());
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webView.loadUrl("https://nam.ncue.edu.tw/AGLogout");

                    CookieManager cookieManager = CookieManager.getInstance();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                            @Override
                            public void onReceiveValue(Boolean value) {

                            }
                        });
                    }
                    else
                        cookieManager.removeAllCookie();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    return  true;

                }
                return false;
            }
        });


        final ViewPager viewPager = rootView.findViewById(R.id.pager);


        setupViewPager(viewPager);


        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("position", String.valueOf(position));

                prefPage.edit()
                        .putInt("pSub0", position)
                        .apply();



                /*WebviewFragment fragment = (WebviewFragment) adapter.mFragmentList.get(position);
                fragment.init();*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPager.addOnPageChangeListener(onPageChangeListener);
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(prefPage.getInt("pSub0",0));
        Log.d("homeFragment", "select");

        return rootView;
    }

    ViewPagerAdapter adapter;

    private void setupViewPager(ViewPager viewPager) {
        Log.d("webView", "?");
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        String intentURL = null;
        adapter.addFragment(new WebviewFragment("https://aps.ncue.edu.tw/app/news1.php",intentURL), "最新消息");
        intentURL = MainActivity.intentURL;
        MainActivity.intentURL = null;
        adapter.addFragment(new WebviewFragment("https://aps.ncue.edu.tw/app/signup.php",intentURL),"活動報名");
        intentURL = null;
        adapter.addFragment(new WebviewFragment("https://portal.ncue.edu.tw/portal/home.php",intentURL), "入口");

        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        public final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("校務系統");

    }
}
