package DSCoinPackage;
import HelperClasses.*;

public class TransactionQueue {

	public Transaction firstTransaction;
	public Transaction lastTransaction;
	public int numTransactions;

	public void AddTransactions (Transaction transaction) {
		if(numTransactions == 0){
			firstTransaction = transaction;
			lastTransaction = transaction;
			numTransactions = 1;
		} else {
			lastTransaction.qNext = transaction;

			lastTransaction = transaction;
			numTransactions += 1;
		}

	}
	
	public Transaction RemoveTransaction () throws EmptyQueueException {
		if(numTransactions == 0) {
			throw new EmptyQueueException();
		} else {
			Transaction answer = firstTransaction;
			firstTransaction = firstTransaction.qNext;
			numTransactions -= 1;

			return answer;
		}
	}

	public int size() {
		return numTransactions;
	}
}
