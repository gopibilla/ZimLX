/*
 *     Copyright (C) 2019 Lawnchair Team.
 *
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.zimmob.zimlx.preferences

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.preference.Preference
import com.android.launcher3.BuildConfig
import com.android.launcher3.Launcher
import com.android.launcher3.LauncherAppState
import com.android.launcher3.states.InternalStateHandler
import com.android.launcher3.widget.WidgetsFullSheet
import org.zimmob.zimlx.settings.ui.ControlledPreference


class SmartspaceAddToHomePreference(context: Context, attrs: AttributeSet?) :
        Preference(context, attrs), ResumablePreference,
        ControlledPreference by ControlledPreference.Delegate(context, attrs) {

    override fun onResume() {

        isVisible = LauncherAppState.getInstance(context).model
                .loadedWidgets?.any { it.isCustomWidget } != true

    }

    override fun onClick() {
        val homeIntent = OpenWidgetsInitListener().addToIntent(
                Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .setPackage(BuildConfig.APPLICATION_ID)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

        context.startActivity(homeIntent)
    }

    class OpenWidgetsInitListener : InternalStateHandler() {

        override fun init(launcher: Launcher, alreadyOnHome: Boolean): Boolean {
            WidgetsFullSheet.show(launcher, true)
            return true
        }
    }
}