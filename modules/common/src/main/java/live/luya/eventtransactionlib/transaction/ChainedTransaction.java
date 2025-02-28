package live.luya.eventtransactionlib.transaction;

public interface ChainedTransaction<BASED_ON, TRANSACTION extends BaseTransaction<BASED_ON>> {
	TRANSACTION getChained();
}
