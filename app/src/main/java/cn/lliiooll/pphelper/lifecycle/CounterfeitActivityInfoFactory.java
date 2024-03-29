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

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import cn.lliiooll.pphelper.utils.AppUtils;

public class CounterfeitActivityInfoFactory {

    public static ActivityInfo makeProxyActivityInfo(String className, long flags) {
        try {
            Context ctx = AppUtils.getHostAppInstance();
            Class<?> cl = Class.forName(className);
            try {
                // TODO: 2022-02-11 cast flags from long to int loses information
                ActivityInfo proto = ctx.getPackageManager().getActivityInfo(new ComponentName(
                        ctx.getPackageName(), AppUtils.getSettingActivity(ctx.getPackageName())), (int) flags);
                // init style here, comment it out if it crashes on Android >= 10
                proto.theme = android.R.style.Theme;
                return initCommon(proto, className);
            } catch (PackageManager.NameNotFoundException e) {
                throw new IllegalStateException(
                        "SettingActivity not found, are we in the host?", e);
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static ActivityInfo initCommon(ActivityInfo ai, String name) {
        ai.targetActivity = null;
        ai.taskAffinity = null;
        ai.descriptionRes = 0;
        ai.name = name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ai.splitName = null;
        }
        return ai;
    }
}
