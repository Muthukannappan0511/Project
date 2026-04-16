package com.rvz.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rvz.dto.BranchListDto;
import com.rvz.entity.Branch;
import com.rvz.repo.BranchRepo;

@Service
public class SuperAdminBranchService {

    private final BranchRepo branchRepo;

    public SuperAdminBranchService(BranchRepo branchRepo) {
        this.branchRepo = branchRepo;
    }

    public List<BranchListDto> getAllBranches() {
        List<Branch> branches = branchRepo.findAll();

        List<BranchListDto> result = new ArrayList<>();
        for (Branch b : branches) {
            result.add(new BranchListDto(b.getBranchId(), b.getBranchName()));
        }
        return result;
    }
}
