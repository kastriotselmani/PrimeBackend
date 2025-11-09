package com.example.backend.domain;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NoteItem implements ProjectItem {
    private final String type = "note";
    private String text;
}
