package com.rvz.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rvz.entity.BookingRequest;
import com.rvz.entity.BookingStatus;

public interface BookingRequestRepo extends JpaRepository<BookingRequest, Long> {

	  @Query("""
		        SELECT COUNT(b) FROM BookingRequest b
		        WHERE b.room.roomId = :roomId
		          AND b.status = :status
		          AND b.startTime < :endTime
		          AND :startTime < b.endTime
		    """)
		    long countOverlapsByStatus(
		            @Param("roomId") Long roomId,
		            @Param("startTime") LocalDateTime startTime,
		            @Param("endTime") LocalDateTime endTime,
		            @Param("status") BookingStatus status
		    );

		    @Query("""
		        SELECT COUNT(b) FROM BookingRequest b
		        WHERE b.room.roomId = :roomId
		          AND b.status IN :statuses
		          AND b.startTime < :endTime
		          AND :startTime < b.endTime
		    """)
		    long countOverlapsByStatuses(
		            @Param("roomId") Long roomId,
		            @Param("startTime") LocalDateTime startTime,
		            @Param("endTime") LocalDateTime endTime,
		            @Param("statuses") List<BookingStatus> statuses
		    );

	List<BookingRequest> findByEmployee_UserIdOrderByRequestedAtDesc(Long userId);
	List<BookingRequest> findByBranch_BranchIdAndStatusOrderByRequestedAtDesc(Long branchId, BookingStatus status);
}