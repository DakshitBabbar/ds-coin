package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.*;

import java.lang.reflect.Member;

import javax.print.DocFlavor;
import javax.swing.text.TabExpander;

import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  public TransactionBlock(Transaction[] t) {
    //set trarray
    int n = t.length;
    trarray = new Transaction[n];

    for(int i = 0; i<n; i++){
      trarray[i] = t[i];
    }

    //set previous
    previous = null;

    //set Tree and trsummary
    MerkleTree mtree = new MerkleTree();
    trsummary = mtree.Build(trarray);
    Tree = mtree;

    //set nonce and dgst
    nonce = null;
    dgst = null;
  }

  public TransactionBlock(){
    
  }

  public boolean checkTransaction (Transaction t) {
    boolean valid;
    String cid = t.coinID;
    Members newsrc = t.Source;

    if(newsrc == null || newsrc.UID.equals("Moderator")){
      return true;
    } else { 
      //check in the same block 
      int c;
      int n;
      // for(int i = 0; i<n; i++) {
      //   if(trarray[i].coinID.equals(cid)){
      //     c+=1;
      //   }
      // }
      // if(c>1){
      //   return false;
      // }

      //check in the intermediate blocks
      TransactionBlock block = this.previous;
      while(block != t.coinsrc_block){
        n = block.trarray.length;
        for(int i = 0; i<n; i++){
          if(block.trarray[i].coinID.equals(cid)){
            return false;
          }
        }
        block = block.previous;
      }

      // System.out.println(t.coinsrc_block);
      //check the last block
      int s = 0;
      c = 0;
      n = block.trarray.length;
      for(int i = 0; i<n; i++){
        if(block.trarray[i].coinID.equals(cid)){
          c+=1;
          if(block.trarray[i].Destination == newsrc){
            s+=1;
          }
        }
      }
      if(c!=1 && s!=1){
        return false;
      }

      return true;
    }
  }
}
