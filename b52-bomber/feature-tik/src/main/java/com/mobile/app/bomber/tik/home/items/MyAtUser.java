package com.mobile.app.bomber.tik.home.items;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.linkedin.android.spyglass.mentions.Mentionable;
import com.mobile.app.bomber.data.http.entities.ApiAtUser;
import com.mobile.app.bomber.data.http.entities.ApiComment;
import com.mobile.app.bomber.data.http.entities.Atuids;

import java.util.Objects;

public class MyAtUser implements Mentionable {

    private final String username;
    private final String profile;
    private final long userId;

    private MyAtUser(ApiAtUser.User user) {
        this.username = user.getUsername();
        this.profile = user.getProfile();
        this.userId = user.getUid();
    }

    public String getUsername() {
        return username;
    }

    public String getProfile() {
        return profile;
    }

    public long getUserId() {
        return userId;
    }

    public String getFullName() {
        return "@" + getUsername() + " ";
    }

    public Atuids toAtuids() {
        return new Atuids(userId, username);
    }

    public ApiComment.At toAt() {
        return new ApiComment.At(userId, username, profile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyAtUser myAtUser = (MyAtUser) o;
        return userId == myAtUser.userId && Objects.equals(username, myAtUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, userId);
    }

    // --------------------------------------------------
    // Mentionable/Suggestible Implementation
    // --------------------------------------------------

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        return getFullName();
    }

    @NonNull
    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.FULL_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return getUsername().hashCode();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return getFullName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(profile);
        dest.writeLong(userId);
    }

    public MyAtUser(Parcel in) {
        username = in.readString();
        profile = in.readString();
        userId = in.readLong();
    }

    public static final Creator<MyAtUser> CREATOR = new Creator<MyAtUser>() {
        public MyAtUser createFromParcel(Parcel in) {
            return new MyAtUser(in);
        }

        public MyAtUser[] newArray(int size) {
            return new MyAtUser[size];
        }
    };

    public static MyAtUser create(ApiAtUser.User user) {
        return new MyAtUser(user);
    }
}
