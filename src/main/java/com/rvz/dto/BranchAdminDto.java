package com.rvz.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.rvz.entity.RoomType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BranchAdminDto {
    public record RoomRequest(
            @NotBlank String roomCode,
            @NotNull RoomType roomType,
            @Min(1) int capacity,
            @NotNull LocalTime availableFrom,
            @NotNull LocalTime availableTo
    ) {}

    public record UsageLogRequest(
            @NotNull Long roomId,
            @NotNull LocalDateTime startTime,
            @NotNull LocalDateTime endTime,
            Integer occupancyCount,
            String notes
    ) {}
}
