package com.example.backend.domain;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AttachmentItem implements ProjectItem {
    private final String type = "attachment";
    private String url;
}