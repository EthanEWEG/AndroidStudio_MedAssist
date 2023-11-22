package algonquin.cst2335.medassist.Medicine;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
/**
 * A Parcelable class representing a doctor with associated information.
 * <p>
 * This class implements the Parcelable interface, allowing instances to be passed
 * between different components in an Android application.
 */
public class Doctor implements Parcelable {

    private long docID;
    private String docName;
    private String docNumber;

    /**
     * Parameterized constructor for the Doctor class.
     *
     * @param doctorName    The name of the doctor.
     * @param doctorNumber  The contact number of the doctor.
     */
    public Doctor(String doctorName, String doctorNumber){
        this.docName = doctorName;
        this.docNumber = doctorNumber;
    }

    /**
     * Constructor used for Parcelable implementation. It reads values from the Parcel.
     *
     * @param in  The Parcel containing the serialized Doctor instance.
     */
    protected Doctor(Parcel in) {
        docName = in.readString();
        docNumber = in.readString();
    }

    /**
     * Parcelable creator for the Doctor class.
     */
    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

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
        dest.writeString(docName);
        dest.writeString(docNumber);
    }
    /**
     * Get the name of the doctor.
     *
     * @return The name of the doctor.
     */
    public String getDocName() {
        return docName;
    }

    /**
     * Set the name of the doctor.
     *
     * @param docName  The name of the doctor.
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * Get the contact number of the doctor.
     *
     * @return The contact number of the doctor.
     */
    public String getDocNumber() {
        return docNumber;
    }

    /**
     * Set the contact number of the doctor.
     *
     * @param docNumber  The contact number of the doctor.
     */
    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    /**
     * Get the ID of the doctor.
     *
     * @return The ID of the doctor.
     */
    public long getDocID() {
        return docID;
    }

    /**
     * Set the ID of the doctor.
     *
     * @param docID  The ID of the doctor.
     */
    public void setDocID(long docID) {
        this.docID = docID;
    }
}
