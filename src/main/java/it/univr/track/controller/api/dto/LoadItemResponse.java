package it.univr.track.controller.api.dto;

import it.univr.track.entity.data.LoadItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadItemResponse {

    private Long id;
    private String name;
    private String qrCode;
    private String description;

    public LoadItemResponse(LoadItem loadItem) {
        this.id = loadItem.getId();
        this.name = loadItem.getName();
        this.qrCode = loadItem.getQrCode();
        this.description = loadItem.getDescription();
    }
}
