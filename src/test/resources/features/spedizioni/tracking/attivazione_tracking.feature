Feature: Attivazione tracking spedizione

  Scenario: Attivazione del tracking con device correttamente configurato
    Given esiste una spedizione con un device associato
    And il device è acceso
    And il device è connesso al cloud
    When l'utente attiva il tracking della spedizione
    Then il tracking viene attivato
    And il sistema inizia la raccolta dei dati
