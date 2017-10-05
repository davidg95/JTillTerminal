/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.TillReport;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 *
 * @author David
 */
public class ReportPrinter implements Printable {

    private final TillReport report;
    public static PrinterJob job;
    public static boolean ready;

    public ReportPrinter(TillReport tr) {
        this.report = tr;
    }

    public static void initPrinter() {
        job = PrinterJob.getPrinterJob();
        ready = job.printDialog();
    }

    public static PrinterJob getJob() {
        return job;
    }

    public static void print(TillReport tr) throws PrinterException {
        if (ready) {
            ReportPrinter print = new ReportPrinter(tr);
            job.setPrintable(print);
            job.print();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        final String header = "Cashup for terminal " + report.terminal;

        Graphics2D g = (Graphics2D) graphics;
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        final int x = 70;
        int y = 70;

        g.drawString(header, x, y);

        y += 30;

        g.drawString("Declared takings: £" + report.declared.toString(), x, y);
        y += 30;
        g.drawString("Expected takings: £" + report.actualTakings.toString(), x, y);
        y += 30;
        g.drawString("Difference: £" + report.difference, x, y);
        y += 30;
        g.drawString("Transaction count: " + report.transactions, x, y);
        y += 30;
        g.drawString("Average spend: £" + report.averageSpend, x, y);
        y += 30;
        g.drawString("Tax value: £" + report.tax, x, y);

        return PAGE_EXISTS;
    }

}
