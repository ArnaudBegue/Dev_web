import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Main {

	 public static void main(String[] args) {
	 
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("the-file-name.txt", true));
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		};
		 
		 Parser p = new Parser("/home/taylor/Dropbox/Partage/Projet_web/DataManager/Data/db_enzyme.txt");
		 p.describe();
		 
		 p.dbConnect();
		 p.sendToDB(
		 	"CREATE TABLE reaction (EC VARCHAR(20),"
		 	+"S_NAME TEXT,"
		 	+"COMMENTS TEXT,"
		 	+"DISEASE TEXT,"
		 	+"PRIMARY KEY (EC));"
		 );
		 
		 
		 /////////////// SCHEMAS RELATIONNELS ///////////////
		 //Tables
		 p.sendToDB("CREATE TABLE o_name (other_name TEXT, PRIMARY KEY (other_name));");
		 p.sendToDB("CREATE TABLE activity (name TEXT, PRIMARY KEY (name));");
		 p.sendToDB("CREATE TABLE cofactors (cof_name TEXT, PRIMARY KEY (cof_name));");
		 p.sendToDB("CREATE TABLE swissprot (id_swiss TEXT, PRIMARY KEY (id_swiss));");
		 p.sendToDB("CREATE TABLE prosite (id_prosite TEXT, PRIMARY KEY (id_prosite));");
		 
		 //Relations
		 p.sendToDB("CREATE TABELE possede (EC VARCHAR(20), other_name TEXT, FOREING KEY(EC) REFERENCES reaction(EC), FOREING KEY(other_name) REFERENCES o_name(other_name));");
		 p.sendToDB("CREATE TABLE pcatalyse(EC VARCHAR(20), id_prosite TEXT, FOREING KEY(EC) REFERENCES reaction(EC), FOREING KEY(id_prosite) REFERENCES prosite(id_prosite));");
		 p.sendToDB("CREATE TABLE scatalyse(EC VARCHAR(20), id_swissprot TEXT, FOREING KEY(EC) REFERENCES reaction(EC)), FOREING KEY(id_swissprot) REFERENCES swissprot(id_swissprot);");
		 p.sendToDB("CREATE TABLE abesoin(EC VARCHAR(20), cof_name TEXT, FOREING KEY(EC) REFERENCES reaction(EC), FOREING KEY(cof_name) REFERENCES cofactors(cof_name));");
		 p.sendToDB("CREATE TABLE catalyse(EC VARCHAR(20), nom_activity TEXT, FOREING KEY(EC) REFERENCES reaction(EC)), FOREING KEY(id_swissprot) REFERENCES swissprot(id_swissprot);");
		 ///////////////////////////////////////////////////
		 
		 
		 p.sendToDB("begin transaction;");
		 String str="";
		 int count=0;
		 int percentage=0;
		 int total_size = p.getReactions().size();
		 
		 ///////////////////////// AJOUT DES ENREGISTREMENTS /////////////////////////
		 for (Enzyme e : p.getReactions()){
			ArrayList<String> list_values = e.get_values();
			str +="INSERT INTO reaction (EC, S_NAME, COMMENTS, DISEASE) VALUES(";
			
			for(int i=0; i<list_values.size()-1;i++) {
				str+="\'"+list_values.get(i).replace("'" , "''")+"\'"+",";
			}
			str+="\'"+list_values.get(list_values.size()-1).replace("'" , "''")+"\'"+");\n";
			count++;
			System.out.println("Count:"+count);
			
			try {
				writer.append(str);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				p.sendToDB(str);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			str="";
			
			/*
			if(Math.round((count/total_size)*100)>percentage) {
				percentage=Math.round((count/total_size)*100);
				System.out.println("nbLignesTraitées:"+count+" "+percentage+"%");
			}*/
		 }
		 ///////////////////////////////////////////////////////////////////////////
		 p.sendToDB("commit;end transaction;");
		 p.dbDisconnect();
		 try {
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		 

		 System.out.println("Création Terminée !");		 
	 }
}
