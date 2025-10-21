package com.campusgo.mapper;

import com.campusgo.domain.Runner;
import com.campusgo.dto.RunnerAuthDTO;
import com.campusgo.dto.RunnerDTO;

public class RunnerConverter {
        public static RunnerDTO toPublicDTO(Runner r) {
            if (r == null) return null;
            RunnerDTO dto = new RunnerDTO();
            dto.setId(r.getId());
            dto.setUsername(r.getUsername());
            dto.setPhone(r.getPhone());
            dto.setVehicleType(r.getVehicleType());
            dto.setStatus(r.getStatus());
            dto.setLatitude(r.getLatitude());
            dto.setLongitude(r.getLongitude());
            dto.setRating(r.getRating());
            dto.setCompletedOrders(r.getCompletedOrders());
            dto.setTotalEarningsCents(r.getTotalEarningsCents());
            return dto;
        }

        public static RunnerAuthDTO toAuthDTO(Runner r) {
            if (r == null) return null;
            return RunnerAuthDTO.builder()
                    .id(r.getId())
                    .username(r.getUsername())
                    .passwordHash(r.getPasswordHash())
                    .role("ROLE_RUNNER")
                    .build();
        }
    }


