package org.example.server;

import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import org.example.grpc.*;
import org.example.model.*;
import org.example.servizi.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.util.CodiceCartaFedelta.generaCodice;

public class TrenicalServiceImpl extends TrenicalServiceGrpc.TrenicalServiceImplBase {
    private ViaggioService viaggioService = new ViaggioService();
    private BigliettiService bigliettiService = new BigliettiService();
    private ClienteService clienteService = new ClienteService();
    private FedeltaService fedeltaService = new FedeltaService();
    private PrenotazioneService prenotazioneService = new PrenotazioneService();
    private PromozioneService promozioneService = new PromozioneService();
    private TrenoService trenoService = new TrenoService();

    @Override //COMPLETATO
    public void cercaViaggi(CercaViaggiRequest request, StreamObserver<CercaViaggiResponse> responseObserver) {
        // Estrai i dati della richiesta
        String stazionePartenza = request.getStazionePartenza();
        String stazioneArrivo = request.getStazioneArrivo();
        String data = request.getData();
        String tipoTreno = request.getTipoTreno();

        List<Viaggio> viaggiRet = viaggioService.ricercaViaggi(stazionePartenza, stazioneArrivo, data, tipoTreno);
        List<ViaggioDTO> viaggioDTOret = new ArrayList<>();

        for(Viaggio v : viaggiRet) {
            ViaggioDTO viaggioNuovo = ViaggioDTO.newBuilder()
                    .setIdViaggio(v.getIDViaggio())
                    .setData(v.getData())
                    .setPrezzo(v.getPrezzo())
                    .setOraPartenza(v.getOraPartenza())
                    .setOraArrivo(v.getOraArrivo())
                    .setStazionePartenza(v.getStazionePartenza())
                    .setStazioneArrivo(v.getStazioneArrivo())
                    .setClassiDisponibili(v.getClassiDisponibili())
                    .setNumeroPostiDisponibili(v.getNumPostiDisponibili())
                    .build();
            viaggioDTOret.add(viaggioNuovo);
        }

        // Costruisci la risposta
        CercaViaggiResponse response = CercaViaggiResponse.newBuilder()
                .addAllViaggi(viaggioDTOret)
                .build();

        // Invia la risposta al client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override //COMPLETATO
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        String email = request.getEmail();
        String password = request.getPassword();

        boolean autenticatoNorm = clienteService.loginUtente(email, password) != null;
        boolean autenticatoAdmin = clienteService.loginAdmin(email, password) != null;

        if (autenticatoNorm) {
            Utente trovato = clienteService.findUtenteByEmail(email);
            UtenteDTO utente = UtenteDTO.newBuilder()
                    .setNome(trovato.getNome())
                    .setCognome(trovato.getCognome())
                    .setCodiceFiscale(trovato.getCodiceFiscale())
                    .setDataDiNascita(trovato.getDataNascita())
                    .build();

            Fedelta fedUt = fedeltaService.findFedeltaByCF(utente.getCodiceFiscale());
            FedeltaDTO fedelta = FedeltaDTO.newBuilder()
                    .setIDcarta(fedUt.getID())
                    .setCFpossess(fedUt.getCFpossessore())
                    .setPuntiFed(fedUt.getPuntiCarta())
                    .build();

            LoginResponse response = LoginResponse.newBuilder()
                    .setLoginUt(utente)
                    .setLoginFed(fedelta)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } else if (autenticatoAdmin) {
            Utente admin = clienteService.findUtenteByEmail(email);
            UtenteDTO amministratore = UtenteDTO.newBuilder()
                    .setNome(admin.getNome())
                    .setCognome(admin.getCognome())
                    .setCodiceFiscale(admin.getCodiceFiscale())
                    .setDataDiNascita(admin.getDataNascita())
                    .build();

            LoginResponse response = LoginResponse.newBuilder()
                    .setLoginUt(amministratore)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }

    }

    @Override //COMPLETATO
    public void registraUtente(RegistraRequest request, StreamObserver<RegistraResponse> responseObserver){
        String codFiscale = request.getUtenteDaRegistrare().getCodiceFiscale();
        String nome = request.getUtenteDaRegistrare().getNome();
        String cognome = request.getUtenteDaRegistrare().getCognome();
        String dataNascita = request.getUtenteDaRegistrare().getDataDiNascita();
        String password = request.getUtenteDaRegistrare().getPassword();
        String email = request.getUtenteDaRegistrare().getEmail();
        boolean admin = request.getUtenteDaRegistrare().getAdmin();

        boolean giaPresenteCf = clienteService.findUtenteByCf(codFiscale) != null;
        boolean giaPresenteEmail = clienteService.findUtenteByEmail(email) != null;

        if(!giaPresenteCf || !giaPresenteEmail){
            Utente daRegistrare = new Utente(nome, cognome, codFiscale, dataNascita, password, email, admin);
            clienteService.addCliente(daRegistrare);

            RegistraResponse response = RegistraResponse.newBuilder()
                    .setSuccesso(true)
                    .setMessage("Registrazione andata a buon fine")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } else {
            RegistraResponse response = RegistraResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessage("Utente già presente nel database")
                    .build();
        }
    }

