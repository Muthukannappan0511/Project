package com.rvz.service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rvz.dto.employee.BookingCreateRequestDto;
import com.rvz.dto.employee.BookingDto;
import com.rvz.dto.employee.RoomSuggestionDto;
import com.rvz.entity.AppUser;
import com.rvz.entity.BookingRequest;
import com.rvz.entity.BookingStatus;
import com.rvz.entity.Branch;
import com.rvz.entity.Room;
import com.rvz.repo.BookingRequestRepo;
import com.rvz.repo.BranchRepo;
import com.rvz.repo.RoomRepo;
import com.rvz.repo.UserRepo;

@Service
public class BookingService {

    private final UserRepo userRepo;
    private final BranchRepo branchRepo;
    private final RoomRepo roomRepo;
    private final BookingRequestRepo bookingRepo;

    public BookingService(
            UserRepo userRepo,
            BranchRepo branchRepo,
            RoomRepo roomRepo,
            BookingRequestRepo bookingRepo
    ) {
        this.userRepo = userRepo;
        this.branchRepo = branchRepo;
        this.roomRepo = roomRepo;
        this.bookingRepo = bookingRepo;
    }

    // ✅ 1) Suggest available rooms (capacity + availability + no overlap)
    public List<RoomSuggestionDto> suggestRooms(
            Long branchId,
            String date,
            String start,
            String end,
            Integer capacityNeeded,
            String facilitiesCsv
    ) {

        if (branchId == null) {
            throw new RuntimeException("branchId is required");
        }
        if (date == null || date.isBlank()) {
            throw new RuntimeException("date is required");
        }
        if (start == null || start.isBlank()) {
            throw new RuntimeException("start is required");
        }
        if (end == null || end.isBlank()) {
            throw new RuntimeException("end is required");
        }

        LocalDate d = LocalDate.parse(date);
        LocalTime st = LocalTime.parse(start);
        LocalTime et = LocalTime.parse(end);

        if (!st.isBefore(et)) {
            throw new RuntimeException("start must be before end");
        }

        if (capacityNeeded == null || capacityNeeded < 1) {
            capacityNeeded = 1;
        }

        branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        List<String> neededFacilities =
                normalizeFacilitiesList(facilitiesCsv);

        LocalDateTime startDT = LocalDateTime.of(d, st);
        LocalDateTime endDT   = LocalDateTime.of(d, et);

        List<Room> rooms =
                roomRepo.findByBranch_BranchId(branchId);

        List<RoomSuggestionDto> result = new ArrayList<>();

        for (Room room : rooms) {

            if (room.getCapacity() == null ||
                room.getCapacity() < capacityNeeded) {
                continue;
            }

            if (room.getAvailableFrom() != null
                    && st.isBefore(room.getAvailableFrom())) {
                continue;
            }

            if (room.getAvailableTo() != null
                    && et.isAfter(room.getAvailableTo())) {
                continue;
            }

            if (!roomHasAllFacilities(
                    room.getFacilities(),
                    neededFacilities
            )) {
                continue;
            }

            long overlaps = bookingRepo.countOverlapsByStatuses(
                    room.getRoomId(),
                    startDT,
                    endDT,
                    List.of(
                        BookingStatus.APPROVED,
                        BookingStatus.PENDING
                    )
            );

            if (overlaps > 0) {
                continue;
            }

            result.add(new RoomSuggestionDto(
                    room.getRoomId(),
                    room.getRoomCode(),
                    room.getRoomType() != null
                            ? room.getRoomType().name()
                            : null,
                    room.getCapacity(),
                    room.getAvailableFrom() != null
                            ? room.getAvailableFrom().toString()
                            : null,
                    room.getAvailableTo() != null
                            ? room.getAvailableTo().toString()
                            : null,
                    room.getFacilities()
            ));
        }

        return result;
    }

