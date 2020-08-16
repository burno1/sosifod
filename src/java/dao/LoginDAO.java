/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.OficialJustica;
import model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import util.Ferramentas;
import util.NewHibernateUtil;

/**
 *
 * @author Bruno Fernandes
 */
public class LoginDAO {

    public static boolean validate(String user, String password) {
        try {
            Session session = NewHibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            System.out.println(user + ' ' + password);
            //pesquisa pelo cpf, caso não tenha retorna mensagem volta pra tela 
            Query query = session.createQuery(
                    "FROM User WHERE uname = :cpf AND password =:pass");
            query.setString("cpf", user);
            query.setString("pass", password);
            User userObj = (User) query.uniqueResult();
            session.getTransaction().commit();
            session.close();
            if (userObj != null) {
                //result found, means valid inputs
                return true;
            }
        } catch (Exception ex) {
            System.out.println("Login error -->" + ex.getMessage());
            return false;
        }
        return false;
    }

    public static int getUserRole(String cpf) {
        Session session = NewHibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        //pesquisa pelo cpf, caso não tenha retorna mensagem volta pra tela 
        Query query = session.createQuery(
                "FROM User WHERE uname = :cpf");
        query.setString("cpf", cpf);
        User userObj = (User) query.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return userObj.getTipo();
    }

    public static User getUSerByCPF(String cpf) {
        Session session = NewHibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        //pesquisa pelo cpf, caso não tenha retorna mensagem volta pra tela 
        Query query = session.createQuery(
                "FROM User WHERE uname = :cpf");
        query.setString("cpf", cpf);
        User userObj = (User) query.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return userObj;
    }

}
