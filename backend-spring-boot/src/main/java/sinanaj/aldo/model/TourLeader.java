package sinanaj.aldo.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TourLeader {

    @Column(name = "TL_NAME")
    private String fullName;
    @Column(name = "TL_PHONE")
    private String phone;

    public TourLeader() {
    }

    public TourLeader(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
