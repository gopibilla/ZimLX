package org.zimmob.zimlx.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import net.gsantner.opoc.util.Callback;

import org.zimmob.zimlx.R;
import org.zimmob.zimlx.activity.HomeActivity;
import org.zimmob.zimlx.manager.Setup;
import org.zimmob.zimlx.util.Tool;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

import static org.zimmob.zimlx.config.Config.DRAWER_HORIZONTAL;
import static org.zimmob.zimlx.config.Config.DRAWER_VERTICAL;

public class AppDrawerController extends RevealFrameLayout {
    public AppDrawerPaged _drawerViewPaged;
    public AppDrawerVertical _drawerViewGrid;
    public int _drawerMode;
    public boolean isOpen = false;
    private Callback.a2<Boolean, Boolean> _appDrawerCallback;
    private Animator _appDrawerAnimator;
    private Long _drawerAnimationTime = 250L;
    Button searchButton;

    public AppDrawerController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppDrawerController(Context context) {
        super(context);

    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AppDrawerController(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param callBack
     */
    public void setCallBack(Callback.a2<Boolean, Boolean> callBack) {
        _appDrawerCallback = callBack;
    }

    /**
     * @return
     */
    public View getDrawer() {
        return getChildAt(0);
    }

    /**
     * @param cx
     * @param cy
     * @param startRadius
     * @param finalRadius
     */
    public void open(int cx, int cy, int startRadius, int finalRadius) {
        if (isOpen) return;
        isOpen = true;
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.search_apps, this, true);
        searchButton=findViewById(R.id.drawerMenu);
        searchButton.setOnClickListener((View v) ->{
            showDrawerMenu(v);
        } );


        _drawerAnimationTime = (long) (240 * Setup.appSettings().getOverallAnimationSpeedModifier());

        _appDrawerAnimator = io.codetail.animation.ViewAnimationUtils.createCircularReveal(getChildAt(0), cx, cy, startRadius, finalRadius);
        _appDrawerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        _appDrawerAnimator.setDuration(_drawerAnimationTime);
        _appDrawerAnimator.setStartDelay((int) (Setup.appSettings().getOverallAnimationSpeedModifier() * 200));
        _appDrawerCallback.callback(true, true);
        _appDrawerAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator p1) {
                getChildAt(0).setVisibility(View.VISIBLE);

                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(getBackground(), PropertyValuesHolder.ofInt("alpha", 0, 255));
                animator.setDuration(_drawerAnimationTime);
                animator.start();

                switch (_drawerMode) {
                    case DRAWER_HORIZONTAL:
                        for (int i = 0; i < _drawerViewPaged._pages.size(); i++) {
                            _drawerViewPaged._pages.get(i).findViewById(R.id.group).setAlpha(1);
                        }
                        if (_drawerViewPaged._pages.size() > 0) {
                            View mGrid = _drawerViewPaged._pages.get(_drawerViewPaged.getCurrentItem()).findViewById(R.id.group);
                            mGrid.setAlpha(0);
                            mGrid.animate().alpha(1).setDuration(150L).setStartDelay(Math.max(_drawerAnimationTime - 50, 1)).setInterpolator(new AccelerateDecelerateInterpolator());
                        }
                        break;
                    case DRAWER_VERTICAL:
                        _drawerViewGrid.recyclerView.setAlpha(0);
                        _drawerViewGrid.recyclerView.animate().alpha(1).setDuration(150L).setStartDelay(Math.max(_drawerAnimationTime - 50, 1)).setInterpolator(new AccelerateDecelerateInterpolator());
                        break;
                }
            }

            @Override
            public void onAnimationEnd(Animator p1) {
                _appDrawerCallback.callback(true, false);
            }

            @Override
            public void onAnimationCancel(Animator p1) {
            }

            @Override
            public void onAnimationRepeat(Animator p1) {
            }
        });

        _appDrawerAnimator.start();
    }

    public void close(int cx, int cy, int startRadius, int finalRadius) {
        removeView(findViewById(R.id.search_apps));
        if (!isOpen) {

            return;
        }
        isOpen = false;

        if (_appDrawerAnimator == null || _appDrawerAnimator.isRunning())
            return;

        _appDrawerAnimator = ViewAnimationUtils.createCircularReveal(getChildAt(0), cx, cy, finalRadius, startRadius);
        _appDrawerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        _appDrawerAnimator.setDuration(_drawerAnimationTime);
        _appDrawerAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator p1) {
                _appDrawerCallback.callback(false, true);

                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(getBackground(), PropertyValuesHolder.ofInt("alpha", 255, 0));
                animator.setDuration(_drawerAnimationTime);
                animator.start();
            }

            @Override
            public void onAnimationEnd(Animator p1) {
                _appDrawerCallback.callback(false, false);
            }

            @Override
            public void onAnimationCancel(Animator p1) {
            }

            @Override
            public void onAnimationRepeat(Animator p1) {
            }
        });

        switch (_drawerMode) {
            case DRAWER_HORIZONTAL:
                if (_drawerViewPaged._pages.size() > 0) {
                    View mGrid = _drawerViewPaged._pages.get(_drawerViewPaged.getCurrentItem()).findViewById(R.id.group);
                    mGrid.animate().setStartDelay(0).alpha(0).setDuration(60L).withEndAction(() -> {
                        try {
                            _appDrawerAnimator.start();
                        } catch (NullPointerException ignored) {
                        }
                    });
                }
                break;
            case DRAWER_VERTICAL:
                _drawerViewGrid.recyclerView.animate().setStartDelay(0).alpha(0).setDuration(60L).withEndAction(() -> {
                    try {
                        _appDrawerAnimator.start();
                    } catch (NullPointerException ignored) {
                    }
                });
                break;
        }
    }

    public void init() {
        if (isInEditMode()) return;
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        _drawerMode = Setup.appSettings().getDrawerStyle();
        switch (_drawerMode) {
            case DRAWER_HORIZONTAL:
                _drawerViewPaged = (AppDrawerPaged) layoutInflater.inflate(R.layout.view_app_drawer_paged, this, false);
                addView(_drawerViewPaged);
                layoutInflater.inflate(R.layout.view_drawer_indicator, this, true);
                break;
            case DRAWER_VERTICAL:
                _drawerViewGrid = (AppDrawerVertical) layoutInflater.inflate(R.layout.view_app_drawer_vertical, this, false);
                int marginHorizontal = Tool.dp2px(Setup.appSettings().getVerticalDrawerHorizontalMargin(), getContext());
                int marginVertical = Tool.dp2px(Setup.appSettings().getVerticalDrawerVerticalMargin(), getContext());
                RevealFrameLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                lp.leftMargin = marginHorizontal;
                lp.rightMargin = marginHorizontal;
                lp.topMargin = marginVertical;
                lp.bottomMargin = marginVertical;
                addView(_drawerViewGrid, lp);

                break;
        }
    }

    public void showDrawerMenu(View v){
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.drawer_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        popupMenu.show();;
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
            return insets;
        }
        return insets;
    }

    public void reloadDrawerCardTheme() {
        switch (_drawerMode) {
            case DRAWER_HORIZONTAL:
                _drawerViewPaged.resetAdapter();
                break;
            case DRAWER_VERTICAL:
                if (!Setup.appSettings().isDrawerShowCardView()) {
                    _drawerViewGrid.setCardBackgroundColor(Color.TRANSPARENT);
                    _drawerViewGrid.setCardElevation(0);
                } else {
                    _drawerViewGrid.setCardBackgroundColor(Setup.appSettings().getDrawerCardColor());
                    _drawerViewGrid.setCardElevation(Tool.dp2px(4, getContext()));
                }
                if (_drawerViewGrid.gridDrawerAdapter != null) {
                    _drawerViewGrid.gridDrawerAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public void scrollToStart() {
        switch (_drawerMode) {
            case DRAWER_HORIZONTAL:
                _drawerViewPaged.setCurrentItem(0, false);
                break;
            case DRAWER_VERTICAL:
                _drawerViewGrid.recyclerView.scrollToPosition(0);
                break;
        }
    }

    public void setHome(HomeActivity home) {
        switch (_drawerMode) {
            case DRAWER_HORIZONTAL:
                _drawerViewPaged.withHome(home, findViewById(R.id.appDrawerIndicator));
                break;
            case DRAWER_VERTICAL:
                break;
        }
    }

}
