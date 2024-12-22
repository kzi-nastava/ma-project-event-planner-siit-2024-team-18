package com.example.eventplanner.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UpdatedInvite implements Parcelable, Serializable {

    @SerializedName("inviteId")
    @Expose
    private int inviteId;

    @SerializedName("invitationStatus")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("updatedAt")
    @Expose
    private LocalDateTime updatedAt;

    @SerializedName("loggedIn")
    @Expose
    private boolean isLoggedIn;

    public UpdatedInvite() {
    }

    protected UpdatedInvite(Parcel in) {
        inviteId = in.readInt();
        status = in.readString();
        message = in.readString();
        updatedAt = (LocalDateTime) in.readSerializable();
        isLoggedIn = in.readByte() != 0;
    }

    public static final Creator<UpdatedInvite> CREATOR = new Creator<UpdatedInvite>() {
        @Override
        public UpdatedInvite createFromParcel(Parcel in) {
            return new UpdatedInvite(in);
        }

        @Override
        public UpdatedInvite[] newArray(int size) {
            return new UpdatedInvite[size];
        }
    };

    public int getInviteId() {
        return inviteId;
    }

    public void setInviteId(int inviteId) {
        this.inviteId = inviteId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    @NonNull
    @Override
    public String toString() {
        return "UpdatedInvite{" +
                "inviteId=" + inviteId +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", updatedAt=" + updatedAt +
                ", isLoggedIn=" + isLoggedIn +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(inviteId);
        dest.writeString(status);
        dest.writeString(message);
        dest.writeSerializable(updatedAt);
        dest.writeByte((byte) (isLoggedIn ? 1 : 0));
    }
}
