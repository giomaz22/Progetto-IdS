import org.example.model.Biglietto;
import org.example.model.Utente;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.BigliettoDAO;
import org.example.persistence.dao.UtenteDAO;
import org.example.persistence.popolaDBUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BigliettoTest {

    private BigliettoDAO bigliettoDAO;
    private Connection dbConnection;
    private UtenteDAO utenteDAO;

    @BeforeEach
    public void init(){
        popolaDBUtil.resetDatabase();
        this.bigliettoDAO = new BigliettoDAO();
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
    @DisplayName("Test che verifica l'aggiunta di un nuovo viaggio")
    public void checkAggiuntaBiglietto() throws SQLException {
        Biglietto bigliettoDaInserire = new Biglietto("CODBI2704", "SECONDA", null, "MZZSVT68D27B968B", 1120, 3, 4);
        bigliettoDAO.addBiglietto(bigliettoDaInserire, dbConnection);

        Biglietto check = bigliettoDAO.trovaBigliettoPerCodice("CODBI2704");
        assertFalse(check == null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CODBI2211"})
    @DisplayName("Test che verifica se esiste un biglietto nel DB dato il codice")
    public void checkTrovaBiglietto(String codice) {
        Biglietto ret = bigliettoDAO.trovaBigliettoPerCodice(codice);
        assertFalse(ret == null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"MZZGNN03S22D122I"})
    @DisplayName("Test che controlla i biglietti presenti per utente nel DB")
    public void checkEsistenzaBiglietti(String cf){
        List<Biglietto> ret = bigliettoDAO.bigliettiPerUtente(cf);
        assertFalse(ret.isEmpty());
    }

    @Test
    @DisplayName("Test che verifica se la modifica di un biglietto è andata a buon fine")
    public void checkModificaBigl() {
        Biglietto biglietto = bigliettoDAO.trovaBigliettoPerCodice("CODBI2211");
        assertNotNull(biglietto, "Il biglietto CODBI2211 non è stato trovato.");
        assertEquals("SECONDA", biglietto.getClasse());
    }
}
