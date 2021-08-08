/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cotdolar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucas
 */
public class DolarHistory {
    RandomAccessFile historico;
    GregorianCalendar hoje;
    GregorianCalendar comeco;
    GregorianCalendar varDia;
    SecureRandom rng;
    double val;
    double maxVar;
    double dailyMaxDelta;
    double delta;
    
    DolarHistory(Calendar data, double initialValue, double maxVar) throws FileNotFoundException, UnsupportedEncodingException
    {
        hoje = (GregorianCalendar) data;
        hoje.set(Calendar.HOUR_OF_DAY, 12);
        hoje.set(Calendar.MINUTE, 0);
        hoje.set(Calendar.SECOND, 0);
        hoje.set(Calendar.MILLISECOND, 0);
        
        rng = new SecureRandom();
        
        comeco = new GregorianCalendar();
        comeco.setTimeInMillis(hoje.getTimeInMillis());
        comeco.add(Calendar.YEAR, -5);
        
        varDia = new GregorianCalendar();
        varDia.setTimeInMillis(comeco.getTimeInMillis());
        varDia.setLenient(false);
        
        val = initialValue; // Cotação inicia em (initialValue) BRL/USD.
        this.maxVar = maxVar; // % máximo de variação diária.
        
        generateLast5Years();
        
        historico = new RandomAccessFile("resources/dolarHist.dat", "r");
    }
    
    double generateNext()
    {
        if (varDia.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            varDia.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
        {
            return val;
        }
        
        dailyMaxDelta = val * (maxVar * 1.01); // Permitir aumento;
        val *= (1 - (maxVar / 2));
        if (val < 1) {
            val = Math.sqrt(val);
        }
        delta = rng.nextDouble();
        delta -= 0.5;
        delta = Math.sqrt(delta);
        delta = rng.nextDouble() * dailyMaxDelta;
        val += delta;
        
        return val;
    }
    
    long getDaysOffset(Calendar c1, Calendar c2)
    {
        long dt1 = c1.getTimeInMillis();
        long dt2 = c2.getTimeInMillis();
        return (Math.round((dt1 - dt2) / 86400000.f));
    }
    
    double getValorDia(long numDiasOffset)
    {
        try {
            //System.out.printf("Seek diff: %d\n", 32 * numDiasAtras);
            
            historico.seek(16 * numDiasOffset);
            String cotS = historico.readLine();
            double cotD = Double.valueOf(cotS);
            return cotD;
        } catch (IOException ex) {
            Logger.getLogger(DolarHistory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.printf("Unhandled exception (getValorDia): %s\n", ex.toString());
            Logger.getLogger(DolarHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    String getValorDiaString(int dia, int mes, int ano)
    {
        
        try {
            varDia.set(Calendar.YEAR, ano);
            varDia.set(Calendar.MONTH, mes);
            varDia.set(Calendar.DAY_OF_MONTH, dia);
            varDia.set(Calendar.HOUR_OF_DAY, 12);
            varDia.set(Calendar.MINUTE, 0);
            varDia.set(Calendar.SECOND, 0);
            varDia.set(Calendar.MILLISECOND, 0);
            
            //System.out.printf("Diferença: %d dias\n", getDaysOffset(varDia, hoje));
            
            if (getDaysOffset(varDia, comeco) < 0 || getDaysOffset(varDia, hoje) > 0)
            return String.format(new Locale("en"), "Data fora do período amostrado: %2d/%2d/%4d.",
                    varDia.get(Calendar.DAY_OF_MONTH), varDia.get(Calendar.MONTH) + 1, varDia.get(Calendar.YEAR));

            double cotacaoDoDia = getValorDia(getDaysOffset(varDia, comeco));
            String ret = String.format(new Locale("en"), "Cotação do dia %2d/%2d/%4d: US$ 1.0000 = R$ %.4f",
                        varDia.get(Calendar.DAY_OF_MONTH), varDia.get(Calendar.MONTH) + 1, varDia.get(Calendar.YEAR),
                        cotacaoDoDia);

            return ret;
        } catch (ArrayIndexOutOfBoundsException ex) {
            return String.format("Data inválida: %2d/%2d/%4d", dia, mes, ano);
        }
    }
    
    void generateLast5Years()
    {
        try {
            PrintWriter dolarHistory;
            dolarHistory = new PrintWriter("resources/dolarHist.dat", "UTF-8");
            
            while (getDaysOffset(varDia, hoje) <= 0)
            {
                dolarHistory.write(String.format(new Locale("en"), "%015.6f\n", generateNext()));
                varDia.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            dolarHistory.close();
        } catch (FileNotFoundException ex) {
            System.out.printf("Error: %s\n", ex.toString());
            Logger.getLogger(DolarHistory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            System.out.printf("Error: %s\n", ex.toString());
            Logger.getLogger(DolarHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
