package com.kvitka.dossier.dtos;

import com.kvitka.dossier.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "info")
public class EmailMessage {
    String address;
    Theme theme;
    Long applicationId;
    String info;
}
