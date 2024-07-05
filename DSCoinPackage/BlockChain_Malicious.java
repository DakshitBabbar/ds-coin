package DSCoinPackage;

import java.util.*;

import HelperClasses.CRF;
import HelperClasses.*;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public String next(String x){
    int y = Integer.valueOf(x);
    String s = String.valueOf(y+1);
    return s;
  }

  public String Nonce(String pdgst, String sum){
    CRF f = new CRF(64);
    String dg = null;
    String s = "1000000001";
    for(;;){
      boolean allzero = true;
      dg = f.Fn(pdgst + "#" + sum + "#" + s);
      for(int i = 0; i<4; i++){
        if(dg.charAt(i) != '0'){
          allzero = false;
          break;
        }
      }
      if(allzero){
        break;
      }
      s = next(s);
    }
    return s;
  }

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    CRF f = new CRF(64);

    //check all transactions of the block
    // System.out.println(tB);
    int n = tB.trarray.length;
    for(int i = 0; i<n; i++){
      if(!tB.checkTransaction(tB.trarray[i])){
        return false;
      }
    }

    //check summary
    String csum = tB.Tree.Build(tB.trarray);
    if(!tB.trsummary.equals(csum)){
      return false;
    }

    //check dgst
    String pdgst = null;
    String sum = tB.trsummary;
    String nonce = tB.nonce;
    String cdg = null;
    if(tB.previous == null){
      pdgst = start_string;

      cdg = f.Fn(pdgst + "#" + sum + "#" + nonce);

      if(!tB.dgst.equals(cdg)){
        return false;
      }
    } else {
      pdgst = tB.previous.dgst;

      cdg = f.Fn(pdgst + "#" + sum + "#" + nonce);

      if(!tB.dgst.equals(cdg)){
        return false;
      }
    }

    //check nonce
    String dg = tB.dgst;
    for(int i = 0; i<4; i++){
      if(dg.charAt(i) != '0'){
        return false;
      }
    }

    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    int n = lastBlocksList.length;
    // System.out.println(n);
    TransactionBlock ans = null; //store the maximum length of valid subchain we got in every iteration and its corresponding lastblock.
    int l = 0;
    for(int i = 0; i<n; i++){//iterate over the lastBlockList.
      if(lastBlocksList[i] != null){
        // System.out.println("CHECKING");
        int prevL = l;//store the length of the previously maximum valid chain.
        TransactionBlock startBlock = lastBlocksList[i];
        TransactionBlock pointerBlock = null;
        while(true){//move back from the startBlock.
          pointerBlock = startBlock;
          if(!checkTransactionBlock(startBlock)){//if startBLock is invalid start again from its previous block.
            startBlock = startBlock.previous;
          } else {
            while(pointerBlock != null && checkTransactionBlock(pointerBlock)){
              pointerBlock = pointerBlock.previous;
              l+=1;
            }
            
            if(pointerBlock == null){//if reached the first block then stop.
              if(prevL < l){//compare the previouly maximum length of the valid chain with the present value of the valid chain, keep the maximum one. 
                ans = startBlock;
              } else {
                l = prevL;
              }
              break;
            } else {//if reched a invalid block before reaching the first block then start again fro there.
              startBlock = pointerBlock;
            }
          }
        }
      }  
    }
    return ans;//return the the last block of the valid chain corresponding to the maximum lenght l.
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    CRF f = new CRF(64);
    
    String pdgst = null;
    String sum = newBlock.trsummary;
    //check if all elements of lastblocklist is null
    boolean allnull = true;
    for(int i = 0; i<lastBlocksList.length; i++){
      if(lastBlocksList[i] != null){
        allnull = false;
        break;
      }
    }

    if(lastBlocksList == null || allnull){
      pdgst = start_string;

      newBlock.nonce = Nonce(pdgst, sum);
      newBlock.dgst = f.Fn(pdgst + "#" + sum + "#" + newBlock.nonce);

      newBlock.previous = null;
      TransactionBlock[] newList = new TransactionBlock[1];
      newList[0] = newBlock;

      lastBlocksList = newList;
    } else {
      //find the appropriate last block
      TransactionBlock block = FindLongestValidChain();

      //set the previous pointer of the newblock
      newBlock.previous = block;
      
      //set nonce and dgst
      pdgst = block.dgst;
      newBlock.nonce = Nonce(pdgst, sum);
      newBlock.dgst = f.Fn(pdgst + "#" + sum + "#" + newBlock.nonce);

      //modify the lastBlockList accordingly.
      int n = lastBlocksList.length;
      boolean found = false;
      for(int i = 0; i<n; i++){
        if(lastBlocksList[i] == block){
          lastBlocksList[i] = newBlock;
          found = true;
          break;
        }
      }
      if(!found){
        TransactionBlock[] newList = new TransactionBlock[n+1];
        newList[n] = newBlock;
        for(int i = 0; i<n; i++){
          newList[i] = lastBlocksList[i];
        }
        lastBlocksList = newList;
      }
    }
  }

  public void Temporary_InsertBlock_Malicious(TransactionBlock newBlock){
    //find the appropriate last block
    TransactionBlock block = FindLongestValidChain();
    //set the previous pointer of the newblock
    newBlock.previous = block;
  }
}
