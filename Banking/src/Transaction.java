import java.util.GregorianCalendar;


public class Transaction {

	private int transactionType;
	private double amount;
	private GregorianCalendar tranDate;
	
	public int getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		
		switch(transactionType.toLowerCase())
		{
		case "c": this.transactionType =2; break;
		case "d": this.transactionType =1; break;
		case "w": this.transactionType =3; break;
		case "dc": this.transactionType =4; break;
		}
	}
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public GregorianCalendar getTranDate() {
		return tranDate;
	}
	public void setTranDate(GregorianCalendar tranDate) {
		this.tranDate = tranDate;
	}
	
	public Transaction()
	{
		this.tranDate = new GregorianCalendar();
		this.transactionType = 0;
		this.amount = 0;
	}
}
