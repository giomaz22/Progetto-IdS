import org.example.model.Viaggio;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.ViaggioDAO;
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

public class ViaggioTest {
    private ViaggioDAO viaggioDAO;
    private Connection conn;

    @BeforeEach
    public void init() {
        popolaDBUtil.resetDatabase();
        viaggioDAO = new ViaggioDAO();
        try{
            this.conn = DBConnectionSingleton.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test che verifica l'aggiunta di un nuovo viaggio")
    public void checkNuovoViaggio() {
        Viaggio vDaAggiungere = new Viaggio(0123, 200, "RE-5584", "10:30", "12:00", "Crotone", "Mirto", "SECONDA", "10/07/2025", 7.50);
        viaggioDAO.addNewViaggio(vDaAggiungere);

        Viaggio checkAggiunto = viaggioDAO.trovaViaggioPerID(0123, conn);
        assertFalse(checkAggiunto == null);
        assertEquals(checkAggiunto.getStazionePartenza(), "Crotone");
    }

    @ParameterizedTest
    @ValueSource(ints = {1120})
    @DisplayName("Test che controlla se il viaggio indicato è presente nel DB")
    public void checkViaggioIndicato(int idViaggio) throws SQLException {
        Viaggio ret = viaggioDAO.trovaViaggioPerID(idViaggio, conn);
        assertTrue(ret != null);
    }

    @Test
    @DisplayName("Test che verifica se sono presenti nel DB i viaggi con le caratteristiche specificate dall'utente")
    public void checkViaggioSpecifico(){
        List<Viaggio> ret = viaggioDAO.cercaViaggi("COSENZA", "PAOLA", "17/07/2025", "REGIONALE");
        assertFalse(ret.isEmpty(), "Lista vuota, viaggi non presenti");
    }

    @Test
    @DisplayName("Test che restituisce i viaggi per un'ora ed una data di partenza specificati")
    public void checkViaggioRestituisce(){
        List<Viaggio> ret = viaggioDAO.cercaViaggiDataOraNew("11:45", "17/07/2025");
        assertFalse(ret.isEmpty(), "Lista vuota, viaggi non presenti");
    }

    @Test
    @DisplayName("Test che verifica se l'aggiornamento dei posti di un viaggio è funzionante")
    public void checkViaggioPosti() throws SQLException {
        int postiIniziali = viaggioDAO.trovaViaggioPerID(1120, conn).getNumPostiDisponibili();

        viaggioDAO.aggiornaDisponibilitaPosti(1120, 75, conn);
        // ovviamente il numero dei nuovi posti va cambiato quando si ripete nuovamente il test
        assertTrue(postiIniziali != viaggioDAO.trovaViaggioPerID(1120, conn).getNumPostiDisponibili());
    }

    @Test
    @DisplayName("Test che verifica se l'aggiornamento di un viaggio è funzionante")
    public void checkViaggioUpdate() throws SQLException {
        String oraParIniziale = viaggioDAO.trovaViaggioPerID(1120, conn).getOraPartenza();
        viaggioDAO.aggiornaStatoViaggio("RE-5584", "11:47", "12:07", conn);

        Viaggio vAgg = viaggioDAO.trovaViaggioPerID(1120, conn);
        assertFalse(vAgg.getOraPartenza().equals(oraParIniziale));
    }

    @Test
    @DisplayName("Test che verifica se un viaggio viene eliminato o meno")
    public void checkViaggioEliminato() {
        viaggioDAO.eliminaViaggio(0123);

        Viaggio vElimina = viaggioDAO.trovaViaggioPerID(0123, conn);
        assertTrue(vElimina == null);
    }

}
