package com.luck.picture.lib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class LocalMediaStatus implements Parcelable {

    MediaStatus status = MediaStatus.NONE;
    String statusDesc = "";

    public LocalMediaStatus() {

    }

    public LocalMediaStatus(MediaStatus status, String statusDesc) {
        this.status = status;
        this.statusDesc = statusDesc;
    }

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(MediaStatus status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeString(this.statusDesc);
    }

    public void readFromParcel(Parcel source) {
        int tmpStatus = source.readInt();
        this.status = tmpStatus == -1 ? null : MediaStatus.values()[tmpStatus];
        this.statusDesc = source.readString();
    }

    protected LocalMediaStatus(Parcel in) {
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : MediaStatus.values()[tmpStatus];
        this.statusDesc = in.readString();
    }

    public static final Creator<LocalMediaStatus> CREATOR = new Creator<LocalMediaStatus>() {
        @Override
        public LocalMediaStatus createFromParcel(Parcel source) {
            return new LocalMediaStatus(source);
        }

        @Override
        public LocalMediaStatus[] newArray(int size) {
            return new LocalMediaStatus[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalMediaStatus status1 = (LocalMediaStatus) o;
        return status == status1.status && Objects.equals(statusDesc, status1.statusDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, statusDesc);
    }

    public static LocalMediaStatus generateUploadingStatus() {
        return new LocalMediaStatus(MediaStatus.UPLOADING, "上传中...");
    }

    public static LocalMediaStatus generateFailedStatus() {
        return new LocalMediaStatus(MediaStatus.FAILED, "上传失败");
    }

    public static LocalMediaStatus create() {
        return new LocalMediaStatus();
    }

    public  enum MediaStatus {
        NONE,
        UPLOADING,
        FAILED,
        SUCCESS
    }

}
