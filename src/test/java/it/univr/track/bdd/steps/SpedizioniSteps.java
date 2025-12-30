package it.univr.track.bdd.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SpedizioniSteps {

    // --- Precondizioni comuni ---
    @Given("l'utente è autenticato")
    public void utenteAutenticato() { }

    @Given("il sistema cloud è operativo")
    public void cloudOperativo() { }

    @Given("esiste una spedizione registrata")
    public void esisteSpedizioneRegistrata() { }

    @Given("esiste almeno un device disponibile")
    public void esisteDeviceDisponibile() { }

    @Given("esiste una spedizione con un device associato")
    public void esisteSpedizioneConDeviceAssociato() { }

    @Given("il device è acceso")
    public void deviceAcceso() { }

    @Given("il device è connesso al cloud")
    public void deviceConnessoCloud() { }

    @Given("il tracking della spedizione è attivo")
    public void trackingSpedizioneAttivo() { }

    @Given("esistono più spedizioni nel sistema")
    public void esistonoPiuSpedizioni() { }

    @Given("esiste già una spedizione con ID {string}")
    public void esisteGiaSpedizioneConId(String id) { }

    // --- Azioni ---
    @When("l'utente crea una spedizione con:")
    public void utenteCreaSpedizioneCon(DataTable table) { }

    @When("l'utente associa un device alla spedizione")
    public void utenteAssociaDeviceAllaSpedizione() { }

    @When("l'utente attiva il tracking della spedizione")
    public void utenteAttivaTracking() { }

    @When("l'utente disattiva il tracking della spedizione")
    public void utenteDisattivaTracking() { }

    @When("l'utente accede alla lista delle spedizioni")
    public void utenteAccedeListaSpedizioni() { }

    // --- Verifiche ---
    @Then("la spedizione viene salvata")
    public void spedizioneSalvata() { }

    @Then("il tracking della spedizione è disattivo")
    public void trackingDisattivo() { }

    @Then("il sistema segnala un errore di validazione")
    public void erroreValidazione() { }

    @Then("la spedizione non viene salvata")
    public void spedizioneNonSalvata() { }

    @Then("la creazione della spedizione viene bloccata")
    public void creazioneBloccata() { }

    @Then("il sistema segnala un errore di ID duplicato")
    public void erroreIdDuplicato() { }

    @Then("il tracking viene attivato")
    public void trackingAttivato() { }

    @Then("il sistema inizia la raccolta dei dati")
    public void raccoltaDatiIniziata() { }

    @Then("il sistema interrompe la raccolta dei dati")
    public void raccoltaDatiInterrotta() { }

    @Then("il device viene marcato come disponibile")
    public void deviceMarcatoDisponibile() { }

    @Then("il device risulta associato alla spedizione")
    public void deviceAssociato() { }

    @Then("il tracking è ancora disattivo")
    public void trackingAncoraDisattivo() { }

    @Then("il sistema mostra per ogni spedizione:")
    public void sistemaMostraPerOgniSpedizione(DataTable table) { }

    @Then("l'utente può selezionare una spedizione per visualizzarne i dettagli")
    public void utentePuoSelezionareDettagli() { }
}
