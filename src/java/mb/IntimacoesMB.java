/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import dao.IntimacaoDAO;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
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
import net.sf.ehcache.hibernate.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import util.Ferramentas;
import util.NewHibernateUtil;
import util.SessionUtils;

/**
 *
 * @author Bruno Fernandes
 */
@Named(value = "intimacao")
@RequestScoped
public class IntimacoesMB {

    private Date dataIntimacao;
    private Date dataExecucaoIntimacao;
    private UIComponent mybutton;
    private String cpfIntimado;
    private String nomeIntimado;
    private String endereçoIntimado;
    private boolean isEfetivada;
    private Long oficialAlocado;
    private String cpfOficial;
    private String nomeOficialAlocado;
    private String processo;
    private List<Intimacao> intimacoes;
    private Long idIntimacao;
    
    private static final SessionFactory factory = NewHibernateUtil.getSessionFactory();

    @PostConstruct
    public void init() {
      
        setIntimacoes();
        
        HttpSession sessionLocal = SessionUtils.getSession();

        if (Integer.valueOf(sessionLocal.getAttribute("tipoUser").toString()) == 2) {
            String userName = sessionLocal.getAttribute("username").toString();
            intimacoes = IntimacaoDAO.getIntimacoesByCpf(userName);
        } else {
            intimacoes = IntimacaoDAO.getIntimacoes();
        }
        
        //Atualiza o WebService
        restUpdate();

        
    }

    public String salvar() {
        
                        
        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        session.beginTransaction();
        //pesquisa pelo cpf, caso não tenha retorna mensagem volta pra tela 
        Query query = session.createQuery(
                "FROM User WHERE uname = :cpf");
        query.setString("cpf", Ferramentas.removerCaracteresCpf(this.cpfOficial));
        User user = (User) query.uniqueResult();

        Query queryOficial = session.createQuery("FROM OficialJustica WHERE cpf = :cpf");
        queryOficial.setString("cpf", this.cpfOficial);
        OficialJustica oficial = (OficialJustica) queryOficial.uniqueResult();
        session.getTransaction().commit();

        if (user == null || oficial == null) {
            
                FacesContext.getCurrentInstance().addMessage("formulario:cpfOficial", new FacesMessage("não há oficial com esse cpf", "não há oficial com esse cpf"));
                return "homeAdminIntimacao";

        }
        session.beginTransaction();

        Intimacao intimacao;
        if (this.idIntimacao != null) {
            System.out.println("IF");
            intimacao = new Intimacao(dataIntimacao, cpfIntimado, nomeIntimado, endereçoIntimado, dataExecucaoIntimacao, isEfetivada, oficial.getNome(), processo, cpfOficial, idIntimacao);
        } else {
            System.out.println("ELSE");
            intimacao = new Intimacao(dataIntimacao, cpfIntimado, nomeIntimado, endereçoIntimado, dataExecucaoIntimacao, isEfetivada, oficial.getNome(), processo, cpfOficial);
        }

        session.saveOrUpdate(intimacao);
        session.getTransaction().commit();
        session.close();
        
        restPut(intimacao);

        HttpSession sessionLocal = SessionUtils.getSession();
        if (Integer.valueOf(sessionLocal.getAttribute("tipoUser").toString()) == 2) {
            String userName = sessionLocal.getAttribute("username").toString();

            intimacoes = IntimacaoDAO.getIntimacoesByCpf(userName);
            this.cpfIntimado = null;
            this.cpfOficial = null;
            this.dataExecucaoIntimacao = null;
            this.dataIntimacao = null;
            this.endereçoIntimado = null;
            this.idIntimacao = null;
            this.isEfetivada = false;
            this.nomeIntimado = null;
            this.nomeOficialAlocado = null;
            this.processo = null;
            return "homeOficial";
        } else {

            intimacoes = IntimacaoDAO.getIntimacoes();

        }

        this.cpfIntimado = null;
        this.cpfOficial = null;
        this.dataExecucaoIntimacao = null;
        this.dataIntimacao = null;
        this.endereçoIntimado = null;
        this.idIntimacao = null;
        this.isEfetivada = false;
        this.nomeIntimado = null;
        this.nomeOficialAlocado = null;
        this.processo = null;


        
        return "homeAdminIntimacao";
    }

    public String editar(Intimacao intimacao) {
        this.cpfIntimado = intimacao.getCpfIntimado();
        this.cpfOficial = intimacao.getCpfOficial();
        this.dataExecucaoIntimacao = intimacao.getDataExecucaoIntimacao();
        this.dataIntimacao = intimacao.getDataIntimacao();
        this.endereçoIntimado = intimacao.getEndereçoIntimado();
        this.idIntimacao = intimacao.getId();
        this.isEfetivada = intimacao.isIsEfetivada();
        this.nomeIntimado = intimacao.getNomeIntimado();
        this.nomeOficialAlocado = intimacao.getNomeOficialAlocado();
        this.processo = intimacao.getProcesso();

        return "homeAdminIntimacao";
    }

    public String deletar(Intimacao intimacao) {
        
        
        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        // Inicia-se a transação 
        session.beginTransaction();
        session.delete(intimacao);
        // Efetua-se a consulta (aqui em HQL) 
        session.getTransaction().commit();
        session.close();

        intimacoes = IntimacaoDAO.getIntimacoes();

        

        return "homeAdminIntimacao";
    }

    public String getCpfOficial() {
        return cpfOficial;
    }

    public List<Intimacao> getIntimacoes() {
        return intimacoes;
    }

