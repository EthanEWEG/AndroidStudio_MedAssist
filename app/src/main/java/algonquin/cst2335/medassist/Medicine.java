package algonquin.cst2335.medassist;

public class Medicine {
    private long Id;
    private String name;
    private String dosage;
    private String frequency;
    private String expiration;
    private String whenToRefill;

    public Medicine(String name, String dosage, String frequency, String expiration, String whenToRefill) {
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.expiration = expiration;
        this.whenToRefill = whenToRefill;
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

    public String getRefillDate() {
        return whenToRefill;
    }

    public void setRefillDate(String whenToRefill) {
        this.whenToRefill = whenToRefill;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }
}

