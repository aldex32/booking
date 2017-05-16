package sinanaj.aldo.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import sinanaj.aldo.util.JsonLocalDateDeserializer;
import sinanaj.aldo.util.JsonLocalDateSerializer;
import sinanaj.aldo.util.JsonLocalDateTimeSerializer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Booking {

    public enum Status {
        CONFIRMED,
        NOT_CONFIRMED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Embedded
    private Agency agency;
    @Embedded
    private TourLeader tourLeader;
    @Embedded
    private LocalGuide localGuide;
    private String place;
    @Column(name = "BOOKING_DATE")
    private LocalDate date;
    @Column(name = "BOOKING_TIME")
    private String time;
    @Column(name = "NR_OF_PEOPLE")
    private int nrOfPeople;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "STAFF_ID")
    private Staff staff;
    private String notes;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Booking() {
    }

    @PrePersist
    protected void onCreate() {
        updated = created = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public TourLeader getTourLeader() {
        return tourLeader;
    }

    public void setTourLeader(TourLeader tourLeader) {
        this.tourLeader = tourLeader;
    }

    public LocalGuide getLocalGuide() {
        return localGuide;
    }

    public void setLocalGuide(LocalGuide localGuide) {
        this.localGuide = localGuide;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @JsonSerialize(using = JsonLocalDateSerializer.class)
    public LocalDate getDate() {
        return date;
    }

    @JsonDeserialize(using = JsonLocalDateDeserializer.class)
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNrOfPeople() {
        return nrOfPeople;
    }

    public void setNrOfPeople(int nrOfPeople) {
        this.nrOfPeople = nrOfPeople;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    public LocalDateTime getCreated() {
        return created;
    }

    @JsonSerialize(using = JsonLocalDateTimeSerializer.class)
    public LocalDateTime getUpdated() {
        return updated;
    }
}
