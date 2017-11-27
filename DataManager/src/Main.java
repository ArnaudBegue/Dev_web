import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Main {

	 public static void main(String[] args) {
		 /*
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("the-file-name.txt", true));
		} catch (IOException e3) {
			e3.printStackTrace();
		};
		 */
		 Parser p = new Parser("/home/taylor/Dropbox/Partage/Projet_web/DataManager/Data/db_enzyme.txt");
		 //p.describe();

		 
		 p.dbConnect();		 
		 
		 /////////////// SCHEMAS RELATIONNELS ///////////////
		 //Tables
		 p.sendToDB("CREATE TABLE IF NOT EXISTS reaction (EC VARCHAR(20) PRIMARY KEY, S_NAME TEXT,COMMENTS TEXT,DISEASE TEXT);");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS o_name (other_name TEXT PRIMARY KEY);");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS activity (name TEXT PRIMARY KEY);");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS cofactors (cof_name TEXT PRIMARY KEY);");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS swissprot (id_swiss TEXT PRIMARY KEY, name TEXT);");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS prosite (id_prosite TEXT PRIMARY KEY);");
		 
		 //Relations
		 p.sendToDB("CREATE TABLE IF NOT EXISTS possede (EC VARCHAR(20), other_name TEXT,FOREIGN KEY(EC) REFERENCES reaction(EC), FOREIGN KEY(other_name) REFERENCES o_name(other_name));");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS pcatalyse (EC VARCHAR(20), id_prosite TEXT, FOREIGN KEY(EC) REFERENCES reaction(EC), FOREIGN KEY(id_prosite) REFERENCES prosite(id_prosite));");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS scatalyse (EC VARCHAR(20), id_swiss TEXT, FOREIGN KEY(EC) REFERENCES reaction(EC), FOREIGN KEY(id_swiss) REFERENCES swissprot(id_swiss));");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS abesoin (EC VARCHAR(20), cof_name TEXT, FOREIGN KEY(EC) REFERENCES reaction(EC), FOREIGN KEY(cof_name) REFERENCES cofactors(cof_name));");
		 p.sendToDB("CREATE TABLE IF NOT EXISTS catalyse (EC VARCHAR(20), name TEXT, FOREIGN KEY(EC) REFERENCES reaction(EC), FOREIGN KEY(name) REFERENCES activity(name));");
		
		 //Functions
		 p.sendToDB("DROP FUNCTION insert_prosite_ine(text);" + 
		 		"CREATE OR REPLACE FUNCTION insert_prosite_ine(text) RETURNS VOID AS $BODY$" + 
		 		"BEGIN" + 
		 		"    IF NOT EXISTS (SELECT id_prosite FROM prosite WHERE id_prosite=$1) THEN " + 
		 		"    INSERT INTO prosite VALUES ($1); " + 
		 		"    END IF; " + 
		 		"END " + 
		 		"$BODY$ " + 
		 		"LANGUAGE 'plpgsql' ;");
		 ///////////////////////////////////////////////////
		 
		 
		 p.sendToDB("begin transaction;");
		 	 
		 ///////////////////////// AJOUT DES ENREGISTREMENTS /////////////////////////
		 for (Enzyme e : p.getReactions()){
			 String db_records = e.formatRecord();
			 
			 /*try {
				 writer.append(db_records);
			 } catch (IOException e1) {
				 e1.printStackTrace();
			 }*/

			 try {
				 p.sendToDB(db_records);
			 } catch (Exception e2) {
				 System.out.println(e.toString());
				 e2.printStackTrace();
			 }
		 }
		 ///////////////////////////////////////////////////////////////////////////
		 
		 p.sendToDB("commit;end transaction;");
		 p.sendToDB("ANALYSE");
		 p.dbDisconnect();
		 /*
		 try {
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		

		 System.out.println("Création Terminée !");		 
	 }
}
