package mb;

import dao.LoginDAO;
import dao.OficialDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Intimacao;
import model.OficialJustica;
import model.User;
import org.hibernate.Session;
import util.Ferramentas;
import util.NewHibernateUtil;
import util.SessionUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Bruno Fernandes
 */
@Named(value = "admin")
@RequestScoped
public class AdminMB {

    private String nome;
    private String cpf;
    private String email;
    private Long oficialId;
    private List<OficialJustica> oficiais = new ArrayList<OficialJustica>();
    private List<Intimacao> intimacoes = new ArrayList<Intimacao>();
    private boolean isEditar;
    
    private Intimacao intimacao;

    
    
    public Intimacao getIntimacao() {
        return intimacao;
    }

    public void setIntimacao(Intimacao intimacao) {
        this.intimacao = intimacao;
    }

    @PostConstruct
    public void init() {
        HttpSession session = SessionUtils.getSession();
        User user = LoginDAO.getUSerByCPF(session.getAttribute("username").toString());

        if (session.getAttribute("tipoUser").toString().equals("2")) {

            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "/faces/homeOficial.xhtml?faces-redirect=true");

        }

        oficiais = OficialDAO.getOficiais();

        //Atualiza o WebService
        restUpdate();
    }

    public String salvar() {

        OficialJustica oficial;
        OficialJustica oficialBanco;
        //String cpfAux = Ferramentas.removerCaracteresCpf(this.cpf);
        oficialBanco = OficialDAO.getOficiaisByCpf(cpf);
        if (oficialBanco != null) {
            this.oficialId = oficialBanco.getId();
        }

        if (this.oficialId != null) {
            System.out.println("IF");
            oficial = new OficialJustica(this.oficialId, this.nome, this.cpf, this.email);
        } else {
            System.out.println("ELSE");
            oficial = new OficialJustica(this.oficialId, this.nome, this.cpf, this.email);
        }

        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        session.beginTransaction();

        oficial.toString();

        session.saveOrUpdate(oficial);

        session.getTransaction().commit();
        session.close();

        oficiais = OficialDAO.getOficiais();
        
        this.createUser(oficial.getCPF(), oficial.getCPF());

        this.oficialId = null;
        this.cpf = null;
        this.nome = null;
        this.email = null;
        
        //Atualiza o WebService
        restUpdate();

        return "homeAdmin";
    }

    public String editar(OficialJustica oficial) {
        System.out.println(oficial.toString());
        this.cpf = oficial.getCPF();
        this.email = oficial.getEmail();
        this.nome = oficial.getNome();
        this.oficialId = oficial.getId();
        return "homeAdmin";
    }

    public String deletar(OficialJustica oficial) {
        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        // Inicia-se a transação 

        session.beginTransaction();
        session.delete(oficial);
        // Efetua-se a consulta (aqui em HQL) 
        session.getTransaction().commit();
        session.close();
        oficiais = OficialDAO.getOficiais();
        
        //Atualiza o WebService
        restUpdate();

        return "homeAdmin";
    }

    public void createUser(String user, String password) {
        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        session.beginTransaction();
        user = Ferramentas.removerCaracteresCpf(user);
        password = Ferramentas.convertStringToMD5(Ferramentas.removerCaracteresCpf(user));
        User oldUser = LoginDAO.getUSerByCPF(user);
        User newUser = new User();

        if (oldUser != null) {
            newUser.setId(oldUser.getId());
            newUser.setTipo(oldUser.getTipo());
            newUser.setuName(oldUser.getuName());
            newUser.setPassword(oldUser.getPassword());
        } else {
            newUser.setuName(user);
            newUser.setPassword(password);
            newUser.setTipo(2);
        }
        session.saveOrUpdate(newUser);

        session.getTransaction().commit();
        session.close();
    }

    public List<OficialJustica> getOficiais() {
        return oficiais;
    }

    public Long getOficialId() {
        return oficialId;
    }

    public void setOficialId(Long oficialId) {
        this.oficialId = oficialId;
    }

    public boolean isIsEditar() {
        return isEditar;
    }

    public void setIsEditar(boolean isEditar) {
        this.isEditar = isEditar;
    }

    public void setOficiais(List<OficialJustica> oficiais) {
        this.oficiais = oficiais;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void restUpdate(){
                        
        Client client = ClientBuilder.newClient();            

        GenericEntity<List<OficialJustica>> listaOficial = 
            new GenericEntity<List<OficialJustica>>(getOficiais()){};

        try {
            Response r = client
                            .target("http://localhost:8080/webresources/oficial")
                            .request(MediaType.APPLICATION_JSON)
                            .post(Entity.json(listaOficial));        
        }
        catch (Exception e) {
            
        }
        
    }
    
    
    
    public void intimacaoUpdate(){
                        
        Client client = ClientBuilder.newClient();            
        
        try {
            Intimacao intimacao = client
                            .target("http://localhost:8080/webresources/intimacao")
                            .request(MediaType.APPLICATION_JSON)
                            .get(Intimacao.class);    
            
            // intimacoes.add(intimacao);
            
            
            
        }
        catch (Exception e) {
            
        }
        
    }

}
