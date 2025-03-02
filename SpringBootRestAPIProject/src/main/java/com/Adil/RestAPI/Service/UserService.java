package com.Adil.RestAPI.Service;

import com.Adil.RestAPI.model.*;
import com.Adil.RestAPI.model.Manager;
import com.Adil.RestAPI.model.User;
import com.Adil.RestAPI.Repository.ManagerRepository;
import com.Adil.RestAPI.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

    @Autowired
    public UserService(UserRepository userRepository, ManagerRepository managerRepository) {
        this.userRepository = userRepository;
        this.managerRepository = managerRepository;
    }


    @Transactional
    public User createUser(CreateUserRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (request.getMobNum() == null || request.getMobNum().trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be empty");
        }
        if (request.getPanNum() == null || request.getPanNum().trim().isEmpty()) {
            throw new IllegalArgumentException("PAN number cannot be empty");
        }

        String processedMob = processMobileNumber(request.getMobNum());
        String processedPan = processPanNumber(request.getPanNum());

        if (userRepository.existsByMobNumAndIsActiveTrue(processedMob)) {
            throw new IllegalArgumentException("Mobile number already exists");
        }
        if (userRepository.existsByPanNumAndIsActiveTrue(processedPan)) {
            throw new IllegalArgumentException("PAN number already exists");
        }

        Manager manager = null;
        if (request.getManagerId() != null) {
            manager = managerRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found or inactive"));
            if (!manager.isActive()) {
                throw new IllegalArgumentException("Manager is inactive");
            }
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setMobNum(processedMob);
        user.setPanNum(processedPan);
        user.setManager(manager);
        return userRepository.save(user);
    }


    private String processMobileNumber(String mobNum) {
        String processed = mobNum.replaceAll("[^0-9]", "");
        if (processed.startsWith("91") && processed.length() == 12) {
            processed = processed.substring(2);
        } else if (processed.startsWith("+91")) {
            processed = processed.substring(3);
        } else if (processed.startsWith("0")) {
            processed = processed.substring(1);
        }

        if (processed.length() != 10) {
            throw new IllegalArgumentException("Invalid mobile number");
        }
        return processed;
    }

    private String processPanNumber(String pan) {
        String processed = pan.toUpperCase();
        if (!PAN_PATTERN.matcher(processed).matches()) {
            throw new IllegalArgumentException("Invalid PAN format");
        }
        return processed;
    }

    public List<User> getUsers(GetUsersRequest request) {
        if (request.getUserId() != null) {
            return userRepository.findByUserIdAndIsActiveTrue(request.getUserId())
                    .map(List::of)
                    .orElse(Collections.emptyList());
        } else if (request.getMobNum() != null) {
            return userRepository.findByMobNumAndIsActiveTrue(request.getMobNum())
                    .map(List::of)
                    .orElse(Collections.emptyList());
        } else if (request.getManagerId() != null) {
            return userRepository.findAllByManager_ManagerIdAndIsActiveTrue(request.getManagerId());
        }
        return userRepository.findAllByIsActiveTrue();
    }

    private boolean hasNonManagerUpdate(UpdateData updateData) {
        return updateData.getFullName() != null ||
                updateData.getMobNum() != null ||
                updateData.getPanNum() != null;
    }

    @Transactional
    public void deleteUser(DeleteUserRequest request) {
        User user;

        if (request.getUserId() != null) {
            user = userRepository.findByUserIdAndIsActiveTrue(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else if (request.getMobNum() != null) {
            user = userRepository.findByMobNumAndIsActiveTrue(request.getMobNum())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else {
            throw new IllegalArgumentException("Either userId or mobNum must be provided");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(UpdateUserRequest request) {
        if (request.getUserIds().isEmpty()) {
            throw new IllegalArgumentException("User list cannot be empty");
        }

        boolean isBulkUpdate = request.getUserIds().size() > 1;
        boolean containsNonManagerUpdates = hasNonManagerUpdate(request.getUpdateData());

        if (isBulkUpdate && containsNonManagerUpdates) {
            throw new IllegalArgumentException("Bulk updates only allowed for manager_id");
        }

        for (UUID userId : request.getUserIds()) {
            User currentUser = userRepository.findByUserIdAndIsActiveTrue(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            if (!currentUser.isActive()) {
                throw new IllegalArgumentException("Cannot update an inactive user");
            }

            if (request.getUpdateData().getManagerId() != null) {
                handleManagerUpdate(currentUser, request.getUpdateData().getManagerId());
            } else {
                updateIndividualFields(currentUser, request.getUpdateData());
            }
        }
    }


    private void handleManagerUpdate(User currentUser, UUID newManagerId) {
        Manager newManager = managerRepository.findById(newManagerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        if (newManager.equals(currentUser.getManager())) return;

        // Deactivate current user
        currentUser.setActive(false);
        userRepository.save(currentUser);

        // Create new user version
        User newUser = new User();
        newUser.setUserId(currentUser.getUserId());
        newUser.setFullName(currentUser.getFullName());
        newUser.setMobNum(currentUser.getMobNum());
        newUser.setPanNum(currentUser.getPanNum());
        newUser.setManager(newManager);
        newUser.setCreatedAt(currentUser.getCreatedAt());
        newUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(newUser);
    }

    private void updateIndividualFields(User user, UpdateData updateData) {
        if (updateData.getFullName() != null) {
            user.setFullName(updateData.getFullName());
        }
        if (updateData.getMobNum() != null) {
            user.setMobNum(processMobileNumber(updateData.getMobNum()));
        }
        if (updateData.getPanNum() != null) {
            user.setPanNum(processPanNumber(updateData.getPanNum())); // Ensures uppercase PAN
        }
        userRepository.save(user);
    }

}