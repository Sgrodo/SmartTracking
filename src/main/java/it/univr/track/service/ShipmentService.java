package it.univr.track.service;

import it.univr.track.entity.Device;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.Trackable;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.enumeration.ShipmentState;
import it.univr.track.helper.Thrower;
import it.univr.track.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final TrackableService trackableService;
    private final DeviceService deviceService;
    public ShipmentService(ShipmentRepository shipmentRepository,
                           TrackableService trackableService,
                           DeviceService deviceService) {
        this.shipmentRepository = shipmentRepository;
        this.trackableService = trackableService;
        this.deviceService = deviceService;
    }

    @Transactional
    public Shipment create(ZonedDateTime startDate, String description, Set<UnitLoad> unitloads) {
        Shipment s = new Shipment(startDate, description, unitloads);
        return shipmentRepository.save(s);
    }

    @Transactional(readOnly = true)
    public Shipment getById(Long shipmentId) {
        if (shipmentId == null) throw new IllegalArgumentException("shipmentId is null");
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + shipmentId));
    }

    @Transactional
    public List<Shipment> getAllShipments(){
        return shipmentRepository.findAll();
    }

    @Transactional
    public void update( Shipment shipment, String description, ZonedDateTime endDate, ShipmentState state, Set<UnitLoad> unitLoads) {
        if(endDate != null){
            //se esiste una endDate allora lo stato dello SHipment deve essere coerente
            Thrower.state(state.equals(ShipmentState.COMPLETED) || state.equals(ShipmentState.CANCELLED),
                    "Shipment with endDate must be in COMPLETED or CANCELLED state");
        }
        if(state.equals(ShipmentState.CANCELLED) || state.equals(ShipmentState.COMPLETED)){
            // se ha uno stato "finale" allora deve avere una endDate
            if(endDate == null){
                endDate =ZonedDateTime.now();
            }
        }
        shipment.edit(description, state, endDate);
        for (UnitLoad ul : unitLoads){
            if(!shipment.getUnitLoads().contains(ul)){
                shipment.addTrackable(ul);
            }else{
                shipment.removeUnitLoad(ul);
            }
        }
        shipmentRepository.save(shipment);
    }

    @Transactional
    public void attachTrackable(Long shipmentId, Long trackableId) {
        Shipment s = getById(shipmentId);
        Trackable t = getTrackable(trackableId);

        // se già assegnato a questa shipment: no-op
        if (t instanceof UnitLoad ul) {
            if (ul.getShipment() != null && ul.getShipment().getId().equals(shipmentId)) return;
            if (ul.getShipment() != null && !ul.getShipment().getId().equals(shipmentId)) {
                throw new IllegalStateException("UnitLoad " + trackableId + " already belongs to another Shipment");
            }
            s.addTrackable(ul);
            return;
        }

        if (t instanceof Package pkg) {
            if (pkg.getShipment() != null && pkg.getShipment().getId().equals(shipmentId)) return;
            if (pkg.getShipment() != null && !pkg.getShipment().getId().equals(shipmentId)) {
                throw new IllegalStateException("Package " + trackableId + " already belongs to another Shipment");
            }
            s.addPackage(pkg);
            return;
        }

        throw new IllegalStateException("Unsupported Trackable type for id " + trackableId);
    }

    @Transactional
    public void detachTrackable(Long shipmentId, Long trackableId) {
        Shipment s = getById(shipmentId);
        Trackable t = getTrackable(trackableId);

        if (t instanceof UnitLoad ul) {
            if (ul.getShipment() == null || !ul.getShipment().getId().equals(shipmentId)) {
                throw new IllegalStateException("UnitLoad " + trackableId + " is not in Shipment " + shipmentId);
            }
            s.removeUnitLoad(ul);
            return;
        }

        if (t instanceof Package pkg) {
            if (pkg.getShipment() == null || !pkg.getShipment().getId().equals(shipmentId)) {
                throw new IllegalStateException("Package " + trackableId + " is not in Shipment " + shipmentId);
            }
            s.removePackage(pkg);
            return;
        }

        throw new IllegalStateException("Unsupported Trackable type for id " + trackableId);
    }

    private Trackable getTrackable(Long trackableId) {
        if (trackableId == null) throw new IllegalArgumentException("trackableId is null");
        return trackableRepository.findById(trackableId)
                .orElseThrow(() -> new IllegalArgumentException("Trackable not found: " + trackableId));
    }

    private boolean legalDateFields(Long shipmentId){
        if ( shipmentRepository.findById(shipmentId).isPresent()){
            Shipment s = shipmentRepository.getReferenceById(shipmentId);
            return (s.getStartDate().isBefore(s.getEndDate()));

        }else{
            throw new IllegalArgumentException("Shipment of id "+ shipmentId + " does not exist");
        }
    }

    @Transactional
    public boolean deleteUnitLoadById(Long unitLoadId) {
        Thrower.notNull(unitLoadId, "unitLoadId is null");
        if (unitLoadId == null) throw new IllegalArgumentException("unitLoadId is null");
        if (shipmentRepository.existsById(unitLoadId)) {
            shipmentRepository.deleteById(unitLoadId);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    private List<Device> getAvailableDevices(){
         return deviceService.getAvailableDevices();
    }
}
