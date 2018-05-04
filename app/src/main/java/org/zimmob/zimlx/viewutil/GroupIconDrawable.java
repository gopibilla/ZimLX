package org.zimmob.zimlx.viewutil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.zimmob.zimlx.interfaces.IconDrawer;
import org.zimmob.zimlx.manager.Setup;
import org.zimmob.zimlx.model.Item;
import org.zimmob.zimlx.util.App;
import org.zimmob.zimlx.util.AppSettings;
import org.zimmob.zimlx.util.Tool;
import org.zimmob.zimlx.widget.Folder;

public class GroupIconDrawable extends Drawable implements IconDrawer {
    private Drawable[] icons;
    private Paint paintInnerCircle;
    private Paint paintIcon;
    private boolean needAnimate;
    private boolean needAnimateScale;
    private float scaleFactor = 1;
    private float iconSize;
    private float padding;
    private int outline;
    private int iconSizeDiv2;
    AppSettings appSettings = Setup.appSettings();
    public GroupIconDrawable(Context context, Item item, int iconSize) {
        final float size = Tool.dp2px(iconSize, context);
        final Drawable[] icons = new Drawable[4];
        for (int i = 0; i < 4; i++) {
            icons[i] = null;
        }
        init(icons, size);
        for (int i = 0; i < 4 && i < item.getItems().size(); i++) {
            Item temp = item.getItems().get(i);
            App app = null;
            if (temp != null) {
                app = Setup.appLoader().findItemApp(temp);
            }
            if (app == null) {
                Setup.logger().log(this, Log.DEBUG, null, "Item %s has a null app at index %d (Intent: %s)", item.getLabel(), i, temp == null ? "Item is NULL" : temp.getIntent());
                icons[i] = new ColorDrawable(Color.TRANSPARENT);
            } else {
                app.getIconProvider().loadIconIntoIconDrawer(this, (int) size, i);
            }
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) iconSize;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) iconSize;
    }

    private void init(Drawable[] icons, float size) {
        this.icons = icons;
        this.iconSize = size;
        iconSizeDiv2 = Math.round(iconSize / 2f);
        padding = iconSize / 20f;

        this.paintIcon = new Paint();
        paintIcon.setAntiAlias(true);
        paintIcon.setFilterBitmap(true);
    }

    public void popUp() {
        needAnimate = true;
        needAnimateScale = true;
        invalidateSelf();
    }

    public void popBack() {
        needAnimate = false;
        needAnimateScale = false;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        if (needAnimateScale) {
            scaleFactor = Tool.clampFloat(scaleFactor - 0.09f, 0.5f, 1f);
        } else {
            scaleFactor = Tool.clampFloat(scaleFactor + 0.09f, 0.5f, 1f);
        }
        paintInnerCircle = new Paint();
        paintInnerCircle.setColor(Color.parseColor("#EA040404"));
        paintInnerCircle.setAlpha(150);
        paintInnerCircle.setAntiAlias(true);
        canvas.scale(scaleFactor, scaleFactor, iconSize / 2, iconSize / 2);
        if (appSettings.getFolderShape() == Folder.Shape.CIRCLE) {

            Path clip = new Path();
            clip.addCircle(iconSize / 2, iconSize / 2, iconSize / 2 - outline, Path.Direction.CW);
            canvas.clipPath(clip, Region.Op.REPLACE);
            canvas.drawCircle(iconSize / 2, iconSize / 2, iconSize / 2 - outline, paintInnerCircle);
        } else {
            Rect rect = new Rect(10, 10, 164, 164);
            canvas.drawRect(rect, paintInnerCircle);
        }
        if (icons[0] != null) {
            drawIcon(canvas, icons[0], padding + 5, padding + 5, iconSizeDiv2 - padding + 5, iconSizeDiv2 - padding, paintIcon);
        }
        if (icons[1] != null) {
            drawIcon(canvas, icons[1], iconSizeDiv2 + padding - 5, padding + 5, iconSize - padding - 5, iconSizeDiv2 - padding, paintIcon);
        }
        if (icons[2] != null) {
            drawIcon(canvas, icons[2], padding + 5, iconSizeDiv2 + padding, iconSizeDiv2 - padding + 5, iconSize - padding - 5, paintIcon);
        }
        if (icons[3] != null) {
            drawIcon(canvas, icons[3], iconSizeDiv2 + padding - 5, iconSizeDiv2 + padding, iconSize - padding - 5, iconSize - padding - 5, paintIcon);
        }
        canvas.clipRect(0, 0, iconSize, iconSize, Region.Op.REPLACE);

        canvas.restore();

        if (needAnimate) {
            paintIcon.setAlpha(Tool.clampInt(paintIcon.getAlpha() - 25, 0, 255));
            invalidateSelf();
        } else if (paintIcon.getAlpha() != 255) {
            paintIcon.setAlpha(Tool.clampInt(paintIcon.getAlpha() + 25, 0, 255));
            invalidateSelf();
        }
    }

    /**
     * @param canvas
     * @param icon
     * @param l
     * @param t
     * @param r
     * @param b
     * @param paint
     */
    private void drawIcon(Canvas canvas, Drawable icon, float l, float t, float r, float b, Paint paint) {
        icon.setBounds((int) l, (int) t, (int) r, (int) b);
        icon.setFilterBitmap(true);
        icon.setAlpha(paint.getAlpha());
        icon.draw(canvas);
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public void onIconAvailable(Drawable drawable, int index) {
        icons[index] = drawable;
        invalidateSelf();
    }

    @Override
    public void onIconCleared(Drawable placeholder, int index) {
        icons[index] = placeholder == null ? new ColorDrawable(Color.TRANSPARENT) : placeholder;
        invalidateSelf();
    }
}