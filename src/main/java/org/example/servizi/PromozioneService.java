package org.example.servizi;

import org.example.model.Promozione;
import org.example.persistence.DBConnectionSingleton;
import org.example.persistence.dao.PromozioneDAO;

import java.sql.Connection;
import java.sql.SQLException;

public class PromozioneService {
    private final PromozioneDAO promoDataBase;

    public PromozioneService(){
        this.promoDataBase = new PromozioneDAO();
    }

    public void addNewPromotion(Promozione promo) throws SQLException {
        Connection conn = DBConnectionSingleton.getConnection();
        promoDataBase.addPromozione(promo, conn);
    }
}
