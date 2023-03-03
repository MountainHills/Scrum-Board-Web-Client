package com.pagejump.scrumboardwebclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private long id;
    private String title;
    private String description;
    private String status;
    private boolean deleted;
    private String creationTime;
    private String updateTime;
}
