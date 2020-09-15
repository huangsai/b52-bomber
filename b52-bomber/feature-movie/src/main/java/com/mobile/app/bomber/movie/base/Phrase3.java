package com.mobile.app.bomber.movie.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.mobile.guava.android.mvvm.AndroidX;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.quote;

public final class Phrase3 {

    private Phrase3() {
    }

    public static Builder format(@StringRes int stringRes) {
        return new Builder(AndroidX.INSTANCE.myApp().getString(stringRes));
    }

    public static Builder format(@NonNull String string) {
        return new Builder(string);
    }

    public static Builder format(@NonNull String string,
                                 @NonNull String prefix,
                                 @NonNull String suffix) {
        return new Builder(string, prefix, suffix);
    }

    public static class Builder {

        private final Pattern pattern;

        private String baseString;
        private String prefix;
        private String suffix;
        private boolean strictMode = true;

        private Builder(String string) {
            this(string, "{", "}");
        }

        private Builder(String string, String prefix, String suffix) {
            baseString = string;
            this.prefix = prefix;
            this.suffix = suffix;
            pattern = Pattern.compile(quote(prefix) + ".*?" + quote(suffix));
        }

        public Builder strictMode(boolean active) {
            this.strictMode = active;
            return this;
        }

        public Builder with(@NonNull String key, @Nullable Object value) {
            if (value == null) {
                value = "";
            }
            if (strictMode && !baseString.contains(prefix + key + suffix)) {
                throw new RuntimeException("Couldn't find key " + key);
            }
            baseString = baseString.replace(prefix + key + suffix, value.toString());
            return this;
        }

        public String build() {
            final Matcher matcher = pattern.matcher(baseString);
            if (strictMode && matcher.find()) {
                throw new RuntimeException("You didn't pass an argument for key " + matcher.group());
            } else {
                return baseString;
            }
        }
    }
}
