package core.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI implements Runnable {

    private InputStream in;
    private OutputStream out;
    private boolean running;
    private ArrayList<CommandHandler> handlers;
    private final String PROMPT = "\n> ";
    private CommandInterpreter interpreter;

    public CLI(CommandInterpreter interpreter, InputStream in, OutputStream out){
        this.in =  in;
        this.out = out;
        this.running = false;
        this.handlers = new ArrayList<CommandHandler>();
        this.interpreter = interpreter;
    }

    public void run (){
        setRunning(true);
        Scanner sc = new Scanner(in);

        while (isRunning()){
            write(PROMPT);
            String line = sc.nextLine();
            try {
                Command command = this.interpreter.parseCommand(line);
                notifyHandlers(command);
            } catch (CommandInputException e) {
                write(e.getMessage() + "\n");
            }
        }

        sc.close();
    }

    public void stop(){
        setRunning(false);
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

    private synchronized void setRunning(boolean value){
        this.running = value;
    }

    private synchronized boolean isRunning(){
        return running;
    }
}
