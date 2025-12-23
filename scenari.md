## Scenario 1 – Creazione di una nuova spedizione

### Assunzioni iniziali
- L’utente (WarehouseWorker o Manager) è autenticato.
- Il sistema cloud è operativo.

### Flusso normale
1. L’utente accede alla sezione **Gestione spedizioni**.
2. Seleziona **Crea spedizione**.
3. Inserisce:
   - ID spedizione
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
