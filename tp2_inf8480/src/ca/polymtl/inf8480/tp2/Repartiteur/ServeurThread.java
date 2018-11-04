package ca.polymtl.inf8480.tp2.Repartiteur;

import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;
import java.rmi.RemoteException;
import java.util.Map;

public class ServeurThread extends Thread {
	private String IP;
	private Map<String,String> dataServer;
	private int result=0;
	public ServeurThread(String IP, Map<String,String> dataServer) {
		IP=IP;
		dataServer=dataServer;
	}
	public void run() {
		Communication communication = new Communication();
		ServerInterface stubServeur1 = communication.loadServerStub(IP);
		try {
			result = communication.calcul(stubServeur1, dataServer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getResult() {
		return result;
	}
	public Map<String,String> getDataNotsend(){
		return dataServer;
	}
	public String getIP() {
		return IP;
	}
	public Map<String,String> getData(){
		return dataServer;
	}
	
}
