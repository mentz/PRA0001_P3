/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cotdolar;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucas
 */
public class CotDolar {
    static GregorianCalendar hoje;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        hoje = new GregorianCalendar();
        DolarHistory dhg;
        try {
            dhg = new DolarHistory(hoje, 2.02, 0.05);
            System.out.println("Cotações criadas com sucesso!");
            int qd, qm, qy;
            while (true)
            {
                System.out.println("\n\nInsira a data que deseja buscar a cotação dd mm aaaa, ou '0' caso queira parar.");
                qd = in.nextInt();
                if (qd == 0) break;
                qm = in.nextInt(); qm--;
                qy = in.nextInt();
                
                System.out.printf("%s\n", dhg.getValorDiaString(qd, qm, qy));
            }
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(CotDolar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
