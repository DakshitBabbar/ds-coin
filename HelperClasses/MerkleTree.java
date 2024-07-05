package HelperClasses;

import DSCoinPackage.Transaction;
import java.util.*;

public class MerkleTree {

  // Check the TreeNode.java file for more details
  public TreeNode rootnode;
  public int numdocs;

  void nodeinit(TreeNode node, TreeNode l, TreeNode r, TreeNode p, String val) {
    node.left = l;
    node.right = r;
    node.parent = p;
    node.val = val;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis"; 
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public String Build(Transaction[] tr) {
    CRF obj = new CRF(64);
    int num_trans = tr.length;
    List<TreeNode> q = new ArrayList<TreeNode>();
    for (int i = 0; i < num_trans; i++) {
      TreeNode nd = new TreeNode();
      String val = get_str(tr[i]);
      nodeinit(nd, null, null, null, val);
      q.add(nd);
    }
    TreeNode l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      TreeNode nd = new TreeNode();
      String l_val = l.val;
      String r_val = r.val;
      String data = obj.Fn(l_val + "#" + r_val);
      nodeinit(nd, l, r, null, data);
      l.parent = nd;
      r.parent = nd;
      q.add(nd);
    }
    rootnode = q.get(0);
    numdocs = num_trans;

    return rootnode.val;
  }

  public List<Pair<String, String>> sib_couple_path(int x){
    List<Pair<String,String>> v = new ArrayList<Pair<String,String>>();

		int n = numdocs;
		int idx = x;

		TreeNode node = rootnode;

		int s = 1;
		int e = n;
		int a;

		Pair<String,String> p = new Pair<String,String>(node.val, null);
		v.add(p);

		while(node.right != null){

			Pair<String,String> j = new Pair<String,String>(node.left.val, node.right.val);
			v.add(j);

			a = (s + e)/2;
			
			if(idx <= a){
				node = node.left;
				e = a;
			} else {
				node = node.right;
				s = a + 1;
			}
		}

		int l = v.size();
		List<Pair<String,String>> finalList = new ArrayList<Pair<String,String>>(l);
		for(int i = 0; i<l; i++){
			Pair<String,String> k = v.get(l-i-1);
			finalList.add(k);
		}
		return finalList;
  }
}
