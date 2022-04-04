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
package cn.lliiooll.pphelper.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.lliiooll.pphelper.utils.SavedInstanceStatePatchedClassReferencer;

public abstract class AppCompatTransferActivity extends AppCompatActivity {

    private ClassLoader mXref = null;

    @Override
    public ClassLoader getClassLoader() {
        if (mXref == null) {
            mXref = new SavedInstanceStatePatchedClassReferencer(
                AppCompatTransferActivity.class.getClassLoader());
        }
        return mXref;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Bundle windowState = savedInstanceState.getBundle("android:viewHierarchyState");
        if (windowState != null) {
            windowState.setClassLoader(AppCompatTransferActivity.class.getClassLoader());
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void requestTranslucentStatusBar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = decorView.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}
