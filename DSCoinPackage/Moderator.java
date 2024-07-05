package DSCoinPackage;
import HelperClasses.*;

import java.util.*;

public class Moderator
 {

  public String next(String x){
    int y = Integer.valueOf(x);
    String s = String.valueOf(y+1);
    return s;
  }

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) throws EmptyQueueException{
    //instantiate the mycoins list of all the members in the memberlist
    Members[] mlist = DSObj.memberlist;
    int memberMax = mlist.length;
    for(int i = 0; i<memberMax; i++){
      List<Pair<String,TransactionBlock>> l = new ArrayList<Pair<String,TransactionBlock>>();
      mlist[i].mycoins = l;
    }

    //make coincount no. of transactions and add them to the transaction queue.
    //also set the first value of the pair in the mycoins list of the concerned member as the coinID being assigned to him.
    Members ModMem = new Members();
    ModMem.UID = "Moderator";
    TransactionQueue q = new TransactionQueue();
    String s = "100000";
    int memberIdx = 0;
    for(int i = 0; i<coinCount; i++){
      Transaction trobj = new Transaction();
      trobj.coinID = s;
      trobj.Source = ModMem;
      trobj.Destination = mlist[memberIdx];
      trobj.coinsrc_block = null;

      q.AddTransactions(trobj);

      Pair<String,TransactionBlock> p = new Pair<String,TransactionBlock>();
      p.first = s;
      mlist[memberIdx].mycoins.add(p);
      
      s = next(s);

      if(memberIdx == memberMax-1){
        memberIdx = 0;
      } else {
        memberIdx += 1;
      }
    }

    //make coincount/trcount no. of transaction blocks and add trcount transactions into each one of them.
    //also set the second value of the pair in the mycoins list of the concerned member as the transaction block being filled.
    int tcount = DSObj.bChain.tr_count;
    int a = coinCount/tcount;
    for(int i = 0; i<a; i++){
      Transaction[] tarr = new Transaction[tcount];//trarray of the transaction block.
      for(int j = 0; j<tcount; j++){
        Transaction tr = q.RemoveTransaction();
        tarr[j] = tr;
      }
      TransactionBlock tB = new TransactionBlock(tarr);//tansaction block made.
      DSObj.bChain.InsertBlock_Honest(tB);//transaction block fit into the blockchain.
 
      for(int j = 0; j<tcount; j++){//find that member in the memberlist whose UID is equal to the transaction destination.
        Transaction tr = tarr[j];
        Members member = tr.Destination;
        //"member" is the concerned member.
        List<Pair<String,TransactionBlock>> l = member.mycoins;
        int b = l.size();
        for(int k = 0; k<b; k++){//find that pair in the mycoins list of the concerned member whose first value is equal to the transactio coinID.
          if(l.get(k).first.equals(tr.coinID)){
            l.get(k).second = tB;//set the second value of that pair appropriately.
            break;
          }
        }
      }


    }

    //set latest coin attribute
    DSObj.latestCoinID = s;
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) throws EmptyQueueException {
    //instantiate the mycoins list of all the members in the memberlist
    Members[] mlist = DSObj.memberlist;
    int memberMax = mlist.length;
    for(int i = 0; i<memberMax; i++){
      List<Pair<String,TransactionBlock>> l = new ArrayList<Pair<String,TransactionBlock>>();
      mlist[i].mycoins = l;
    }

    //make coincount no. of transactions and add them to the transaction queue.
    //also set the first value of the pair in the mycoins list of the concerned member as the coinID being assigned to him.
    Members ModMem = new Members();
    ModMem.UID = "Moderator";
    TransactionQueue q = new TransactionQueue();
    String s = "100000";
    int memberIdx = 0;
    for(int i = 0; i<coinCount; i++){
      Transaction trobj = new Transaction();
      trobj.coinID = s;
      trobj.Source = ModMem;
      trobj.Destination = mlist[memberIdx];
      trobj.coinsrc_block = null;

      q.AddTransactions(trobj);

      Pair<String,TransactionBlock> p = new Pair<String,TransactionBlock>();
      p.first = s;
      mlist[memberIdx].mycoins.add(p);
      
      s = next(s);

      if(memberIdx == memberMax-1){
        memberIdx = 0;
      } else {
        memberIdx += 1;
      }
    }

    //make coincount/trcount no. of transaction blocks and add trcount transactions into each one of them.
    //also set the second value of the pair in the mycoins list of the concerned member as the transaction block being filled.
    int tcount = DSObj.bChain.tr_count;
    int a = coinCount/tcount;
    for(int i = 0; i<a; i++){
      Transaction[] tarr = new Transaction[tcount];//trarray of the transaction block.
      for(int j = 0; j<tcount; j++){
        Transaction tr = q.RemoveTransaction();
        tarr[j] = tr;
      }
      TransactionBlock tB = new TransactionBlock(tarr);//tansaction block made.
      DSObj.bChain.InsertBlock_Malicious(tB);//transaction block fit into the blockchain.
 
      for(int j = 0; j<tcount; j++){//find that member in the memberlist whose UID is equal to the transaction destination.
        Transaction tr = tarr[j];
        Members member = tr.Destination;
        //"member" is the concerned member.
        List<Pair<String,TransactionBlock>> l = member.mycoins;
        int b = l.size();
        for(int k = 0; k<b; k++){//find that pair in the mycoins list of the concerned member whose first value is equal to the transactio coinID.
          if(l.get(k).first.equals(tr.coinID)){
            l.get(k).second = tB;//set the second value of that pair appropriately.
            break;
          }
        }
      }


    }

    //set latest coin attribute
    DSObj.latestCoinID = s;
  }
}
