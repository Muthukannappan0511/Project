package com.rvz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rvz.entity.Branch;

@Repository
public interface BranchRepo extends JpaRepository<Branch, Long> {
    boolean existsByBranchName(String branchName);
}