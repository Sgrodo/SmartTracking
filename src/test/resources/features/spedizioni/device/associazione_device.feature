Feature: Associazione device a spedizione

  Scenario: Associazione di un device disponibile a una spedizione
    Given esiste una spedizione registrata
    And esiste almeno un device disponibile
    When l'utente associa un device alla spedizione
    Then il device risulta associato alla spedizione
    And il tracking è ancora disattivo
