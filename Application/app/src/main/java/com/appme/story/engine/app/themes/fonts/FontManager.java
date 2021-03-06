package com.appme.story.engine.app.themes.fonts;

import android.content.Context;
import android.graphics.Typeface;

import com.appme.story.R;
import com.appme.story.engine.app.folders.file.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by Duy on 18-Mar-17.
 */

public class FontManager {
    private static final String PATH_TO_FONT = "fonts/";
    private static final String TAG = "Typefaces";
    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    private static Typeface get(Context c, String assetPath) throws IOException {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    throw new IOException("Could not get typeface '" +
                                          assetPath + "' because " + e.getMessage());
                }
            }
            return cache.get(assetPath);
        }
    }

    public synchronized static Typeface getFontFromAsset(Context context, String name) {
        try {
            if (name.equalsIgnoreCase(context.getString(R.string.font_consolas))) {
                return get(context, PATH_TO_FONT + "consolas.ttf");
            } else if (name.equalsIgnoreCase(context.getString(R.string.font_courier_new))) {
                return get(context, PATH_TO_FONT + "courier_new.ttf");
            } else if (name.equalsIgnoreCase(context.getString(R.string.font_lucida_sans_typewriter))) {
                return get(context, PATH_TO_FONT + "lucida_sans_typewriter_regular.ttf");
            } else if (name.equalsIgnoreCase(context.getString(R.string.font_monospace))) {
                return Typeface.MONOSPACE;
            } else if (name.equalsIgnoreCase(context.getString(R.string.font_source_code_pro))) {
                return get(context, PATH_TO_FONT + "source_code_pro.ttf");
            } else {
                return get(context, PATH_TO_FONT + name);
            }
        } catch (Exception e) {

        }
        return Typeface.MONOSPACE;
    }

    public synchronized static Typeface getFontFromStorage(String name) {
        try {
            synchronized (cache) {
                if (!cache.containsKey(name)) {
                    try {
                        Typeface font = Typeface.createFromFile(FileManager.EXTERNAL_DIR_SCRIPTME_FONT + "/" + name);
                        cache.put(name, font);
                    } catch (Exception e) {
                        throw new IOException("Could not get typeface '" + name + "' because " + e.getMessage());
                    }
                }
                return cache.get(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Typeface.MONOSPACE;
    }

    private static boolean isFontFile(String name) {
        return name.toLowerCase().endsWith(".ttf") || name.toLowerCase().endsWith(".otf");
    }

    public static LinkedList<FontEntry> getAll(Context context) {
        LinkedList<FontEntry> fontEntries = new LinkedList<>();
        try {
            String[] fonts = context.getAssets().list("fonts");
            for (String font : fonts) {
                if (isFontFile(font)) {
                    fontEntries.add(new FontEntry(false, font));
                }
            }
            fontEntries.addFirst(new FontEntry(false, "monospace"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*if (DonateUtils.DONATED) {
            File parent = new File(FileManager.EXTERNAL_DIR_SRC + "fonts");
            if (parent.exists() && parent.isDirectory()) {
                File[] files = parent.listFiles();
                for (File f : files) {
                    if (isFontFile(f.getName())) {
                        fontEntries.add(new FontEntry(true, f.getName()));
                    }
                }
            }
        }*/
        return fontEntries;
    }

    public static Typeface getFont(FontEntry fontEntry, Context context) {
        return fontEntry.fromStorage ? getFontFromStorage(fontEntry.name) :
            getFontFromAsset(context, fontEntry.name);
    }
}

