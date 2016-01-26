package core.cli;

public enum CommandToken {
	READ		("read", 1),
	WRITE		("write", 1),
	SHUTDOWN	("shutdown", 0),
	HELP		("help", 0);
	
	private String token;
	private int argc;
	
	CommandToken(String token, int argc) {
		this.token = token;
		this.argc = argc;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public int getArgumentCount() {
		return this.argc;
	}
}
