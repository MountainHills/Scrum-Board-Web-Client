package com.pagejump.scrumboardwebclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskRequestDTO {
    private String title;
    private String description;
    private String status;
}
