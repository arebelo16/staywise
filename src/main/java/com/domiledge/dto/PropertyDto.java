package com.domiledge.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDto {
    private UUID id;
    private String title;
    private String address;
    private String description;
    private String coverImageUrl;
}