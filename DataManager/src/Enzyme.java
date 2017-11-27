import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Enzyme  {
	
	public static int count=0;
	private HashMap _hm;
	
	/**Constructeur par défaut
	 * */
	public Enzyme(){
		this._hm=new HashMap<String, ArrayList<String>>();
	}
	
	/**Extrait les attributs d'une enzyme et les sauvegarde.
	 * @line : la ligne contenant les informations sous forme de balises pour une enzyme donnée.*/
	public void setAttributes(String line) {
		String attrsTab[]=line.split("\t");
		Pattern balise = Pattern.compile("<([^>]+)>(.*)</.+>");
		
		for (int i=0; i<attrsTab.length;i++) {	
			Matcher nCode = balise.matcher(attrsTab[i]);

			if(nCode.find()) {
				//System.out.println((nCode.group(1)) instanceof String);
				switch(nCode.group(1)) {
				
					case "ACTIVITY":
						String out[]=nCode.group(2).split("\\.");
						ArrayList<String> vals=new ArrayList<String>();
						for (int j=0;j<out.length;j++) {vals.add(out[j]);}
						this._hm.put(nCode.group(1), vals);
						
						break;
					case "PROSITE":
						String pout[]=nCode.group(2).split(";");
						ArrayList<String> pvals=new ArrayList<String>();
						for (int j=0;j<pout.length;j++) {pvals.add(pout[j]);}
						this._hm.put(nCode.group(1), pvals);
						break;
					
					case "SWISSPROT":
						String sout[]=nCode.group(2).split(";");
						ArrayList<String> svals=new ArrayList<String>();
						for (int j=0;j<sout.length;j++) {svals.add(sout[j]);}
						this._hm.put(nCode.group(1), svals);
						break;
				
					default:
						this._hm.put(nCode.group(1),nCode.group(2));
						break;
				/*
				if(nCode.group(1).equals("ACTIVITY")) {
					//System.out.println(nCode.group(2));
				}else{
				
				}*/
				}
			}
		}
	}

	public HashMap<String, ArrayList<String>> get_hm() {return this._hm;}
	
	public String insert_ine(String table, String attribute, String value) {
		String str="DO $$ " + 
				"BEGIN " + 
				"    IF NOT EXISTS (SELECT * FROM "+table+" WHERE "+attribute+"='"+value+"') THEN" + 
				"    INSERT INTO "+table+" VALUES ('"+value+"');" + 
				"    END IF;" + 
				"END " + 
				"$$;\n";
		
		return str;
	}
	
	public String formatRecord() {
		//REACTION//
		String instReaction	="";
		if (!_hm.get("EC").toString().isEmpty()) {
			instReaction="INSERT INTO reaction (EC,S_NAME,COMMENTS,DISEASE) VALUES('"+_hm.get("EC")+"', '"+_hm.get("S_NAME").toString().replace("'" , "''")+"', '"+_hm.get("COMMENTS").toString().replace("'" , "''")+"', '"+_hm.get("DISEASE").toString().replace("'" , "''")+"');\n";
		}
		///////////
		
		//PROSITE & Relation: pcatalyse//
		String instProsite	="";
		String instPcatalyse="";
		
		for (String vals : (ArrayList<String>)this._hm.get("PROSITE")) { 
			if (!vals.isEmpty()) {
				instProsite+=insert_ine("prosite", "id_prosite", vals);
				if (!_hm.get("EC").toString().isEmpty()) {instPcatalyse+="INSERT INTO pcatalyse (EC, id_prosite) VALUES('"+_hm.get("EC")+"','"+vals+"');\n";}
			}
		}
		//////////
		
		//SWISSPROT & Relation: scatalyse//
		String instSwissprot="";
		String instScatalyse="";
		for (String vals : (ArrayList<String>)this._hm.get("SWISSPROT")) {
			vals = vals.replace("'" , "''");
			String[] tabVals=vals.split(",");
			if(tabVals.length==2) {
				instSwissprot+="DO $$"
						+ "BEGIN "
							+ "IF NOT EXISTS (SELECT id_swiss FROM swissprot WHERE id_swiss='"+tabVals[0]+"') THEN"
							+ " INSERT INTO swissprot  (id_swiss, name) VALUES('"+tabVals[0]+"','"+tabVals[1]+"');"
						+ " END IF; END $$;\n";
				if (!_hm.get("EC").toString().isEmpty()) {
					instScatalyse+="INSERT INTO scatalyse (EC, id_swiss) VALUES('"+_hm.get("EC")+"','"+tabVals[0]+"');\n"; //tabVals[0] = id_swiss
				}
			}
		}//sinon la balise est vide et on ajoute rien
			//instSwissprot+="INSERT INTO swissprot  (id_swiss, name) VALUES('"+tabVals[0]+"','"+tabVals[1]+"');\n";
			

		////////////
		
		//COFACTORS & Relation: ABesoin//
		String instCofactors="";
		String instABesoin="";
		if (!_hm.get("COFACTORS").toString().isEmpty()) {
			instCofactors=insert_ine("cofactors","cof_name",_hm.get("COFACTORS").toString().replace("'" , "''"));
			if (!_hm.get("EC").toString().isEmpty()) {
				instABesoin="INSERT INTO abesoin (EC,cof_name) VALUES('"+_hm.get("EC")+"','"+_hm.get("COFACTORS").toString().replace("'" , "''")+"');\n";
			}
		}
		/////////////
		
		//ACTIVITY & Relation: catalyse//
		String instActivity="";
		String instCatalyse="";
		for (String vals : (ArrayList<String>)this._hm.get("ACTIVITY")) {
			if(!vals.isEmpty()) {
				vals=vals.replace("'" , "''");			
				instActivity+=insert_ine("activity", "name", vals);
				if (!_hm.get("EC").toString().isEmpty()) {
					instCatalyse+="INSERT INTO catalyse  (EC, name) VALUES('"+_hm.get("EC")+"','"+vals+"');\n";
				}
			}
		}
		///////////
		
		//O_NAME & Relation: possede//
		String instOname="";
		String instPossede ="";
		if (!_hm.get("O_NAME").toString().isEmpty()) {
			instOname= insert_ine("o_name","other_name", _hm.get("O_NAME").toString().replace("'" , "''"));
			if (!_hm.get("EC").toString().isEmpty()) {
				instPossede ="INSERT INTO possede (EC, other_name) VALUES('"+_hm.get("EC")+"','"+_hm.get("O_NAME").toString().replace("'" , "''")+"');\n";
			}
		}
		/////////
		
		return (instReaction+instProsite+instPcatalyse+instSwissprot+instScatalyse+instCofactors+instABesoin+instActivity+instCatalyse+instOname+instPossede);
	}
	
	
	/**Affiche les informations enregistrées pour l'enzyme.
	 * */
	public void infos(){
		
		for(Object s : _hm.keySet()) {
			System.out.print(s+" --> ");
			System.out.println(_hm.get(s));
		}
	}

}
