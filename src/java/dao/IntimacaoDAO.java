/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import model.Intimacao;
import org.hibernate.Query;
import org.hibernate.Session;
import util.NewHibernateUtil;
import util.SessionUtils;

/**
 *
 * @author Bruno Fernandes
 */
public class IntimacaoDAO {

    public static List<Intimacao> getIntimacoes() {
        List<Intimacao> listToReturn = new ArrayList<Intimacao>();
        
        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        session.beginTransaction();
        
        Query query = session.createQuery("from Intimacao");
        listToReturn = query.list();
        
        session.getTransaction().commit();
        session.close();

        return listToReturn;
    }

    public static List<Intimacao> getIntimacoesByCpf(String cpf) {
        List<Intimacao> listToReturn = new ArrayList<Intimacao>();
        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        session.beginTransaction();
        
        Query query = session.createQuery("from Intimacao where cpfOficial = :cpf");
        query.setString("cpf", cpf);
        
        listToReturn = query.list();
        
        session.getTransaction().commit();
        session.close();
        
        return listToReturn;
    }
}
