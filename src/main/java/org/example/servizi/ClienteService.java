package org.example.servizi;

import org.example.model.Utente;
import org.example.persistence.dao.ClienteDAO;

public class ClienteService {
    private final ClienteDAO clienteDataBase;

    public ClienteService(){
        this.clienteDataBase = new ClienteDAO();
    }

    public void addCliente(Utente ut){
        clienteDataBase.addCliente(ut);
    }

    public Utente findUtenteByCf(String cf){
        return clienteDataBase.trovaPerCF(cf);
    }
}
