/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author cassiano
 */
public class Ferramentas {

    private static Ferramentas instance;

    public static Ferramentas getInstance() {
        if (instance == null) {
            instance = new Ferramentas();
        }
        return instance;
    }

    public static String removerCaracteresCpf(String cpf) {
        String cpfLimpo = cpf.replace(".", "");
        cpfLimpo = cpfLimpo.replace("-", "");
        return cpfLimpo;
    }

    public static String removerCaracteresCep(String cep) {
        String cepLimpo = cep.replace("-", "");
        return cepLimpo;
    }

    public static String convertStringToMD5(String valor) {
        MessageDigest mDigest;
        try {
            //Instanciamos o nosso HASH MD5, poderíamos usar outro como
            //SHA, por exemplo, mas optamos por MD5.
            mDigest = MessageDigest.getInstance("MD5");

            //Convert a String valor para um array de bytes em MD5
            byte[] valorMD5 = mDigest.digest(valor.getBytes("UTF-8"));

            //Convertemos os bytes para hexadecimal, assim podemos salvar
            //no banco para posterior comparação se senhas
            StringBuffer sb = new StringBuffer();
            for (byte b : valorMD5) {
                sb.append(Integer.toHexString((b & 0xFF)
                        | 0x100).substring(1, 3));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
