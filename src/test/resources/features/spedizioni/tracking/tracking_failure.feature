Feature: Attivazione tracking fallita per device non connesso

  Scenario: Attivazione del tracking fallita per device non connesso al cloud
    Given esiste una spedizione con almeno un device associato
    And il device è acceso ma non connesso al cloud
    When l'utente tenta di attivare il tracking
    Then il sistema blocca l'attivazione del tracking
    And il tracking rimane disattivo
