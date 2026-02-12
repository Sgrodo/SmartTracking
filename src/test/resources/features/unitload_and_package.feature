@web @unitload @package
Feature: UnitLoad and Package creation
  Users can create UnitLoads from warehouse LoadItems and create Packages from UnitLoads.

  Background:
    Given I am logged in as "WarehouseWorker"

  # Scenario 2
  @unit_load @create
  Scenario: Create a UnitLoad using warehouse LoadItems and optional physical constraints
    Given there is a warehouse LoadItem with name "Item-A"
    Given there is a device with origin "Italy"
    And there is a warehouse LoadItem with name "Item-B"
    When I open the "Create UnitLoad" page
    And I enter unitload description "UL created from warehouse"
    And I set humidity constraint to 55.0
    And I select LoadItem "Item-A"
    And I select LoadItem "Item-B"
    And I select Device with origin "Italy"
    And I submit the unitload creation
    Then a UnitLoad "UL created from warehouse" should exist

  # Scenario 2 (validation case)
  @unit_load @validation
  Scenario: UnitLoad creation is blocked when no LoadItem is selected
    Given there is a warehouse LoadItem with name "Item-A"
    When I open the "Create UnitLoad" page
    And I enter unitload description "UL without items"
    And I submit the unitload creation
    Then I should remain on the "Create UnitLoad" page
    And I should see an error message

  # Scenario 3
  @package @create
  Scenario: Create a Package selecting one or more UnitLoads
    Given  there is an available UnitLoad with description "UL-1" and loadItem "banana"
    And  there is an available UnitLoad with description "UL-2" and loadItem "banana"
    When I open the "Create Package" page
    And I enter package description "PK new"
    And I select UnitLoad "UL-1" for package
    And I select UnitLoad "UL-2" for package
    And I submit the package creation
    Then a Package "PK new" should exist containing UnitLoads "UL-1" and "UL-2"

  # Scenario 3 (validation case)
  @package @validation
  Scenario: Package creation is blocked when no UnitLoad is selected
    Given  there is an available UnitLoad with description "UL-1" and loadItem "banana"
    When I open the "Create Package" page
    And I enter package description "PK invalid"
    And I submit the package creation
    Then I should remain on the "Create Package" page
    And I should see an error message on Create Package page
