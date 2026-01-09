package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.Shipment;
import it.univr.track.entity.enumeration.TrackingState;
import it.univr.track.entity.sensor.PhysicalConstraint;
import it.univr.track.repository.DeviceRepository;
import it.univr.track.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Questa Classe si occupa della gestione delle spedizioni e verrà utilizzata
 * direttamente dal controller che dovrà effettuare:
 *
 creazione spedizioni
 lista spedizioni
 attiva/disattiva tracking
 associazione device/spedizione

 */
@Service
public class ShipmentService {
    private final DeviceService deviceService;
    private final ShipmentRepository shipmentRepository;
    private final DeviceRepository deviceRepository;
    public ShipmentService(ShipmentRepository shipmentRepository, DeviceRepository deviceRepository, DeviceService deviceService) {
        this.shipmentRepository = shipmentRepository;
        this.deviceRepository = deviceRepository;
        this.deviceService = deviceService;
    }

    /**
     * Crea e registra una nuova Shipment nel sistema.
     */
    public Shipment createShipment(
            String description,
            ZonedDateTime startDate,
            Set<PhysicalConstraint> physicalConstraints
    ) {
        // le validazioni principali sono già nel costruttore di Shipment
        Shipment shipment = new Shipment(description, startDate, physicalConstraints);

        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
    }

    @Transactional
    public boolean deleteShipmentById(Long id){
        if(shipmentRepository.existsById(id)){
            shipmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Shipment[] getAllShipments(){
        return shipmentRepository.findAll().toArray(new Shipment[0]);
    }



    @Transactional
    public void associateDevice(Long shipmentId, Long deviceId) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Shipment not found: " + shipmentId));

        boolean alreadyAssociated = shipment.getDevices().stream()
                .anyMatch(d -> d.getId().equals(deviceId));

        if (alreadyAssociated) {
            throw new IllegalStateException(
                    "Device " + deviceId + " already associated to shipment " + shipmentId
            );
        }

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Device not found: " + deviceId));

        deviceService.addDeviceToShipment(shipmentId, device.getId());
    }
    /**
     * Ogni device può essere associato al più ad una spedizione
     */
    @Transactional
    public void deAssociateDevice(Long shipmentId, Long deviceId) {
        Shipment s = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        Device d = deviceService.getDeviceById(deviceId);
        s.removeDevice(d);
        deviceService.removeDeviceFromShipment(deviceId);
        shipmentRepository.save(s);
    }

    @Transactional
    public Set<Device> getDevicesOfShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow()
                .getDevices();
    }

    @Transactional
    public void setTrackingState(Long shipmentId, TrackingState state){

        Shipment s = this.shipmentRepository.findById(shipmentId).orElseThrow(()->
                new IllegalArgumentException(
                        "Shipment of Id "+ shipmentId.toString() + " does not exists"));

        s.setTrackingState(state);
        Set<Device> devices = s.getDevices();
        deviceService.setDeviceTrackingState(devices,state);
    }

}
