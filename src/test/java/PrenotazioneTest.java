import org.example.model.Prenotazione;
import org.example.model.Utente;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.PrenotazioneDAO;
import org.example.persistence.dao.UtenteDAO;
import org.example.persistence.popolaDBUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PrenotazioneTest {

    private PrenotazioneDAO prenotazioneDAO;
    private Connection dbConnection;
    private UtenteDAO utenteDAO;

    @BeforeEach
    public void init(){
        popolaDBUtil.resetDatabase();
        this.prenotazioneDAO = new PrenotazioneDAO();
        this.utenteDAO = new UtenteDAO();

        Utente nuovoUtente = new Utente("Salvatore", "Mazzei", "MZZSVT68D27B968B", "27/04/1968", "salvatore", "salv@alice.it", false);
        utenteDAO.addCliente(nuovoUtente);
        try {
            this.dbConnection = DBConnectionSingleton.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test che verifica l'aggiunta di un nuova prenotazione")
    public void checkAggiuntaPrenotazione() throws SQLException {
        Prenotazione prenotDaInserire = new Prenotazione("PNR2", "27/07/2025", "MZZSVT68D27B968B", 1120, 7, 6);
        prenotazioneDAO.addPrenotazione(prenotDaInserire, dbConnection);

        Prenotazione check = prenotazioneDAO.trovaPerPNR("PNR2");
        assertFalse(check == null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PNR1"})
    @DisplayName("Test che verifica se esiste una prenotazione nel DB dato il pnr")
    public void checkTrovaPrenotazione(String pnr) {
        Prenotazione ret = prenotazioneDAO.trovaPerPNR(pnr);
        assertFalse(ret == null);
    }

    @Test
    @DisplayName("Test che controlla le prenotazioni presenti nel DB")
    public void checkEsistenzaPrenotazioni(){
        List<Prenotazione> ret = prenotazioneDAO.tutteLePrenotazioni();
        assertFalse(ret.isEmpty());
    }

    @Test
    @DisplayName("Test che verifica l'eliminazione di una prenotazione")
    public void checkEliminazionePrenotazione() throws SQLException {
        Prenotazione p = prenotazioneDAO.trovaPerPNR("PNR2");
        if (p == null) {
            p = new Prenotazione("PNR2", "27/07/2025", "MZZSVT68D27B968B", 1120, 7, 6);
            prenotazioneDAO.addPrenotazione(p, dbConnection);
        }
        prenotazioneDAO.eliminaPrenotazione(p, dbConnection);
        Prenotazione checkElimina = prenotazioneDAO.trovaPerPNR("PNR2");
        assertTrue(checkElimina == null);
    }


}
