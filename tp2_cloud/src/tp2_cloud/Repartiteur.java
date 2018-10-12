package tp2_cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

public class Repartiteur {

	
	public static void main(String args[]) throws NamingException, IOException {
		String file="";
		int result=0;
		if(args.length>0) {
			
		//recupere la requete du client
			file=args[0];
			String ldapIP="192.168.0.2";//exemple d'adresse ip
			int port=389;//port
		//recupere les capacites et IP des serveurs
			RepertoireNoms repertoireNoms= new RepertoireNoms(ldapIP,port);
			
			repertoireNoms.ajouterRepartiteur("username","password");
			
			int nombreServeurs=repertoireNoms.getNombreServeurs();
			
			String[] IpServeurs=new String[nombreServeurs];
			
			IpServeurs=repertoireNoms.getIpServeurs();
			
			int[] CapaciteServeurs=new int[nombreServeurs];
			
			CapaciteServeurs=repertoireNoms.getCapaciteServeurs();
			
			
		//diviser le travail selon la capacite des serveurs
			Travail travail=new Travail(file);
			
			int[] NbLinesServer=new int[nombreServeurs];//tableau contient le nombre de lines de chaque serveur
			
			NbLinesServer=travail.diviserLines(CapaciteServeurs);//retourne les noms des fichiers crees, cad un tableau de strings
			
			Map<String,String> dataServer = new HashMap<String, String>();
			
		    List<Map<String , String>> dataServers  = new ArrayList<Map<String,String>>();
		    
			dataServers=travail.dataToSend(NbLinesServer, file);
			
		//communique avec les serveurs(donner le travail a faire, et recevoir le resultat)
			Repartiteur repartiteur=new Repartiteur();
			
			ServeurThread[] serveursThreads=new ServeurThread[nombreServeurs];
			
			serveursThreads=repartiteur.createThreads(IpServeurs, dataServers);
			
			repartiteur.beginThreads(serveursThreads);
			
			//on verifier si tous les serveurs ont donné le bon resultat
			
			//if(repartiteur.isMalicieux());
			
			result=repartiteur.finalResult(serveursThreads);
			
			System.out.println("le résultat est: "+result);
			
			
		}
		else {
			System.out.println("Vous devez donner le fichier comme argument.");
		}
	}
	
	public Repartiteur() {
		
	}
	
	private ServeurThread[] createThreads(String[] IpServeurs, List<Map<String , String>> dataServers) {
		ServeurThread[] serveurThread=new ServeurThread[IpServeurs.length];
		int i=0;
		for (Map<String, String> data : dataServers) {
			serveurThread[i]=new ServeurThread(IpServeurs[i], data);
			i+=1;
		}
		return serveurThread;
	}
	
	private void beginThreads(ServeurThread[] serveurThreads) {
		int i=0;
		while(i<serveurThreads.length) {
			new Thread(serveurThreads[i]).start();
			i+=1;
		}
	}

	private int finalResult(ServeurThread[] serveurThreads) {
		int result=0;
		for(ServeurThread t:serveurThreads) {
			while(t.getResult()==0) {
			result+=result+t.getResult();
			}
		}
		return result;
	}
}
