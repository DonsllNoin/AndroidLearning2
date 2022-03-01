package com.example.androidlearning2.MaterialDesign.DrawerLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidlearning2.MaterialDesign.RecyclerView.Fruit;
import com.example.androidlearning2.MaterialDesign.RecyclerView.FruitAdapter;
import com.example.androidlearning2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawerLayoutActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private SwipeRefreshLayout swipeRefresh;

    // 统一创建水果对象的数组
    private Fruit[] fruits = {
            new Fruit("Apple", R.drawable.apple),
            new Fruit("Banana", R.drawable.banana),
            new Fruit("Orange", R.drawable.orange),
            new Fruit("Watermelon", R.drawable.watermelon),
            new Fruit("Pear", R.drawable.pear),
            new Fruit("Grape", R.drawable.grape),
            new Fruit("Pineapple", R.drawable.pineapple),
            new Fruit("Strawberry", R.drawable.strawberry),
            new Fruit("Cherry", R.drawable.cherry),
            new Fruit("mango", R.drawable.mango),
    };

    private List<Fruit> fruitList = new ArrayList<>();

    // 水果适配器
    private FruitAdapter adapter;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        // 设置 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_DrawerLayout);
        setSupportActionBar(toolbar);

        // 设置 DrawerLayout
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // 通过 ActionBar 来设置 toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){

            // HomeAsUp: 表示 Toolbar 最左侧的按钮，其 id 永远是 android.R.id.home
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);

        }

        // 设置 NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Call 图标默认选中
        navigationView.setCheckedItem(R.id.nav_call);

        // 在用户点击了任意菜单项之后，就会回调到下面这个方法，现在默认是关闭，没有设置逻辑
        navigationView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        // 悬浮按钮点击事件
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 只能显示一个选项
                Snackbar.make(v, "Data deleted", Snackbar.LENGTH_SHORT)
                        .setAction("No", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(DrawerLayoutActivity.this,
                                        "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        }).show();


            }
        });

    // 初始化水果们
        initFruits();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // 指定每一行有多少列水果
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        // 导入适配器
        adapter = new FruitAdapter(fruitList);

        // 配置 recyclerView
        recyclerView.setAdapter(adapter);

    // 配置 SwipeRefresh
        swipeRefresh = findViewById(R.id.swipe_refresh);
        // 设置 下拉刷新 的颜色
        swipeRefresh.setColorSchemeColors(R.color.design_default_color_primary);
        // 下拉刷新 的 监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 如果开始刷新了
                refreshFruits();
            }
        });

    }

    // 下拉刷新的逻辑
    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 在 主线程 上面更新 UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initFruits();
                        adapter.notifyDataSetChanged();
                        // 关闭刷新
                        swipeRefresh.setRefreshing(false);

                    }
                });
            }
        }).start();

    }

    // 初始化水果对象们
    private void initFruits() {

        fruitList.clear();
        // 随机挑选一个水果放入 fruitList 中， 每次打开程序看到的水果数据都会不同
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int index = random.nextInt(fruits.length);
            fruitList.add(fruits[index]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.backup:
                Toast.makeText(this, "Backup", Toast.LENGTH_SHORT).show();
                break;

            case R.id.delete:
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                break;

            case R.id.settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

                // HomeAsUp 的 id
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
        }
        return true;
    }
}