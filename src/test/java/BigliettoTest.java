import org.example.model.Biglietto;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.BigliettoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BigliettoTest {

    private BigliettoDAO bigliettoDAO;
    private Connection dbConnection;

    @BeforeEach
    public void init(){
        this.bigliettoDAO = new BigliettoDAO();
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
    public void checkModificaBigl() throws SQLException {
        String classeCorrente = bigliettoDAO.trovaBigliettoPerCodice("CODBI2704").getClasse();
        Biglietto b = bigliettoDAO.trovaBigliettoPerCodice("CODBI2704");
        b.setClasse("PRIMA");
        b.setPosto(4);
        b.setPNR(null);
        b.setCf("MZZSVT68D27B968B");
        b.setNumCarrozza(2);
        b.setIdViaggio(1120);
        bigliettoDAO.aggiornaBiglietto(b,dbConnection);

        assertFalse(b.getClasse().equals(classeCorrente));
    }
}
