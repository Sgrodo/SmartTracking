@web @shipments
Feature: Shipment management
  Users can create shipments, view them, and toggle tracking state.

  Background:
    Given I am logged in as "WarehouseWorker"

  # Scenario 1
  @create
  Scenario: Create a new shipment selecting UnitLoads and/or Packages
    Given there is an available UnitLoad with description "UL-1" and loadItem "banana"
    And there is an available Package with description "PK-1"
    When I open the "Create shipment" page
    And I enter from-location "Verona" and to-location "Padova"
    And I enter shipment description "Fragile goods"
    And I select UnitLoad "UL-1"
    And I select Package "PK-1"
    And I submit the shipment creation
    Then I should be redirected to the "Shipments list" page
    And the shipment "Fragile goods" should exist with tracking state "INACTIVE"

  # Scenario 1 (validation case)
  @create @validation
  Scenario: Shipment creation is blocked when no trackable is selected
    Given there is an available UnitLoad with description "UL-1" and loadItem "banana"
    And there is an available Package with description "PK-1"
    When I open the "Create shipment" page
    And I enter shipment description "No trackables"
    And I submit the shipment creation
    Then I should remain on the "Create shipment" page
    And I should see an error message

  # Scenario 4
  @list
  Scenario: View shipments list and open shipment details
    Given there is an existing shipment with description "Shipment A"
    When I open the "Shipments list" page
    Then I should see shipment "Shipment A" in the list
    When I open details for shipment "Shipment A"
    Then I should see the shipment details page for "Shipment A"

  # Scenario 5
  @tracking
  Scenario: Toggle tracking state of an existing shipment
    Given there is an existing shipment with description "Shipment T" and tracking state "INACTIVE"
    When I open details for shipment "Shipment T"
    And I click "Activate tracking"
    Then shipment "Shipment T" tracking state should be "ACTIVE"
    When I click "Deactivate tracking"
    Then shipment "Shipment T" tracking state should be "INACTIVE"
