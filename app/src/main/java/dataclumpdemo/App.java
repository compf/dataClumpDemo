/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package dataclumpdemo;

import java.sql.Date;

public class App {

    public static void main(String[] args) {
        CompanyUtils utils = new CompanyUtils();
        String exampleIban="DE02120300000000202051";
        utils.payBill(exampleIban, 10, Date.valueOf("2025-01-01"),"0", 0);
    }
}
