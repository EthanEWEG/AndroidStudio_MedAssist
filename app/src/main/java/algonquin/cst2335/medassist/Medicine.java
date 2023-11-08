package algonquin.cst2335.medassist;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Medicine implements Parcelable {
    private long Id;
    private String name;
    private String dosage;
    private String quantity;
    private String frequency;
    private String refills;
    private String duration;
    private String expiration;
    private String instructions;

    public Medicine(){

    }


    public Medicine(String name, String dosage, String quantity, String frequency, String refills, String duration, String expiration, String instructions) {
        this.name = name;
        this.dosage = dosage;
        this.quantity = quantity;
        this.frequency = frequency;
        this.refills = refills;
        this.duration = duration;
        this.expiration = expiration;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRefills() {
        return refills;
    }

    public void setRefills(String refills) {
        this.refills = refills;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(Id);
        dest.writeString(name);
        dest.writeString(dosage);
        dest.writeString(quantity);
        dest.writeString(frequency);
        dest.writeString(refills);
        dest.writeString(duration);
        dest.writeString(expiration);
        dest.writeString(instructions);
    }

    // Parcelable implementation
    protected Medicine(Parcel in) {
        Id = in.readLong();
        name = in.readString();
        dosage = in.readString();
        quantity = in.readString();
        frequency = in.readString();
        refills = in.readString();
        duration = in.readString();
        expiration = in.readString();
        instructions = in.readString();
    }

    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };
}

