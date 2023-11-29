package algonquin.cst2335.medassist.Main;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class NotificationMed implements Parcelable {

    private long notiID;
    private String notiName;
    private String notiDate;
    private String notiTime;
    private String notiRepeatDate;
    private String notiRepeatAmount;
    private String notiTimeBefore;
    private long medicineId;

    public NotificationMed(){}

    public NotificationMed(String notificationName, String notificationDate, String notificationTime, String notificationRepeatDate,
                           String notificationRepeatAmount, String notificationTimeBefore, long medicineId){
        this.notiName = notificationName;
        this.notiDate = notificationDate;
        this.notiTime = notificationTime;
        this.notiRepeatDate = notificationRepeatDate;
        this.notiRepeatAmount = notificationRepeatAmount;
        this.notiTimeBefore = notificationTimeBefore;
        this.medicineId = medicineId;
    }

    protected NotificationMed(Parcel in) {
        notiID = in.readLong();
        notiName = in.readString();
        notiDate = in.readString();
        notiTime = in.readString();
        notiRepeatDate = in.readString();
        notiRepeatAmount = in.readString();
        notiTimeBefore = in.readString();
        medicineId = in.readLong();
    }

    public static final Creator<NotificationMed> CREATOR = new Creator<NotificationMed>() {
        @Override
        public NotificationMed createFromParcel(Parcel in) {
            return new NotificationMed(in);
        }

        @Override
        public NotificationMed[] newArray(int size) { return new NotificationMed[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(notiID);
        dest.writeString(notiName);
        dest.writeString(notiDate);
        dest.writeString(notiTime);
        dest.writeString(notiRepeatDate);
        dest.writeString(notiRepeatAmount);
        dest.writeString(notiTimeBefore);
        dest.writeLong(medicineId);
    }

    public long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(long medicineId) {
        this.medicineId = medicineId;
    }

    public long getNotiID() {
        return notiID;
    }

    public void setNotiID(long notiID) {
        this.notiID = notiID;
    }

    public String getNotiName() {
        return notiName;
    }

    public void setNotiName(String notiName) {
        this.notiName = notiName;
    }

    public String getNotiDate() {
        return notiDate;
    }

    public void setNotiDate(String notiDate) {
        this.notiDate = notiDate;
    }

    public String getNotiTime() {
        return notiTime;
    }

    public void setNotiTime(String notiTime) {
        this.notiTime = notiTime;
    }

    public String getNotiRepeatDate() {
        return notiRepeatDate;
    }

    public void setNotiRepeatDate(String notiRepeatDate) {
        this.notiRepeatDate = notiRepeatDate;
    }

    public String getNotiRepeatAmount() {
        return notiRepeatAmount;
    }

    public void setNotiRepeatAmount(String notiRepeatAmount) { this.notiRepeatAmount = notiRepeatAmount; }

    public String getNotiTimeBefore() {
        return notiTimeBefore;
    }

    public void setNotiTimeBefore(String notiTimeBefore) { this.notiTimeBefore = notiTimeBefore; }

}
