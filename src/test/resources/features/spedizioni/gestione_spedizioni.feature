Feature: Creazione spedizione
  La creazione di una spedizione consente al sistema di registrare
  una nuova spedizione con le relative specifiche di trasporto.
  Il tracking non viene attivato automaticamente alla creazione.

  Scenario: Creazione di una nuova spedizione valida
    Given l'utente è autenticato
    And il sistema cloud è operativo
    When l'utente crea una spedizione con:
      | idSpedizione | descrizioneMerce | dataInizio   | specificheTrasporto |
      | S-001        | Merce fragile    | 2025-01-10   | T:2-8C; U:30-60%    |
    Then la spedizione viene salvata
    And il tracking della spedizione è disattivo

  Scenario: Creazione spedizione con campi obbligatori mancanti
    Given l'utente è autenticato
    And il sistema cloud è operativo
    When l'utente crea una spedizione con:
      | idSpedizione | descrizioneMerce | dataInizio | specificheTrasporto |
      |              | Merce fragile    | 2025-01-10 | T:2-8C              |
    Then il sistema segnala un errore di validazione
    And la spedizione non viene salvata

  Scenario: Creazione spedizione con ID duplicato
    Given l'utente è autenticato
    And il sistema cloud è operativo
    And esiste già una spedizione con ID "S-001"
    When l'utente crea una spedizione con:
      | idSpedizione | descrizioneMerce | dataInizio   | specificheTrasporto |
      | S-001        | Merce fragile    | 2025-01-10   | T:2-8C; U:30-60%    |
    Then la creazione della spedizione viene bloccata
    And il sistema segnala un errore di ID duplicato
