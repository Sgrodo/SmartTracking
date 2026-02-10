package it.univr.track.mock;

import it.univr.track.entity.Device;
import it.univr.track.entity.DeviceAssignment;
import it.univr.track.entity.data.LoadItem;
import it.univr.track.entity.data.Package;
import it.univr.track.entity.data.Shipment;
import it.univr.track.entity.data.UnitLoad;
import it.univr.track.entity.sensor.HumidityConstraint;
import it.univr.track.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Configuration
@Profile("mock")
public class DataInitializer {

    @Bean
    public CommandLineRunner initSampleDate(UnitLoadRepository unitLoadRepository, LoadItemRepository loadItemRepository,
                                            PackageRepository packageRepository, DeviceRepository deviceRepository,
                                            ShipmentRepository shipmentRepository, DeviceAssignmentRepository deviceAssignmentRepository) {
        return args -> {
            LoadItem loadItem1 = new LoadItem("banana", "QR_BAN", "1 banana");
            LoadItem loadItem2 = new LoadItem("mela", "QR_MEL", "1 mela");
            loadItemRepository.save(loadItem1);
            loadItemRepository.save(loadItem2);

            UnitLoad utSample1 = new UnitLoad("banane", ZonedDateTime.now(), List.of(new HumidityConstraint(20.0)));
            utSample1 = unitLoadRepository.save(utSample1);
            utSample1.addLoadItem(loadItem1);
            utSample1 = unitLoadRepository.save(utSample1);

            UnitLoad utSample2 = new UnitLoad("mele", ZonedDateTime.now(), List.of(new HumidityConstraint(50.0)));
            utSample2 = unitLoadRepository.save(utSample2);
            utSample2.addLoadItem(loadItem2);
            utSample2 = unitLoadRepository.save(utSample2);

            // Create Unitload for package
            UnitLoad utSample3 = new UnitLoad("arance", ZonedDateTime.now(), List.of(new HumidityConstraint(30.0)));
            utSample3 = unitLoadRepository.save(utSample3);
            utSample3.addLoadItem(new LoadItem("arancia", "QR_ARA", "1 arancia"));
            utSample3 = unitLoadRepository.save(utSample3);

            UnitLoad utSample4 = new UnitLoad("kiwi", ZonedDateTime.now(), List.of(new HumidityConstraint(40.0)));
            utSample4 = unitLoadRepository.save(utSample4);
            utSample4.addLoadItem(new LoadItem("kiwi", "QR_KIW", "1 kiwi"));
            utSample4 = unitLoadRepository.save(utSample4);

            Package pkgSample = new Package("frutta", "FRUTTA2024");
            pkgSample = packageRepository.save(pkgSample);
            pkgSample.addUnitLoad(utSample3);
            pkgSample.addUnitLoad(utSample4);
            pkgSample = packageRepository.save(pkgSample);


            //Device to use
            deviceRepository.saveAll(
                    List.of(
                            new Device(true, true, true, new BigDecimal("100.00"), "Italy"),
                            new Device(true, false, true, new BigDecimal("85.50"), "Germany"),
                            new Device(false, true, false, new BigDecimal("120.00"), "France"),
                            new Device(true, true, false, new BigDecimal("95.00"), "Spain"),
                            new Device(false, false, true, new BigDecimal("70.00"), "USA"),
                            new Device(true, false, true, new BigDecimal("150.00"), "Japan")
                    )
            );

            Device chinaDevice = deviceRepository.save(new Device(false, true, false, new BigDecimal("110.00"), "China"));

            Device brazilDevice = deviceRepository.save(new Device(true, true, true, new BigDecimal("200.00"), "Brazil"));

            DeviceAssignment chinaAssignment = deviceAssignmentRepository.save(new DeviceAssignment(chinaDevice, utSample1, ZonedDateTime.now()));
            DeviceAssignment brazilAssignment = deviceAssignmentRepository.save(new DeviceAssignment(brazilDevice, pkgSample, ZonedDateTime.now()));

            // Create sample shipment
            Shipment shipment = new Shipment("SAMPLE SHIPMENT");
            shipment.addTrackable(utSample1);
            shipment.addTrackable(pkgSample);

            shipmentRepository.save(shipment);
            unitLoadRepository.save(utSample1);
            packageRepository.save(pkgSample);

            loadItemRepository.flush();
            deviceRepository.flush();
            unitLoadRepository.flush();
            packageRepository.flush();
            deviceAssignmentRepository.flush();
            shipmentRepository.flush();
        };
    }
}
