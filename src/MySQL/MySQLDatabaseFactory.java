package MySQL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import interfaces.DataBaseFactory;
import interfaces.IConnection;
import interfaces.IDatabase;

public class MySQLDatabaseFactory extends DataBaseFactory{
	private String host;
	private String port;
	private String user;
	private String pass;
	public MySQLDatabaseFactory(String host, String port, String user, String pass) {
		super(host, port, user, pass);
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
	}
	public IDatabase createDatabase(String name, String colletion, String character) {
		List<IDatabase> m = this.getDatabase()==null? new ArrayList<IDatabase>() : this.getDatabase();
		m.add(new MySQLDatabase(name, colletion, character));
		this.setDatabase(m);
		return m.get(m.size()-1);
	}
	public IConnection createConnection(String name) {
		return new MySQLConnection(host, port, user, pass, name);
	}
	public void toRun(){
		for(int i=0; i<getDatabase().size();i++) {
			String[] args = this.getDatabase().get(i).toCreateLog();
			try {
				Connection connG = createConnection("").getConnection();
				Statement p = connG.createStatement();
					p.executeUpdate("CREATE DATABASE IF NOT EXISTS "+ args[0] +" CHARACTER SET " + args[4] + " COLLATE " + args[3] + ";");
				p.close();
				connG.close();
				
				System.out.println(args[0] + ".create = sucesso");
			
			} catch (SQLException e) {
				System.out.println(args[0] + ".create = erro");
			}
			if(!args[1].isEmpty()) {
				try {
					System.out.println(args[0]);
					Connection connE = createConnection(args[0]).getConnection();
					String[] partes = args[1].split(";");
					for(String parte : partes) {
						Statement p = connE.createStatement();
							p.executeUpdate(parte+";");
						p.close();
					}
					connE.close(); 
					System.out.println("+->" + args[0] + ".tabelas.create = sucesso");
				}catch (SQLException e) {
					System.out.println("+->" + args[0] + ".tabelas.create = erro");
				}
			}
			
			if(!args[2].isEmpty()) {
				try {
					Connection connE = createConnection(args[0]).getConnection();
					String[] partes = args[2].split(";");
					for(String parte : partes) {
						Statement p = connE.createStatement();
							p.executeUpdate(parte+";");
						p.close();
					}
					connE.close(); 
					
					System.out.println(" +->" + args[0] + ".foreignKey.create = sucesso");
				}catch (SQLException e) {
					System.out.println(" +->" + args[0] + ".foreignKey.create = erro");
				}
			}
		}
	}
	public void down() {
		for(int i=0; i<getDatabase().size();i++) {
			String name = this.getDatabase().get(i).getName();
			try {
				Connection connG = createConnection("").getConnection();
				if(connG!=null) {
					Statement p = connG.createStatement();
						p.executeUpdate("DROP DATABASE " + name);
					p.close();
					connG.close();
					
					System.out.println(name + ".delete = sucesso");
				}
			} catch (SQLException e) {
				System.out.println(name + ".delete = erro");
			}
		}
	}
	public String getString() {
		String R="";
		for(int i=0; i<getDatabase().size();i++) {
			String[] args = this.getDatabase().get(i).toCreateLog();
			R+="CREATE DATABASE IF NOT EXISTS "+ args[0] +" CHARACTER SET " + args[4] + " COLLATE " + args[3] + ";\n";
			if(!args[1].isEmpty()) {
				String[] partes = args[1].split(";");
				for(String parte : partes) {
					R+=parte+";\n";
				}
			}
			if(!args[2].isEmpty()) {
				String[] partes = args[2].split(";");
				for(String parte : partes) {
					R+= parte+";\n";
				}
			}
		}
		return R;
	}
}
