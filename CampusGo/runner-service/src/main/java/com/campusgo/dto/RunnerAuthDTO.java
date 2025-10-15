package com.campusgo.dto;


import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunnerAuthDTO {
    private Long id;
    private String username;
    private String passwordHash;
    private String role; // e.g. ROLE_RUNNER
}
