package it.univr.track.controller.api.dto.trackables.packages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Set;

@Getter
public class PackageRequest {

    @NotNull
    @NotBlank
    private String groupCode;

    private String description = "";

    @NotNull(message = "unitLoadIds is required")
    @NotEmpty(message = "At least one unitLoadId is needed")
    private Set<@NotNull(message = "unitLoadId can't be null") Long> unitLoadIds;
}
