package org.example.servizi;

import org.example.model.Utente;
import org.example.persistence.dao.UtenteDAO;
import org.example.util.PasswordUtil;
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

    // quando questo metodo viene invocato, la password che gli viene passata
    // è in testo normale non "hashata-codificata", mentre quella sul DB lo è.
    public Utente loginUtente(String email, String password){
        Utente u = clienteDataBase.trovaPerEmail(email);
        // password è la password in String
        // u.getPassword() è presente nel database ed è hashata
        if(PasswordUtil.checkPassword(password, u.getPassword())){
            return u;
        }else{
            return null;
        }

    }

    public Utente loginAdmin(String email, String password){
        Utente u = loginUtente(email, password);
        if(u.isAmministratore() == false){
            return null;
        }else{
            return u;
        }
    }


}
