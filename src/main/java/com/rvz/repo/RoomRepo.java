package com.rvz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rvz.entity.Room;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    List<Room> findByBranch_BranchId(Long branchId);
    long countByBranch_BranchId(Long branchId);
    boolean existsByBranch_BranchIdAndRoomCode(Long branchId, String roomCode);
    boolean existsByBranch_BranchIdAndRoomCodeIgnoreCase(Long branchId, String roomCode);
}
