Feature: Visualizzazione spedizioni

  Scenario: Visualizzazione della lista delle spedizioni con stato tracking
    Given l'utente è autenticato
    And esistono più spedizioni nel sistema
    When l'utente accede alla lista delle spedizioni
    Then il sistema mostra per ogni spedizione:
      | idSpedizione | statoTracking |
      | S-001        | attivo        |
      | S-002        | non attivo    |
    And l'utente può selezionare una spedizione per visualizzarne i dettagli
