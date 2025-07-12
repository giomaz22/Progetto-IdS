package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.application.Platform;
import org.example.controller.StatoTrenoController;
import org.example.grpc.*;

public class TrenicalClient {
    private final ManagedChannel channel;
    private final TrenicalServiceGrpc.TrenicalServiceBlockingStub blockingStub;
    private final TrenoNotifier notifier;

    public TrenicalClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = TrenicalServiceGrpc.newBlockingStub(channel);
        this.notifier = new TrenoNotifier(TrenicalServiceGrpc.newStub(channel));
    }

    // Chiude il canale gRPC in modo sicuro.
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(3, java.util.concurrent.TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
            }
        }
    }

    // loginUtente OK
    public LoginResponse loginUtente(String email, String password) {
        LoginRequest request = LoginRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();

        return blockingStub.login(request);
    }

    // cercaViaggi OK
    public CercaViaggiResponse utenteCercaViaggi(String data, String tipoTreno, String stazionePartenza,
                                                 String stazioneArrivo){
        CercaViaggiRequest request = CercaViaggiRequest.newBuilder()
                .setData(data)
                .setTipoTreno(tipoTreno)
                .setStazionePartenza(stazionePartenza)
                .setStazioneArrivo(stazioneArrivo)
                .build();

        return blockingStub.cercaViaggi(request);
    }

    // registraUtente OK
    public RegistraResponse utenteRegistrami(UtenteDTO utente) {
        // Validazione semplice
        if (utente.getEmail().isEmpty() || utente.getPassword().isEmpty() ||
                utente.getNome().isEmpty() || utente.getCognome().isEmpty() ||
                utente.getCodiceFiscale().isEmpty() || utente.getDataDiNascita().isEmpty()) {
            return RegistraResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessage("Dati obbligatori mancanti")
                    .build();
        }

        RegistraRequest request = RegistraRequest.newBuilder()
                .setUtenteDaRegistrare(utente)
                .build();

        return blockingStub.registraUtente(request);
    }

    // sottoscrivi fedeltà utente OK
    public FedeltaResponse fedeltaUtente(String cfUtente) {
        FedeltaRequest request = FedeltaRequest.newBuilder()
                .setCodFiscale(cfUtente)
                .build();

        return blockingStub.sottoscriviFedelta(request);
    }

    public PrenotaResponse prenotaViaggioUtente(int idViaggio, String cfUtente) {
        try {
            PrenotaRequest request = PrenotaRequest.newBuilder()
                    .setViaggioID(idViaggio)
                    .setCfUtente(cfUtente)
                    .build();

            PrenotaResponse response = blockingStub.prenotaViaggio(request);

            if (!response.getSuccesso()) {
                System.out.println("Prenotazione fallita: " + response.getMessaggio());
                return response;
            }


            StatusViaggioTRequest statoRequest = StatusViaggioTRequest.newBuilder()
                    .setIdViaggio(idViaggio)
                    .build();

            StatusViaggioTResponse statoResponse = blockingStub.statusAttualeViaggioTren(statoRequest);
            String idTreno = statoResponse.getIdTreno();
            String statoIniziale = statoResponse.getMessage();


            Platform.runLater(() -> StatoTrenoController.mostraConMonitoraggio(idTreno, statoIniziale));

            return response;

        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
            return PrenotaResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggio("Errore interno durante la prenotazione")
                    .build();
        }
    }


    // acquista biglietto OK
    public AcquistaBigliettoResponse acquistaBigliettoUtente(int idViaggio, String cfUtente, String numCarta, String PNR) {
        try {
            AcquistaBigliettoRequest request = AcquistaBigliettoRequest.newBuilder()
                    .setViaggioID(idViaggio)
                    .setCfUtente(cfUtente)
                    .setNumCarta(numCarta)
                    .setPNR(PNR == null ? "" : PNR)  // potrebbe non esistere la prenotazione
                    .build();
            AcquistaBigliettoResponse response = blockingStub.acquistaBiglietto(request);

            if (!response.getSuccesso()) {
                System.out.println("Acquisto fallito: " + response.getMessaggioConferma());
                return response;
            }

            StatusViaggioTRequest statoRequest = StatusViaggioTRequest.newBuilder()
                    .setIdViaggio(idViaggio)
                    .build();

            StatusViaggioTResponse statoResponse = blockingStub.statusAttualeViaggioTren(statoRequest);
            String idTreno = statoResponse.getIdTreno();
            String statoIniziale = statoResponse.getMessage();


            Platform.runLater(() -> StatoTrenoController.mostraConMonitoraggio(idTreno, statoIniziale));

            return response;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return AcquistaBigliettoResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggioConferma("Errore interno durante l'acquisto.")
                    .build();
        }

    }

    // richiedi promozioni attive OK
    public PromoAttiveResponse richiediPromoAttive(boolean fedelta, String tipoTreno, String dataPartenza) {
        PromoAttiveRequest request = PromoAttiveRequest.newBuilder()
                .setHaiFedelta(fedelta)
                .setTipoTreno(tipoTreno)
                .setQuandoParti(dataPartenza)
                .build();

        return blockingStub.promozioniAttive(request);
    }

    // modifica biglietto OK
    public ModificaBigliettoResponse modificaBigliettoAcq(BigliettoDTO biglietto,
                                                          String nuovaData, String nuovaOraPartenza, String nuovaClasseTreno) {
        ModificaBigliettoRequest request = ModificaBigliettoRequest.newBuilder()
                .setBigliettoGiaAcquistato(biglietto)
                .setNuovaData(nuovaData)
                .setNuovaClasse(nuovaClasseTreno)
                .setNuovaOraPartenza(nuovaOraPartenza)
                .build();

        return blockingStub.modificaBiglietto(request);
    }

    // messaggio per coloro che possiedono la carta fedeltà se richiesto OK
    public AccettiNotificaFedResponse notificaFedelta (FedeltaDTO cartaFed, boolean contattami) {
        AccettiNotificaFedRequest request = AccettiNotificaFedRequest.newBuilder()
                .setLaTuaCarta(cartaFed)
                .setDesideroContatto(contattami)
                .build();

        return blockingStub.fedeltaNotificaPromoOfferte(request);
    }

    public StatusViaggioTResponse statusAttualeViaggio(int idViaggio) {
        StatusViaggioTRequest req = StatusViaggioTRequest.newBuilder()
                .setIdViaggio(idViaggio)
                .build();

        return blockingStub.statusAttualeViaggioTren(req);
    }

    public TrenoNotifier getNotifier() {
        return this.notifier;
    }

}
