package com.Adil.RestAPI.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    private String mobNum;

    @NotBlank(message = "PAN number is required")
    private String panNum;

    private UUID managerId;
}

