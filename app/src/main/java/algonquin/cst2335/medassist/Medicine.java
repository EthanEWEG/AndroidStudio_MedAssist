package algonquin.cst2335.medassist;

public class Medicine {
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
}

