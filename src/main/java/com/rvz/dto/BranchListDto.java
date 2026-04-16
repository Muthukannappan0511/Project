package com.rvz.dto;
public class BranchListDto {

    private Long branchId;
    private String branchName;

    public BranchListDto() {}

    public BranchListDto(Long branchId, String branchName) {
        this.branchId = branchId;
        this.branchName = branchName;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
