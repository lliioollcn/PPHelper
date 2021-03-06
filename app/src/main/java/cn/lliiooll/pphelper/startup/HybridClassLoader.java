package cn.lliiooll.pphelper.startup;

/**
 * 混合类加载器
 */
public class HybridClassLoader extends ClassLoader {

    private final ClassLoader appCl;// 宿主提供的类加载器
    private final ClassLoader bootCl;// Context的类加载器
    private final ClassLoader xpCl;// Xposed提供的类加载器
    private final ClassLoader parent;// 父类加载器
    public static HybridClassLoader clLoader;// 类加载器

    public HybridClassLoader(ClassLoader appCl, ClassLoader bootCl, ClassLoader xpCl, ClassLoader parent) {
        this.appCl = appCl;
        this.bootCl = bootCl;
        this.xpCl = xpCl;
        this.parent = parent;
        clLoader = this;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return bootCl.loadClass(name);
        } catch (ClassNotFoundException ignored) {
            //TODO: Nothing
        }
        if (name != null && isConflictingClass(name)) {
            throw new ClassNotFoundException();
        }
        if (appCl != null) {
            try {
                return appCl.loadClass(name);
            } catch (ClassNotFoundException ignored) {
                //TODO: Nothing
            }
        }
        if (parent != null) {
            try {
                return parent.loadClass(name);
            } catch (ClassNotFoundException ignored) {
                //TODO: Nothing
            }
        }
        if (xpCl != null) {
            try {
                return xpCl.loadClass(name);
            } catch (ClassNotFoundException ignored) {
                //TODO: Nothing
            }
        }
        return super.loadClass(name, resolve);
    }


    /**
     * 把宿主和模块共有的 package 扔这里.
     *
     * @param name NonNull, class name
     * @return true if conflicting
     */

    public static boolean isConflictingClass(String name) {
        return name.startsWith("androidx.") || name.startsWith("android.support.") || name.startsWith("kotlin.") || name.startsWith("kotlinx.") || name.startsWith("com.tencent.mmkv.") || name.startsWith("com.android.tools.r8.") || name.startsWith("com.google.android.material.") || name.startsWith("com.google.gson.") || name.startsWith("com.google.common.") || name.startsWith("org.intellij.lang.annotations.") || name.startsWith("org.jetbrains.annotations.");
    }

}
