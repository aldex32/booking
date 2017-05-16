package sinanaj.aldo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import sinanaj.aldo.model.Agency;
import sinanaj.aldo.model.Booking;
import sinanaj.aldo.model.LocalGuide;
import sinanaj.aldo.model.Staff;
import sinanaj.aldo.model.TourLeader;
import sinanaj.aldo.repository.BookingRepository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingIT {

    private static final String BOOKING_URL = "/api/booking/";
    private static final String AGENCY_NAME = "Globus";
    private static final String TOUR_LEADER_NAME = "Higuain";
    private static final String LOCAL_GUIDE_NAME = "Dybala";
    private static final String PLACE = "Florence";
    private static final String STAFF_NAME = "Aldo";

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @SuppressWarnings("rawtypes")
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("The JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setUp() {
        bookingRepository.save(getBooking());
    }

    @After
    public void tearDown() {
        bookingRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void testCRUD() throws Exception {
        Booking booking = getBooking();

        // CREATE
        final long bookingId = createBooking(booking);

        // READ
        readBooking(bookingId, booking);

        //UPDATE
        booking.setPlace("Haarlem");

        updateBooking(bookingId, booking);

        // READ UPDATED
        readBooking(bookingId, booking);

        // DELETE
        deleteBooking(bookingId);

        // RETRIEVE should fail
        mockMvc.perform(get(BOOKING_URL + bookingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void findByBookingDate() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                get(BOOKING_URL + "search/findByDate")
                        .param("date", "2015-01-22")
                        .contentType(contentType)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['date']", is(booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))));
    }

    @Test
    @WithMockUser
    public void findByBookingDateAndConfirmed() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                get(BOOKING_URL + "search/findByDateAndStatus")
                        .param("date", "2015-01-22")
                        .param("status", Booking.Status.CONFIRMED.name())
                        .contentType(contentType)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['date']", is(booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .andExpect(jsonPath("$._embedded.bookings[0]['status']", is(Booking.Status.CONFIRMED.name())));
    }

    @Test
    @WithMockUser
    public void findByBookingDateAndCancelledNotFound() throws Exception {

        mockMvc.perform(
                get(BOOKING_URL + "search/findByDateAndStatus")
                        .param("date", "2015-01-22")
                        .param("status", Booking.Status.CANCELLED.name())
                        .contentType(contentType)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(0)));
    }

    @Test
    @WithMockUser
    public void findByBookingDateAfter_2015_01_20() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                    get(BOOKING_URL + "search/findByDateAfter")
                    .param("after", "2015-01-20")
                    .contentType(contentType)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['date']", is(booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .andExpect(jsonPath("$._embedded.bookings[0]['agency']['name']", is(booking.getAgency().getName())));
    }

    @Test
    @WithMockUser
    public void findByBookingDateAfter_2015_01_20_AndConfirmed() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                get(BOOKING_URL + "search/findByDateAfterAndStatus")
                        .param("after", "2015-01-20")
                        .param("status", Booking.Status.CONFIRMED.name())
                        .contentType(contentType)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['date']", is(booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .andExpect(jsonPath("$._embedded.bookings[0]['status']", is(Booking.Status.CONFIRMED.name())));
    }

    @Test
    @WithMockUser
    public void noBookingAfter_2015_01_30() throws Exception {
        mockMvc.perform(
                    get(BOOKING_URL + "search/findByDateAfter")
                    .param("after", "2015-01-30")
                    .contentType(contentType)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(0)));
    }

    @Test
    @WithMockUser
    public void findByBookingDateBetween_2015_01_20_And_2015_01_23() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                    get(BOOKING_URL + "search/findByDateBetween")
                    .param("after", "2015-01-20")
                    .param("before", "2015-01-23")
                    .contentType(contentType)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['date']", is(booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))));
    }

    @Test
    @WithMockUser
    public void findByBookingDateBetween_2015_01_20_And_2015_01_23_Confirmed() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                get(BOOKING_URL + "search/findByDateBetween")
                        .param("after", "2015-01-20")
                        .param("before", "2015-01-23")
                        .param("status", Booking.Status.CONFIRMED.name())
                        .contentType(contentType)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['date']", is(booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .andExpect(jsonPath("$._embedded.bookings[0]['status']", is(Booking.Status.CONFIRMED.name())));
    }

    @Test
    @WithMockUser
    public void findByAgencyName() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                    get(BOOKING_URL + "search/findByAgencyNameContaining")
                    .param("name", AGENCY_NAME)
                    .contentType(contentType)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['agency']['name']", is(booking.getAgency().getName())));
    }

    @Test
    @WithMockUser
    public void findByTourLeaderName() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                    get(BOOKING_URL + "search/findByTourLeaderFullNameContaining")
                    .param("name", TOUR_LEADER_NAME)
                    .contentType(contentType)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['tourLeader']['fullName']", is(booking.getTourLeader().getFullName())));
    }

    @Test
    @WithMockUser
    public void findByLocalGuideName() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                    get(BOOKING_URL + "search/findByLocalGuideFullNameContaining")
                    .param("name", LOCAL_GUIDE_NAME)
                    .contentType(contentType)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['localGuide']['fullName']", is(booking.getLocalGuide().getFullName())));
    }

    @Test
    @WithMockUser
    public void findByStaffName() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                    get(BOOKING_URL + "search/findByStaffFullNameContaining")
                    .param("name", STAFF_NAME)
                    .contentType(contentType)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['staff']['fullName']", is(booking.getStaff().getFullName())));
    }

    @Test
    @WithMockUser
    public void findByPlace() throws Exception {
        final Booking booking = getBooking();

        mockMvc.perform(
                    get(BOOKING_URL + "search/findByPlaceContaining")
                    .param("place", PLACE)
                    .contentType(contentType)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookings", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.bookings[0]['place']", is(booking.getPlace())));
    }

    private Booking getBooking() {
        Booking booking = new Booking();
        booking.setAgency(new Agency(AGENCY_NAME, "DIR"));
        booking.setTourLeader(new TourLeader(TOUR_LEADER_NAME, "9999"));
        booking.setLocalGuide(new LocalGuide(LOCAL_GUIDE_NAME, "2121"));
        booking.setPlace(PLACE);
        booking.setDate(LocalDate.of(2015, Month.JANUARY, 22));
        booking.setTime("18:15");
        booking.setNrOfPeople(30);
        booking.setStaff(new Staff(STAFF_NAME, "1123"));
        booking.setNotes("Test Notes");
        booking.setStatus(Booking.Status.CONFIRMED);

        return booking;
    }

    private long createBooking(Booking booking) throws Exception {
        final MvcResult mvcResult = mockMvc.perform(post(BOOKING_URL)
                .content(this.toJsonString(booking)).contentType(contentType))
                .andExpect(status().isCreated())
                .andReturn();

        return getResourceIdFromUrl(mvcResult.getResponse().getRedirectedUrl());
    }

    private void readBooking(long bookingId, Booking booking) throws Exception {
        mockMvc.perform(get(BOOKING_URL + bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agency.name", is(booking.getAgency().getName())))
                .andExpect(jsonPath("$.agency.reference", is(booking.getAgency().getReference())))
                .andExpect(jsonPath("$.tourLeader.fullName", is(booking.getTourLeader().getFullName())))
                .andExpect(jsonPath("$.tourLeader.phone", is(booking.getTourLeader().getPhone())))
                .andExpect(jsonPath("$.localGuide.fullName", is(booking.getLocalGuide().getFullName())))
                .andExpect(jsonPath("$.localGuide.phone", is(booking.getLocalGuide().getPhone())))
                .andExpect(jsonPath("$.place", is(booking.getPlace())))
                .andExpect(jsonPath("$.date", is(booking.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .andExpect(jsonPath("$.time", is(booking.getTime())))
                .andExpect(jsonPath("$.nrOfPeople", is(booking.getNrOfPeople())))
                .andExpect(jsonPath("$.staff.fullName", is(booking.getStaff().getFullName())))
                .andExpect(jsonPath("$.staff.phone", is(booking.getStaff().getPhone())))
                .andExpect(jsonPath("$.notes", is(booking.getNotes())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())));
    }

    private void deleteBooking(long bookingId) throws Exception {
        mockMvc.perform(delete(BOOKING_URL + bookingId))
                .andExpect(status().isNoContent());
    }

    private void updateBooking(long bookingId, Booking booking) throws Exception {
        mockMvc.perform(put(BOOKING_URL + bookingId)
                .content(this.toJsonString(booking)).contentType(contentType))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @SuppressWarnings("unchecked")
    private String toJsonString(Object obj) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(obj, MediaType.APPLICATION_JSON, mockHttpOutputMessage);

        return mockHttpOutputMessage.getBodyAsString();
    }

    private long getResourceIdFromUrl(String locationUrl) {
        String[] parts = locationUrl.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}
