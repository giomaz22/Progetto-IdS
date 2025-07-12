package org.example.persistence;

import org.example.model.*;
import org.example.persistence.dao.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class popolaDBUtil {
    public static void popolaDatabase () throws SQLException {
        CreaTabelleUtil.init(); // creo nuove tabelle

        TrenoDAO trenoDB;
        trenoDB = new TrenoDAO();

        Treno treno = new Treno();
        treno.setIDtreno("RE-5584");
        treno.setTipologia("REGIONALE");
        treno.setStatoTreno("In attesa di comunicazioni");
        treno.setNumCarrozze(6);
        trenoDB.addTreno(treno);

        ViaggioDAO viaggioDB;
        viaggioDB = new ViaggioDAO();

        Viaggio viaggio = new Viaggio();
        viaggio.setIDViaggio(1120);
        viaggio.setIDtreno("RE-5584");
        viaggio.setClassiDisponibili("SECONDA");
        viaggio.setData("17/07/2025");
        viaggio.setOraPartenza("11:45");
        viaggio.setOraArrivo("12:05");
        viaggio.setStazionePartenza("COSENZA");
        viaggio.setStazioneArrivo("PAOLA");
        viaggio.setPrezzo(2.0);
        viaggio.setNumPostiDisponibili(50);
        viaggioDB.addNewViaggio(viaggio);

        UtenteDAO utenteDB;
        utenteDB = new UtenteDAO();

        Utente utente = new Utente();
        utente.setNome("Giovanni");
        utente.setCognome("Mazzei");
        utente.setEmail("giovmazzei03@gmail.com");
        utente.setPassword("giovanni");
        utente.setCodiceFiscale("MZZGNN03S22D122I");
        utente.setDataNascita("22/11/2003");
        utente.setAmministratore(true);
        utenteDB.addCliente(utente);

        FedeltaDAO fedeltaDB;
        fedeltaDB = new FedeltaDAO();

        Fedelta fedelta = new Fedelta();
        fedelta.setCFpossessore("MZZGNN03S22D122I");
        fedelta.setID("COD221103");
        fedelta.setPuntiCarta(20);
        fedeltaDB.addCFedeltaPerCliente(fedelta);

        PromozioneDAO promozioneDB;
        promozioneDB = new PromozioneDAO();

        Promozione promozione = new Promozione();
        promozione.setInizioPromo("01/07/2025");
        promozione.setFinePromo("30/07/2025");
        promozione.setCodicePromozione("ESTATE");
        promozione.setPercentualeSconto(30);
        promozione.setTipoTreno("REGIONALE");
        promozione.setSoloFedelta(false);
        promozioneDB.addPromozione(promozione, DBConnectionSingleton.getConnection());

        BigliettoDAO bigliettoDB;
        bigliettoDB = new BigliettoDAO();

        Biglietto biglietto = new Biglietto();
        biglietto.setCodBiglietto("CODBI2211");
        biglietto.setPNR(null);
        biglietto.setCf("MZZGNN03S22D122I");
        biglietto.setClasse("SECONDA");
        biglietto.setNumCarrozza(2);
        biglietto.setPosto(4);
        biglietto.setIdViaggio(1120);
        bigliettoDB.addBiglietto(biglietto, DBConnectionSingleton.getConnection());

        PrenotazioneDAO prenotazioneDB;
        prenotazioneDB = new PrenotazioneDAO();

        Prenotazione p = new Prenotazione();
        p.setId_viaggio(1120);
        p.setCf("MZZGNN03S22D122I");
        p.setPosto(5);
        p.setPNR("PNR1");
        p.setDataScadenza("23/07/2025");
        p.setNumCarrozza(5);
        prenotazioneDB.addPrenotazione(p, DBConnectionSingleton.getConnection());
    }

    public static void resetDatabase() {
        try (Connection conn = DBConnectionSingleton.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            stmt.execute("DELETE FROM prenotazioni");
            stmt.execute("DELETE FROM biglietti");
            stmt.execute("DELETE FROM fedelta");
            stmt.execute("DELETE FROM utenti");
            stmt.execute("DELETE FROM viaggi");
            stmt.execute("DELETE FROM treni");
            stmt.execute("DELETE FROM promozioni");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");

            popolaDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore reset");
        }
    }
}
