# Autori
- **Mattia Cappelletti** (VR543935)
- **Matteo Baldi** (VR536965)

# Modulo Spedizioni

## Assunzioni
- Il modulo non si occupa di gestire l'autorizzazione degli utenti. Si assume che l'utente sia autorizzato all'accesso del modulo.
- Si assume che la gestione dei prodotti sia gestita da altri moduli del sistema, e che le informazioni necessarie siano disponibili per l'associazione con le spedizioni.

## Requisiti

### Modelli di dominio
- Una spedizione dovrebbe avere i seguenti attributi:
    - ID univoco (intero incrementale)
    - Descrizione
    - Luogo di partenza
    - Luogo di destinazione
    - Data di creazione
    - Data di fine
    - Stato (creato, in transito, consegnato, annullato)
    - Lista dei device associati
    - Lista dei tracciabili associati (unità di carico e pacchetti)
    - Informazioni sul tracciamento (se attivo)

- Un tracciabile è un'astrazione che rappresenta le unità di carico e i pacchetti:
    - Attributi comuni: 
        - ID univoco
        - Descrizione
        - Device associati
    - Unità di carico:
        - Prodotto contenuto
        - Vincoli fisici (temperatura, umidità)
    - Pacchetto:
        - Lista di unità di carico contenute

### Requisiti funzionali
* **[RF-01] Creazione di una spedizione**: L'utente deve poter creare una nuova spedizione inserendo i dettagli richiesti. Il sistema deve generare un ID univoco per ogni spedizione creata. **Deve** contenere almeno un tracciabile con un device associato per essere considerata valida.
* **[RF-02] Creazione di una unità di carico**: L'utente deve poter creare una nuova unità di carico inserendo le informazioni necessarie per il prodotto specifico.
* **[RF-03] Definizione dei vincoli fisici**: L'utente deve poter definire le soglie massime critiche per una unità di carico, come temperatura massima e umidità massima, se necessario.
* **[RF-04] Composizione di un pacchetto**: L'utente deve poter creare un pacchetto associando una o più unità di carico. Il sistema deve consentire di visualizzare le unità di carico disponibili e selezionarle per la composizione del pacchetto.
* **[RF-05] Visualizzazione dei dettagli della spedizione**: L'utente deve poter visualizzare i dettagli di una spedizione specifica, inclusi i device e i tracciabili associati.
* **[RF-06] Aggiornamento delle informazioni della spedizione**: L'utente deve poter aggiornare le informazioni di una spedizione, come la descrizione, i luoghi di partenza e destinazione, le date e poter cambiarne lo stato, ovvero cancellare, completare o impostarla come in transito.
* **[RF-07] Completamento di una spedizione**: L'utente deve poter completare una spedizione, aggiornando il suo stato a "Completato". Il sistema deve registrare la data di fine della spedizione al momento del completamento e deve deassociare ogni device della spedizione.
* **[RF-08] Cancellazione di una spedizione**: L'utente deve poter cancellare una spedizione, aggiornando il suo stato a "Annullato". Il sistema deve deassociare ogni tracciabile della spedizione.
* **[RF-9] Attivazione o disattivazione del tracciamento**: L'utente deve poter attivare o disattivare il tracciamento di una spedizione. Lo stato del tracciamento **deve** essere associato al singolo device.
* **[RF-10] Associazione e deassociazione di un device**: L'utente deve poter associare o deassociare un device ad uno specifico tracciabile di una spedizione.

