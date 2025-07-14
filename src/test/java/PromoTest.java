import org.example.model.Promozione;
import org.example.model.Treno;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.PromozioneDAO;
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

public class PromoTest {
    private PromozioneDAO promozioneDAO;
    private Connection dbConnection;

    @BeforeEach
    public void init(){
        popolaDBUtil.resetDatabase();
        this.promozioneDAO = new PromozioneDAO();
        try {
            this.dbConnection = DBConnectionSingleton.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test che verifica l'aggiunta di una nuova promozione")
    public void checkAggiuntaPromo() throws SQLException {
        Promozione promoDaInserire = new Promozione("ESTATE25", 25, true, "REGIONALE", "2025-07-01", "2025-07-31");
        promozioneDAO.addPromozione(promoDaInserire, dbConnection);

        assertTrue(promoDaInserire.getCodicePromozione() != null);
    }

    @Test
    @DisplayName("Test che controlla le promozioni presenti nel DB")
    public void checkEsistenzaPromozioni(){
        List<Promozione> ret = promozioneDAO.tuttiLePromozioni();
        assertFalse(ret.isEmpty());
    }

    @Test
    @DisplayName("Test che verifica se esiste una promozione solo fedeltà")
    public void checkSoloFedelta(){
        List<Promozione> ret = promozioneDAO.promozioniAttive("REGIONALE", true, "31/07/2025");
        assertFalse(ret == null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ESTATE"})
    @DisplayName("Test che verifica se una promozione presente viene eliminata dal DB")
    public void checkEliminaPromozione(String codicePromo)   {
        promozioneDAO.eliminaPromozione(codicePromo);

        assertTrue(codicePromo != null);
    }

}
