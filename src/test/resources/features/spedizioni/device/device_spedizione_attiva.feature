Feature: Associazione device già assegnato a spedizione attiva

  Scenario: Tentativo di associazione di un device già assegnato a una spedizione attiva
    Given esiste una spedizione registrata S1 con tracking disattivo
    And esiste una spedizione S2 con tracking attivo
    And esiste un device D1 associato alla spedizione S2
    When l'utente tenta di associare il device D1 alla spedizione S1
    Then il sistema blocca l'associazione
    And il tracking della spedizione S1 rimane disattivo
