Feature: Disattivazione tracking spedizione

  Scenario: Disattivazione del tracking di una spedizione attiva
    Given il tracking della spedizione è attivo
    When l'utente disattiva il tracking della spedizione
    Then il sistema interrompe la raccolta dei dati
    And il device viene marcato come disponibile
