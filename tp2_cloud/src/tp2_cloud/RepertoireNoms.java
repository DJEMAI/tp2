package tp2_cloud;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;

public class RepertoireNoms {
	private String name=null;
	private DirContext context;
	public RepertoireNoms(String ldapIP,int port) {
		 try {
			             DirContext context1 = getContext(ldapIP,port);
			             context=context1;
			             String name1 = "employeeNumber=00001,ou=system";
			             name=name1;
			             createLDAPObject(context, name);
			             //addAttribute(context, name, "displayName", "JOBS");
			             //getAttribute(context, name, "displayName");
			             //updateAttribute(context, name, "displayName", "STEVE");
			             //getAttribute(context, name, "displayName");
			             //removeAttribute(context, name, "displayName");
			             //removeLDAPObject(context, name);
			         } catch (NamingException e) {
			             e.printStackTrace();
			         }
	}
	public void ajouterRepartiteur(String username, String password) throws NamingException {
		String[] client= {username,password};
		addAttribute(context, name, "client", client);
	}
	
	public int getNombreServeurs() throws NamingException {
		
		return (int) getAttribute(context, name, "NombreServeurs");
		
	}
	
	public String[] getIpServeurs() throws NamingException {
		return (String[]) getAttribute(context, name, "IpServeurs");
	}
	
	public int[] getCapaciteServeurs() throws NamingException {
		return (int[]) getAttribute(context, name, "CapaciteServeurs");
	}
	
	public void ajouterServeur(String IP, int capacite) throws NamingException {
		String[] IPServeursOld=getIpServeurs();
		int[] CapaciteOld= getCapaciteServeurs();
 
        String[] IPServeursNew = new String[IPServeursOld.length + 1];
        int[] CapaciteNew = new int[CapaciteOld.length+1];
        
        System.arraycopy(IPServeursOld, 0, IPServeursNew, 0, IPServeursOld.length);
        System.arraycopy(CapaciteOld, 0, CapaciteNew, 0, CapaciteOld.length);
       
        IPServeursNew[IPServeursNew.length-1] = IP;
        CapaciteNew[CapaciteNew.length-1]=capacite;
       
        updateAttribute(context, name, "IpServeurs", IPServeursNew);
        updateAttribute(context, name, "CapaciteServeurs", CapaciteNew);
		
	}
	
	private void removeLDAPObject(DirContext context, String name) throws NamingException {
		context.destroySubcontext(name);
	}
	
	public DirContext getContext(String ldapIP, int port) throws NamingException {
		        Properties properties = new Properties();
		        properties.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		        properties.put(Context.PROVIDER_URL, "ldap://"+ldapIP+":"+port);
		        return new InitialDirContext(properties);
		    }

	public void addAttribute(DirContext context, String name , String attrName, Object attrValue) throws NamingException {
	         Attribute attribute = new BasicAttribute(attrName, attrValue);
	         ModificationItem[] item = new ModificationItem[1];
	         item[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
	         context.modifyAttributes(name, item);
		     }
	
	public Object getAttribute(DirContext context, String name , String attrName) throws NamingException {
		        Attributes attrs = context.getAttributes(name);
		        return (int) attrs.get(attrName).get();
		    }
	
	private void createLDAPObject(DirContext context, String name) throws NamingException {
		        Attributes attributes = new BasicAttributes();
		        Attribute attribute = new BasicAttribute("objectClass");
		        attribute.add("inetOrgPerson");
		        attributes.put(attribute);
		        Attribute sn = new BasicAttribute("sn");
		        sn.add("Steve");
		        attributes.put(sn);
		        Attribute cn = new BasicAttribute("cn");
		        cn.add("Jobs");
		        attributes.put(cn);
		        attributes.put("telephoneNumber", "123456");
		        context.createSubcontext(name, attributes);
		    }
	public void updateAttribute(DirContext context, String name , String attrName, Object attrValue) throws NamingException {
		        Attribute attribute = new BasicAttribute(attrName, attrValue);
		        ModificationItem[] item = new ModificationItem[1];
		        item[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute);
		        context.modifyAttributes(name, item);
		    }
	public void removeAttribute(DirContext context, String name , String attrName) throws NamingException {
		        Attribute attribute = new BasicAttribute(attrName);
		        ModificationItem[] item = new ModificationItem[1];
		        item[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
		        context.modifyAttributes(name, item);
		    }
	




}
