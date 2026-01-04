## Scenario 1 – Creazione di una nuova spedizione

### Assunzioni iniziali
- L’utente (WarehouseWorker o Manager) è autenticato.
- Il sistema cloud è operativo.

### Flusso normale
1. L’utente accede alla sezione **Gestione spedizioni**.
2. Seleziona **Crea spedizione**.
3. Inserisce:
   - ID spedizione (automatico)
   - descrizione della merce
   - data di inizio
   - specifiche di trasporto
4. Conferma l’operazione.
5. Il sistema salva la spedizione.

### Cosa può andare storto
- Campi obbligatori mancanti → il sistema segnala l’errore.
- ID spedizione duplicato → la creazione viene bloccata.

### Stato finale
- La spedizione è registrata ma il tracking non è ancora attivo.


## Scenario 2 – Associazione di un device a una spedizione

### Assunzioni iniziali
- Esiste una spedizione registrata.
- Esiste almeno un device disponibile.

### Flusso normale
1. L’utente seleziona una spedizione esistente.
2. Seleziona **Associa device**.
3. Sceglie un device dalla lista.
4. Il sistema associa il device alla spedizione.

### Cosa può andare storto
- Il device è già associato a un’altra spedizione attiva.
- Il device è dismesso (*decommissioned*).

### Stato finale
- Il device è associato alla spedizione ma il tracking è disattivo.


## Scenario 3 – Attivazione del tracking di una spedizione

### Assunzioni iniziali
- Una spedizione ha almeno un device associato.
- Il device è acceso e correttamente configurato.

### Flusso normale
1. L’utente seleziona una spedizione.
2. Clicca **Attiva tracking**.
3. Il sistema verifica:
   - lo stato del device
   - la connessione al cloud
4. Il tracking viene attivato.

### Cosa può andare storto
- Il device non è connesso al cloud.
- La configurazione del device non è valida.

### Stato finale
- La spedizione risulta attiva e inizia la raccolta dei dati.


## Scenario 4 – Disattivazione del tracking di una spedizione

### Assunzioni iniziali
- Il tracking della spedizione è attivo.

### Flusso normale
1. L’utente seleziona la spedizione.
2. Seleziona **Disattiva tracking**.
3. Il sistema interrompe la raccolta dei dati.
4. Il device viene marcato come disponibile.

### Cosa può andare storto
- La spedizione non viene trovata.
- Il tracking è già disattivato.

### Stato finale
- La spedizione rimane registrata ma non raccoglie più dati.


## Scenario 5 – Visualizzazione lista spedizioni e stato tracking

### Assunzioni iniziali
- L’utente è autenticato.
- Esistono più spedizioni nel sistema.

### Flusso normale
1. L’utente accede alla sezione **Lista spedizioni**.
2. Il sistema mostra, per ogni spedizione:
   - ID spedizione
   - stato del tracking (attivo / non attivo)
   - device associati
3. L’utente seleziona una spedizione per visualizzarne i dettagli.

### Cosa può andare storto
- Nessuna spedizione presente → il sistema mostra una lista vuota.
- Errore di comunicazione con il backend.

### Stato finale
- L’utente ha una visione aggiornata e completa dello stato delle spedizioni.

## Scenario 6 – Tentativo di associazione di un device già assegnato a una spedizione attiva

### Assunzioni iniziali
- Esiste una spedizione registrata S1 con tracking non attivo.
- Esiste una spedizione S2 con tracking attivo.
- Un device D1 è associato alla spedizione S2.

### Flusso normale
1. L’utente seleziona la spedizione S1.
2. Seleziona **Associa device**.
3. Seleziona il device D1.
4. Il sistema verifica lo stato del device.

### Cosa può andare storto
- Il device è già associato a una spedizione attiva → l’operazione viene bloccata.
- L’utente non ha i permessi necessari → accesso negato.

### Stato finale
- Il device non viene associato alla spedizione S1.
- Il tracking rimane disattivo.


## Scenario 7 – Associazione fallita di un device dismesso (decommissioned)

### Assunzioni iniziali
- Esiste una spedizione registrata.
- Esiste un device con stato decommissioned.

### Flusso normale
1. L’utente seleziona una spedizione.
2. Seleziona **Associa device**.
3. Seleziona il device dismesso.
4. Il sistema verifica lo stato del device.

### Cosa può andare storto
- Il device è dismesso → il sistema segnala che il device non è utilizzabile.
- Il device non è più disponibile al momento della conferma.

### Stato finale
- Nessuna associazione viene creata.
- Lo stato della spedizione non cambia.


## Scenario 8 – Rimozione di un device da una spedizione con tracking disattivo

### Assunzioni iniziali
- Esiste una spedizione con tracking non attivo.
- Alla spedizione è associato un device.

### Flusso normale
1. L’utente seleziona la spedizione.
2. Seleziona **Gestisci device associati**.
3. Seleziona **Rimuovi device**.
4. Conferma l’operazione.
5. Il sistema rimuove l’associazione.

### Cosa può andare storto
- Il tracking è attivo → la rimozione viene bloccata.
- L’associazione non viene trovata.

### Stato finale
- Il device risulta disponibile.
- La spedizione rimane registrata.


## Scenario 9 – Consultazione dei dettagli di una spedizione

### Assunzioni iniziali
- L’utente è autenticato.
- Esiste una spedizione nel sistema.

### Flusso normale
1. L’utente accede alla lista delle spedizioni.
2. Seleziona una spedizione.
3. Il sistema mostra:
   - ID spedizione
   - descrizione merce
   - data di inizio
   - specifiche di trasporto
   - device associati
   - stato del tracking

### Cosa può andare storto
- La spedizione non esiste più → il sistema segnala errore.
- Errore di comunicazione con il backend.

### Stato finale
- L’utente visualizza i dettagli o riceve un errore gestito.


## Scenario 10 – Attivazione del tracking fallita per device non connesso al cloud

### Assunzioni iniziali
- Esiste una spedizione con almeno un device associato.
- Il device è acceso ma non connesso al cloud.

### Flusso normale
1. L’utente seleziona la spedizione.
2. Seleziona **Attiva tracking**.
3. Il sistema verifica la connessione del device.
4. Il sistema blocca l’attivazione.

### Cosa può andare storto
- Stato del device non verificabile → errore temporaneo.
- Tentativi ripetuti → il sistema evita duplicazioni.

### Stato finale
- Il tracking rimane disattivo.
- Nessuna raccolta dati viene avviata.
