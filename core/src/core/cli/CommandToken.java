package core.cli;

public enum CommandToken {
	READ		("read"),
	WRITE		("write"),
	SHUTDOWN	("shutdown"),
	HELP		("help");
	
	private String token;
	
	CommandToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return this.token;
	}
}
