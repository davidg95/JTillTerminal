/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ReceiptPrinter implements Printable {

    private final Sale sale; //The Sale to print.
    private final DataConnect dc;
    public static PrinterJob job;
    public static boolean ready;

    public ReceiptPrinter(DataConnect dc, Sale s) {
        this.sale = s;
        this.dc = dc;
    }

    public static void initPrinter() {
        job = PrinterJob.getPrinterJob();
        ready = job.printDialog();
    }

    public static PrinterJob getJob() {
        return job;
    }

    public static void print(DataConnect dc, Sale sale) throws PrinterException {
        if (ready) {
            ReceiptPrinter print = new ReceiptPrinter(dc, sale);
            job.setPrintable(print);
            job.print();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        String header = "Sale Receipt";
        String footer = "Thank you for your custom";
        try {
            header = dc.getSetting("RECEIPT_HEADER"); //Get the receipt header for the receipt.
            footer = dc.getSetting("RECEIPT_FOOTER"); //Get the receipt footer for ther receipt.
        } catch (IOException ex) {
            Logger.getLogger(ReceiptPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }

        Graphics2D g2 = (Graphics2D) graphics;
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        Font oldFont = graphics.getFont();

        final int x = 70;
        int y = 60;

        g2.setFont(new Font("Arial", Font.BOLD, 20)); //Use a differnt font for the header.
        g2.drawString(header, x, y);
        g2.setFont(oldFont); //Chagne back to the old font.

        y += 30;

        try {
            if (dc.getSetting("SHOW_RECEIPT_ADDRESS").equals("TRUE")) {
                y += 30;
            }
            if (dc.getSetting("SHOW_RECEIPT_STAFF").equals("TRUE")) {
                final Staff s = dc.getStaff(sale.getStaffID());
                g2.drawString("You were served by: " + s.getName(), x, y);
                y += 30;
            }
            if (dc.getSetting("SHOW_RECEIPT_TERMINAL").equals("TRUE")) {
                g2.drawString("Terminal " + sale.getTerminalID(), x, y);
                y += 30;
            }
        } catch (IOException | StaffNotFoundException | SQLException ex) {
            Logger.getLogger(ReceiptPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Print sale info.
        g2.drawString("Receipt for sale: " + sale.getId(), x, y);
        y += 20;
        g2.drawString("Time: " + sale.getDate(), x, y);
        y += 20;
        g2.drawString("Served by " + sale.getStaffID(), x, y);

        final int item = x + 30;
        final int quantity = x + 230;
        final int total = x + 350;
        y += 50;

        //Print collumn headers.
        g2.drawString("Item", item, y);
        g2.drawString("Quantity", quantity, y);
        g2.drawString("Total", total, y);
        g2.drawLine(item - 30, y + 10, total + 100, y + 10);

        y += 30;

        //Print the sale items.
        for (SaleItem it : sale.getSaleItems()) {
            if (it.getType() == SaleItem.PRODUCT) {
                final Product p = (Product) it.getProduct();
                g2.drawString(p.getName(), item, y);
            } else {
                final Discount d = (Discount) it.getProduct();
                g2.drawString(d.getName(), item, y);
            }
            g2.drawString("" + it.getQuantity(), quantity, y);
            g2.drawString("£" + it.getPrice(), total, y);
            y += 30;
        }
        g2.drawLine(item - 30, y - 20, total + 100, y - 20);
        g2.drawString("Total: £" + sale.getTotal(), total, y);

        //Print the footer.
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString(footer, 150, y + 50);

        return PAGE_EXISTS;
    }

}
