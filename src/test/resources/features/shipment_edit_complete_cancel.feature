@web @coverage @shipments
Feature: Shipment edit, complete and cancel coverage
  These scenarios are meant to increase controller coverage using Selenium PageObjects.

  Background:
    Given I am logged in as "WarehouseWorker"

  # Covers: GET /web/shipments  -> redirect /web/shipments/list
  Scenario: Redirect from /shipments to shipments list
    When I open the "Shipments" page
    Then I should be redirected to the "Shipments list" page

  # Covers: GET /web/shipments/{id}/edit  and helper prepareShipmentEditModel
  Scenario: Open edit page for an active shipment
    Given there is a non-terminated shipment with description "S-EDIT-1"
    And shipment "S-EDIT-1" has trackable "T1"
    When I open the "Shipments list" page
    And I open edit for shipment "S-EDIT-1"
    Then I should be on the "Edit shipment" page

  # Covers: POST /web/shipments/{id}/edit success branch
  Scenario: Edit shipment succeeds when locations and trackables are valid
    Given there is a non-terminated shipment with description "S-EDIT-OK"
    And shipment "S-EDIT-OK" has trackable "T1"
    When I open the "Shipments list" page
    And I open edit for shipment "S-EDIT-OK"
    And I set fromLocation to "Verona"
    And I set toLocation to "Milano"
    And I select trackable "T1" in edit form
    And I submit shipment edit
    Then I should be redirected to the "Shipments list" page

  # Covers: POST /web/shipments/{id}/edit catch branch (invalid locations and/or empty trackables)
  Scenario: Edit shipment fails and shows error when trackables are missing
    Given there is a non-terminated shipment with description "S-EDIT-ERR"
    And shipment "S-EDIT-ERR" has trackable "T1"
    When I open the "Shipments list" page
    And I open edit for shipment "S-EDIT-ERR"
    And I set fromLocation to ""
    And I set toLocation to ""
    And I unselect all trackables in edit form
    And I submit shipment edit
    Then I should remain on the "Edit shipment" page
    And I should see an error message

  # Covers: GET /web/shipments/{id}/complete
  Scenario: Complete an active shipment from list
    Given there is a non-terminated shipment with description "S-COMPLETE-1"
    When I open the "Shipments list" page
    And I click complete for shipment "S-COMPLETE-1"
    Then I should be redirected to the "Shipments list" page

  # Covers: GET /web/shipments/{id}/cancel
  Scenario: Cancel an active shipment from list
    Given there is a non-terminated shipment with description "S-CANCEL-1"
    When I open the "Shipments list" page
    And I click cancel for shipment "S-CANCEL-1"
    Then I should be redirected to the "Shipments list" page