    // ✅ 2) Create booking request (PENDING)
    public BookingDto createBooking(
            String employeeEmail,
            BookingCreateRequestDto dto
    ) {

        AppUser employee = userRepo.findByEmail(employeeEmail)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        Branch branch = branchRepo.findById(dto.getBranchId())
                .orElseThrow(() ->
                        new RuntimeException("Branch not found"));

        Room room = roomRepo.findById(dto.getRoomId())
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        if (room.getBranch() == null ||
            !room.getBranch().getBranchId()
                    .equals(branch.getBranchId())) {
            throw new RuntimeException(
                "Room does not belong to selected branch"
            );
        }

        LocalDate d = LocalDate.parse(dto.getDate());
        LocalTime st = LocalTime.parse(dto.getStartTime());
        LocalTime et = LocalTime.parse(dto.getEndTime());

        if (!st.isBefore(et)) {
            throw new RuntimeException("start must be before end");
        }

        if (room.getAvailableFrom() != null &&
            st.isBefore(room.getAvailableFrom())) {
            throw new RuntimeException(
                "Selected time is before room availability"
            );
        }

        if (room.getAvailableTo() != null &&
            et.isAfter(room.getAvailableTo())) {
            throw new RuntimeException(
                "Selected time is after room availability"
            );
        }

        LocalDateTime startDT = LocalDateTime.of(d, st);
        LocalDateTime endDT   = LocalDateTime.of(d, et);

        long overlaps = bookingRepo.countOverlapsByStatuses(
                room.getRoomId(),
                startDT,
                endDT,
                List.of(
                    BookingStatus.APPROVED,
                    BookingStatus.PENDING
                )
        );

        if (overlaps > 0) {
            throw new RuntimeException(
                "Room is already booked for selected time"
            );
        }

        BookingRequest req = new BookingRequest();
        req.setEmployee(employee);
        req.setBranch(branch);
        req.setRoom(room);
        req.setStartTime(startDT);
        req.setEndTime(endDT);
        req.setStatus(BookingStatus.PENDING);
        req.setEmployeeNote(dto.getNote());
        req.setRequestedAt(LocalDateTime.now());

        bookingRepo.save(req);

        return new BookingDto(
                req.getRequestId(),
                branch.getBranchName(),
                room.getRoomCode(),
                startDT.toString(),
                endDT.toString(),
                req.getStatus().name(),
                req.getEmployeeNote(),
                req.getAdminComment()
        );
    }

    // ✅ 3) View my bookings
    public List<BookingDto> myBookings(String employeeEmail) {

        AppUser employee = userRepo.findByEmail(employeeEmail)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        List<BookingRequest> list =
                bookingRepo.findByEmployee_UserIdOrderByRequestedAtDesc(
                        employee.getUserId()
                );

        return list.stream()
                .map(b -> new BookingDto(
                        b.getRequestId(),
                        b.getBranch() != null
                                ? b.getBranch().getBranchName()
                                : "-",
                        b.getRoom() != null
                                ? b.getRoom().getRoomCode()
                                : "-",
                        b.getStartTime() != null
                                ? b.getStartTime().toString()
                                : "",
                        b.getEndTime() != null
                                ? b.getEndTime().toString()
                                : "",
                        b.getStatus() != null
                                ? b.getStatus().name()
                                : "",
                        b.getEmployeeNote(),
                        b.getAdminComment()
                ))
                .collect(Collectors.toList());
    }

    // ✅ 4) Cancel booking (PENDING only)
    public void cancelBooking(String employeeEmail, Long requestId) {

        AppUser employee = userRepo.findByEmail(employeeEmail)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        BookingRequest req = bookingRepo.findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException("Booking request not found"));

        if (req.getEmployee() == null ||
            !req.getEmployee().getUserId()
                    .equals(employee.getUserId())) {
            throw new RuntimeException("Not allowed");
        }

        if (req.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException(
                "Only PENDING booking can be cancelled"
            );
        }

        req.setStatus(BookingStatus.CANCELLED);
        req.setDecidedAt(LocalDateTime.now());
        bookingRepo.save(req);
    }

    // -------- Facilities helpers --------

    private List<String> normalizeFacilitiesList(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return List.of();
        }

        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private boolean roomHasAllFacilities(
            String roomCsv,
            List<String> needed
    ) {
        if (needed == null || needed.isEmpty()) return true;
        if (roomCsv == null || roomCsv.trim().isEmpty()) return false;

        Set<String> have = Arrays.stream(roomCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return have.containsAll(needed);
    }
}


