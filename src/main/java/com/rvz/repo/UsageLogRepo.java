package com.rvz.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rvz.entity.AppUser;
import com.rvz.entity.UsageLog;

@Repository
public interface UsageLogRepo extends JpaRepository<UsageLog, Long> {
	List<UsageLog> findByRoom_Branch_BranchIdAndStartTimeBetween(Long branchId, LocalDateTime from, LocalDateTime to);

	List<UsageLog> findTop5ByRoom_Branch_BranchIdOrderByStartTimeDesc(Long branchId);

	List<UsageLog> findByRoom_Branch_BranchIdAndStartTimeBetweenOrderByStartTimeDesc(Long branchId, LocalDateTime start,
			LocalDateTime end);

	List<UsageLog> findByRoom_Branch_BranchId(Long branchId);

	List<UsageLog> findByCreatedByOrderByStartTimeDesc(AppUser user);

	long countByRoom_Branch_BranchId(Long branchId);
	List<UsageLog> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);
}