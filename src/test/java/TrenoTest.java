import org.example.model.Treno;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.TrenoDAO;
import org.example.persistence.popolaDBUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TrenoTest {
    private TrenoDAO trenoDAO;
    private Connection dbConnection;

    @BeforeEach
    public void init(){
        popolaDBUtil.resetDatabase();
        this.trenoDAO = new TrenoDAO();
        try {
            this.dbConnection = DBConnectionSingleton.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test che controlla i treni presenti nel DB")
    public void checkEsistenzaTreni(){
        List<Treno> ret = trenoDAO.tuttiItreni();
        assertFalse(ret.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"RE-5584"})
    @DisplayName("Test che controlla se il treno indicato è presente nel DB")
    public void checkEsistenzaTreniIndicato(String treno){
        Treno ret = trenoDAO.trovaTrenoPerID(treno);
        assertFalse(ret == null);
    }

    @Test
    @DisplayName("Test che verifica l'aggiunta di un treno")
    public void checkAggiuntaTreno(){
        Treno trenoDaInserire = new Treno("FR-273", "FrecciaRossa", "ARRIVATO", 5);
        trenoDAO.addTreno(trenoDaInserire);

        Treno check = trenoDAO.trovaTrenoPerID("FR-273");
        assertFalse(check == null);
        assertEquals(check.getStatoTreno(), "ARRIVATO");
    }

    @Test
    @DisplayName("Test che verifica l'aggiornamento di un treno")
    public void checkAggiornamentoTreno() throws SQLException {
        Treno trenoDaInserire = new Treno("FR-273", "FrecciaRossa", "ARRIVATO", 5);
        trenoDAO.addTreno(trenoDaInserire);

        trenoDAO.aggiornaStatoTreno("FR-273", "IN PARTENZA", dbConnection);

        Treno checkAggiornato = trenoDAO.trovaTrenoPerID("FR-273");
        assertFalse(checkAggiornato == null);
        assertEquals("IN PARTENZA", checkAggiornato.getStatoTreno());
    }

    @Test
    @DisplayName("Test che verifica l'eliminazione di un treno")
    public void checkEliminazioneTreno()  {
        trenoDAO.eliminaTreno("FR-273");

        Treno checkElimina = trenoDAO.trovaTrenoPerID("FR-273");
        assertFalse(checkElimina != null);
    }

}
