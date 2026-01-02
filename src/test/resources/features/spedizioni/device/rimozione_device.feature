Feature: Rimozione device da spedizione

  Scenario: Rimozione di un device da una spedizione con tracking disattivo
    Given esiste una spedizione con tracking disattivo
    And la spedizione ha un device associato
    When l'utente rimuove il device dalla spedizione
    Then il device risulta disponibile
    And la spedizione rimane registrata
