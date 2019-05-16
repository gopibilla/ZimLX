/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.config;

/**
 * Defines a set of flags used to control various launcher behaviors
 */
public final class FeatureFlags extends BaseFlags {

    public static boolean LAUNCHER3_P_ALL_APPS = false;
    public static boolean LEGACY_ALL_APPS_BACKGROUND = !LAUNCHER3_GRADIENT_ALL_APPS && !LAUNCHER3_P_ALL_APPS;
    public static boolean FORCE_FEED_BRIDGE = false;
    public static boolean FEATURE_SETTINGS_SEARCH = false;
    public static boolean REFLECTION_FORCE_OVERVIEW_MODE = true;

    private FeatureFlags() {
    }
}
