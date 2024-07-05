package DSCoinPackage;

import java.util.List;
import java.util.*;

import HelperClasses.CRF;
import HelperClasses.*;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

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

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF f = new CRF(64);
    
    String pdgst = null;
    String sum = newBlock.trsummary; 

    if(lastBlock == null){
      pdgst = start_string;

      newBlock.nonce = Nonce(pdgst, sum);
      newBlock.dgst = f.Fn(pdgst + "#" + sum + "#" + newBlock.nonce);
  
      lastBlock = newBlock;
    } else {
      pdgst = lastBlock.dgst;

      newBlock.nonce = Nonce(pdgst, sum);
      newBlock.dgst = f.Fn(pdgst + "#" + sum + "#" + newBlock.nonce);
      newBlock.previous = lastBlock;

      lastBlock = newBlock;
    }
  }

  public void Temporary_InsertBlock_Honest(TransactionBlock newBlock){
    newBlock.previous = lastBlock;
  }

  public List<Pair<String, String>> back_path(TransactionBlock tB){
    List<Pair<String, String>> l = new ArrayList<Pair<String, String>>();
    List<Pair<String, String>> tl = new ArrayList<Pair<String, String>>();
    
    TransactionBlock block = lastBlock;
    String dg;
    String pdgst;
    String sum;
    String nonce;
    String v;
    while(block != tB.previous){
      dg = block.dgst;
      pdgst = block.previous.dgst;
      sum = block.trsummary;
      nonce = block.nonce;
      v = pdgst + "#" + sum + "#" + nonce;

      Pair<String, String> r = new Pair<String, String>(dg,v);
      tl.add(r);

      block = block.previous;
    }

    Pair<String, String> p = new Pair<String, String>(tB.previous.dgst, null);
    tl.add(p);

    for(int i = 0; i<tl.size(); i++){
      Pair<String, String> k = tl.get(tl.size()-i-1);
      l.add(k);
    }

    return l;
  }
}
