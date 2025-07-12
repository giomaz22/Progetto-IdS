import org.example.model.Utente;
import org.example.persistence.dao.UtenteDAO;
import org.example.persistence.popolaDBUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UtenteTest {
    private UtenteDAO utenteDAO;

    @BeforeEach
    public void init() {
        popolaDBUtil.resetDatabase();
        utenteDAO = new UtenteDAO();
    }

    @Test
    @DisplayName("Test che verifica se l'aggiunta di un nuovo utente è andata a buon fine")
    public void checkNuovoUtente(){
        Utente utDaInserire = new Utente("Salvatore", "Mazzei", "MZZSVT68D27B968B", "27/04/1968", "salvatore", "salv@alice.it", false);
        utenteDAO.addCliente(utDaInserire);

        Utente utCheck = utenteDAO.trovaPerCF("MZZSVT68D27B968B");
        assertFalse(utCheck == null, "Utente non trovato");
    }

    @ParameterizedTest
    @ValueSource(strings = {"MZZGNN03S22D122I"})
    @DisplayName("Test che verifica se è presente un utente nel DB dato il CF")
    public void checkCFutente(String cf){
        Utente ret = utenteDAO.trovaPerCF(cf);
        assertNotNull(ret, "Utente non trovato con cf" + cf);
    }

    @ParameterizedTest
    @ValueSource(strings = {"salv@alice.it"})
    @DisplayName("Test che verifica se è presente un utente nel DB data l'email")
    public void checkEmailUtente(String email){
        Utente utDaInserire = new Utente("Salvatore", "Mazzei", "MZZSVT68D27B968B", "27/04/1968", "salvatore", email, false);
        utenteDAO.addCliente(utDaInserire);

        Utente ret = utenteDAO.trovaPerEmail(email);
        assertNotNull(ret, "Utente non trovato");
    }
}
