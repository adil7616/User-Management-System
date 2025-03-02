package com.Adil.RestAPI.model;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteUserRequest {
    private UUID userId;
    private String mobNum;
}
