package org.example.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import org.example.grpc.*;
import org.example.model.*;
import org.example.servizi.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

    @Override //COMPLETATO  --- FUNZIONANTE
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
        // System.out.println("Lunghezza ViaggiRet: " + viaggiRet.size());
        // System.out.println(viaggioDTOret.size());

        // Costruisci la risposta
        CercaViaggiResponse response = CercaViaggiResponse.newBuilder()
                .addAllViaggi(viaggioDTOret)
                .build();

        // Invia la risposta al client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override //COMPLETATO  --- FUNZIONANTE
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        String email = request.getEmail();
        String password = request.getPassword();

        Utente trovato = clienteService.findUtenteByEmail(email);
        if (!trovato.isAmministratore()) {
            UtenteDTO utente = UtenteDTO.newBuilder()
                    .setNome(trovato.getNome())
                    .setCognome(trovato.getCognome())
                    .setCodiceFiscale(trovato.getCodiceFiscale())
                    .setDataDiNascita(trovato.getDataNascita())
                    .setAdmin(trovato.isAmministratore())
                    .build();

            Fedelta fedUt = fedeltaService.findFedeltaByCF(utente.getCodiceFiscale());
            FedeltaDTO.Builder fedeltaBuilder = FedeltaDTO.newBuilder();
            if(fedUt != null) {
                fedeltaBuilder
                        .setIDcarta(fedUt.getID())
                        .setCFpossess(fedUt.getCFpossessore())
                        .setPuntiFed(fedUt.getPuntiCarta());
            }

            LoginResponse response = LoginResponse.newBuilder()
                    .setLoginUt(utente)
                    .setLoginFed(fedeltaBuilder.build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } else if (trovato.isAmministratore()){
            UtenteDTO amministratore = UtenteDTO.newBuilder()
                    .setNome(trovato.getNome())
                    .setCognome(trovato.getCognome())
                    .setCodiceFiscale(trovato.getCodiceFiscale())
                    .setDataDiNascita(trovato.getDataNascita())
                    .setAdmin(trovato.isAmministratore())
                    .build();

            LoginResponse response = LoginResponse.newBuilder()
                    .setLoginUt(amministratore)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }else{
            responseObserver.onError(Status.NOT_FOUND.withDescription("Utente non trovato e/o credenziali errate.").asRuntimeException());
        }

    }

    @Override //COMPLETATO  --- FUNZIONANTE
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

    @Override //COMPLETATO  --- FUNZIONANTE
    public void sottoscriviFedelta(FedeltaRequest request, StreamObserver<FedeltaResponse> responseObserver) {
        String codFiscale = request.getCodFiscale();

        Utente daRegistrare = clienteService.findUtenteByCf(codFiscale);
        if (daRegistrare == null) {
            FedeltaResponse response = FedeltaResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggioConfermaFedelta("Utente non presente nel database")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        Fedelta cartaEsistente = fedeltaService.findFedeltaByCF(codFiscale);
        if (cartaEsistente != null) {
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
            return;
        }

        // Nuova iscrizione
        String IDcarta = generaCodice();
        int puntiNuovaIscrizione = 20;
        Fedelta cartaDaGenerare = new Fedelta(IDcarta, codFiscale, puntiNuovaIscrizione);
        fedeltaService.addNewFidelityCard(daRegistrare, cartaDaGenerare);

        FedeltaDTO fed = FedeltaDTO.newBuilder()
                .setCFpossess(codFiscale)
                .setIDcarta(IDcarta)
                .setPuntiFed(puntiNuovaIscrizione)
                .build();

        FedeltaResponse response = FedeltaResponse.newBuilder()
                .setSuccesso(true)
                .setMessaggioConfermaFedelta("CF: " + codFiscale + " è iscritto al programma fedeltà")
                .setDettagliCarta(fed)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @SneakyThrows //COMPLETATO  --- FUNZIONANTE
    @Override
    public void prenotaViaggio(PrenotaRequest request, StreamObserver<PrenotaResponse> responseObserver) {
        int idViaggio = request.getViaggioID();
        String cfUt = request.getCfUtente();

        Viaggio v = viaggioService.findViaggioById(idViaggio);

        if (clienteService.findUtenteByCf(cfUt) == null) {
            PrenotaResponse response = PrenotaResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggio("Utente non presente nel DB!")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        String PNR = "PNR" + System.currentTimeMillis();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse(v.getData(), formatter);

        LocalDateTime dataViaggio = data.atStartOfDay();
        LocalDateTime dataScadenza = dataViaggio.minusHours(12);

        String scadenzaFormattata = dataScadenza.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        int numCarrozza = new Random().nextInt(6) + 1;
        int posto = new Random().nextInt(30) + 1;

        Prenotazione nuovaP = new Prenotazione(PNR, scadenzaFormattata, cfUt, idViaggio, posto, numCarrozza);

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
                .setMessaggio("Prenotazione " + PNR + " effettuata con successo. Scade il " + scadenzaFormattata + ". Ulteriori info su tua email!" )
                .setViaggioPrenotato(dto)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @SneakyThrows //COMPLETATO  --- FUNZIONANTE
    @Override
    public void acquistaBiglietto(AcquistaBigliettoRequest request, StreamObserver<AcquistaBigliettoResponse> responseObserver) {
        int idViaggio = request.getViaggioID();
        String cfUte = request.getCfUtente();
        String cartaCredito = request.getNumCarta();
        String PNR = request.getPNR();

        if(clienteService.findUtenteByCf(cfUte) == null) {
            AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                    .setSuccesso(false)
                    .setMessaggioConferma("Utente non trovato nel DB")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {

            double prezzoFinale = 0;

            //acquisto un biglietto già prenotato
            if (PNR != null && !PNR.isBlank()) {
                Prenotazione pre = prenotazioneService.findByPNR(PNR);
                if (pre == null) {
                    AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                            .setSuccesso(false)
                            .setMessaggioConferma("Prenotazione non trovata con PNR: " + PNR)
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    return;
                }
                LocalDateTime oraCorrente = LocalDateTime.now();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate oraScadenzaDate = LocalDate.parse(pre.getDataScadenza(), formatter);
                LocalDateTime oraScadenzaPrenotazione = oraScadenzaDate.atStartOfDay();

                if (oraCorrente.isBefore(oraScadenzaPrenotazione)) {
                    Viaggio viaggioPrenotato = viaggioService.findViaggioById(pre.getId_viaggio());
                    if (calcoloPrezzoEventualePromo(cfUte, viaggioPrenotato) < viaggioPrenotato.getPrezzo()) {
                        prezzoFinale = calcoloPrezzoEventualePromo(cfUte, viaggioPrenotato);
                    } else {
                        prezzoFinale = viaggioPrenotato.getPrezzo();
                    }
                    ViaggioDTO nuovoViaggio = ViaggioDTO.newBuilder()
                            .setIdViaggio(viaggioPrenotato.getIDViaggio())
                            .setOraPartenza(viaggioPrenotato.getOraPartenza())
                            .setOraArrivo(viaggioPrenotato.getOraArrivo())
                            .setStazionePartenza(viaggioPrenotato.getStazionePartenza())
                            .setStazioneArrivo(viaggioPrenotato.getStazioneArrivo())
                            .setPrezzo(prezzoFinale)
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
                        System.out.println("Pagamento di " + cfUte + " accettato con carta " + cartaCredito);
                        AcquistaBigliettoResponse response = AcquistaBigliettoResponse.newBuilder()
                                .setSuccesso(true)
                                .setMessaggioConferma("Biglietto " + codB + " acquistato correttamente. Numero carrozza: " + numCarrozza + " posto: " + posto)
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
                if (calcoloPrezzoEventualePromo(cfUte, viaggioPrenot) < viaggioPrenot.getPrezzo()) {
                    prezzoFinale = calcoloPrezzoEventualePromo(cfUte, viaggioPrenot);
                } else {
                    prezzoFinale = viaggioPrenot.getPrezzo();
                }
                ViaggioDTO nuovoViaggi = ViaggioDTO.newBuilder()
                        .setIdViaggio(viaggioPrenot.getIDViaggio())
                        .setOraPartenza(viaggioPrenot.getOraPartenza())
                        .setOraArrivo(viaggioPrenot.getOraArrivo())
                        .setStazionePartenza(viaggioPrenot.getStazionePartenza())
                        .setStazioneArrivo(viaggioPrenot.getStazioneArrivo())
                        .setPrezzo(prezzoFinale)
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
                            .setMessaggioConferma("Biglietto " + codB + " acquistato correttamente. Numero carrozza: " + numCarrozza + " posto: " + posto)
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

    }

    public double calcoloPrezzoEventualePromo(String cfUte, Viaggio v) {
        boolean isFedelta = fedeltaService.findFedeltaByCF(cfUte) != null;
        Treno t = trenoService.getTrainByID(v.getIDtreno());
        List<Promozione> promoAttive = promozioneService.getPromozioniPerViaggio(
                t.getTipologia(), isFedelta, v.getData());

        double sconto = promoAttive.stream()
                .mapToDouble(Promozione::getPercentualeSconto)
                .max()
                .orElse(0.0);

        double prezzoBase = v.getPrezzo();
        double prezzoFinale = prezzoBase * (1 - sconto / 100.0);

        return prezzoFinale;
    }

    @Override //COMPLETATO  --- FUNZIONANTE
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

    @SneakyThrows //COMPLETATO  --- FUNZIONANTE
    @Override
    public void modificaBiglietto(ModificaBigliettoRequest request, StreamObserver<ModificaBigliettoResponse> responseObserver){
        BigliettoDTO bigliettoDaModificare = request.getBigliettoGiaAcquistato();

        String nuovaClasse = request.getNuovaClasse();

        Biglietto biglietto = bigliettiService.findById(bigliettoDaModificare.getCodBiglietto());

        String dataRicevuta = request.getNuovaData();
        DateTimeFormatter formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate dataParsed = LocalDate.parse(dataRicevuta, formatterInput);
        String dataFormattataPerDB = dataParsed.format(formatterOutput);


        String oraRicevuta = request.getNuovaOraPartenza();

        List<Viaggio> ciSonoViaggiDataeOra = viaggioService.findViaggiNewDataOra(oraRicevuta, dataFormattataPerDB);

        if(!ciSonoViaggiDataeOra.isEmpty()){
            Viaggio aggiornatoDataOra = ciSonoViaggiDataeOra.get(0);
            double penale = 5.00;
            double differenza = 0.0;
            if (!nuovaClasse.equals(biglietto.getClasse())) {
                differenza = viaggioService.calcolaDifferenzaPrezzoClasse(
                        aggiornatoDataOra, biglietto.getClasse(), nuovaClasse);
            }
            double totale = aggiornatoDataOra.getPrezzo() + differenza + penale;


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
            biglietto.setIdViaggio(aggiornatoDataOra.getIDViaggio());
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
                    .setMessaggioModificaEffettuata("Biglietto modificato correttamente. Totale: " + totale + "Biglietto " + aggiornato)
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

    private final Map<String, List<StreamObserver<IscrivimiTrenoSpeResponse>>> iscritti = new ConcurrentHashMap<>();
    @Override //COMPLETATO  --- OK
    public void andamentoTrenoSpecifico(IscrivimiTrenoSpeRequest request, StreamObserver<IscrivimiTrenoSpeResponse> responseObserver){
        String idtreno = request.getIdTreno();

        iscritti.computeIfAbsent(idtreno, k -> new CopyOnWriteArrayList<>()).add(responseObserver);
        Treno treno = trenoService.getTrainByID(idtreno);

        if(treno.getStatoTreno().equals("ARRIVATO") || treno.getStatoTreno().equals("TERMINATO")){
            responseObserver.onCompleted();
        } // la connessione va chiusa quando il viaggio è terminato

    }

    @Override //COMPLETATO  --- FUNZIONANTE
    public void fedeltaNotificaPromoOfferte(AccettiNotificaFedRequest request, StreamObserver<AccettiNotificaFedResponse> responseObserver){
        FedeltaDTO fed = request.getLaTuaCarta();
        boolean attiva = request.getDesideroContatto();

        String cf = fed.getCFpossess();

        List<Promozione> promoList = promozioneService.promoSoloFedelta(cf);

        if (!attiva) {
            AccettiNotificaFedResponse response = AccettiNotificaFedResponse.newBuilder()
                    .setMessage("Ok, non sei iscritto alle notifiche riguardanti le offerte del programma fedeltà")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        if (promoList == null || promoList.isEmpty()) {
            AccettiNotificaFedResponse response = AccettiNotificaFedResponse.newBuilder()
                    .setMessage("Nessuna promozione disponibile al momento")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        Promozione migliorePromo = promoList.get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate oggi = LocalDate.now();
            LocalDate inizio = LocalDate.parse(migliorePromo.getInizioPromo(), formatter);
            LocalDate fine = LocalDate.parse(migliorePromo.getFinePromo(), formatter);

            if (!oggi.isBefore(inizio) && !oggi.isAfter(fine)) {
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
            } else {
                AccettiNotificaFedResponse response = AccettiNotificaFedResponse.newBuilder()
                        .setMessage("Nessuna promozione attiva per oggi")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        } catch (Exception e) {
            AccettiNotificaFedResponse response = AccettiNotificaFedResponse.newBuilder()
                    .setMessage("Errore nella lettura delle date della promozione")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            e.printStackTrace();
        }
    }

    // COMPLETATO  --- FUNZIONANTE
    @Override
    public void statusAttualeViaggioTren(StatusViaggioTRequest request, StreamObserver<StatusViaggioTResponse> responseObserver){
        int idViaggio = request.getIdViaggio();
        Viaggio v = viaggioService.findViaggioById(idViaggio);

        if(v != null){
            String status = viaggioService.dettagliAttualiViaggio(idViaggio);
            StatusViaggioTResponse response = StatusViaggioTResponse.newBuilder()
                    .setIdTreno(v.getIDtreno())
                    .setMessage(status)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Viaggio non trovato e/o ID errato").asRuntimeException());
        }
    }
}