import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;
import java.util.Scanner;

public class JDBCbank
{

	public static String get_branch(String add){
		String Bno = null;
		try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
                        CallableStatement cs = conn.prepareCall("{call bank.get_branch(?,?)}");
                        cs.setString(1, add);
                        cs.registerOutParameter(2, Types.VARCHAR);
			cs.executeUpdate();
                        Bno = cs.getString(2);
			if (Bno != null) {
				System.out.println("Branch number: "+Bno);
			}
			else{
				System.out.println("Error: branch with address "+add+" does not exist.");
			}
			cs.close();
                        conn.close();
		}
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
		return Bno;
        }

	public static String get_account(String name, String branch){
                String Ano = null;
                try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
                       String Bno = get_branch(branch);
                        if (Bno == null) {
                                return null;
                        }
			CallableStatement cs = conn.prepareCall("{call bank.get_account(?,?,?)}");
                        cs.setString(1, name);
			cs.setString(2, Bno);
                        cs.registerOutParameter(3, Types.VARCHAR);
                        cs.executeUpdate();
                        Ano = cs.getString(3);
                        if (Ano != null) {
                                System.out.println("Account number: "+Ano);
                        }
                        else{
                                System.out.println("Error: customer or account does not exist.");
                        }
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
                return Ano;
        }

        public static void open_branch(String add){
                try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora","oracle");
			CallableStatement cs = conn.prepareCall("{call bank.open_branch(?,?)}");
			cs.setString(1, add);
 			cs.registerOutParameter(2, Types.VARCHAR);
 			cs.executeUpdate();
			String Bno = cs.getString(2);
			if (Bno != null) {
                                System.out.println("Created new branch with B#: "+Bno+" and address: "+add);
                        }
                        else{
                                System.out.println("Error branch with address "+add+" already exists.");
                        }
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }

	public static void close_branch(String branch){
                try{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
                        String Bno = get_branch(branch);
			if (Bno == null) {
				return;
			}
			CallableStatement cs = conn.prepareCall("{call bank.close_branch(?,?)}");
			cs.setString(1, Bno);
			cs.registerOutParameter(2, Types.INTEGER);
                        cs.executeUpdate();
			int chk = cs.getInt(2);
			if (chk == 0){
				System.out.println("Error: Branch still has open accounts.");
			}
			else{
				System.out.println("Closed branch.");
			}
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }

	public static void create_customer(String name){
                try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
                        CallableStatement cs = conn.prepareCall("{call bank.create_customer(?,?)}");
                        cs.setString(1, name);
                        cs.registerOutParameter(2, Types.VARCHAR);
                        cs.executeUpdate();
                        String Cno = cs.getString(2);
                        if (Cno != null) {
                                System.out.println("Created new customer with C#: "+Cno+" and name: "+name);
                        }
                        else{
                                System.out.println("Error customer with name "+name+" already exists.");
                        }
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }

	public static void remove_customer(String name){
                try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
                        CallableStatement cs = conn.prepareCall("{call bank.remove_customer(?,?)}");
                        cs.setString(1, name);
                        cs.registerOutParameter(2, Types.INTEGER);
                        cs.executeUpdate();
                        int chk = cs.getInt(2);
                        if (chk == 1){
                                System.out.println("Error: Customer still has open accounts.");
                        }
			else if (chk == 0){
				System.out.println("Error: Customer does not exist.");
                        }
			else{
                                System.out.println("Removed customer.");
                        }
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }

	public static void open_account(String name, String branch, int amount){
		try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
                        String Bno = get_branch(branch);
			if (amount < 0) {
				System.out.println("Error: Balance below 0.");
				return;
			}
			if (Bno == null) {
                                return;
                        }
			CallableStatement cs = conn.prepareCall("{call bank.open_account(?,?,?,?)}");
			cs.setString(1, name);
			cs.setString(2, Bno);
			cs.setInt(3, amount);
			cs.registerOutParameter(4, Types.INTEGER);
			cs.executeUpdate();
			int chk = cs.getInt(4);
			if (chk == 0){
                                System.out.println("Error: No customer with that name exists.");
                        }
			else if (chk == 1){
                                System.out.println("Error: Customer already has an account at that branch.");
                        }
			else{
                                System.out.println("Opened a new account for "+name+".");
                        }
                        cs.close();
                        conn.close();
                }
		catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }

	public static void close_account(String name, String branch){
		try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
			String Ano = get_account(name, branch);
			if (Ano == null){
				return;
			}
			CallableStatement cs = conn.prepareCall("{call bank.close_account(?,?)}");
			cs.setString(1,Ano);
			cs.registerOutParameter(2, Types.INTEGER);
			cs.executeUpdate();
			int chk = cs.getInt(2);
			if (chk == 0){
                                System.out.println("Error: Account does not exist.");
                        }
			else if (chk == 1){
                                System.out.println("Error: Account does not have a balance of 0.");
                        }
			else{
                                System.out.println("Closed "+name+"'s account at branch "+branch+".");
                        }
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }


	public static boolean withdraw(String branch, String name, int amount){
		boolean result = false;
                try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
			String Bno = get_branch(branch);
                        if (Bno == null) {
                                return result;
                        }
			String Ano = get_account(name, branch);
                        if (Ano == null){
                                return result;
                        }
                        CallableStatement cs = conn.prepareCall("{call bank.withdraw(?,?,?)}");
                        cs.setString(1,Ano);
                        cs.setInt(2,amount);
                        cs.registerOutParameter(3, Types.INTEGER);
                        cs.executeUpdate();
                        int chk = cs.getInt(3);
                        if (chk == 0){
				System.out.println("Error: Not enough money in account.");
			}
                        else{
				result = true;
                                System.out.println("Withdrew $"+Integer.toString(amount)+" from "+name+"'s "+branch+" account.");
                        }
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
		return result;
        }


	public static boolean deposit(String branch, String name, int amount){
		boolean result = false;
		if (amount < 0){
			System.out.println("Error: amount to deposit is less than 0.");
			return result;
		}
		try{
			
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora","oracle");
                        String Bno = get_branch(branch);
                        if (Bno == null) {
                                return result;
                        }
			String Ano = get_account(name, branch);
                        if (Ano == null){
                                return result;
                        }
			CallableStatement cs = conn.prepareCall("{call bank.deposit(?,?)}");
                        cs.setString(1,Ano);
			cs.setInt(2,amount);
			cs.executeUpdate();
			result = true;
                        System.out.println("Deposited $"+Integer.toString(amount)+" from "+name+"'s "+branch+" account.");
			cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
		return result;
        }


	public static void transfer(String branchW, String nameW, String branchD, String nameD, int amount){
		if (amount < 0){
                        System.out.println("Error: amount to deposit is less than 0.");
                        return;
                }
                if(withdraw(branchW, nameW, amount)){
			if(deposit(branchD, nameD, amount)){
				System.out.println("Transfer from "+nameW+"'s "+branchW+" account to "+nameD+"'s "+branchD+" account successful.");
			}
			else{
				deposit(branchW, nameW, amount);
			}
		}
	}


	public static void show_branch(String branch){
                try{
			System.out.println("");
			String Bno = get_branch(branch);
                        if (Bno == null) {
                                return;
                        }
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora","oracle");
                        CallableStatement cs = conn.prepareCall("{call bank.show_branch(?,?,?,?)}");
			cs.setString(1,Bno);
			cs.registerOutParameter(2, Types.VARCHAR);
                        cs.registerOutParameter(3, OracleTypes.CURSOR);
			cs.registerOutParameter(4, Types.INTEGER);
                        cs.executeUpdate();
			String add;
			add = cs.getString(2);
			System.out.println("with Address: "+add);
			System.out.println("  A#   |  C#  | Name |Balance");
			System.out.println("------- ------ ------ -------");
                        ResultSet rs = (ResultSet) cs.getObject(3);
			while (rs.next()) {
				System.out.print(rs.getString("A#")+" ");
				System.out.print(rs.getString("C#")+"  ");
				System.out.print(rs.getString("Name")+"   ");
				System.out.print("$"+rs.getString("Balance")+"\n");
			}
			String total;
			total = Integer.toString(cs.getInt(4));
			System.out.println("The branch total is $"+total);
			System.out.println("");
			rs.close();
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }

	public static void show_all_branches(){
		try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora","oracle");
                        CallableStatement cs = conn.prepareCall("{call bank.show_all_branches(?)}");
			cs.registerOutParameter(1, OracleTypes.CURSOR);
			cs.executeUpdate();
			ResultSet rs = (ResultSet) cs.getObject(1);
			while (rs.next()) {
				show_branch(rs.getString("B#"));
			}
			rs.close();
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }

	public static void show_customer(String name){
		try{
                        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","fedora",
"oracle");
                        CallableStatement cs = conn.prepareCall("{call bank.show_customer(?,?,?,?,?)}");
			cs.setString(1,name);
                        cs.registerOutParameter(2, Types.INTEGER);
			cs.registerOutParameter(3, Types.VARCHAR);
                        cs.registerOutParameter(4, OracleTypes.CURSOR);
			cs.registerOutParameter(5, Types.INTEGER);
                        cs.executeUpdate();
			int chk = cs.getInt(2);
                        if (chk == 0){
                                System.out.println("Error: customer does not exist");
				return;
                        }
                        String Cno;
                        Cno = cs.getString(3);
			System.out.print("Customer "+name);
                        System.out.println(" with C#: "+Cno);
                        System.out.println("Addres      |  A#   | Balance");
                        System.out.println("------------ ------- --------");
                        ResultSet rs = (ResultSet) cs.getObject(4);
                        while (rs.next()) {
                                System.out.print(rs.getString("Address")+"       ");
                                System.out.print(rs.getString("A#")+" ");
                                System.out.print("$"+rs.getString("Balance")+"\n");
                        }
                        String total;
                        total = Integer.toString(cs.getInt(5));
                        System.out.println("The branch total is $"+total);
                        System.out.println("");
                        rs.close();
                        cs.close();
                        conn.close();
                }
                catch(Exception e){
                        System.out.println("SQL exception: ");
                        e.printStackTrace();
                        System.exit(-1);
                }
        }
			

	public static void main(String[] args){
                int choice = -1;
                String line = "";
                Scanner scan = new Scanner(System.in);
                while(choice != 0){
                        System.out.print("Please choose a number from the following options:\n");
                        System.out.print("0) Exit\n");
			System.out.print("1) Get branch B#\n");
                        System.out.print("2) Open a branch\n");
                        System.out.print("3) Close a branch\n");
			System.out.print("4) Create  a new customer\n");
                        System.out.print("5) Remove a customer\n");
                        System.out.print("6) Open a new account\n");
			System.out.print("7) Close an account\n");
			System.out.print("8) Withdraw from an account\n");
                        System.out.print("9) Deposit to account\n");
                        System.out.print("10) Transfer between accounts\n");
                        System.out.print("11) Show branch information\n");
                        System.out.print("12) Show all branch information\n");
                        System.out.print("13) Show customer information\n");
                        choice = scan.nextInt();
                        if(choice == 0){
                                System.out.println("Thank you. Goodbye.");
                        }
                        else if(choice == 1){
				System.out.print("Getting a branch B#\n");
                                System.out.print("Please enter the address:\n");
                                String add = scan.next();
                                scan.nextLine();
                                get_branch(add);
                        }
			else if(choice == 2){
				System.out.print("Opening a branch\n");
                		System.out.print("Please enter the address:\n");
                		String add = scan.next();
                		scan.nextLine();
                                open_branch(add);
                        }
			else if(choice == 3){
                                System.out.print("Closing a branch\n");
                                System.out.print("Please enter the address or branch number:\n");
                                String branch = scan.next();
                                scan.nextLine();
                                close_branch(branch);
                        }
			else if(choice == 4){
                                System.out.print("Creating a new customer\n");
                                System.out.print("Please enter the new customer name:\n");
                                String name = scan.next();
                                scan.nextLine();
                                create_customer(name);
                        }
			else if(choice == 5){
                                System.out.print("Removing a customer\n");
                                System.out.print("Please enter the customer name:\n");
                                String name = scan.next();
                                scan.nextLine();
                                remove_customer(name);
                        }
			else if(choice == 6){
                                System.out.print("Opening a new account\n");
                                System.out.print("Please enter the customer name:\n");
                                String name = scan.next();
                                scan.nextLine();
				System.out.print("Please enter branch number or address:\n");
				String branch = scan.next();
                                scan.nextLine();
				System.out.print("Please enter starting balance\n");
				String amount = scan.next();
                                scan.nextLine();
                                open_account(name, branch, Integer.parseInt(amount));
                        }
			else if(choice == 7){
                                System.out.print("Closing an account\n");
                                System.out.print("Please enter the customer name:\n");
                                String name = scan.next();
                                scan.nextLine();
				System.out.print("Please enter the branch number or address:\n");
                                String branch = scan.next();
                                scan.nextLine();
                                close_account(name, branch);
                        }
			else if(choice == 8){
                                System.out.print("Withdrawing money.\n");
                                System.out.print("Please enter the customer name:\n");
                                String name = scan.next();
                                scan.nextLine();
				System.out.print("Please enter the branch number or address:\n");
                                String branch = scan.next();
                                scan.nextLine();
                                System.out.print("Please enter the amount to withdraw\n");
                                String amount = scan.next();
                                scan.nextLine();
                                withdraw(branch, name, Integer.parseInt(amount));
                        }
			else if(choice == 9){
                                System.out.print("Depositing money.\n");
				System.out.print("Please enter the customer name:\n");
                                String name = scan.next();
                                scan.nextLine();
                                System.out.print("Please enter the branch number or address:\n");
                                String branch = scan.next();
                                scan.nextLine();
				System.out.print("Please enter the amount to deposit\n");
				String amount = scan.next();
                                scan.nextLine();
				deposit(branch, name, Integer.parseInt(amount));
			}
			else if(choice == 10){
                                System.out.print("Transferring money.\n");
                                System.out.print("Please enter the customer name to withdraw from:\n");
                                String nameW = scan.next();
                                scan.nextLine();
                                System.out.print("Please enter the branch number or address to withdraw from:\n");
                                String branchW = scan.next();
                                scan.nextLine();
				System.out.print("Please enter the customer name to deposit to:\n");
                                String nameD = scan.next();
                                scan.nextLine();
                                System.out.print("Please enter the branch number or address to deposit to:\n");
                                String branchD = scan.next();
                                scan.nextLine();
                                System.out.print("Please enter the amount to transfer\n");
                                String amount = scan.next();
                                scan.nextLine();
                                transfer(branchW, nameW, branchD, nameD, Integer.parseInt(amount));
                        }
			else if(choice == 11){
				System.out.print("Please enter a branch number or address:\n");
				String branch = scan.next();
                                scan.nextLine();
				show_branch(branch);
			}
			else if(choice == 12){
				show_all_branches();
			}
			else if(choice == 13){
				System.out.print("Please enter the customer name:\n");
				String name = scan.next();
                                scan.nextLine();
                                show_customer(name);
			}
		}
		scan.close();
	}
}
