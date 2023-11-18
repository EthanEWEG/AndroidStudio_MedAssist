package algonquin.cst2335.medassist;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Doctor implements Parcelable {

    private long docID;
    private String docName;
    private String docNumber;

    public Doctor(){}

    public Doctor(String doctorName, String doctorNumber){
        this.docName = doctorName;
        this.docNumber = doctorNumber;
    }

    protected Doctor(Parcel in) {
        docName = in.readString();
        docNumber = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(docName);
        dest.writeString(docNumber);
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public long getDocID() {
        return docID;
    }

    public void setDocID(long docID) {
        this.docID = docID;
    }
}
