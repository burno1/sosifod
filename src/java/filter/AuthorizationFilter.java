package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"*.xhtml"})
public class AuthorizationFilter implements Filter {

    public AuthorizationFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {

            HttpServletRequest reqt = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            HttpSession ses = reqt.getSession(false);

            String reqURI = reqt.getRequestURI();
            if (reqURI.contains("/index.xhtml")
                    || (ses != null && ses.getAttribute("username") != null)
                    || reqURI.contains("/public/")
                    || reqURI.contains("javax.faces.resource")) {
                
                chain.doFilter(request, response);
            } else {
                resp.sendRedirect(reqt.getContextPath() + "/faces/index.xhtml");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
/*
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        String userName = null;
        int perfil = 0;
        String urlDestino = "";
        HttpServletRequest reqt = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession ses = reqt.getSession(false);
        String url = reqt.getRequestURI();

        if (ses != null) {
            userName = ses.getAttribute("username").toString();
            perfil = Integer.valueOf(ses.getAttribute("tipoUser").toString());
        }

        if (userName == null) {
            System.out.println(url);
            if (url.indexOf("index.xhtml") >= 0) {
                chain.doFilter(request, response);
            } else {
                resp.sendRedirect(reqt.getServletContext().getContextPath() + "/index.xhtml");
            }
            // Se tem uma pessoa logada
        } else {
            if (url.indexOf("/index.xhtml") >= 0) {
                urlDestino = checkPerfil(perfil);
                resp.sendRedirect(reqt.getServletContext().getContextPath() + urlDestino);
            } else if (url.indexOf("admin") >= 0) {
                if (perfil != 1) {
                    urlDestino = checkPerfil(perfil);
                    resp.sendRedirect(reqt.getServletContext().getContextPath() + urlDestino);
                } else {
                    chain.doFilter(request, response);
                }
            } else if (url.indexOf("Oficial") >= 0) {
                if (perfil != 2 ) {
                    urlDestino = checkPerfil(perfil);
                    resp.sendRedirect(reqt.getServletContext().getContextPath() + urlDestino);
                } else {
                    chain.doFilter(request, response);
                }
            } else {
                chain.doFilter(request, response);
            }
        }

    }*/

    @Override
    public void destroy() {

    }

    private String checkPerfil(int perfil) {
        String url = "/index.xhtml"; // Só por padrão

        switch (perfil) {
            case 1:
                url = "/homeAdmin.xhtml";
                break;
            case 2:
                url = "/homeOficial.xhtml";
                break;
            default:
                url = "/index.xhtml";
        }
        return url;
    }
}
