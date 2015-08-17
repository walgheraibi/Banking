import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import oracle.sql.DATE;

public class Banking {

	public static void main(String[] args) {
		// variables decelerations
		Scanner keyboard = new Scanner(System.in);
		String choice = "", tranchoice = "";
		boolean choiceToclose = false, choiceanotheracc = true, choiceToclose2 = true;
		int accountNo = 0, nextaccountNo = 0;
		int sum = 0;
	Transaction tran = new Transaction();
	Accounts acc = new Accounts();
		HashMap<Integer, String> accountsHash = new HashMap<Integer, String>();
		HashMap<Integer, String> transactionsHash = new HashMap<Integer, String>();
		String sql = "";
	//	PreparedStatement preStatement = null;
		ResultSet result = null;
		System.out.println("Welcome to Evil Corp Saving and Loan");
		System.out.println();

		String url = "jdbc:oracle:thin:testuser/password@localhost";

		// properties for creating connection to Oracle database
		Properties props = new Properties();
		props.setProperty("user", "testdb");
		props.setProperty("password", "password");

		// creating connection to Oracle database using JDBC
		try {
			Connection conn = DriverManager.getConnection(url, props);

			sql = "select * from ACCOUNTS";
			result = sqlConnection(conn,sql );

			while (result.next()) {

				accountsHash.put(
						Integer.parseInt(result.getString("Acct")),
						result.getString("Name1") + " "
								+ result.getString("birthday")+ " " +
								+ result.getInt("SAVINGS")+ " " +
								Integer.parseInt(result.getString("StartingBalance"))
								);

				System.out.printf("%s\t%20s\t%s\t%10s\t%s\n",
						result.getString("Acct"),
						result.getString("Name1"),
						result.getInt("SAVINGS"),
						result.getDate("birthday"),
						result.getString("StartingBalance"));
		
				if (nextaccountNo < Integer.parseInt(result
						.getString("Acct")))
					nextaccountNo = Integer.parseInt(result
							.getString("Acct"));

			}

			while (!choice.equals("-1")) {

				choiceToclose = Validator.getBoolean(keyboard,
						"Is it an existing account? (y/n) : ");

				if (choiceToclose) {
					choiceToclose = Validator.getBoolean(keyboard,
							"Do you want to delete an account? (y/n) : ");
					if (choiceToclose) {
						int account = Validator
								.getInt(keyboard,
										"Enter an account # or -1 to stop entering accounts : ");
						if (account != -1) {

							if (accountsHash.containsKey(account)) {

								String[] Key_value_pair = accountsHash.get(
										account).split(" ");
								double balance = Double
										.parseDouble(Key_value_pair[Key_value_pair.length - 1]);
								if (balance == 0) {
									accountsHash.remove(account,
											accountsHash.get(account));

									// / add delete the account here
									sql = "DELETE from ACCOUNTS WHERE Acct ="
											+ account;
									result = sqlConnection(conn,sql );
									System.out.println("Account " + account
											+ " was closed");
								} else
									System.out
											.println("Cannot delete this accout beacause it has $"
													+ balance);
							}

							else
								System.out
										.println("This account does not exist!");
						}
						} else {
							
							choiceToclose = Validator.getBoolean(keyboard,
									"Do you want to transfer money between accounts? (y/n) : ");
							if (choiceToclose) {
								int account = Validator
										.getInt(keyboard,
												"Enter first account # (transfer from) or -1 to Exit : ");
								if(account!= -1)
								{
									int account2 = Validator
											.getInt(keyboard,
													"Enter second account # (transfer to) or -1 to Exit : ");
									if(account2!= -1)
									{
										if (accountsHash.containsKey(account) && accountsHash.containsKey(account2)) {

											String[] Key_value_pair = accountsHash.get(
													account).split(" ");
											double balance1 = Double
													.parseDouble(Key_value_pair[Key_value_pair.length - 1]);
											
											String[] Key_value_pair2 = accountsHash.get(
													account2).split(" ");
											double balance2 = Double
													.parseDouble(Key_value_pair2[Key_value_pair2.length - 1]);	
										int amount = Validator.getInt(keyboard,
												"Enter the amount of the " + accountNo + " : ",
												0, Integer.MAX_VALUE);
										
										if (amount <= balance1)
										{
										sql = "UPDATE ACCOUNTS SET StartingBalance = "+  (int) (balance1-amount) +" WHERE Acct ="+account;
										result = sqlConnection(conn,sql );
										
										sql = "UPDATE ACCOUNTS SET StartingBalance = "+  (int) (balance2 +amount) +" WHERE Acct ="+account2;
										result = sqlConnection(conn,sql );
										choice = "-1";
										break;
										}
										else
										{
											String name = "";
											int strbalance=0;
											sql = "select name1 from accounts where acct =" + account;
											result = sqlConnection(conn,sql );
											while(result.next())
											{
												name = result.getString("name1");
											}
											System.out.println("OVERDRAFT!");
											sql = "select STARTINGBALANCE from accounts where name1 = '" +name+ "'  and savings = 1";
											result = sqlConnection(conn,sql );
											while(result.next())
											{
												strbalance = result.getInt("STARTINGBALANCE");
											}

											sql = "UPDATE ACCOUNTS SET StartingBalance = "+  (int) (strbalance -15) +" WHERE name1 = '" +name+ "'  and savings = 1";
											result = sqlConnection(conn,sql );
											
										}
									}
												}
									else
									{
										choice = "-1";
										break;
									}
								}
								else
								{
									choice = "-1";
									break;
								}
									
							}
							else
							{
							choice = "-1";
							break;
							}
						}
					} else 
				{
						if (choiceToclose2) {
				
					while (choiceanotheracc) {

						Accounts.setNextAcountNumber(++nextaccountNo);
						int account = Accounts.getNextAcountNumber();

						String accountName = Validator.getString(keyboard,
								"Enter the name for acct ## " + account
										+ " : ");
						int accountBalance = Validator.getInt(
								keyboard, "Enter the balance for acct #  "
										+ account + " : ", 0,
								Integer.MAX_VALUE);
						String dob = Validator.getString(keyboard,
								"Enter the date of birth: (MM/dd/yyyy) ");
						int accounttype = Validator.getInt(
								keyboard, "Enter (1 for Saving and 0 for checking) :");
					
						accountsHash.put(
								account,
								"	" + accountName + " " +
								"	" + accounttype + " "
								+accountBalance);
						

						
						sql = "select SAVINGS from ACCOUNTS where NAME1=' "+ accountName+"' and BIRTHDAY= to_date('"+dob+"','mm/dd/yyyy')";

						result = sqlConnection(conn,sql );
					
						///***********
						int i= 0, saving = 2;
						while(result.next()){
						saving = result.getInt("SAVINGS");	
						i++;
						}
				
						if(i >= 2)
						{
							System.out.println("You have 2 accounts you cannot add more accounts");
							break;
						}
						
						else
						{
						
							if(saving == 0)
							{
								System.out.println("You have a Checking account (Saving account is created)");	
								sql = "insert into accounts (Acct, Name1, SAVINGS, StartingBalance,birthday)values('"
										+account
										+ "',' "
										+ accountName
										+ "', 1 ,'"
										+ accountBalance+ "', to_date('"+dob+"','mm/dd/yyyy'))";
								result = sqlConnection(conn,sql );

							}
							else if(saving == 1)
							{
								System.out.println("You have a Saving account (Checking account is created)");	
								sql = "insert into accounts (Acct, Name1, SAVINGS, StartingBalance,birthday)values('"
										+ account
										+ "',' "
										+ accountName
										+ "', 0 ,'"
										+accountBalance + "', to_date('"+dob+"','mm/dd/yyyy'))";
								result = sqlConnection(conn,sql );

							}
						
						//*************
						
						}
						Accounts.setNextAcountNumber(++account);
						choiceanotheracc = Validator.getBoolean(keyboard,
								"Add another account? (y/n) : ");
					}
					choice = "-1";
				} else
					break;

			}

			System.out.println();

			while (!tranchoice.equals("-1")) {

				String transaction = Validator
						.getString(
								keyboard,
								"Enter a transaction type (Check (C), Debit card (DC), Deposit(D) or Withdrawal(W)) or -1 to finish : ");

				if (!transaction.equals("-1")) {

					accountNo = Validator.getInt(keyboard,
							"Enter the account # : ");
					if (accountsHash.containsKey(accountNo)) {
				
						tran.setTransactionType(transaction);

						int amount = Validator.getInt(keyboard,
								"Enter the amount of the " + accountNo + " : ",
								0, Integer.MAX_VALUE);
						tran.setAmount(amount);
						String tranDateStr = Validator.getString(keyboard,
								"Enter the date of the check: (MM/dd/yyyy) ");
						GregorianCalendar gregorianCalendar = new GregorianCalendar(
								Integer.parseInt(tranDateStr.substring(6)),
								Integer.parseInt(tranDateStr.substring(0, 2)) - 1,
								Integer.parseInt(tranDateStr.substring(3, 5)));
						tran.setTranDate(gregorianCalendar);
						;
						transaction = ""+tran.getTransactionType();
						transactionsHash.put(accountNo,
								"	" + transaction + " " + amount + "	"
										+ gregorianCalendar.toZonedDateTime());
						String[] Key_value_pair = accountsHash.get(accountNo)
								.split(" ");
						acc.setAccountName(Key_value_pair[Key_value_pair.length - 2]);
						acc.setAccountBalance(Double
								.parseDouble(Key_value_pair[Key_value_pair.length - 1]));
						acc.updateBalance(tran.getTransactionType(), amount);
						accountsHash.replace(accountNo, " " + Key_value_pair[0]
								+ " " + acc.getAccountBalance());
					
						///HERE
						sql = "insert into transactions (account, amount, transaction, date1)values("+
								acc.getAccount()
								+ ","
								+ tran.getAmount()
								+ ","
								+ tran.getTransactionType()+ 
								", to_date('"+tranDateStr+"','mm/dd/yyyy'))";
						result = sqlConnection(conn,sql );

					} else {
						System.out
								.print("The account number does not exist make sure you entered the right number");
					}
				} else
					tranchoice = "-1";

				System.out.println();

				sql = "select * from TRANSACTIONs";
				result = sqlConnection(conn,sql );

				while (result.next()) {
					System.out.printf("%s\t%s\t%s\t%s\t%s\n",
							result.getInt("ID"),
							result.getInt("ACCOUNT"),
							result.getInt("AMOUNT"),
							result.getInt("TRANSACTION"),
							result.getDate("DATE1"));

				}
			}

		} 
		}catch (SQLException e) {
			e.printStackTrace();
		}

		
		
	}
	
	public static ResultSet sqlConnection(Connection conn, String sql)
	{
		ResultSet result = null;
		PreparedStatement preStatement = null;
		try {
			preStatement = conn.prepareStatement(sql);
			result = preStatement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return result;
	}

}
