package core.cli;

import core.util.Worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI extends Worker {

    private InputStream in;
    private OutputStream out;
    private ArrayList<CommandHandler> handlers;
    private final String PROMPT = "> ";
    private CommandInterpreter interpreter;
    private Scanner scanner;

    public CLI(CommandInterpreter interpreter, InputStream in, OutputStream out){
        super();
        this.in =  in;
        this.out = out;
        this.handlers = new ArrayList<CommandHandler>();
        this.interpreter = interpreter;
    }

    public void setup (){
        this.scanner = new Scanner(this.in);
    }

    public void teardown () {
        this.scanner.close();
    }

    public void execute (){
        write(PROMPT);
        String line = scanner.nextLine();
        try {
            Command command = this.interpreter.parseCommand(line);
            notifyHandlers(command);
        } catch (CommandInputException e) {
            write(e.getMessage() + "\n");
        }
    }


    public void addCommandHandler(CommandHandler handler){
        handlers.add(handler);
    }

    public void notifyHandlers(Command command){
        for (CommandHandler handler : handlers){
            handler.handleCommand(command);
        }
    }

    private void write(String output){
        try {
            out.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void message(String message){
    	write(message + "\n");
    }
}
