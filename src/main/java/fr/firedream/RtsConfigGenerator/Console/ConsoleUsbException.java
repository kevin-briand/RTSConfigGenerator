package fr.firedream.RtsConfigGenerator.Console;

public class ConsoleUsbException extends RuntimeException {
    public ConsoleUsbException() {
        super("ConsoleUsbException Error");
    }

    public ConsoleUsbException(String message) {
        super("ConsoleUsbException - " + message);
    }
}
