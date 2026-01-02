Feature: Associazione device dismesso

  Scenario: Associazione fallita di un device dismesso
    Given esiste una spedizione registrata
    And esiste un device dismesso
    When l'utente tenta di associare il device dismesso alla spedizione
    Then il sistema segnala che il device non è utilizzabile
    And nessuna associazione viene creata
