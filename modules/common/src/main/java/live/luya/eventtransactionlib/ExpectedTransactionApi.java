package live.luya.eventtransactionlib;

public class ExpectedTransactionApi {
	public static final ExpectedTransactionApi BUKKIT = new ExpectedTransactionApi("Bukkit");

	public static final ExpectedTransactionApi BUNGEE = new ExpectedTransactionApi("BungeeCord");

	private final String name;

	public ExpectedTransactionApi(String name) {
		this.name = name;
	}
}