    @Override //COMPLETATO
    public void sottoscriviFedelta(FedeltaRequest request, StreamObserver<FedeltaResponse> responseObserver) {
        String codFiscale = request.getCodFiscale();

        boolean giaFedele = fedeltaService.findFedeltaByCF(codFiscale) != null;

        if(!giaFedele){
            String IDcarta = generaCodice();
            int puntiNuovaIscrizione = 20; // ai nuovi iscritti diamo dei punti bonus

            Utente daRegistrare = clienteService.findUtenteByCf(codFiscale);
            Fedelta cartaDaGenerare = new Fedelta(IDcarta, codFiscale, puntiNuovaIscrizione);

            fedeltaService.addNewFidelityCard(daRegistrare, cartaDaGenerare);

            FedeltaDTO fed = FedeltaDTO.newBuilder()
                    .setCFpossess(codFiscale)
                    .setIDcarta(cartaDaGenerare.getID())
                    .setPuntiFed(cartaDaGenerare.getPuntiCarta())
                    .build();

            FedeltaResponse response = FedeltaResponse.newBuilder()
                    .setSuccesso(true)
                    .setMessaggioConfermaFedelta("CF: " + codFiscale + " è iscritto al programma fedeltà")
                    .setDettagliCarta(fed)
                    .build();
        }else{
            Fedelta cartaEsistente = fedeltaService.findFedeltaByCF(codFiscale);
            FedeltaDTO fed = FedeltaDTO.newBuilder()
                    .setCFpossess(cartaEsistente.getCFpossessore())
                    .setIDcarta(cartaEsistente.getID())
                    .setPuntiFed(cartaEsistente.getPuntiCarta())
                    .build();

            FedeltaResponse response = FedeltaResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggioConfermaFedelta("Utente già iscritto al programma fedeltà")
                    .setDettagliCarta(fed)
                    .build();
        }
    }

    @SneakyThrows //COMPLETATO
    @Override
    public void prenotaViaggio(PrenotaRequest request, StreamObserver<PrenotaResponse> responseObserver) {
        int idViaggio = request.getViaggioID();
        String cfUt = request.getCfUtente();
        /*
        Supponiamo che nella lista di viaggi che il server ha mandato al cliente,
        questi ne abbia scelto uno e ora vuole prenotarlo.

        Poichè l'utente cerca i viaggi in base ad altri parametri, assumiamo che il
        posto e il numero di carrozza siano random.
         */
        Viaggio v = viaggioService.findViaggioById(idViaggio);

        String PNR = "PNR" + System.currentTimeMillis(); // numero prenotazione reso univoco

        LocalDateTime dataConvertita = LocalDateTime.parse(v.getData());
        LocalDateTime dataScadenzaLDT = dataConvertita.minusHours(12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String dataScadenza = dataScadenzaLDT.format(formatter);

        int numCarrozza = new Random().nextInt(6) + 1;
        int posto = new Random().nextInt(30) + 1;

        Prenotazione nuovaP = new Prenotazione(PNR, dataScadenza, cfUt, idViaggio, posto, numCarrozza);

        PrenotazioneDTO prenotazione = PrenotazioneDTO.newBuilder()
                .setNumPren(nuovaP.getPNR())
                .setScadenzaPrenotazione(nuovaP.getDataScadenza())
                .setCodFis(nuovaP.getCf())
                .setIdViaggio(nuovaP.getId_viaggio())
                .setPosto(nuovaP.getPosto())
                .setCarrozza(nuovaP.getNumCarrozza())                                   // carrozza simulata
                .build();

        boolean preNonTrovata = prenotazioneService.findByPNR(PNR) == null;
        if(preNonTrovata)
            prenotazioneService.add(nuovaP);

        PrenotaResponse response = PrenotaResponse.newBuilder()
                .setSuccesso(true)
                .setMessaggio("Prenotazione effettuata con successo. Scade il " + nuovaP.getDataScadenza())
                .setViaggioPrenotato(prenotazione)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        LocalDateTime oraCorrente = LocalDateTime.now();
        if(dataScadenzaLDT.isAfter(oraCorrente)){
            prenotazioneService.deleteP(nuovaP);
            PrenotaResponse resp = PrenotaResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggio("Prenotazione scaduta")
                    .build();

            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }

    }

    @Override
    public void acquistaBiglietto(AcquistaBigliettoRequest request, StreamObserver<AcquistaBigliettoResponse> responseObserver){
        UtenteDTO utente = request.getUtenteDati();
        CercaViaggiResponse viaggiDisponibili = request.getViaggioDesiderato();

        if (viaggiDisponibili.getViaggiCount() == 0) {
            AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggioConferma("Nessun viaggio disponibile per la prenotazione.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        /*
        Visto che mi ritorna una lista di messaggi disponibili, facciamo in modo che
        lo scelga l'utente ed assegnamo la posizione data dall'utente in input alla
        chiamata sottostante
         */
        ViaggioDTO viaggio = viaggiDisponibili.getViaggi(0);

        // Simula generazione acquisto viaggio
        BigliettoDTO bigliettoAcq = BigliettoDTO.newBuilder()
                .setCodBiglietto("")
                .setPostoAssegnato(10)
                .setViaggioAcquistato(viaggio)
                .setNumCarrozza(22)
                .build();

        //qua ho dubbi ovviamente
        BigliettiService.add(bigliettoAcq); // non va aggiunta anche al database?
        //Come gestire l'acquisto?

        AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                .setSuccesso(true)
                .setMessaggioConferma("Biglietto acquistato")
                .setDettagliBiglietto(bigliettoAcq)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void promozioniAttive(PromoAttiveRequest request, StreamObserver<PromoAttiveResponse> responseObserver){

    }

    @Override
    public void modificaBiglietto(ModificaBigliettoRequest request, StreamObserver<ModificaBigliettoResponse> responseObserver){


    }

    @Override
    public void andamentoTrenoSpecifico(IscrivimiTrenoSpeRequest request, StreamObserver<IscrivimiTrenoSpeResponse> responseObserver){

    }

    @Override
    public void notificheAutomatiche(IscriviUtenteRequest request, StreamObserver<IscriviUtenteResponse> responseObserver){

    }

    @Override
    public void fedeltaNotificaPromoOfferte(AccettiNotificaFedRequest request, StreamObserver<AccettiNotificaFedResponse> responseObserver){

    }

}
