import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Enzyme  {
	
	private ArrayList<String> _tags;
	private ArrayList<String> _values;
	
	/**Constructeur par défaut
	 * */
	public Enzyme(){
		this._tags	=new ArrayList<String>();
		this._values=new ArrayList<String>();
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
						
						for (int j=0; j<out.length;j++) {
							_tags.add(nCode.group(1));
							_values.add(out[j]);
						}
						break;
				
					default:
						_tags.add(nCode.group(1));
						_values.add(nCode.group(2));
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
	
	public String get_tags(){
		String out="";
		Boolean b=false;
		for(String s : this._tags) {
			if(b==true) {out+=",";}
			out+=s;
			b=true;
		}
		return out;
	}
	public ArrayList<String> get_values(){return this._values;}
	
	/**Affiche les informations enregistrées pour l'enzyme.
	 * */
	public void infos(){
		for (int i=0; i<_tags.size();i++) {
			System.out.println(_tags.get(i));
			//System.out.print(_tags.get(i)+" --> ");
			//System.out.println(_values.get(i));
		}
	}

}
