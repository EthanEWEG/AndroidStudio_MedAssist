package algonquin.cst2335.medassist.Medicine;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * A Parcelable class representing information about a medicine.
 * <p>
 * This class implements the Parcelable interface, allowing instances to be passed
 * between different components in an Android application.
 */
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

    /**
     * Default constructor for the Medicine class.
     */
    public Medicine(){}

    /**
     * Parameterized constructor for the Medicine class.
     *
     * @param name         The name of the medicine.
     * @param dosage       The dosage of the medicine.
     * @param quantity     The quantity of the medicine.
     * @param frequency    The frequency of the medicine.
     * @param refills      The number of refills for the medicine.
     * @param duration     The duration of the medicine.
     * @param expiration   The expiration date of the medicine.
     * @param instructions The instructions for taking the medicine.
     */
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
    /**
     * Get the name of the medicine.
     *
     * @return The name of the medicine.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the medicine.
     *
     * @param name The name of the medicine.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the dosage of the medicine.
     *
     * @return The dosage of the medicine.
     */
    public String getDosage() {
        return dosage;
    }

    /**
     * Set the dosage of the medicine.
     *
     * @param dosage The dosage of the medicine.
     */
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    /**
     * Get the frequency of the medicine.
     *
     * @return The frequency of the medicine.
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Set the frequency of the medicine.
     *
     * @param frequency The frequency of the medicine.
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * Get the expiration date of the medicine.
     *
     * @return The expiration date of the medicine.
     */
    public String getExpiration() {
        return expiration;
    }

    /**
     * Set the expiration date of the medicine.
     *
     * @param expiration The expiration date of the medicine.
     */
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    /**
     * Get the ID of the medicine.
     *
     * @return The ID of the medicine.
     */
    public long getId() {
        return Id;
    }

    /**
     * Set the ID of the medicine.
     *
     * @param id The ID of the medicine.
     */
    public void setId(long id) {
        Id = id;
    }

    /**
     * Get the quantity of the medicine.
     *
     * @return The quantity of the medicine.
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Set the quantity of the medicine.
     *
     * @param quantity The quantity of the medicine.
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * Get the number of refills for the medicine.
     *
     * @return The number of refills for the medicine.
     */
    public String getRefills() {
        return refills;
    }

    /**
     * Set the number of refills for the medicine.
     *
     * @param refills The number of refills for the medicine.
     */
    public void setRefills(String refills) {
        this.refills = refills;
    }

    /**
     * Get the duration of the medicine.
     *
     * @return The duration of the medicine.
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Set the duration of the medicine.
     *
     * @param duration The duration of the medicine.
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Get the instructions for taking the medicine.
     *
     * @return The instructions for taking the medicine.
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Set the instructions for taking the medicine.
     *
     * @param instructions The instructions for taking the medicine.
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable instance.
     *
     * @return a bitmask indicating the set of special object types.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
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

    /**
     * Protected constructor used for reconstructing a {@code Medicine} object from a {@code Parcel}.
     * @param in The {@code Parcel} containing the serialized data.
     */
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

    /**
     * Parcelable creator for the Medicine class.
     */
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

