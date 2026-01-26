package it.univr.track.controller.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PackageRequest {
    private Set<Long> unitLoadIds;
}
