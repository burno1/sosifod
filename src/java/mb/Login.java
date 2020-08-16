package mb;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static com.mchange.v2.c3p0.impl.C3P0Defaults.user;
import dao.LoginDAO;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import util.Ferramentas;
import util.SessionUtils;

/**
 *
 * @author Bruno Fernandes
 */
@ManagedBean
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {

    private String nome;
    private String senha;

    /**
     * Creates a new instance of Login
     */
    public Login() {
    }

    //validate login
    public String validateUsernamePassword() {
        String nomeToValidate = Ferramentas.removerCaracteresCpf(nome);
        senha = Ferramentas.convertStringToMD5(senha);
        boolean valid = LoginDAO.validate(nomeToValidate, senha);
        if (valid ) {
            int tipoUser = LoginDAO.getUserRole(nomeToValidate);
            String toDirect = null;
            if(tipoUser == 1){
                toDirect = "homeAdmin";
            }
            else {
                toDirect = "homeOficial";
            }
            
            HttpSession session = SessionUtils.getSession();
            session.setAttribute("username", nome);
            session.setAttribute("tipoUser", tipoUser);
            
            return toDirect;
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Incorrect Username and Passowrd",
                            "Please enter correct username and Password"));
            return "index";
        }
    }

    //logout event, invalidate session
    public String logout() {
        this.senha = null;
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        return "index";
    }
    private int numero;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        System.out.println(nome);
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        System.out.println(senha);
        this.senha = senha;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String goHome() {
        //se admin
        if (true) {

        } else {
            return "homeAdmin";
        }
        System.out.println(this.nome + this.senha);

        return "homeAdmin";
    }

}
