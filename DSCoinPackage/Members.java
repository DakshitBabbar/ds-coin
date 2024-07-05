package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    //find the member with UID = destUID.
    Members[] mlist = DSobj.memberlist;
    int l = mlist.length;
    Members mem = null;
    for(int i = 0; i<l; i++){
      if(mlist[i].UID == destUID){
        mem =  mlist[i];
        break;
      }
    }

    //make a new transaction object from the very first element of my coins list.
    Transaction tobj = new Transaction();
    tobj.coinID = mycoins.get(0).first;
    tobj.Source = this;
    tobj.Destination = mem;
    tobj.coinsrc_block = mycoins.get(0).second;

    //remove the very first element from the my coins list.
    mycoins.remove(0);

    //add the newly made transacion in the in_process_trans list.
    if(in_process_trans == null || in_process_trans.length ==0 || in_process_trans[0] == null){
      Transaction[] new_in_process_trans = new Transaction[1];
      new_in_process_trans[0] = tobj;
      in_process_trans = new_in_process_trans;
    } else {
      int n = in_process_trans.length;
      Transaction[] new_in_process_trans = new Transaction[n+1];
      new_in_process_trans[n] = tobj;
      for(int i = 0; i<n; i++){
        new_in_process_trans[i] = in_process_trans[i];
      }
      in_process_trans = new_in_process_trans;
    }

    //add the newly made transaction to the pending transacsions list in 
    DSobj.pendingTransactions.AddTransactions(tobj);

  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    //find the member with UID = destUID.
    Members[] mlist = DSobj.memberlist;
    int l = mlist.length;
    Members mem = null;
    for(int i = 0; i<l; i++){
      if(mlist[i].UID == destUID){
        mem =  mlist[i];
        break;
      }
    }

    //make a new transaction object from the very first element of mycoins list.
    Transaction tobj = new Transaction();
    tobj.coinID = mycoins.get(0).first;
    tobj.Source = this;
    tobj.Destination = mem;
    tobj.coinsrc_block = mycoins.get(0).second;

    //remove the very first element from the mycoins list.
    mycoins.remove(0);

    //add the newly made transacion in the in_process_trans list.
    if(in_process_trans == null || in_process_trans[0] == null){
      Transaction[] new_in_process_trans = new Transaction[1];
      new_in_process_trans[0] = tobj;
      in_process_trans = new_in_process_trans;
    } else {
      int n = in_process_trans.length;
      Transaction[] new_in_process_trans = new Transaction[n+1];
      new_in_process_trans[n] = tobj;
      for(int i = 0; i<n; i++){
        new_in_process_trans[i] = in_process_trans[i];
      }
      in_process_trans = new_in_process_trans;
    }

    //add the newly made transaction to the pending transacsions list in 
    DSobj.pendingTransactions.AddTransactions(tobj);

  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    TransactionBlock block = DSObj.bChain.lastBlock;
    int index = 0;
    Transaction[] tarr = null;
    boolean found = false;
    while(block != null){
      tarr = block.trarray;
      int n = tarr.length;
      for(int i = 0; i<n; i++){
        if(tarr[i].coinID.equals(tobj.coinID) && tarr[i].Source == tobj.Source && tarr[i].Destination == tobj.Destination && tarr[i].coinsrc_block == tobj.coinsrc_block){
          found = true;
          index = i;
          break;
        }
      }
      if(found){
        break;
      }
      block = block.previous;
    }

    if(!found){
      System.out.println("The transaction initiated by UID " + tobj.Source.UID + " with coinID " + tobj.coinID + " to UID " + tobj.Destination.UID + " was not processed, please look into it as that transaction might be invalid.");
      throw new MissingTransactionException();
    } else {
      List<Pair<String, String>> treeProof = block.Tree.sib_couple_path(index);
      List<Pair<String, String>> blockProof = DSObj.bChain.back_path(block);
      Pair<List<Pair<String, String>>, List<Pair<String, String>>> p = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(treeProof, blockProof);

      int n = in_process_trans.length;
      int k = 0;
      for(int i = 0; i<n; i++){
        if(in_process_trans[i] == tobj){
          k = i;
          break;
        }
      }
      Transaction[] new_in_process_trans = new Transaction[n-1];
      for(int i = 0; i<n-1; i++){
        if(i<k){
          new_in_process_trans[i] = in_process_trans[i];
        } else {
          new_in_process_trans[i] = in_process_trans[i+1];
        }
      }
      in_process_trans = new_in_process_trans;

      Pair<String, TransactionBlock> np = new Pair<String, TransactionBlock>(tobj.coinID, block);
      if(tobj.Destination.mycoins.size() == 0){
        tobj.Destination.mycoins.add(np);
      } else {
        int a;
        int b = Integer.valueOf(tobj.coinID);
        int i = 0;
        do{
          a = Integer.valueOf(tobj.Destination.mycoins.get(i).first);
          i+=1;
        } while(a<b && i!=tobj.Destination.mycoins.size());

        if(i == tobj.Destination.mycoins.size() && a<b){
          tobj.Destination.mycoins.add(np);
        } else {
          tobj.Destination.mycoins.add(i-1, np);
        }
        
      }
      
      return p;
    }
  }

  public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException{
    TransactionBlock TempBlock = new TransactionBlock();
    DSObj.bChain.Temporary_InsertBlock_Honest(TempBlock);

    int m = DSObj.bChain.tr_count;
    Transaction tr;
    Transaction[] tarr = new Transaction[m];
    int idx = 0;
    while(idx != m-1){
      tr = DSObj.pendingTransactions.RemoveTransaction();
      if(TempBlock.checkTransaction(tr)){
        if(idx==0){
          tarr[idx] = tr;
          idx += 1;
        } else {
          boolean exists = false;
          for(int j = 0; j<idx; j++){
            if(tarr[j].coinID == tr.coinID){
              exists = true;
              break;
            }
          }
          if(!exists){
            tarr[idx] = tr;
            idx += 1;
          }
        }
      } 
    }

    Transaction minerRewardTransaction = new Transaction();
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;

    tarr[m-1] = minerRewardTransaction;

    TransactionBlock newBlock = new TransactionBlock(tarr);
    DSObj.bChain.InsertBlock_Honest(newBlock);

    Pair<String, TransactionBlock> p = new Pair<String, TransactionBlock>(DSObj.latestCoinID,newBlock);
    if(mycoins.size() == 0){
      mycoins.add(p);
    } else {
      int a;
      int b = Integer.valueOf(DSObj.latestCoinID);
      int i = 0;
      do{
        a = Integer.valueOf(mycoins.get(i).first);
        i+=1;
      } while(a<b && i!=mycoins.size());

      if(i == mycoins.size() && a<b){
        mycoins.add(p);
      } else {
        mycoins.add(i-1, p);
      }
       
    }
    String s = String.valueOf(Integer.valueOf(DSObj.latestCoinID)+1);
    DSObj.latestCoinID = s;
  }  

  public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException {
    TransactionBlock TempBlock = new TransactionBlock();
    DSObj.bChain.Temporary_InsertBlock_Malicious(TempBlock);

    int m = DSObj.bChain.tr_count;
    Transaction tr;
    Transaction[] tarr = new Transaction[m];
    int idx = 0;
    while(idx != m-1){
      tr = DSObj.pendingTransactions.RemoveTransaction();
      if(TempBlock.checkTransaction(tr)){
        if(idx==0){
          tarr[idx] = tr;
          idx += 1;
        } else {
          boolean exists = false;
          for(int j = 0; j<idx; j++){
            if(tarr[j].coinID == tr.coinID){
              exists = true;
              break;
            }
          }
          if(!exists){
            tarr[idx] = tr;
            idx += 1;
          }
        }
      } 
    }

    Transaction minerRewardTransaction = new Transaction();
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;

    tarr[m-1] = minerRewardTransaction;

    TransactionBlock newBlock = new TransactionBlock(tarr);
    DSObj.bChain.InsertBlock_Malicious(newBlock);

    Pair<String, TransactionBlock> p = new Pair<String, TransactionBlock>(DSObj.latestCoinID,newBlock);
    if(mycoins.size() == 0){
      mycoins.add(p);
    } else {
      int a;
      int b = Integer.valueOf(DSObj.latestCoinID);
      int i = 0;
      do{
        a = Integer.valueOf(mycoins.get(i).first);
        i+=1;
      } while(a<b && i!=mycoins.size());

      if(i == mycoins.size() && a<b){
        mycoins.add(p);
      } else {
        mycoins.add(i-1, p);
      }
       
    }

    String s = String.valueOf(Integer.valueOf(DSObj.latestCoinID)+1);
    DSObj.latestCoinID = s;
  }  

  
}
