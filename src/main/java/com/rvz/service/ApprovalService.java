package com.rvz.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rvz.entity.AppUser;
import com.rvz.entity.BookingRequest;
import com.rvz.entity.BookingStatus;
import com.rvz.entity.UsageLog;
import com.rvz.repo.BookingRequestRepo;
import com.rvz.repo.UsageLogRepo;
import com.rvz.repo.UserRepo;

@Service
public class ApprovalService {

    private final BookingRequestRepo bookingRepo;
    private final UsageLogRepo logRepo;
    private final UserRepo userRepo;

    public ApprovalService(BookingRequestRepo bookingRepo, UsageLogRepo logRepo, UserRepo userRepo) {
        this.bookingRepo = bookingRepo;
        this.logRepo = logRepo;
        this.userRepo = userRepo;
    }

    // ✅ Branch Admin: view pending requests for branch
    public List<BookingRequest> pendingForBranch(Long branchId) {
        return bookingRepo.findByBranch_BranchIdAndStatusOrderByRequestedAtDesc(branchId, BookingStatus.PENDING);
    }

    // ✅ Approve request + create UsageLog
    public void approve(String approverEmail, Long requestId, String comment) {

        AppUser approver = userRepo.findByEmail(approverEmail)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        BookingRequest req = bookingRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking request not found"));

        if (req.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only PENDING bookings can be approved");
        }

        // ✅ Ensure approver is same branch (strong validation)
        if (approver.getBranch() == null || req.getBranch() == null ||
                !approver.getBranch().getBranchId().equals(req.getBranch().getBranchId())) {
            throw new RuntimeException("You are not allowed to approve bookings for other branches");
        }

        // ✅ Conflict check again (APPROVED only)
        long overlaps = bookingRepo.countOverlapsByStatus(
                req.getRoom().getRoomId(),
                req.getStartTime(),
                req.getEndTime(),
                BookingStatus.APPROVED
        );

        if (overlaps > 0) {
            throw new RuntimeException("Room already booked for this slot");
        }

        // ✅ Update booking status
        req.setStatus(BookingStatus.APPROVED);
        req.setAdminComment(comment);
        req.setDecidedAt(LocalDateTime.now());
        req.setDecidedBy(approver);
        bookingRepo.save(req);

        // ✅ Create UsageLog AFTER approval (your requirement)
        UsageLog log = new UsageLog();
        log.setRoom(req.getRoom());
        log.setStartTime(req.getStartTime());
        log.setEndTime(req.getEndTime());
        log.setOccupancyCount(0);
        log.setNotes("Approved Booking: " + req.getRequestId());

        // IMPORTANT: If your UsageLog entity uses different field name, adjust:
        // Some projects call it setCreatedByUser / setUser / setCreatedBy
        log.setCreatedBy(req.getEmployee());

        logRepo.save(log);
    }

    // ✅ Reject request
    public void reject(String approverEmail, Long requestId, String comment) {

        AppUser approver = userRepo.findByEmail(approverEmail)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        BookingRequest req = bookingRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking request not found"));

        if (req.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only PENDING bookings can be rejected");
        }

        // ✅ Ensure approver is same branch
        if (approver.getBranch() == null || req.getBranch() == null ||
                !approver.getBranch().getBranchId().equals(req.getBranch().getBranchId())) {
            throw new RuntimeException("You are not allowed to reject bookings for other branches");
        }

        req.setStatus(BookingStatus.REJECTED);
        req.setAdminComment(comment);
        req.setDecidedAt(LocalDateTime.now());
        req.setDecidedBy(approver);
        bookingRepo.save(req);
    }
}