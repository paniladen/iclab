/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iclab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author pani
 */
public class Assertions {
    
    List<String> liste = new LinkedList<String>();
    List<Integer> AssertionStarts = new LinkedList<Integer>();

    void testCheck() {
        String check = "";
        for (int i = 0; i < AssertionStarts.size(); i++) {
            check = "";
            int tmp = 0;
            if (AssertionStarts.size() == i + 1) {
                tmp = liste.size();
            } else {
                tmp = AssertionStarts.get(i + 1);
            }

            for (int j = AssertionStarts.get(i) + 8; j < tmp; j++) {
                check += liste.get(j) + "";
            }
            Statement statement;
            try {
                statement = connection.createStatement();
                statement.execute("SELECT * FROM TestSysRel WHERE " + check);
                statement.execute("INSERT INTO AssertionSysRel VALUES (\'" + liste.get(AssertionStarts.get(i) + 4) + "\',\'" + check + "\')");
                statement.close();
                System.out.println("Assertion >>" + liste.get(AssertionStarts.get(i) + 4) + "<< wurde erfolgreich in AssertionSysRel eingetragen");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                //System.out.println(e.getErrorCode());
                //System.out.println(e.getMessage());
                //e.printStackTrace();
                errors(e);
            }

            //liste.get(AssertionStarts.get(i)+4)
        }

        //	 ();
    }

    void listausgabe(List<String> ls) {
        Iterator it = ls.iterator();
        while (it.hasNext()) {
            String value = (String) it.next();

            System.out.print(value);
        }
    }

    private void getAssertionStarts() {
        System.out.print("[] Search Assertions");
        if (liste.isEmpty()) {
            System.out.println("								Failure");
            System.out.println(">> list is Empty");
        }
        List<Integer> perfectSyntaxPos = new LinkedList<Integer>();

        for (int i = 0; i < liste.size(); i++) {
//				if(i%512==1 && i >=512) {
//					System.out.println("	");
//					System.out.print("	");
//		    	}
            if (liste.get(i).equals("CREATE") && liste.get(i + 2).equals("ASSERTION") && liste.get(i + 6).equals("CHECK")) {
                perfectSyntaxPos.add(i);
                //System.out.print(" " + liste.get(i+4) + ",");
            }
            if (i > 5 && i + 5 < liste.size()) {
                if (liste.get(i).equals("CREATE") && !liste.get(i + 2).equals("ASSERTION")) {
                    System.out.println("	");
                    System.out.println("In Statement " + perfectSyntaxPos.size() + " fehlt ASSERTION");
                    System.out.println("Use CREATE ASSERTION 'Assertionname' CHECK ()");
                    break;
                }
                if (liste.get(i).equals("CREATE") && liste.get(i + 2).equals("ASSERTION") && !liste.get(i + 6).equals("CHECK")) {
                    System.out.println("CHECK fehlt");
                    System.out.println("Use CREATE ASSERTION 'Assertionname' CHECK ()");
                    break;
                }
                if (!liste.get(i - 2).equals("CREATE") && liste.get(i).equals("ASSERTION")) {
                    System.out.println("CREATE fehlt");
                    System.out.println("Use CREATE ASSERTION 'Assertionname' CHECK ()");
                    break;
                }
                if (!liste.get(i - 6).equals("CREATE") && liste.get(i).equals("CHECK")) {
                    System.out.println("CREATE fehlt");
                    System.out.println("Use CREATE ASSERTION 'Assertionname' CHECK ()");
                    break;
                }
            }
        }
        AssertionStarts = perfectSyntaxPos;
        System.out.println("								OK");
        System.out.println(">> Found " + AssertionStarts.size() + " Assertions");

    }

    private void out(String s, BreakIterator iter) {
        int last = iter.first();
        int next = iter.next();    // erstes Wort einlesen.

        for (; next != BreakIterator.DONE; next = iter.next()) {
            //if (s.subSequence( last, next).equals("CREATE")){
            //System.out.println("ES IST GLEICH");
            //
            //  System.out.println(liste.size());
            //liste.clear();
            //}
            liste.add((String) s.subSequence(last, next));
            last = next;
        }
    }

    public void lesedatei() throws IOException {

        try {
            System.out.print("[] Open Assertion.sql");
            BufferedReader in = new BufferedReader(new FileReader("/home/pani/Dropbox/uni/labIC/Phase 1/miniwelt_assertion_fehler_jan.sql"));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
                //System.out.println("Gelesene Zeile: " + zeile);
                BreakIterator iterator = BreakIterator.getWordInstance();
                iterator.setText(zeile);
                out(zeile, iterator);
            }
            System.out.println("								OK");
        } catch (IOException e) {
            System.out.println("								FAILURE");
            e.printStackTrace();
        }
    }

    private void errors(SQLException e) {
        //System.out.println("Neuer Aufruf von Errors");
        String newvalue = "";
        int bla = 0;
        int loesch = 0;
        String found = "";
        found = e.getMessage().replaceAll("(\\r|\\n)", "<");
        for (int i = 0; i < found.length(); i++) {
            if (found.substring(i, i + 1).equals("\"")) {
                bla++;
                if (bla % 2 == 0) {
                    loesch = 1;
                }
            }
            if (found.substring(i, i + 1).equals("<")) {
                break;
            }
            if (bla % 2 == 0 && loesch == 0) {
                newvalue += found.substring(i, i + 1);
            }
            loesch = 0;
        }

        //System.out.println(newvalue);

        HashMap zuordnung = new HashMap();
        zuordnung.put("ERROR: duplicate key value violates unique constraint ", "1");
        zuordnung.put("ERROR: relation  does not exist", "2");

        if (zuordnung.containsKey(newvalue)) {
            System.out.println(zuordnung.get(newvalue).toString());
        } else {
            System.out.println(e);
        }

    }
}
