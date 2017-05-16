package sinanaj.aldo.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Agency {

    @Column(name = "AGENCY_NAME")
    private String name;
    @Column(name = "AGENCY_REF")
    private String reference;

    public Agency() {
    }

    public Agency(String name, String reference) {
        this.name = name;
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
