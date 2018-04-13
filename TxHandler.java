import java.util.ArrayList;

public class TxHandler {

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
	private UTXOPool upool;
	
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
    	upool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
    	double inTotal=0;
    	double outTotal=0;
    	UTXOPool validPool=new UTXOPool();
    	
    	for (int i=0; i<tx.numInputs(); i++){
    		Transaction.Input in = tx.getInput(i);
    		if (in==null) {
    			return false;
    		}
    		// Check Condition 1
    		UTXO u = new UTXO(in.prevTxHash, in.outputIndex);    		
    		if (!upool.contains(u)) {
    		return false;
    		}
    		// Check Condition 2
    		Transaction.Output prevTxOut = upool.getTxOutput(u);
			RSAKey address = prevTxOut.address
    		if (!address.verifySignature(tx.getRawDataToSign(i), in.signature)){
    			return false;
    		}
    		// Check Condition 3
    		if (validPool!=null && validPool.contains(u)){
    			return false;
    		}
    		validPool.addUTXO(u, prevTxOut);
    		// Sum of input values
    		inTotal+=prevTxOut.value;
    	}
    	
    	for (int i=0; i<tx.numOutputs(); i++){
    		Transaction.Output out = tx.getOutput(i);
    		if (out==null){
    			return false;
    		}
    		//Check condition 4
    		if (out.value<0){
    			return false;
    		}
    		outTotal+=out.value;
       		}
    	
    		// Check condition 5
    	if (inTotal>=outTotal){
    		return true;
    	}
    	return false;
    }
    	
    	
    	
 

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	if (possibleTxs==null){
    		return new Transaction[0];
    	}
    	ArrayList<Transaction> validTxs= new ArrayList<Transaction>();
    	// Check valid transactions from Possible transactions
    	for (Transaction tx : possibleTxs){
    		if (isValidTx(tx)){
    			validTxs.add(tx); 
    			updateUtxoPool(tx); // Update UTXO Pool
    		}    			
    	}
    	return validTxs.toArray(new Transaction[validTxs.size()]);
    }    	
    
    private void updateUtxoPool(Transaction tx){
    	if (tx==null){
    		return;
    	}
    	for (Transaction.Input input : tx.getInputs()){
    		UTXO u = new UTXO(input.prevTxHash, input.outputIndex);
    		upool.removeUTXO(u);    		
    	}
    	int index=0;
    	for (Transaction.Output output : tx.getOutputs()){
    		UTXO u = new UTXO(tx.getHash(), index);
    		index+=1;
    		upool.addUTXO(u, output);
    	}
    	return;
    }
}
    
