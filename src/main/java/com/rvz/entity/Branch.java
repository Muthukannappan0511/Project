package com.rvz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "branch")
public class Branch {

    @Id
    private Long branchId;

    @Column(nullable = false, unique = true, length = 100)
    private String branchName;

    @Column(nullable = false)
    private boolean active = true;

    public Branch() {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

	public Branch(Long branchId, String branchName, boolean active) {
		super();
		this.branchId = branchId;
		this.branchName = branchName;
		this.active = active;
	}
    
}