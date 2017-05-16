package sinanaj.aldo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import sinanaj.aldo.model.Booking;

import java.time.LocalDate;
import java.util.List;

@Transactional
@RepositoryRestResource(collectionResourceRel = "bookings", path = "booking")
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByDate(@Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    List<Booking> findByDateAndStatus(
            @Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Param("status") Booking.Status status
    );

    List<Booking> findByDateAfter(@Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after);

    List<Booking> findByDateAfterAndStatus(
            @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @Param("status") Booking.Status status
    );

    List<Booking> findByDateBetween(
            @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before
    );

    List<Booking> findByDateBetweenAndStatus(
            @Param("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @Param("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
            @Param("status") Booking.Status status
    );

    List<Booking> findByAgencyNameContaining(@Param("name") String word);

    List<Booking> findByTourLeaderFullNameContaining(@Param("name") String word);

    List<Booking> findByLocalGuideFullNameContaining(@Param("name") String word);

    List<Booking> findByStaffFullNameContaining(@Param("name") String word);

    List<Booking> findByPlaceContaining(@Param("place") String word);
}
