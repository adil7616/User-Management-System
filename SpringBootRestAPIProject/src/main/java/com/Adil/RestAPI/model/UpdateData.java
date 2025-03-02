package com.Adil.RestAPI.model;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateData {
    private String fullName;
    private String mobNum;
    private String panNum;
    private UUID managerId;
}
