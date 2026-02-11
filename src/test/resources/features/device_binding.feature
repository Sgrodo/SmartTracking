@web @devices
Feature: Device binding to Trackables
  Users can bind and unbind devices to shipment trackables and availability is updated.

  Background:
    Given I am logged in as "WarehouseWorker"

  # Scenario 6
  @bind
  Scenario: After binding, the device is no longer available
    Given there is a non-terminated shipment with description "Shipment D"
    And shipment "Shipment D" has trackable "T1"
    And shipment "Shipment D" has trackable "T2"
    And there is an available device with code "D1"
    When I open the "Trackables & Devices" page for shipment "Shipment D"
    And I bind device "D1" to trackable "T1"
    Then trackable "T1" should show device "D1" as bound
    And device "D1" should not be in the list of available devices

  # Scenario 7
  @unbind
  Scenario: After unbinding, the device becomes available again
    Given there is a non-terminated shipment with description "Shipment U"
    And shipment "Shipment U" has trackable "T1"
    And device "D1" is bound to trackable "T1" in shipment "Shipment U"
    When I open the "Trackables & Devices" page for shipment "Shipment U"
    And I unbind device "D1" from trackable "T1"
    Then trackable "T1" should not show any bound device
    And device "D1" should be in the list of available devices
