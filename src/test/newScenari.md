# Scenario 1 – Creazione di una nuova spedizione (Shipment)

## Assunzioni iniziali
- L’utente (WarehouseWorker o Manager) è autenticato.
- Esistono UnitLoad e/o Package disponibili nel sistema.

## Flusso normale
1. L’utente accede alla pagina **Create shipment**.
2. Inserisce la descrizione della spedizione.
3. Seleziona una o più **UnitLoad** e/o uno o più **Package**.
4. Conferma la creazione.
5. Il sistema crea la Shipment con identificativo automatico.

## Cosa può andare storto
- Nessun trackable selezionato → il sistema blocca la creazione e mostra un messaggio di validazione.
- Dati obbligatori mancanti o non validi → errore di validazione.

## Stato finale
- La Shipment è creata con tracking **INACTIVE** e con trackable associati.

# Scenario 2 – Creazione di una UnitLoad con LoadItems di magazzino e vincoli fisici

## Assunzioni iniziali
- L’utente è autenticato.
- Nel sistema sono presenti **LoadItems**, che rappresentano **oggetti/prodotti presenti in magazzino**.

## Flusso normale
1. L’utente accede alla pagina **Create UnitLoad**.
2. Inserisce una descrizione.
3. (Opzionale) Inserisce vincoli fisici:
    - temperatura
    - umidità
4. Seleziona uno o più **LoadItems di magazzino**.
5. Conferma la creazione.
6. Il sistema salva la UnitLoad.

## Cosa può andare storto
- Nessun LoadItem selezionato → errore di validazione.
- Umidità fuori dall’intervallo consentito → errore di validazione.
- Valori di temperatura non validi → errore di validazione.

## Stato finale
- La UnitLoad è creata ed è disponibile per essere inserita in Package o Shipment.

# Scenario 3 – Creazione di un Package selezionando UnitLoad

## Assunzioni iniziali
- L’utente è autenticato.
- Esistono UnitLoad disponibili.

## Flusso normale
1. L’utente accede alla pagina **Create Package**.
2. Inserisce la descrizione del Package.
3. Seleziona una o più **UnitLoad**.
4. Conferma la creazione.
5. Il sistema salva il Package.

## Cosa può andare storto
- Nessuna UnitLoad selezionata → il sistema blocca la creazione e mostra un messaggio di validazione.

## Stato finale
- Il Package è creato e contiene le UnitLoad selezionate.


# Scenario 4 – Visualizzazione lista spedizioni e accesso ai dettagli

## Assunzioni iniziali
- L’utente è autenticato.
- Nel sistema possono essere presenti zero o più Shipment.

## Flusso normale
1. L’utente accede alla pagina **Shipments list**.
2. Il sistema mostra l’elenco delle spedizioni.
3. L’utente seleziona una Shipment.
4. Il sistema mostra i **dettagli della Shipment**.

## Cosa può andare storto
- Nessuna Shipment presente → il sistema mostra una lista vuota con messaggio informativo.
- Shipment non trovata → errore gestito.

## Stato finale
- L’utente visualizza correttamente la lista o i dettagli della Shipment.

# Scenario 5 – Attivazione e disattivazione del tracking di una Shipment

## Assunzioni iniziali
- L’utente è autenticato.
- Esiste una Shipment.

## Flusso normale
1. L’utente accede ai **Shipment details**.
2. Seleziona **Activate tracking** oppure **Deactivate tracking**.
3. Il sistema aggiorna lo stato del tracking.

## Cosa può andare storto
- Nessun caso funzionale previsto; eventuali errori sono di natura tecnica e gestiti dal sistema.

## Stato finale
- Lo stato di tracking della Shipment è aggiornato correttamente.

# Scenario 6 – Binding di un device a un Trackable

## Assunzioni iniziali
- L’utente è autenticato.
- Esiste una **Shipment non terminata** con almeno due Trackable (T1 e T2).
- Esiste un Device D1 inizialmente **available**.

## Flusso normale
1. L’utente accede alla pagina **Trackables & Devices** della Shipment.
2. Nella sezione del Trackable **T1**, seleziona il device **D1** dalla lista dei device disponibili.
3. Conferma l’operazione di **Bind**.
4. Il sistema crea l’associazione tra D1 e T1.
5. La pagina viene aggiornata.
6. La lista dei device disponibili **non include più D1**.

## Cosa può andare storto
- Nessun caso funzionale previsto.

## Stato finale
- D1 è associato a T1 e non è più disponibile per altri Trackable.

# Scenario 7 – Unbinding di un device da un Trackable

## Assunzioni iniziali
- L’utente è autenticato.
- Esiste una **Shipment non terminata**.
- Un Trackable T1 ha un Device D1 associato.
- D1 non è presente nella lista dei device disponibili.

## Flusso normale
1. L’utente accede alla pagina **Trackables & Devices**.
2. Nella sezione del Trackable **T1**, seleziona l’azione **Unbind** sul device D1.
3. Il sistema rimuove l’associazione.
4. La pagina viene aggiornata.
5. La lista dei device disponibili **include nuovamente D1**.

## Cosa può andare storto
- Nessun caso funzionale previsto.

## Stato finale
- D1 non è più associato ad alcun Trackable ed è nuovamente disponibile.
