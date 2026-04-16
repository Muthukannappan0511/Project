package com.rvz.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rvz.dto.RoomCreateRequestDto;
import com.rvz.service.RoomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/branch")
public class BranchAdminRoomController {

    private final RoomService roomService;

    public BranchAdminRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/rooms")
    public String createRoom(@Valid @RequestBody RoomCreateRequestDto dto) {
        return roomService.createRoom(dto);
    }
}