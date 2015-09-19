package ua.nau.edu.Drawer;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.utils.Utils;
import com.mikepenz.materialdrawer.R.dimen;
import com.mikepenz.materialdrawer.R.id;
import com.mikepenz.materialdrawer.R.layout;
import com.mikepenz.materialdrawer.R.string;
import com.mikepenz.materialdrawer.adapter.DrawerAdapter;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Iconable;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Drawer {
    protected boolean used = false;
    protected Activity mActivity;
    protected ViewGroup mRootView;
    protected boolean mActionBarCompatibility = false;
    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected LinearLayout mSliderLayout;
    protected int mDrawerWidth = -1;
    protected Integer mDrawerGravity = null;
    protected boolean mActionBarDrawerToggleEnabled = true;
    protected ActionBarDrawerToggle mActionBarDrawerToggle;
    protected View mHeaderView;
    protected int mHeaderOffset = 0;
    protected boolean mHeaderDivider = true;
    protected View mFooterView;
    protected boolean mFooterDivider = true;
    protected View mStickyFooterView;
    protected int mSelectedItem = 0;
    protected ListView mListView;
    protected BaseAdapter mAdapter;
    protected ArrayList<IDrawerItem> mDrawerItems;
    protected boolean mCloseOnClick = true;
    protected Drawer.OnDrawerListener mOnDrawerListener;
    protected Drawer.OnDrawerItemClickListener mOnDrawerItemClickListener;
    protected Drawer.OnDrawerItemLongClickListener mOnDrawerItemLongClickListener;
    protected Drawer.OnDrawerItemSelectedListener mOnDrawerItemSelectedListener;

    public Drawer() {
    }

    public Drawer withActivity(Activity activity) {
        this.mRootView = (ViewGroup)activity.findViewById(16908290);
        this.mActivity = activity;
        return this;
    }

    public Drawer withActionBarCompatibility(boolean actionBarCompatibility) {
        this.mActionBarCompatibility = actionBarCompatibility;
        return this;
    }

    public Drawer withToolbar(Toolbar toolbar) {
        this.mToolbar = toolbar;
        return this;
    }

    public Drawer withDrawerLayout(DrawerLayout drawerLayout) {
        this.mDrawerLayout = drawerLayout;
        return this;
    }

    public Drawer withDrawerLayout(int resLayout) {
        if(this.mActivity == null) {
            throw new RuntimeException("please pass an activity first to use this call");
        } else {
            if(resLayout != -1) {
                this.mDrawerLayout = (DrawerLayout)this.mActivity.getLayoutInflater().inflate(resLayout, this.mRootView, false);
            } else {
                this.mDrawerLayout = (DrawerLayout)this.mActivity.getLayoutInflater().inflate(layout.drawer, this.mRootView, false);
            }

            return this;
        }
    }

    public Drawer withDrawerWidthPx(int drawerWidthPx) {
        this.mDrawerWidth = drawerWidthPx;
        return this;
    }

    public Drawer withDrawerWidthDp(int drawerWidthDp) {
        if(this.mActivity == null) {
            throw new RuntimeException("please pass an activity first to use this call");
        } else {
            this.mDrawerWidth = Utils.convertDpToPx(this.mActivity, (float)drawerWidthDp);
            return this;
        }
    }

    public Drawer withDrawerWidthRes(int drawerWidthRes) {
        if(this.mActivity == null) {
            throw new RuntimeException("please pass an activity first to use this call");
        } else {
            this.mDrawerWidth = this.mActivity.getResources().getDimensionPixelSize(drawerWidthRes);
            return this;
        }
    }

    public Drawer withDrawerGravity(int gravity) {
        this.mDrawerGravity = Integer.valueOf(gravity);
        return this;
    }

    public Drawer withActionBarDrawerToggle(boolean actionBarDrawerToggleEnabled) {
        this.mActionBarDrawerToggleEnabled = actionBarDrawerToggleEnabled;
        return this;
    }

    public Drawer withActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
        this.mActionBarDrawerToggleEnabled = true;
        this.mActionBarDrawerToggle = actionBarDrawerToggle;
        return this;
    }

    public Drawer withHeader(View headerView) {
        this.mHeaderView = headerView;
        this.mHeaderOffset = 1;
        return this;
    }

    public Drawer withHeader(int headerViewRes) {
        if(this.mActivity == null) {
            throw new RuntimeException("please pass an activity first to use this call");
        } else {
            if(headerViewRes != -1) {
                this.mHeaderView = this.mActivity.getLayoutInflater().inflate(headerViewRes, (ViewGroup)null, false);
                this.mHeaderOffset = 1;
            }

            return this;
        }
    }

    public Drawer withHeaderDivider(boolean headerDivider) {
        this.mHeaderDivider = headerDivider;
        return this;
    }

    public Drawer withFooter(View footerView) {
        this.mFooterView = footerView;
        return this;
    }

    public Drawer withFooter(int footerViewRes) {
        if(this.mActivity == null) {
            throw new RuntimeException("please pass an activity first to use this call");
        } else {
            if(footerViewRes != -1) {
                this.mFooterView = this.mActivity.getLayoutInflater().inflate(footerViewRes, (ViewGroup)null, false);
            }

            return this;
        }
    }

    public Drawer withFooterDivider(boolean footerDivider) {
        this.mFooterDivider = footerDivider;
        return this;
    }

    public Drawer withStickyFooter(View stickyFooter) {
        this.mStickyFooterView = stickyFooter;
        return this;
    }

    public Drawer withStickyFooter(int stickyFooterRes) {
        if(this.mActivity == null) {
            throw new RuntimeException("please pass an activity first to use this call");
        } else {
            if(stickyFooterRes != -1) {
                this.mStickyFooterView = this.mActivity.getLayoutInflater().inflate(stickyFooterRes, (ViewGroup)null, false);
            }

            return this;
        }
    }

    public Drawer withSelectedItem(int selectedItem) {
        this.mSelectedItem = selectedItem;
        return this;
    }

    public Drawer withListView(ListView listView) {
        this.mListView = listView;
        return this;
    }

    public Drawer withAdapter(BaseAdapter adapter) {
        this.mAdapter = adapter;
        return this;
    }

    public Drawer withDrawerItems(ArrayList<IDrawerItem> drawerItems) {
        this.mDrawerItems = drawerItems;
        return this;
    }

    public Drawer addDrawerItems(IDrawerItem... drawerItems) {
        if(this.mDrawerItems == null) {
            this.mDrawerItems = new ArrayList();
        }

        if(drawerItems != null) {
            Collections.addAll(this.mDrawerItems, drawerItems);
        }

        return this;
    }

    public Drawer withCloseOnClick(boolean closeOnClick) {
        this.mCloseOnClick = closeOnClick;
        return this;
    }

    public Drawer withOnDrawerListener(Drawer.OnDrawerListener onDrawerListener) {
        this.mOnDrawerListener = onDrawerListener;
        return this;
    }

    public Drawer withOnDrawerItemClickListener(Drawer.OnDrawerItemClickListener onDrawerItemClickListener) {
        this.mOnDrawerItemClickListener = onDrawerItemClickListener;
        return this;
    }

    public Drawer withOnDrawerItemLongClickListener(Drawer.OnDrawerItemLongClickListener onDrawerItemLongClickListener) {
        this.mOnDrawerItemLongClickListener = onDrawerItemLongClickListener;
        return this;
    }

    public Drawer withOnDrawerItemSelectedListener(Drawer.OnDrawerItemSelectedListener onDrawerItemSelectedListener) {
        this.mOnDrawerItemSelectedListener = onDrawerItemSelectedListener;
        return this;
    }

    public Drawer.Result build() {
        if(this.used) {
            throw new RuntimeException("you must not reuse a Drawer builder");
        } else if(this.mActivity == null) {
            throw new RuntimeException("please pass an activity");
        } else {
            this.used = true;
            if(this.mDrawerLayout == null) {
                this.withDrawerLayout(-1);
            }

            ViewGroup drawerContentRoot = (ViewGroup)this.mDrawerLayout.getChildAt(0);
            View contentView = this.mRootView.getChildAt(0);
            this.mRootView.removeView(contentView);
            drawerContentRoot.addView(contentView, new LayoutParams(-1, -1));
            this.mRootView.addView(this.mDrawerLayout, new LayoutParams(-1, -1));
            if(this.mActionBarDrawerToggleEnabled && this.mActionBarDrawerToggle == null) {
                if(this.mToolbar == null) {
                    this.mActionBarDrawerToggle = new ActionBarDrawerToggle(this.mActivity, this.mDrawerLayout, string.drawer_open, string.drawer_close) {
                        public void onDrawerOpened(View drawerView) {
                            if(Drawer.this.mOnDrawerListener != null) {
                                Drawer.this.mOnDrawerListener.onDrawerOpened(drawerView);
                            }

                            super.onDrawerOpened(drawerView);
                        }

                        public void onDrawerClosed(View drawerView) {
                            if(Drawer.this.mOnDrawerListener != null) {
                                Drawer.this.mOnDrawerListener.onDrawerClosed(drawerView);
                            }

                            super.onDrawerClosed(drawerView);
                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset){
                            super.onDrawerSlide(drawerView,slideOffset);
                            mDrawerLayout.bringChildToFront(drawerView);
                            mDrawerLayout.requestLayout();
                            mDrawerLayout.setScrimColor(Color.TRANSPARENT);
                        }
                    };
                } else {
                    this.mActionBarDrawerToggle = new ActionBarDrawerToggle(this.mActivity, this.mDrawerLayout, this.mToolbar, string.drawer_open, string.drawer_close) {
                        public void onDrawerOpened(View drawerView) {
                            if(Drawer.this.mOnDrawerListener != null) {
                                Drawer.this.mOnDrawerListener.onDrawerOpened(drawerView);
                            }

                            super.onDrawerOpened(drawerView);
                        }

                        public void onDrawerClosed(View drawerView) {
                            if(Drawer.this.mOnDrawerListener != null) {
                                Drawer.this.mOnDrawerListener.onDrawerClosed(drawerView);
                            }

                            super.onDrawerClosed(drawerView);
                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset){
                            super.onDrawerSlide(drawerView,slideOffset);
                            mDrawerLayout.bringChildToFront(drawerView);
                            mDrawerLayout.requestLayout();
                            mDrawerLayout.setScrimColor(Color.TRANSPARENT);
                        }
                    };
                }

                this.mActionBarDrawerToggle.syncState();
            }

            if(this.mActionBarDrawerToggle != null) {
                this.mDrawerLayout.setDrawerListener(this.mActionBarDrawerToggle);
            }

            this.mSliderLayout = (LinearLayout)this.mActivity.getLayoutInflater().inflate(layout.drawer_slider, this.mDrawerLayout, false);
            android.support.v4.widget.DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams)this.mSliderLayout.getLayoutParams();
            if(this.mDrawerGravity != null) {
                params.gravity = this.mDrawerGravity.intValue();
            }

            params = this.processDrawerLayoutParams(params);
            this.mSliderLayout.setLayoutParams(params);
            this.mDrawerLayout.addView(this.mSliderLayout, 1);
            this.createContent();
            return new Drawer.Result(this);
        }
    }

    public Drawer.Result append(Drawer.Result result) {
        if(this.used) {
            throw new RuntimeException("you must not reuse a Drawer builder");
        } else if(this.mDrawerGravity == null) {
            throw new RuntimeException("please set the gravity for the drawer");
        } else {
            this.used = true;
            this.mDrawerLayout = result.getDrawerLayout();
            this.mSliderLayout = (LinearLayout)this.mActivity.getLayoutInflater().inflate(layout.drawer_slider, this.mDrawerLayout, false);
            android.support.v4.widget.DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams)this.mSliderLayout.getLayoutParams();
            params.gravity = this.mDrawerGravity.intValue();
            params = this.processDrawerLayoutParams(params);
            this.mSliderLayout.setLayoutParams(params);
            this.mDrawerLayout.addView(this.mSliderLayout, 1);
            this.createContent();
            return new Drawer.Result(this);
        }
    }

    private void createContent() {
        if(this.mListView == null) {
            this.mListView = new ListView(this.mActivity);
            this.mListView.setChoiceMode(1);
            this.mListView.setDivider((Drawable)null);
            this.mListView.setClipToPadding(false);
            this.mListView.setPadding(0, this.mActivity.getResources().getDimensionPixelSize(dimen.tool_bar_top_padding), 0, 0);
        }

        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(-1, -1);
        params.weight = 1.0F;
        this.mSliderLayout.addView(this.mListView, params);
        if(this.mDrawerItems != null && this.mAdapter == null) {
            this.mAdapter = new DrawerAdapter(this.mActivity, this.mDrawerItems);
        }

        if(this.mStickyFooterView != null) {
            this.mSliderLayout.addView(this.mStickyFooterView);
        }

        LinearLayout footerContainer;
        if(this.mHeaderView != null) {
            if(this.mListView == null) {
                throw new RuntimeException("can\'t use a headerView without a listView");
            }

            if(this.mHeaderDivider) {
                footerContainer = (LinearLayout)this.mActivity.getLayoutInflater().inflate(layout.drawer_item_header, this.mListView, false);
                footerContainer.addView(this.mHeaderView, 0);
                this.mListView.addHeaderView(footerContainer);
                this.mListView.setPadding(0, 0, 0, 0);
            } else {
                this.mListView.addHeaderView(this.mHeaderView);
                this.mListView.setPadding(0, 0, 0, 0);
            }
        }

        if(this.mFooterView != null) {
            if(this.mListView == null) {
                throw new RuntimeException("can\'t use a footerView without a listView");
            }

            if(this.mHeaderDivider) {
                footerContainer = (LinearLayout)this.mActivity.getLayoutInflater().inflate(layout.drawer_item_footer, this.mListView, false);
                footerContainer.addView(this.mFooterView, 1);
                this.mListView.addFooterView(footerContainer);
            } else {
                this.mListView.addFooterView(this.mFooterView);
            }
        }

        if(this.mAdapter != null) {
            this.mListView.setAdapter(this.mAdapter);
            if(this.mListView != null && this.mSelectedItem + this.mHeaderOffset > -1) {
                this.mListView.setSelection(this.mSelectedItem + this.mHeaderOffset);
                this.mListView.setItemChecked(this.mSelectedItem + this.mHeaderOffset, true);
            }
        }

        this.mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Drawer.this.mOnDrawerItemClickListener != null) {
                    if(Drawer.this.mDrawerItems != null && Drawer.this.mDrawerItems.size() > position - Drawer.this.mHeaderOffset && position - Drawer.this.mHeaderOffset > -1) {
                        Drawer.this.mOnDrawerItemClickListener.onItemClick(parent, view, position, id, (IDrawerItem)Drawer.this.mDrawerItems.get(position - Drawer.this.mHeaderOffset));
                    } else {
                        Drawer.this.mOnDrawerItemClickListener.onItemClick(parent, view, position, id, (IDrawerItem)null);
                    }
                }

                if(Drawer.this.mCloseOnClick) {
                    Drawer.this.mDrawerLayout.closeDrawers();
                }

            }
        });
        if(this.mOnDrawerItemLongClickListener != null) {
            this.mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return Drawer.this.mDrawerItems != null && Drawer.this.mDrawerItems.size() > position - Drawer.this.mHeaderOffset && position - Drawer.this.mHeaderOffset > -1?Drawer.this.mOnDrawerItemLongClickListener.onItemLongClick(parent, view, position, id, (IDrawerItem)Drawer.this.mDrawerItems.get(position - Drawer.this.mHeaderOffset)):Drawer.this.mOnDrawerItemLongClickListener.onItemLongClick(parent, view, position, id, (IDrawerItem)null);
                }
            });
        }

        if(this.mOnDrawerItemSelectedListener != null) {
            this.mListView.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(Drawer.this.mDrawerItems != null && Drawer.this.mDrawerItems.size() > position - Drawer.this.mHeaderOffset && position - Drawer.this.mHeaderOffset > -1) {
                        Drawer.this.mOnDrawerItemSelectedListener.onItemSelected(parent, view, position, id, (IDrawerItem)Drawer.this.mDrawerItems.get(position - Drawer.this.mHeaderOffset));
                    } else {
                        Drawer.this.mOnDrawerItemSelectedListener.onItemSelected(parent, view, position, id, (IDrawerItem)null);
                    }

                }

                public void onNothingSelected(AdapterView<?> parent) {
                    Drawer.this.mOnDrawerItemSelectedListener.onNothingSelected(parent);
                }
            });
        }

        if(this.mListView != null) {
            this.mListView.smoothScrollToPosition(0);
        }

    }

    private android.support.v4.widget.DrawerLayout.LayoutParams processDrawerLayoutParams(android.support.v4.widget.DrawerLayout.LayoutParams params) {
        if(this.mDrawerGravity != null && (this.mDrawerGravity.intValue() == 5 || this.mDrawerGravity.intValue() == 8388613)) {
            params.rightMargin = 0;
            if(VERSION.SDK_INT >= 17) {
                params.setMarginEnd(0);
            }

            params.leftMargin = this.mActivity.getResources().getDimensionPixelSize(dimen.material_drawer_margin);
            if(VERSION.SDK_INT >= 17) {
                params.setMarginEnd(this.mActivity.getResources().getDimensionPixelSize(dimen.material_drawer_margin));
            }
        }

        if(this.mActionBarCompatibility) {
            TypedValue tv = new TypedValue();
            if(this.mActivity.getTheme().resolveAttribute(16843499, tv, true)) {
                int mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.mActivity.getResources().getDisplayMetrics());
                params.topMargin = mActionBarHeight;
            }
        }

        if(this.mDrawerWidth > -1) {
            params.width = this.mDrawerWidth;
        }

        return params;
    }

    public interface OnDrawerItemSelectedListener {
        void onItemSelected(AdapterView<?> var1, View var2, int var3, long var4, IDrawerItem var6);

        void onNothingSelected(AdapterView<?> var1);
    }

    public interface OnDrawerListener {
        void onDrawerOpened(View var1);

        void onDrawerClosed(View var1);
    }

    public interface OnDrawerItemLongClickListener {
        boolean onItemLongClick(AdapterView<?> var1, View var2, int var3, long var4, IDrawerItem var6);
    }

    public interface OnDrawerItemClickListener {
        void onItemClick(AdapterView<?> var1, View var2, int var3, long var4, IDrawerItem var6);
    }

    public static class Result {
        private Drawer mDrawer;
        private FrameLayout mContentView;

        public Result(Drawer drawer) {
            this.mDrawer = drawer;
        }

        public DrawerLayout getDrawerLayout() {
            return this.mDrawer.mDrawerLayout;
        }

        public void openDrawer() {
            if(this.mDrawer.mDrawerLayout != null && this.mDrawer.mSliderLayout != null) {
                this.mDrawer.mDrawerLayout.openDrawer(this.mDrawer.mSliderLayout);
            }

        }

        public void closeDrawer() {
            if(this.mDrawer.mDrawerLayout != null) {
                this.mDrawer.mDrawerLayout.closeDrawers();
            }

        }

        public boolean isDrawerOpen() {
            return this.mDrawer.mDrawerLayout != null && this.mDrawer.mSliderLayout != null?this.mDrawer.mDrawerLayout.isDrawerOpen(this.mDrawer.mSliderLayout):false;
        }

        public LinearLayout getSlider() {
            return this.mDrawer.mSliderLayout;
        }

        public FrameLayout getContent() {
            if(this.mContentView == null) {
                this.mContentView = (FrameLayout)this.mDrawer.mDrawerLayout.findViewById(id.content_layout);
            }

            return this.mContentView;
        }

        public ListView getListView() {
            return this.mDrawer.mListView;
        }

        public BaseAdapter getAdapter() {
            return this.mDrawer.mAdapter;
        }

        public ArrayList<IDrawerItem> getDrawerItems() {
            return this.mDrawer.mDrawerItems;
        }

        public View getHeader() {
            return this.mDrawer.mHeaderView;
        }

        public View getFooter() {
            return this.mDrawer.mFooterView;
        }

        public View getStickyFooter() {
            return this.mDrawer.mStickyFooterView;
        }

        public ActionBarDrawerToggle getActionBarDrawerToggle() {
            return this.mDrawer.mActionBarDrawerToggle;
        }

        public void setSelection(int position) {
            if(this.mDrawer.mListView != null) {
                this.mDrawer.mListView.setSelection(position + this.mDrawer.mHeaderOffset);
                this.mDrawer.mListView.setItemChecked(position + this.mDrawer.mHeaderOffset, true);
                if(this.mDrawer.mOnDrawerItemSelectedListener != null) {
                    if(this.mDrawer.mDrawerItems != null && this.mDrawer.mDrawerItems.size() > position - this.mDrawer.mHeaderOffset && position - this.mDrawer.mHeaderOffset > -1) {
                        this.mDrawer.mOnDrawerItemSelectedListener.onItemSelected((AdapterView)null, (View)null, position, (long)position, (IDrawerItem)this.mDrawer.mDrawerItems.get(position - this.mDrawer.mHeaderOffset));
                    } else {
                        this.mDrawer.mOnDrawerItemSelectedListener.onItemSelected((AdapterView)null, (View)null, position, (long)position, (IDrawerItem)null);
                    }
                }
            }

        }

        public void updateItem(IDrawerItem drawerItem) {
            if(drawerItem.getIdentifier() <= 0) {
                throw new RuntimeException("the item requires a unique identifier to use this method");
            } else {
                if(this.mDrawer.mDrawerItems != null) {
                    int position = 0;

                    for(Iterator var3 = this.mDrawer.mDrawerItems.iterator(); var3.hasNext(); ++position) {
                        IDrawerItem i = (IDrawerItem)var3.next();
                        if(i.getIdentifier() == drawerItem.getIdentifier()) {
                            this.updateItem(drawerItem, position);
                            return;
                        }
                    }
                }

            }
        }

        public void updateItem(IDrawerItem drawerItem, int position) {
            if(this.mDrawer.mDrawerItems != null && this.mDrawer.mDrawerItems.size() > position - this.mDrawer.mHeaderOffset && position - this.mDrawer.mHeaderOffset > -1) {
                this.mDrawer.mDrawerItems.set(position - this.mDrawer.mHeaderOffset, drawerItem);
                this.mDrawer.mAdapter.notifyDataSetChanged();
            }

        }

        public void updateName(int nameRes, int position) {
            if(this.mDrawer.mDrawerItems != null && this.mDrawer.mDrawerItems.size() > position - this.mDrawer.mHeaderOffset && position - this.mDrawer.mHeaderOffset > -1) {
                IDrawerItem drawerItem = (IDrawerItem)this.mDrawer.mDrawerItems.get(position - this.mDrawer.mHeaderOffset);
                if(drawerItem instanceof Nameable) {
                    ((Nameable)drawerItem).setNameRes(nameRes);
                }

                this.mDrawer.mDrawerItems.set(position - this.mDrawer.mHeaderOffset, drawerItem);
                this.mDrawer.mAdapter.notifyDataSetChanged();
            }

        }

        public void updateName(String name, int position) {
            if(this.mDrawer.mDrawerItems != null && this.mDrawer.mDrawerItems.size() > position - this.mDrawer.mHeaderOffset && position - this.mDrawer.mHeaderOffset > -1) {
                IDrawerItem drawerItem = (IDrawerItem)this.mDrawer.mDrawerItems.get(position - this.mDrawer.mHeaderOffset);
                if(drawerItem instanceof Nameable) {
                    ((Nameable)drawerItem).setName(name);
                }

                this.mDrawer.mDrawerItems.set(position - this.mDrawer.mHeaderOffset, drawerItem);
                this.mDrawer.mAdapter.notifyDataSetChanged();
            }

        }

        public void updateBadge(String badge, int position) {
            if(this.mDrawer.mDrawerItems != null && this.mDrawer.mDrawerItems.size() > position - this.mDrawer.mHeaderOffset && position - this.mDrawer.mHeaderOffset > -1) {
                IDrawerItem drawerItem = (IDrawerItem)this.mDrawer.mDrawerItems.get(position - this.mDrawer.mHeaderOffset);
                if(drawerItem instanceof Badgeable) {
                    ((Badgeable)drawerItem).setBadge(badge);
                }

                this.mDrawer.mDrawerItems.set(position - this.mDrawer.mHeaderOffset, drawerItem);
                this.mDrawer.mAdapter.notifyDataSetChanged();
            }

        }

        public void updateIcon(Drawable icon, int position) {
            if(this.mDrawer.mDrawerItems != null && this.mDrawer.mDrawerItems.size() > position - this.mDrawer.mHeaderOffset && position - this.mDrawer.mHeaderOffset > -1) {
                IDrawerItem drawerItem = (IDrawerItem)this.mDrawer.mDrawerItems.get(position - this.mDrawer.mHeaderOffset);
                if(drawerItem instanceof Iconable) {
                    ((Iconable)drawerItem).setIcon(icon);
                }

                this.mDrawer.mDrawerItems.set(position - this.mDrawer.mHeaderOffset, drawerItem);
                this.mDrawer.mAdapter.notifyDataSetChanged();
            }

        }

        public void updateIcon(IIcon icon, int position) {
            if(this.mDrawer.mDrawerItems != null && this.mDrawer.mDrawerItems.size() > position - this.mDrawer.mHeaderOffset && position - this.mDrawer.mHeaderOffset > -1) {
                IDrawerItem drawerItem = (IDrawerItem)this.mDrawer.mDrawerItems.get(position - this.mDrawer.mHeaderOffset);
                if(drawerItem instanceof Iconable) {
                    ((Iconable)drawerItem).setIIcon(icon);
                }

                this.mDrawer.mDrawerItems.set(position - this.mDrawer.mHeaderOffset, drawerItem);
                this.mDrawer.mAdapter.notifyDataSetChanged();
            }

        }
    }
}
