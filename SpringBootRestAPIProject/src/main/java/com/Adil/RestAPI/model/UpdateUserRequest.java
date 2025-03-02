package com.Adil.RestAPI.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateUserRequest {
    private List<UUID> userIds;
    private UpdateData updateData;
}
