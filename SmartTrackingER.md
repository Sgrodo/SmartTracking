classDiagram
direction BT
class AbstractAuditable {
    Date  createdDate
    Date  lastModifiedDate
}
class AbstractEntity {
    Long  id
}
class AbstractPersistable {
    PK  id
}
class Device {
    Boolean  isCalibrated
    Boolean  isConnected
    String  origin
    BigDecimal  price
    Boolean  state
    TrackingState  trackingState
}
class DeviceAssignment {
    ZonedDateTime  fromTs
    ZonedDateTime  toTs
}
class LoadItem {
    String  description
    String  name
    String  qrCode
}
class Package
class PhysicalConstraintEmbeddable {
    Double  numberValue
    TemperatureUnit  temperatureUnit
    PhysicalConstraintType  type
}
class Sending {
    boolean  dataIntegrity
    boolean  secureChannel
    ZonedDateTime  timestamp
}
class Shipment {
    String  description
    ZonedDateTime  endDate
    ZonedDateTime  startDate
    ShipmentState  state
}
class TrackData
class Trackable
class UnitLoad {
    String  description
    ZonedDateTime  startDate
    TrackingState  trackingState
}
class UserRegistered {
    String  address
    String  city
    String  email
    String  firstname
    Gender  gender
    String  lastname
    String  password
    Role  role
    String  taxIdentificationNumber
    String  telephoneNumber
    String  username
}

AbstractAuditable  --|>  AbstractPersistable 
Device  --|>  AbstractEntity 
Device "0..*" --> "0..1" UnitLoad 
DeviceAssignment  --|>  AbstractEntity 
DeviceAssignment "0..*" --> "1" Device 
DeviceAssignment "0..*" --> "1" Trackable 
LoadItem  --|>  AbstractEntity 
LoadItem "0..*" --> "0..*" UnitLoad 
Package "0..*" <--> "0..1" Shipment 
Package  --|>  Trackable 
Package "0..1" <--> "0..*" UnitLoad 
Sending  --|>  AbstractEntity 
Sending "0..*" --> "1" Device 
Shipment  --|>  AbstractEntity 
Shipment "0..1" <--> "0..*" UnitLoad 
TrackData  --|>  UserRegistered 
Trackable  --|>  AbstractEntity 
Trackable "0..1" --> "0..*" Sending 
UnitLoad "0..*" --> "0..*" LoadItem 
PhysicalConstraintEmbeddable  --*  UnitLoad 
UnitLoad  --|>  Trackable 
UserRegistered  --|>  AbstractEntity 
