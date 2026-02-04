package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.data.Sending;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.exception.InvalidDataException;
import it.univr.track.exception.NotFoundException;
import it.univr.track.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final SendingFactory sendingFactory;

    public DeviceService(DeviceRepository deviceRepository,
                         SendingFactory sendingFactory) {
        this.deviceRepository = deviceRepository;
        this.sendingFactory = sendingFactory;
    }

    /**
     * Setta lo stato di tracking dei device (se ti serve ancora).
     * Nota: qui conviene salvare, oppure assicurarsi che i Device siano managed.
     */
    @Transactional
    public void setDeviceTrackingState(Set<Device> devices, TrackingState state) {
        if (devices == null) throw new InvalidDataException("devices is null");
        if (state == null) throw new InvalidDataException("state is null");

        for (Device d : devices) {
            if (d == null) continue;
            d.setTrackingState(state);
        }
        deviceRepository.saveAll(devices);
    }

    /**
     * Simula "il device invia un report".
     * Ora NON esiste più shipmentId: il target si risolve tramite DeviceAssignment attivo.
     */
    @Transactional
    public Sending ingestMockSending(Long deviceId) {
        if (deviceId == null) throw new IllegalArgumentException("deviceId is null");

        // opzionale: fail fast se device non esiste
        deviceRepository.findById(deviceId)
                .orElseThrow(() -> new NotFoundException("Device not found: " + deviceId));

        // la factory deve: (1) risolvere target attivo, (2) creare sending, (3) salvarlo sul Trackable
        return sendingFactory.ingestSending(deviceId);
    }

    public Device getDeviceById(Long deviceId) {
        if (deviceId == null) throw new InvalidDataException("deviceId is null");
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new NotFoundException("Device not found: " + deviceId));
    }

    public List<Device> getAvailableDevices() {
        return deviceRepository.findDevicesByNotInShipmentStates(List.of(ShipmentState.CREATED, ShipmentState.IN_TRANSIT));
    }
}
