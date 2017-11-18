import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Parser {
	
	
	private ArrayList<Enzyme> ez_list;
	private BufferedReader br;
	private String ligne;
	private String filename;
	private Connection conn;
	
	public Parser(String ficname) {
		this.ez_list = new ArrayList<Enzyme>();
		this.ligne="";
		this.filename=ficname;
		readfile();
	}
	
	public ArrayList<Enzyme> getReactions(){return this.ez_list;}
	
	
	/**Lit le fichier de données et ajoute à la liste d'enzymes les enzymes du fichier lu.
	 * @return 0 si tout s'est bien passé, 1 sinon*/
	private void readfile() {
		
		try {
			
			br = new BufferedReader(new FileReader(filename));
			
			while((ligne = br.readLine())!=null){ //Tant qu'on peut récupérer des lignes
				Enzyme current_ez=new Enzyme();
				current_ez.setAttributes(ligne);
				ez_list.add(current_ez);
			}
			
		} catch(FileNotFoundException e){ //Si erreur de lecture 
			e.printStackTrace();
		} catch(IOException e){ //Si erreur de lecture 
			e.printStackTrace();
		} finally { //Fermeture du fichier
			try{
				if (br != null){br.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**Ajoute l'enzyme ainsi que ses informations dans la base de données.
	 * @return 0 si tout s'est bien passé, 1 sinon*/
	public void sendToDB(String instruction) {
			// javac -cp /home/beguearnaud/Dropbox/M2_bibs/Prog.web/projet/postgresql-42.1.4.jar co.java 
			// java -cp .:/home/beguearnaud/Dropbox/M2_bibs/Prog.web/projet/postgresql-42.1.4.jar co 
			try {
	
			    //Création d'un objet Statement
			    Statement state = this.conn.createStatement();
			    //L'objet ResultSet contient le résultat de la requête SQL
			    state.executeUpdate(instruction);
			    state.close();
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}


	}
	
	public void dbConnect() {
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Driver O.K.");

			String url = "jdbc:postgresql://localhost:5432/dataenz";
			String user = "arno";
			String passwd = "0000";

			this.conn = DriverManager.getConnection(url, user, passwd);
			System.out.println("Connexion effective !");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void dbDisconnect() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**Ajoute l'enzyme ainsi que ses informations dans la base de données.
	 * @return 0 si tout s'est bien passé, 1 sinon*/
	public int getFromDB(String instruction) {
			// javac -cp /home/beguearnaud/Dropbox/M2_bibs/Prog.web/projet/postgresql-42.1.4.jar co.java 
			// java -cp .:/home/beguearnaud/Dropbox/M2_bibs/Prog.web/projet/postgresql-42.1.4.jar co 
			try {
				Class.forName("org.postgresql.Driver");
				System.out.println("Driver O.K.");
	
				String url = "jdbc:postgresql://localhost:5432/dataenz";
				String user = "arno";
				String passwd = "0000";
	
				Connection conn = DriverManager.getConnection(url, user, passwd);
				System.out.println("Connexion effective !");         
	
			    //Création d'un objet Statement
			    Statement state = conn.createStatement();
			    //L'objet ResultSet contient le résultat de la requête SQL
			    ResultSet result = state.executeQuery(instruction);
			    //On récupère les MetaData
			    ResultSetMetaData resultMeta = result.getMetaData();
			    
			    System.out.println("\n**********************************");
			    
			    //On affiche le nom des colonnes
			    for(int i = 1; i <= resultMeta.getColumnCount(); i++)
			    	System.out.print("\t" + resultMeta.getColumnName(i).toUpperCase()+"\t *");

			    System.out.println("\n**********************************");
			    
			    while(result.next()){
			    	for(int i = 1; i <= resultMeta.getColumnCount(); i++)
			    		System.out.print("\t" + result.getObject(i).toString() + "\t |");
			    	
			        System.out.println("\n---------------------------------");
			    }

			    result.close();
			    state.close();
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		return 0;
	}
	
	public void describe() {
		for (Enzyme e: this.ez_list) {
			e.infos();
		}
	}
	
	
}
