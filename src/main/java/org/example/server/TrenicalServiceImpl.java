package org.example.server;

import io.grpc.Status;
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

import static org.example.util.ClassiTrenoUtil.scegliClasseBaseCasuale;
import static org.example.util.CodiceCartaFedelta.generaCodice;
import static org.example.util.PagamentoSimula.checkLuhn;

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

        for (Viaggio v : viaggiRet) {
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

        }else{
            responseObserver.onError(Status.NOT_FOUND.withDescription("Credenziali errate").asRuntimeException());
        }

    }

    @Override //COMPLETATO
    public void registraUtente(RegistraRequest request, StreamObserver<RegistraResponse> responseObserver) {
        String codFiscale = request.getUtenteDaRegistrare().getCodiceFiscale();
        String nome = request.getUtenteDaRegistrare().getNome();
        String cognome = request.getUtenteDaRegistrare().getCognome();
        String dataNascita = request.getUtenteDaRegistrare().getDataDiNascita();
        String password = request.getUtenteDaRegistrare().getPassword();
        String email = request.getUtenteDaRegistrare().getEmail();
        boolean admin = request.getUtenteDaRegistrare().getAdmin();

        boolean giaPresenteCf = clienteService.findUtenteByCf(codFiscale) != null;
        boolean giaPresenteEmail = clienteService.findUtenteByEmail(email) != null;

        if (!giaPresenteCf || !giaPresenteEmail) {
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

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override //COMPLETATO
    public void sottoscriviFedelta(FedeltaRequest request, StreamObserver<FedeltaResponse> responseObserver) {
        String codFiscale = request.getCodFiscale();

        boolean giaFedele = fedeltaService.findFedeltaByCF(codFiscale) != null;

        if (!giaFedele) {
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
        } else {
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

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @SneakyThrows //COMPLETATO
    @Override
    public void prenotaViaggio(PrenotaRequest request, StreamObserver<PrenotaResponse> responseObserver) {
        int idViaggio = request.getViaggioID();
        String cfUt = request.getCfUtente();

        Viaggio v = viaggioService.findViaggioById(idViaggio);

        String PNR = "PNR" + System.currentTimeMillis();

        LocalDateTime dataViaggio = LocalDateTime.parse(v.getData());
        LocalDateTime dataScadenza = dataViaggio.minusHours(12);
        String scadenzaFormattata = dataScadenza.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        int numCarrozza = new Random().nextInt(6) + 1;
        int posto = new Random().nextInt(30) + 1;

        Prenotazione nuovaP = new Prenotazione(PNR, scadenzaFormattata, cfUt, idViaggio, posto, numCarrozza);

        // Verifica che non esista già
        if (prenotazioneService.findByPNR(PNR) == null) {
            prenotazioneService.add(nuovaP);
        }
        viaggioService.decrementaPostiDisponibili(idViaggio, 1);

        PrenotazioneDTO dto = PrenotazioneDTO.newBuilder()
                .setNumPren(PNR)
                .setScadenzaPrenotazione(scadenzaFormattata)
                .setCodFis(cfUt)
                .setIdViaggio(idViaggio)
                .setPosto(posto)
                .setCarrozza(numCarrozza)
                .build();

        PrenotaResponse response = PrenotaResponse.newBuilder()
                .setSuccesso(true)
                .setMessaggio("Prenotazione effettuata con successo. Scade il " + scadenzaFormattata)
                .setViaggioPrenotato(dto)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @SneakyThrows //COMPLETATO
    @Override
    public void acquistaBiglietto(AcquistaBigliettoRequest request, StreamObserver<AcquistaBigliettoResponse> responseObserver) {
        int idViaggio = request.getViaggioID();
        String cfUte = request.getCfUtente();
        String cartaCredito = request.getNumCarta();
        String PNR = request.getPNR();

        //gestisco prima la parte relativa alle promozioni che determinano il prezzo del biglietto?

        //acquisto un biglietto già prenotato
        if (PNR != null) {
            Prenotazione pre = prenotazioneService.findByPNR(PNR);
            LocalDateTime oraCorrente = LocalDateTime.now();
            LocalDateTime oraScadenzaPrenotazione = LocalDateTime.parse(pre.getDataScadenza());
            if (oraCorrente.isBefore(oraScadenzaPrenotazione)) {
                Viaggio viaggioPrenotato = viaggioService.findViaggioById(pre.getId_viaggio());
                ViaggioDTO nuovoViaggio = ViaggioDTO.newBuilder()
                        .setIdViaggio(viaggioPrenotato.getIDViaggio())
                        .setOraPartenza(viaggioPrenotato.getOraPartenza())
                        .setOraArrivo(viaggioPrenotato.getOraArrivo())
                        .setStazionePartenza(viaggioPrenotato.getStazionePartenza())
                        .setStazioneArrivo(viaggioPrenotato.getStazioneArrivo())
                        .setPrezzo(viaggioPrenotato.getPrezzo())
                        .setNumeroPostiDisponibili(viaggioPrenotato.getNumPostiDisponibili())
                        .setClassiDisponibili(viaggioPrenotato.getClassiDisponibili())
                        .build();

                String codB = "CODB" + System.currentTimeMillis();
                String classe = scegliClasseBaseCasuale();
                int numCarrozza = pre.getNumCarrozza();
                int posto = pre.getPosto();
                Biglietto nuovoBiglietto = new Biglietto(codB, classe, PNR, cfUte, idViaggio, numCarrozza, posto);

                BigliettoDTO bigliettoAcq = BigliettoDTO.newBuilder()
                        .setCodBiglietto(nuovoBiglietto.getCodBiglietto())
                        .setPostoAssegnato(nuovoBiglietto.getPosto())
                        .setViaggioAcquistato(nuovoViaggio)
                        .setClasse(nuovoBiglietto.getClasse())
                        .setNumCarrozza(nuovoBiglietto.getNumCarrozza())
                        .build();

                if (checkLuhn(cartaCredito)) {
                    bigliettiService.add(nuovoBiglietto);
                    AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                            .setSuccesso(true)
                            .setMessaggioConferma("Biglietto acquistato correttamente")
                            .setDettagliBiglietto(bigliettoAcq)
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                } else {
                    prenotazioneService.deleteP(pre);
                    viaggioService.incrementaPostiDisponibili(pre.getId_viaggio(), 1);
                    AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                            .setSuccesso(false)
                            .setMessaggioConferma("Carta non valida. Biglietto non acquistato!")
                            .setDettagliBiglietto(bigliettoAcq)
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            } else {
                AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                        .setSuccesso(false)
                        .setMessaggioConferma("Prenotazione già scaduta")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        } else {
            Viaggio viaggioPrenot = viaggioService.findViaggioById(idViaggio);
            ViaggioDTO nuovoViaggi = ViaggioDTO.newBuilder()
                    .setIdViaggio(viaggioPrenot.getIDViaggio())
                    .setOraPartenza(viaggioPrenot.getOraPartenza())
                    .setOraArrivo(viaggioPrenot.getOraArrivo())
                    .setStazionePartenza(viaggioPrenot.getStazionePartenza())
                    .setStazioneArrivo(viaggioPrenot.getStazioneArrivo())
                    .setPrezzo(viaggioPrenot.getPrezzo())
                    .setNumeroPostiDisponibili(viaggioPrenot.getNumPostiDisponibili())
                    .setClassiDisponibili(viaggioPrenot.getClassiDisponibili())
                    .build();

            String codB = "CODB" + System.currentTimeMillis();
            String classe = scegliClasseBaseCasuale();
            int numCarrozza = new Random().nextInt(6) + 1;
            int posto = new Random().nextInt(6) + 1;
            Biglietto nuovoBiglietto = new Biglietto(codB, classe, null, cfUte, idViaggio, numCarrozza, posto);
            viaggioService.decrementaPostiDisponibili(idViaggio, 1);

            BigliettoDTO bigliettoA = BigliettoDTO.newBuilder()
                    .setCodBiglietto(nuovoBiglietto.getCodBiglietto())
                    .setPostoAssegnato(nuovoBiglietto.getPosto())
                    .setViaggioAcquistato(nuovoViaggi)
                    .setClasse(nuovoBiglietto.getClasse())
                    .setNumCarrozza(nuovoBiglietto.getNumCarrozza())
                    .build();

            if (checkLuhn(cartaCredito)) {
                bigliettiService.add(nuovoBiglietto);
                AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                        .setSuccesso(true)
                        .setMessaggioConferma("Biglietto acquistato correttamente")
                        .setDettagliBiglietto(bigliettoA)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                        .setSuccesso(false)
                        .setMessaggioConferma("Carta non valida. Biglietto non acquistato!")
                        .setDettagliBiglietto(bigliettoA)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }


    }

    @Override //COMPLETATO
    public void promozioniAttive(PromoAttiveRequest request, StreamObserver<PromoAttiveResponse> responseObserver){
        boolean haiFedelta = request.getHaiFedelta();
        String tipoTreno = request.getTipoTreno();
        String dataPartenza = request.getQuandoParti();

        List<Promozione> promoAttiveUtente = promozioneService.getPromozioniPerViaggio(tipoTreno, haiFedelta, dataPartenza);

        if(!promoAttiveUtente.isEmpty()){
            //supponiamo che la migliore delle promozioni sia la prima in lista
            Promozione migliore = promoAttiveUtente.get(0);
            PromoAttiveResponse response = PromoAttiveResponse.newBuilder()
                    .setCodPromoAttiva(migliore.getCodicePromozione())
                    .setMessage("Promozione valida fino al: " + migliore.getFinePromo())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            PromoAttiveResponse response = PromoAttiveResponse.newBuilder()
                    .setMessage("Nessuna promozione attiva")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @SneakyThrows //COMPLETATO
    @Override
    public void modificaBiglietto(ModificaBigliettoRequest request, StreamObserver<ModificaBigliettoResponse> responseObserver){
        BigliettoDTO bigliettoDaModificare = request.getBigliettoGiaAcquistato();
        String data = request.getNuovaData();
        String nuovaOraPartenza = request.getNuovaOraPartenza();
        String nuovaClasse = request.getNuovaClasse();

        Biglietto biglietto = bigliettiService.findById(bigliettoDaModificare.getCodBiglietto());
        Viaggio viaggio = viaggioService.findViaggioById(biglietto.getIdViaggio());

        List<Viaggio> ciSonoViaggiDataeOra = viaggioService.findViaggiNewDataOra(nuovaOraPartenza, data);

        double penale = 5.00;
        double differenza = 0.0;

        if (!nuovaClasse.equals(biglietto.getClasse())) {
            differenza = viaggioService.calcolaDifferenzaPrezzoClasse(viaggio, biglietto.getClasse(), nuovaClasse);
        }

        double totale = viaggio.getPrezzo() + differenza + penale;
        System.out.printf("Modifica biglietto - Prezzo finale: %.2f\n", totale);

        if(!ciSonoViaggiDataeOra.isEmpty()){
            Viaggio aggiornatoDataOra = ciSonoViaggiDataeOra.get(0);
            ViaggioDTO nuovoViaggio = ViaggioDTO.newBuilder()
                    .setIdViaggio(aggiornatoDataOra.getIDViaggio())
                    .setOraPartenza(aggiornatoDataOra.getOraPartenza())
                    .setOraArrivo(aggiornatoDataOra.getOraArrivo())
                    .setStazionePartenza(aggiornatoDataOra.getStazionePartenza())
                    .setStazioneArrivo(aggiornatoDataOra.getStazioneArrivo())
                    .setPrezzo(aggiornatoDataOra.getPrezzo())
                    .setNumeroPostiDisponibili(aggiornatoDataOra.getNumPostiDisponibili())
                    .setClassiDisponibili(aggiornatoDataOra.getClassiDisponibili())
                    .build();

            biglietto.setClasse(nuovaClasse);
            biglietto.setCodBiglietto(biglietto.getCodBiglietto());
            bigliettiService.update(biglietto);

            BigliettoDTO aggiornato = BigliettoDTO.newBuilder()
                    .setCodBiglietto(biglietto.getCodBiglietto())
                    .setClasse(biglietto.getClasse())
                    .setPostoAssegnato(biglietto.getPosto())
                    .setViaggioAcquistato(nuovoViaggio)
                    .setNumCarrozza(biglietto.getNumCarrozza())
                    .build();

            ModificaBigliettoResponse response = ModificaBigliettoResponse.newBuilder()
                    .setSuccesso(true)
                    .setMessaggioModificaEffettuata("Biglietto modificato correttamente. Totale: " + totale)
                    .setBigliettoAggiornato(aggiornato)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            ModificaBigliettoResponse response = ModificaBigliettoResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggioModificaEffettuata("Non è stato trovato un viaggio per quella data e ora indicate")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

    }

    @Override
    public void andamentoTrenoSpecifico(IscrivimiTrenoSpeRequest request, StreamObserver<IscrivimiTrenoSpeResponse> responseObserver){

    }

    @Override
    public void notificheAutomatiche(IscriviUtenteRequest request, StreamObserver<IscriviUtenteResponse> responseObserver){

    }

    @Override //COMPLETATO
    public void fedeltaNotificaPromoOfferte(AccettiNotificaFedRequest request, StreamObserver<AccettiNotificaFedResponse> responseObserver){
        FedeltaDTO fed = request.getLaTuaCarta();
        boolean attiva = request.getDesideroContatto();

        String cf = fed.getCFpossess();
        Promozione migliorePromo = promozioneService.promoSoloFedelta(cf).get(0);

        if(attiva == false){
            AccettiNotificaFedResponse response = AccettiNotificaFedResponse.newBuilder()
                    .setMessage("Ok, non sei iscritto alle notifiche riguardanti le offerte " +
                            "del programma fedeltà")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }else {
            LocalDateTime oraCorrente = LocalDateTime.now();
            LocalDateTime oraInizioPromo = LocalDateTime.parse(migliorePromo.getInizioPromo());
            LocalDateTime oraFinePromo = LocalDateTime.parse(migliorePromo.getFinePromo());
            if(oraCorrente.isAfter(oraInizioPromo) && oraInizioPromo.isBefore(oraFinePromo)){
                PromozioneDTO promoDTO = PromozioneDTO.newBuilder()
                        .setPromoCode(migliorePromo.getCodicePromozione())
                        .setPercentSconto(migliorePromo.getPercentualeSconto())
                        .build();

                AccettiNotificaFedResponse response = AccettiNotificaFedResponse.newBuilder()
                        .setPromoPerTe(promoDTO)
                        .setMessage("La migliore promozione per te quest'oggi")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else{
                AccettiNotificaFedResponse response = AccettiNotificaFedResponse.newBuilder()
                        .setMessage("Nessuna promozione")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }

    }

}