    public void setIntimacoes() {       
        
        Client client = ClientBuilder.newClient();
        
        try {
            Response r = client
                        .target("http://localhost:8080/webresources/intimacao")
                        .request(MediaType.APPLICATION_JSON)
                        .get();

            this.intimacoes = r.readEntity(new GenericType<List<Intimacao>>(){});  
           
            for (Intimacao i : intimacoes){            
                
                Intimacao intim = new Intimacao();
                
                intim.setId(i.getId());
                intim.setCpfIntimado(i.getCpfIntimado());
                intim.setCpfOficial(i.getCpfOficial());
                intim.setDataExecucaoIntimacao(null);
                intim.setDataIntimacao(i.getDataIntimacao());
                intim.setEndereçoIntimado(i.getEndereçoIntimado());
                intim.setIsEfetivada(false);
                intim.setNomeIntimado(i.getNomeIntimado());
                intim.setNomeOficialAlocado(i.getNomeOficialAlocado());
                intim.setProcesso(i.getProcesso());
                              
                
                save(intim);
            }
            
            
        }
        
        catch (Exception e){
            
        }
    }
    
    public static void save(Intimacao intimacaoREST) {
        Session s = null;
        try {
            s = factory.openSession();
            s.beginTransaction();
            s.saveOrUpdate(intimacaoREST);
            s.getTransaction().commit();
        } catch (HibernateException e) {
            System.out.println("IntimacaoService.save(): " + e.getMessage());
            throw e;
        } finally {
            s.close();
        }
    }
    

    public void setCpfOficial(String cpfOficial) {
        this.cpfOficial = cpfOficial;
    }

    public Date getDataIntimacao() {
        return dataIntimacao;
    }

    public void setDataIntimacao(Date dataIntimacao) {
        this.dataIntimacao = dataIntimacao;
    }

    public UIComponent getMybutton() {
        return mybutton;
    }

    public void setMybutton(UIComponent mybutton) {
        this.mybutton = mybutton;
    }

    public String getCpfIntimado() {
        return cpfIntimado;
    }

    public void setCpfIntimado(String cpfIntimado) {
        this.cpfIntimado = cpfIntimado;
    }

    public String getNomeIntimado() {
        return nomeIntimado;
    }

    public void setNomeIntimado(String nomeIntimado) {
        this.nomeIntimado = nomeIntimado;
    }

    public String getEndereçoIntimado() {
        return endereçoIntimado;
    }

    public void setEndereçoIntimado(String endereçoIntimado) {
        this.endereçoIntimado = endereçoIntimado;
    }

    public Date getDataExecucaoIntimacao() {
        return dataExecucaoIntimacao;
    }

    public void setDataExecucaoIntimacao(Date dataExecucaoIntimacao) {
        this.dataExecucaoIntimacao = dataExecucaoIntimacao;
    }

    public boolean isIsEfetivada() {
        return isEfetivada;
    }

    public void setIsEfetivada(boolean isEfetivada) {
        this.isEfetivada = isEfetivada;
    }

    public Long getOficialAlocado() {
        return oficialAlocado;
    }

    public void setOficialAlocado(Long oficialAlocado) {
        this.oficialAlocado = oficialAlocado;
    }

    public String getNomeOficialAlocado() {
        return nomeOficialAlocado;
    }

    public void setNomeOficialAlocado(String nomeOficialAlocado) {
        this.nomeOficialAlocado = nomeOficialAlocado;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public Long getIdIntimacao() {
        return idIntimacao;
    }

    public void setIdIntimacao(Long idIntimacao) {
        this.idIntimacao = idIntimacao;
    }
    
    public void restUpdate(){
                        
        Client client = ClientBuilder.newClient();            

        GenericEntity<List<Intimacao>> listaInt = 
            new GenericEntity<List<Intimacao>>(this.intimacoes){};

        try {
            Response r = client
                            .target("http://localhost:8080/webresources/intimacao/lista")
                            .request(MediaType.APPLICATION_JSON)
                            .post(Entity.json(listaInt));        
        }
        catch (Exception e) {
            
        }
    }
    
    public void salvarREST(Intimacao intimacaoREST) {
        Session session = NewHibernateUtil.getSessionFactory().
                openSession();
        session.beginTransaction();
        //pesquisa pelo cpf, caso não tenha retorna mensagem volta pra tela 

        Query queryOficial = session.createQuery("FROM OficialJustica WHERE cpf = :cpf");
        queryOficial.setString("cpf", intimacaoREST.getCpfOficial());
        OficialJustica oficial = (OficialJustica) queryOficial.uniqueResult();
        session.getTransaction().commit();

        session.beginTransaction();

        Intimacao intimacao;

        intimacao = new Intimacao(intimacaoREST.getDataIntimacao(),
                                  intimacaoREST.getCpfIntimado(),
                                  intimacaoREST.getNomeIntimado(),
                                  intimacaoREST.getEndereçoIntimado(),
                                  null,
                                  false,
                                  oficial.getNome(),
                                  intimacaoREST.getProcesso(),
                                  intimacaoREST.getCpfOficial(),
                                  intimacaoREST.getId()
                                    );

        session.saveOrUpdate(intimacao);
        session.getTransaction().commit();
        session.close();
    }
    
    public void restPut(Intimacao inti){
                        
        Client client = ClientBuilder.newClient();            

        try {
        Intimacao r = client
                        .target("http://localhost:8080/webresources/intimacao" + "/" + inti.getId())
                        .request(MediaType.APPLICATION_JSON)
                        .put(Entity.json(inti), Intimacao.class);        
        }
        catch (Exception e){
            
        }
    }

}
