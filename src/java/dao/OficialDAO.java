/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import model.OficialJustica;
import org.hibernate.Query;
import org.hibernate.Session;
import util.NewHibernateUtil;

/**
 *
 * @author Bruno Fernandes
 */
public class OficialDAO {
        public static List<OficialJustica> getOficiais(){
         List<OficialJustica> listToReturn = new ArrayList<OficialJustica>();
        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        // Inicia-se a transação 
        session.beginTransaction();
        // Efetua-se a consulta (aqui em HQL) 
        Query query = session.createQuery("from OficialJustica");
        listToReturn = query.list();
        // Efetiva-se a transação 
        session.getTransaction().commit();
        session.close();
        return listToReturn;
    }       
       
    public static OficialJustica getOficiaisByCpf(String cpf){
       OficialJustica oficial = new OficialJustica();
       Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        // Inicia-se a transação 
        session.beginTransaction();
        // Efetua-se a consulta (aqui em HQL) 
        Query query = session.createQuery("from OficialJustica WHERE cpf = :cpf");
        query.setString("cpf", cpf);
        oficial = (OficialJustica) query.uniqueResult();
        // Efetiva-se a transação 
        session.getTransaction().commit();
        session.close();
        return oficial;
    }
    
    public OficialJustica getOficiaisById(Integer id){
       OficialJustica oficial = new OficialJustica();
       Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        // Inicia-se a transação 
        session.beginTransaction();
        // Efetua-se a consulta (aqui em HQL) 
        Query query = session.createQuery("from OficialJustica WHERE id = :id");
        query.setInteger("id", id);
        oficial = (OficialJustica) query.uniqueResult();
        // Efetiva-se a transação 
        session.getTransaction().commit();
        session.close();
        return oficial;
    }
}
