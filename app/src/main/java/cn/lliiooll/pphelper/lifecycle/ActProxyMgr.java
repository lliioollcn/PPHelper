/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 qwq233@qwq2333.top
 * https://github.com/cinit/QAuxiliary
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */
package cn.lliiooll.pphelper.lifecycle;

import androidx.annotation.NonNull;
import org.intellij.lang.annotations.MagicConstant;

/**
 * This class is used to cope with Activity
 */
public class ActProxyMgr {

    public static final String STUB_DEFAULT_ACTIVITY = "com.tencent.mobileqq.activity.photo.CameraPreviewActivity";
    public static final String STUB_TRANSLUCENT_ACTIVITY = "cooperation.qlink.QlinkStandardDialogActivity";
    @MagicConstant
    public static final String ACTIVITY_PROXY_INTENT = "io.github.qauxv.lifecycle.ActProxyMgr.ACTIVITY_PROXY_INTENT";

    private ActProxyMgr() {
        throw new AssertionError("No instance for you!");
    }

    // NOTICE: ** If you have created your own package, add it to proguard-rules.pro.**

    public static boolean isModuleProxyActivity(@NonNull String className) {
        if (className == null) {
            return false;
        }
        return className.startsWith("cn.lliiooll.pphelper.activity.");
    }

    public static boolean isModuleBundleClassLoaderRequired(@NonNull String className) {
        return isModuleProxyActivity(className);
    }
}
