package fr.firedream.RtsConfigGenerator.Console;

import com.fazecast.jSerialComm.SerialPort;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class ConsoleUsb extends Thread {
    private final SerialPort sp;
    private boolean listenActive = true;

    public ConsoleUsb(String port, int baudRate) {
        System.out.println("Open Console on port " + port + " and baudrate " + baudRate);
        sp = SerialPort.getCommPort(port);
        sp.setComPortParameters(baudRate,
                8,
                SerialPort.ONE_STOP_BIT,
                SerialPort.NO_PARITY);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING,
                1000,
                0);
        sp.openPort();
    }

    public static List<String> getAvailablePorts() {
        List<String> listPorts = new ArrayList<>();
        for(SerialPort sp : SerialPort.getCommPorts()) {
            listPorts.add(sp.getSystemPortName());
        }
        return listPorts;
    }

    public boolean isOpen() {
        return sp.isOpen();
    }

    public boolean sendCommand(String command) {
        byte[] WriteByte = command.getBytes();

        if(sp.writeBytes(WriteByte,WriteByte.length) > 0) {
            System.out.println("sended : " + command);
            return true;
        }
        return false;
    }

    public void receiptData() {
        if(sp.bytesAvailable() > 0) {
            byte[] readBuffer = new byte[sp.bytesAvailable()];
            sp.readBytes(readBuffer,sp.bytesAvailable());
            String commandReceipted = new String(readBuffer, StandardCharsets.UTF_8);

            if ((commandReceipted.charAt(0) == '1' || commandReceipted.charAt(0) == '2') &&
                    commandReceipted.charAt(commandReceipted.length()-1) == ';') {
                System.out.println("Receipted : " + commandReceipted);
                receiptCommand(commandReceipted);
            }
        }
    }

    @Override
    public void run() {
        while (listenActive) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new ConsoleUsbException(e.getMessage());
            }
            receiptData();
        }
    }

    public void close() {
        listenActive = false;
        sp.closePort();
    }

    public abstract void receiptCommand(String command);
}