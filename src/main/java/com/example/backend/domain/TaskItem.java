package com.example.backend.domain;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TaskItem implements ProjectItem {
    private final String type = ItemType.task.name(); // "task" in JSON
    private String title;
    private String assigneeId;
    private String reporterId;
    private int order;
    private long time;
    private Priority priority;
}