### Requisiti di dominio
* **[RD-01] Validazione spedizione**: Una spedizione è considerata valida solo se contiene almeno un tracciabile con un device associato. Il sistema deve impedire la creazione o l'aggiornamento di una spedizione che non soddisfa questo requisito.
* **[RD-02] Vincoli di stato della spedizione**: Le operazioni di Associazione e Deassociazione dei device sono consentite solo se lo stato di una Shipment è Creato o In Transito.
* **[RD-03] Vincoli di integrità fisica**: Il sistema deve validare i dati relativi a umidità e temperatura nei vincoli fisici delle unità di carico, assicurandosi che siano all'interno degli intervalli consentiti.
* **[RD-04] Esclusività del Device**: Un device può essere associato a un solo tracciabile alla volta.
* **[RD-05] Esclusività del Tracciabile**: Un tracciabile può essere associato a una sola spedizione alla volta.
* **[RD-06] Stato di una spedizione**: Lo stato di una spedizione può essere solo uno tra "Creato", "In Transito", "Completato" o "Annullato". Inoltre da stato Completato o Annullato non è possibile effettuare ulteriori transizioni di stato.
* **[RD-07] Vincoli dello stato di tracciamento**: Il tracciamento di una spedizione può essere modificato solo se la spedizione è in stato "In Transito" o "Creato".
* **[RD-08] Vincoli dei tracciabili sulla spedizione**: Una spedizione non può avere tracciabili appartenenti a spedizioni differenti. Inoltre le unità di carico appartenenti a un pacchetto non possono esistere al di fuori di esso e possono appartenente a zero o un singolo pacchetto contemporaneamente.
* **[RD-09] Vincoli di creazione delle uunita di carico**: Un'unità di carico alla creazione deve avere almeno un prodotto e un device associato.

### Requisiti non funzionali
* **[RNF-01] Usabilità**: Il sistema deve essere intuitivo e facile da usare, con un'interfaccia utente chiara e accessibile.
* **[RNF-02] Affidabilità**: Il sistema deve gestire le eccezioni notificando l'utente e senza interruzioni del servizio.
* **[RNF-03] Performance**: Il sistema deve rispondere rapidamente alle azioni dell'utente, con tempi di caricamento al di sotto dei 2 secondi.


## Scenari (Disponibili nel seguente file: [src/test/scenari.md](./src/test/Scenari.md))
Gli scenari funzionali del sistema sono stati formalizzati e successivamente implementati tramite il framework Cucumber, con l’obiettivo di trasformare i requisiti identificati in fase di analisi in acceptance test automatizzati e verificabili.

Gli scenari coprono i principali processi del modulo di gestione logistica, in particolare:
 - creazione e gestione di Shipment,
 - creazione e composizione di UnitLoad e Package,
 - visualizzazione delle spedizioni,
 - attivazione e disattivazione del tracking,
 - associazione e dissociazione (binding/unbinding) dei device ai Trackable.

Ogni scenario è stato strutturato secondo uno schema coerente composto da:
 - Assunzioni iniziali (precondizioni): stato del sistema prima dell’esecuzione.
 - Flusso normale: sequenza principale di interazioni utente-sistema.
 - Casi di errore o alternativi: condizioni che impediscono il completamento corretto dell’’operazione.
 - Stato finale (postcondizioni): stato consistente del sistema al termine dell’esecuzione.

Gli scenari includono sia casi funzionali positivi (creazione, associazione, attivazione), sia casi di validazione (campi mancanti, valori non validi, selezioni obbligatorie assenti), garantendo così copertura dei vincoli applicativi e delle regole di dominio.
L’integrazione con Cucumber ha permesso di:
- mantenere coerenza tra requisiti, scenari e comportamento implementato
- garantire tracciabilità tra requisito funzionale e test di accettazione
- validare automaticamente il rispetto delle regole di business, ridurre ambiguità tra specifica e implementazione.
  In questo modo, gli scenari non rappresentano solo documentazione descrittiva, ma costituiscono una specifica eseguibile del comportamento atteso del sistema.


## Quality Assurance
### Test Selection
Per garantire la qualità del modulo, la selezione è stata guidata dai seguenti criteri:
1. **Copertura dei Requisiti di Dominio (RD):** Priorità alta ai test che verificano i vincoli di sistema (es. validazione sensori e disponibilità device).
2. **Analisi dei Valori Limite:** Selezione di casi di test con dati ai confini degli intervalli consentiti (es. umidità al 0% e 100%).
3. **Robustezza:** Inclusione di scenari negativi per verificare la resilienza del sistema in caso di dati mancanti o stati non validi.

