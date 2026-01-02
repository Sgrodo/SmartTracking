Feature: Consultazione dettagli spedizione

  Scenario: Consultazione dei dettagli di una spedizione
    Given l'utente è autenticato
    And esiste una spedizione nel sistema
    When l'utente seleziona la spedizione
    Then il sistema mostra i dettagli della spedizione
    And lo stato del tracking è visibile
