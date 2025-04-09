package kroryi.apikeymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEvent {
    private Long id;
    private String title;
    private String start; // yyyy-MM-dd
}
