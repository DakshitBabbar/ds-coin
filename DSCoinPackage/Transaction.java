package DSCoinPackage;
import HelperClasses.*;

public class Transaction {

  public String coinID;
  public Members Source;
  public Members Destination;
  public TransactionBlock coinsrc_block;
  public Transaction qNext;
}
