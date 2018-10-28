package tp2_cloud;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.naming.NamingException;

public class Repartiteur {

	
	public static void main(String args[]) throws NamingException, IOException {
		String file="";
		int result=0;
		String IpServeurNoms=null;
		if(args.length>0) {
			
		//recupere la requete du client
			file=args[0];
			IpServeurNoms=args[1];
		//recupere les capacites et IP des serveurs, on les recupere du serveur de noms, a travers RMI
			CommunicationServeurNoms repertoireNoms= new CommunicationServeurNoms();
			NomsInterface stubNoms=repertoireNoms.loadServerStub(IpServeurNoms);
			
			stubNoms.ajouterRepartiteur("username","password");
			
			int nombreServeurs=stubNoms.getNombreServeurs();
			
			String[] IpServeurs=new String[nombreServeurs];
			
			IpServeurs=stubNoms.getIpServeurs();
			
			int[] CapaciteServeurs=new int[nombreServeurs];
			
			CapaciteServeurs=stubNoms.getCapaciteServeurs();
			
			
		//diviser le travail selon la capacite des serveurs
			Travail travail=new Travail(file);
			
			int[] NbLinesServer=new int[nombreServeurs];//tableau contient le nombre de lines de chaque serveur
			
			NbLinesServer=travail.diviserLines(CapaciteServeurs);//le nombre de lignes pour chaque serveur
			
		    List<Map<String , String>> dataServers  = new ArrayList<Map<String,String>>();
		    
			dataServers=travail.dataToSend(NbLinesServer, file);// decpuper le fichier les 3 serveurs, les donnees sont de type map. dataServers est une liste de donnees, chacu'un est une liste de type map
			
		//communique avec les serveurs(donner le travail a faire, et recevoir le resultat)
			Repartiteur repartiteur=new Repartiteur();
			
			ServeurThread[] serveursThreads=new ServeurThread[nombreServeurs];
			
			serveursThreads=repartiteur.createThreads(IpServeurs, dataServers);
			
			repartiteur.beginThreads(serveursThreads);
			
			//on verifier si tous les serveurs ont donné le bon resultat
			
			//if(repartiteur.isMalicieux());
			
			result=repartiteur.result(serveursThreads, stubNoms, repartiteur, result);
			
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
//on utilise une fonction recursive, pour renvoyer le calcul aux autres serveurs
	private int result(ServeurThread[] serveurThreads, NomsInterface stubNoms, Repartiteur repartiteur, int result1) throws RemoteException {
		int result=result1;
		int i=0;
		String IP=null;
		Map<String, String> data=null;
		for(ServeurThread t:serveurThreads) {
			while(t.getResult()<=0) {
				if(t.getResult()==-1) {
					IP=t.getIP();
					data=t.getData();
				}
				else {
					result+=result+t.getResult();
				}
			}
			
		}
		
		if(IP!=null) {
			// a modifier
			//stubNoms.removeServeur(IP);
			
			String[] IpServeurs=stubNoms.getIpServeurs();
			int[] capaciteServeurs=stubNoms.getCapaciteServeurs();
			int index =findIndex(IpServeurs, IP);
			int[] newCapacites=removeCapacite(capaciteServeurs, capaciteServeurs[index]);
			String[] newIpServeurs=removeIp(IpServeurs, IP);
			ServeurThread[] serveursThreads=new ServeurThread[newIpServeurs.length];
			
			List<Map<String , String>> dataServers  = new ArrayList<Map<String,String>>();
		    
			dataServers=dataToResend(data, newCapacites);
			
			serveursThreads=repartiteur.createThreads(newIpServeurs, dataServers);
			
			repartiteur.beginThreads(serveursThreads);
			
			return result + repartiteur.result(serveursThreads, stubNoms, repartiteur, result);
		}
		else {
			return result;
		}
	}
	
	
	public static String[] removeIp(String[] input, String deleteMe) {
	    List result = new LinkedList();

	    for(String item : input)
	        if(!deleteMe.equals(item))
	            result.add(item);

	    return (String[]) result.toArray(input);
	}
	
	public static int[] removeCapacite(int[] input, int deleteMe) {
		int[] capacites=new int[input.length-1];
		int i=0;

	    for(int item : input) {
	        if(item==deleteMe) {
	            continue;
	        }
	        else {
	        	capacites[i]=item;
	    		i++;
	        }
	    }
	    return capacites;
	}
	
	
	private int findIndex( String[] list, String value) {
		int i=0;
	    for(String ip: list) { 
	         if(ip.equals(value)) {
	             return i;
	         }
	    i++;
	    }
	    return (Integer) null;
	}
	
	private List<Map<String , String>> dataToResend(Map<String,String> dataServer, int[]capaciteServeurs){
		List<Map<String , String>> dataServers = null;
		int sum = IntStream.of(capaciteServeurs).sum();
		int[] SizeServers=new int[capaciteServeurs.length];
		int NbLines=dataServer.size();
		int i1=0;
		while(i1<SizeServers.length) {
			int NbLinesForOneServer= (int) capaciteServeurs[i1]*NbLines/sum;
			i1+=1;
			SizeServers[i1]=NbLinesForOneServer;
		}
		int c=0;
		int c2=0;
		int c3=0;
		for(int i: SizeServers) {
		
				for(String key: dataServer.keySet()) {
					if(c2*c3 <= c && c < (i+c3)) {
						dataServer.put(key,dataServer.get(key));
						c++;
					}
					
				}
				dataServers.add(c2,dataServer);
				c3=c;
				c2++;
		}
		return null;
		
	}
}
