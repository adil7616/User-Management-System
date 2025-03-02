package com.Adil.RestAPI.model;

import lombok.Data;

import java.util.UUID;

@Data
public class GetUsersRequest {
    private UUID userId;
    private String mobNum;
    private UUID managerId;
}