| ID Test | Scenario di Riferimento | Requisito Verificato | Descrizione Caso di Test                                                                                | Esito Atteso (Criterio di Accettazione)                                                                         |
|---------|-------------------------|----------------------|---------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| TC-01.0 | Scenario 1              | [RF-01], [RD-01]     | Creazione di una spedizione senza selezionare alcun tracciabile o device.                               | Il sistema blocca l'operazione e mostra un messaggio di errore.                                                 |
| TC-01.1 | Scenario 1              | [RF-01], [RD-05]     | Creazione di una spedizione con un tracciabile presente in una spedizione differente.                   | Il sistema impedisce l'operazione nascondendo i tracciabili invalidanti durante l'operazione di creazione.      |
| TC-01.2 | Scenario 1              | [RF-01], [RD-08]     | Creazione di una spedizione con un tracciabile presente in un pacchetto ma senza il pacchetto stesso.   | Il sistema impedisce l'operazione nascondendo le unità di carico invalidanti durante l'operazione di creazione. |
| TC-02.0 | Scenario 2              | [RF-02], [RD-09]     | Creazione di un'unità di carico senza prodotto o senza device associato.                                | Il sistema blocca l'operazione e mostra un messaggio di errore.                                                 |
| TC-02.1 | Scenario 2              | [RF-03], [RD-03]     | Inserimento di umidità > 100% o temperature non valide in una UnitLoad.                                 | Il sistema invalida i dati e impedisce il salvataggio della UnitLoad.                                           |
| TC-03   | Scenario 3              | [RF-04], [RD-08]     | Creazione di un Package con una unità di carico appartenente as un altro pacchetto.                     | Il sistema impedisce l'operazione mostrando solo unità di carico non associate ad alcune pacchetto.             |
| TC-04   | Scenario 4              | [RF-05], [RNF-02]    | Tentativo di accesso ai dettagli di una Shipment con ID inesistente.                                    | Il sistema gestisce l'eccezione mostrando un feedback appropriato (404).                                        |
| TC-05   | Scenario 5              | [RF-06], [RD-07]     | Tentativo di modifica dello stato di tracciamento di una spedizione in stato "Completato" o "Annullato" | Il sistema impedisce la modifica di una spedizione in stao "Completato" o Annullato".                           |
| TC-06   | Scenario 6              | [RF-10], [RD-04]     | Tentativo di associazione (Bind) di un device D1 già occupato.                                          | Il sistema nega il binding, notificando che il device non è disponibile.                                        |
| TC-07   | Scenario 7              | [RF-10], [RD-02]     | Tentativo di dissociazione (Unbind) da una Shipment in stato ""Completato"".                            | Operazione negata: i device di spedizioni terminate sono bloccati.                                              |
| TC-08   | -                       | [RF-07], [RD-06]     | Completamento di una spedizione: verifica cambio stato e deassociazione automatica device.              | Stato aggiornato a "Completato", data di fine registrata e device tornano disponibili.                          |
| TC-09   | -                       | [RF-08], [RD-06]     | Cancellazione di una spedzione: verifica cambio di stato e deassociazione dei tracciabili.              | Stato aggiornato a "Annullato", data di fine registrata e tracciabili tornano disponibili.                      |


### Dati di Test Utilizzati
Per ogni test case sono stati definiti set di dati specifici:
* **Dati Validi:** Stringhe alfanumeriche standard, ID esistenti, valori entro i range.
* **Dati Non Validi:** Valori nulli, ID inesistenti, caratteri speciali non ammessi, JSON invalidi.
* **Dati di Confine:** Valori minimi e massimi (es. Temperature estreme definite nei vincoli).

## Test Coverage (Unit Testing)
La valutazione dell'efficacia dei test è stata analizzata solo per i seguenti sottomoduli relativi all'implementazione del modulo delle spedizioni:
- Service
- Entity
- Repository
- Controller (ShipmentController, UnitLoadController, PackageController, ShipmentWebController)

### Metriche di copertura
Per valutare l'efficacia dei test, sono state utilizzate le seguenti metriche di copertura:
#### RCI (Requirements Coverage Index)
Indica la capacità dei test di validare i requisiti idenitficati nella sezione [Requisiti](#requisiti).
  * **Copertura dei Requisiti di Dominio (RD):** 100%.
  * **Copertura dei Requisiti Funzionali (RF):** >95%.
#### Code Coverage
Rappresenta l'analisi quantitativa del codice sorgente effettivamente eseguito durante i test.
  * **Branch Coverage:** >80%.
  * **Line Coverage:** >90%.
  * **Method Coverage:** >80%.
