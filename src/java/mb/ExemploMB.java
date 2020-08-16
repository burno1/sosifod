/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author razer
 */
@Named(value = "exemploMB")
@RequestScoped
public class ExemploMB {

    private String nome;
    private String senha;

    private int numero;

    public ExemploMB() {
    }

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
        return "homeAdmin";
    }
}
