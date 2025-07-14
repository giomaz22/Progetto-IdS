package org.example.servizi;

import org.example.model.Utente;
import org.example.persistence.dao.UtenteDAO;
import org.mindrot.jbcrypt.BCrypt;

public class ClienteService {
    private final UtenteDAO clienteDataBase;

    public ClienteService(){
        this.clienteDataBase = new UtenteDAO();
    }

    public void addCliente(Utente ut){
        String password = ut.getPassword(); // password in forma String "testuale" non codificata
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        ut.setPassword(passwordHash);
        clienteDataBase.addCliente(ut);
    }

    public Utente findUtenteByCf(String cf){
        return clienteDataBase.trovaPerCF(cf);
    }

    public Utente findUtenteByEmail(String email){return clienteDataBase.trovaPerEmail(email);}

}
