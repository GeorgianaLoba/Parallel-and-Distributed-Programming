package com.company;

import repository.Bank;
import service.Service;
import ui.Console;

public class Main {

    public static void main(String[] args) {
        Bank bank = new Bank();
        Service service = new Service(bank);
        Console console = new Console(service);
        console.perform();
    }
}
