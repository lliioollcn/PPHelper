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

package cn.lliiooll.pphelper.startup.hookstatus;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

/**
 * This class is only intended to be used in module process, not in host process.
 */
public class HookStatus {

    private HookStatus() {
    }

    private static boolean sExpCpCalled = false;
    private static boolean sExpCpResult = false;

    public enum HookType {
        /**
         * No hook.
         */
        NONE,
        /**
         * Taichi, BugHook(not implemented), etc.
         */
        APP_PATCH,
        /**
         * Legacy Xposed, EdXposed, LSPosed, Dreamland, etc.
         */
        ZYGOTE,
    }

    public static String getZygoteHookProvider() {
        return HookStatusImpl.sZygoteHookProvider;
    }

    public static boolean isLsposedDexObfsEnabled() {
        return HookStatusImpl.sIsLsposedDexObfsEnabled;
    }

    public static boolean isZygoteHookMode() {
        return HookStatusImpl.sZygoteHookMode;
    }

    public static boolean isLegacyXposed() {
        try {
            ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedBridge");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isElderDriverXposed() {
        return new File("/system/framework/edxp.jar").exists();
    }

    public static boolean callTaichiContentProvider(Context context) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
            Bundle result = new Bundle();
            try {
                result = contentResolver.call(uri, "active", null, null);
            } catch (RuntimeException e) {
                // TaiChi is killed, try invoke
                try {
                    Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    return false;
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null);
            }
            if (result == null) {
                return false;
            }
            return result.getBoolean("active", false);
        } catch (Exception e) {
            return false;
        }
    }

    public static void init(Context context) {
        new Thread(() -> {
            sExpCpCalled = callTaichiContentProvider(context);
            sExpCpResult = sExpCpCalled;
        }).start();
    }

    public static HookType getHookType() {
        if (isZygoteHookMode()) {
            return HookType.ZYGOTE;
        }
        return sExpCpResult ? HookType.APP_PATCH : HookType.NONE;
    }

    public static String getHookProviderName() {
        if (isZygoteHookMode()) {
            String name = getZygoteHookProvider();
            if (name != null) {
                return name;
            }
            if (isLegacyXposed()) {
                return "Legacy Xposed";
            }
            if (isElderDriverXposed()) {
                return "EdXposed";
            }
            return "Unknown(Zygote)";
        }
        if (sExpCpResult) {
            return "Taichi";
        }
        return "None";
    }

    public static boolean isModuleEnabled() {
        return getHookType() != HookType.NONE;
    }
}
