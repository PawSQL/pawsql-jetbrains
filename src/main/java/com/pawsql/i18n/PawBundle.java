package com.pawsql.i18n;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class PawBundle extends AbstractBundle {
    private static final String BUNDLE = "messages.messages";
    private static final PawBundle INSTANCE = new PawBundle();
    
    private PawBundle() {
        super(BUNDLE);
    }
    
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
    
    @NotNull
    public static String getString(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key) {
        return message(key);
    }
}
