import org.example.model.Fedelta;
import org.example.model.Utente;
import org.example.persistence.dao.FedeltaDAO;
import org.example.persistence.dao.UtenteDAO;
import org.example.persistence.popolaDBUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class FedeltaTest {

    private FedeltaDAO fedeltaDAO;
    private UtenteDAO utenteDAO;

    @BeforeEach
    public void init() {
        popolaDBUtil.resetDatabase();
        fedeltaDAO = new FedeltaDAO();
        utenteDAO = new UtenteDAO();

        Utente nuovoUtente = new Utente("Salvatore", "Mazzei", "MZZSVT68D27B968B", "27/04/1968", "salvatore", "salv@alice.it", false);
        utenteDAO.addCliente(nuovoUtente);
    }

    @Test
    @DisplayName("Test che verifica se l'aggiunta di una carta fedeltà per CF è andata a buon fine")
    public void checkNuovoUtente(){
        Fedelta cartaDaInserire = new Fedelta("COD270468", "MZZSVT68D27B968B", 10);
        fedeltaDAO.addCFedeltaPerCliente(cartaDaInserire);

        Fedelta fedCheck = fedeltaDAO.trovaCartaPerCFUtente("MZZSVT68D27B968B");
        assertFalse(fedCheck == null, "Fedeltà per cf non trovata");
    }

    @ParameterizedTest
    @ValueSource(strings = {"MZZGNN03S22D122I"})
    @DisplayName("Test che verifica se esiste una carta associata al CF indicato")
    public void checkCartaEsisteGia(String cf){
        Fedelta ret = fedeltaDAO.trovaCartaPerCFUtente(cf);
        assertNotNull(ret, "Carta non associata con cf" + cf);
    }
}
