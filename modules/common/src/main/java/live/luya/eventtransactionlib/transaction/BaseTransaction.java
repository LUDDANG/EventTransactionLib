package live.luya.eventtransactionlib.transaction;

public interface BaseTransaction<BASED_ON> {
	BASED_ON getBase();
}
