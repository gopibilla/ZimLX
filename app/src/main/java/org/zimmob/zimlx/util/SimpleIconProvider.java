package org.zimmob.zimlx.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import org.zimmob.zimlx.config.Config;
import org.zimmob.zimlx.interfaces.IconDrawer;
import org.zimmob.zimlx.manager.Setup;
import org.zimmob.zimlx.viewutil.GroupIconDrawable;

/**
 * Created by saul on 04-25-18.
 * Project ZimLX
 * henriquez.saul@gmail.com
 */
public class SimpleIconProvider extends BaseIconProvider {

    private Drawable _drawable;
    private int _drawableResource;

    public SimpleIconProvider(Drawable drawable) {
        _drawable = drawable;
        _drawableResource = -1;
    }

    public SimpleIconProvider(int drawableResource) {
        _drawable = null;
        _drawableResource = drawableResource;
    }

    private Drawable getDrawable() {
        if (_drawable != null) {
            return _drawable;
        } else if (_drawableResource > 0) {
            return Setup.appContext().getDrawable(_drawableResource);
        }
        return null;
    }

    @Override
    public void loadIcon(IconTargetType type, int forceSize, Object target, Object... args) {
        switch (type) {
            case ImageView: {
                ImageView iv = (ImageView) target;
                Drawable d = getDrawable();
                d = scaleDrawable(d, forceSize);
                iv.setImageDrawable(d);
                break;
            }
            case TextView: {
                TextView tv = (TextView) target;
                int gravity = (Integer) args[0];
                Drawable d = getDrawable();
                d = scaleDrawable(d, forceSize);
                switch (gravity) {
                    case Gravity.START:
                        tv.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
                        break;
                    case Gravity.END:
                        tv.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
                        break;
                    case Gravity.TOP:
                        tv.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
                        break;
                    case Gravity.BOTTOM:
                        tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, d);
                        break;
                }
                break;
            }
            case IconDrawer: {
                IconDrawer iconDrawer = (IconDrawer) target;
                int index = (Integer) args[0];
                // we simply load the drawable in a synchronised way
                iconDrawer.onIconAvailable(getDrawable(), index);
                break;
            }
        }
    }

    @Override
    public void cancelLoad(IconTargetType type, Object target) {
        // nothing to cancel... we load everything in an synchronous way
    }

    @Override
    public Drawable getDrawableSynchronously(int forceSize) {
        Drawable d = getDrawable();
        d = scaleDrawable(d, forceSize);
        return d;
    }

    @Override
    public boolean isGroupIconDrawable() {
        return _drawable != null && _drawable instanceof GroupIconDrawable;
    }

    private Drawable scaleDrawable(Drawable drawable, int forceSize) {
        if (drawable != null && forceSize != Config.NO_SCALE) {
            forceSize = Tool.dp2px(forceSize, Setup.appContext());
            drawable = new BitmapDrawable(Setup.appContext().getResources(), Bitmap.createScaledBitmap(Tool.drawableToBitmap(drawable), forceSize, forceSize, true));
        }
        return drawable;
    }
}
