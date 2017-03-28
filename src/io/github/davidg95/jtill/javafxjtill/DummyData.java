/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import io.github.davidg95.JTill.jtill.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 * THIS CLASS IS PURELY FOR TESTING PURPOSES
 *
 * @author David
 */
public class DummyData implements DataConnect {

    private final List<Product> products;
    private final List<Customer> customers;
    private final List<Category> categories;
    private final List<Tax> taxes;
    private final List<Staff> staff;
    private final List<Plu> plus;
    private final List<Sale> sales;
    private final List<Discount> discounts;
    private final List<Screen> screens;
    private final List<TillButton> buttons;

    private int products_id = 1;
    private int customers_id = 1;
    private int categories_id = 1;
    private int taxes_id = 1;
    private int staff_id = 1;
    private int plus_id = 1;
    private int sales_id = 1;
    private int discount_id = 1;

    private final List<Staff> loggedIn;

    private GUIInterface g;

    public DummyData() {
        products = new ArrayList<>();
        customers = new ArrayList<>();
        categories = new ArrayList<>();
        taxes = new ArrayList<>();
        staff = new ArrayList<>();
        plus = new ArrayList<>();
        sales = new ArrayList<>();
        discounts = new ArrayList<>();
        screens = new ArrayList<>();
        buttons = new ArrayList<>();

        loggedIn = new ArrayList<>();

        createData();
    }

    private void createData() {
        Plu plu1 = new Plu(1, "124234");
        Plu plu2 = new Plu(2, "4230348");

        plus.add(plu1);
        plus.add(plu2);

        Category c1 = new Category(1, "Default", null, null, false, 0);

        categories.add(c1);

        Tax t1 = new Tax(1, "ZERO", 0);

        taxes.add(t1);

        Product p1 = new Product("Open", "Open", c1, new Department(1, "DEFAULT"), "None", t1, plu1, true, 1);
        Product p2 = new Product("Cheese", "Cheese", c1, new Department(1, "DEFAULT"), "None", t1, false, new BigDecimal("5.00"), new BigDecimal("3.00"), 5, 2, 10, plu2, 2);

        products.add(p1);
        products.add(p2);

        Staff s = new Staff(1, "David Grant", 3, "dgrant", "password123");

        staff.add(s);

        Screen sc = new Screen("Main", 1, 0, 1);

        screens.add(sc);

        TillButton b1 = new TillButton("Cheese", p2, sc, 0, 1);
        TillButton b2 = new TillButton("Open", p1, sc, 0, 2);

        buttons.add(b1);
        buttons.add(b2);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void setGUI(GUIInterface g) {
        this.g = g;
    }

    @Override
    public void assisstance(String message) throws IOException {
        g.showMessage("Assisstance", message);
    }

    @Override
    public BigDecimal getTillTakings(String terminal) throws IOException, SQLException {
        return BigDecimal.ZERO;
    }

    @Override
    public void sendEmail(String message) throws IOException {
    }

    @Override
    public void emailReceipt(String email, Sale sale) throws IOException, AddressException, MessagingException {
    }

    @Override
    public void setSetting(String key, String value) throws IOException {
    }

    @Override
    public String getSetting(String key) throws IOException {
        return "";
    }

    @Override
    public Product addProduct(Product p) throws IOException, SQLException {
        p.setId(products_id);
        products_id++;
        products.add(p);
        return p;
    }

    @Override
    public void removeProduct(int code) throws ProductNotFoundException, IOException, SQLException {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == code) {
                products.remove(i);
                return;
            }
        }
        throw new ProductNotFoundException(code + " could not be found");
    }

    @Override
    public void removeProduct(Product p) throws IOException, ProductNotFoundException, SQLException {
        removeProduct(p.getId());
    }

    @Override
    public int purchaseProduct(Product p, int amount) throws IOException, ProductNotFoundException, OutOfStockException, SQLException {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == p.getId()) {
                for (int j = 1; j <= amount; j++) {
                    products.get(i).purchace();
                    return products.get(i).getStock();
                }
            }
        }
        throw new ProductNotFoundException(p.getId() + " could not be found");
    }

    @Override
    public Product getProduct(int code) throws IOException, ProductNotFoundException, SQLException {
        for (Product p : products) {
            if (p.getId() == code) {
                return p;
            }
        }
        throw new ProductNotFoundException(code + " could not be found");
    }

    @Override
    public List<Product> getAllProducts() throws IOException, SQLException {
        return products;
    }

    @Override
    public Product updateProduct(Product p) throws IOException, ProductNotFoundException, SQLException {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).equals(p)) {
                products.set(i, p);
                return p;
            }
        }
        throw new ProductNotFoundException(p.getId() + " could not be found");
    }

    @Override
    public boolean checkBarcode(String barcode) throws IOException, SQLException {
        for (Product p : products) {
            if (p.getPlu().getCode().equals(barcode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Product getProductByBarcode(String barcode) throws IOException, ProductNotFoundException, SQLException {
        for (Product p : products) {
            if (p.getPlu().getCode().equals(barcode)) {
                return p;
            }
        }
        throw new ProductNotFoundException(barcode + " could not be found");
    }

    @Override
    public List<Discount> getProductsDiscount(Product p) throws IOException, SQLException {
        return new ArrayList<>();
    }

    @Override
    public List<Product> productLookup(String terms) throws IOException, SQLException {
        List<Product> pl = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(terms.toLowerCase())) {
                pl.add(p);
            }
        }
        return pl;
    }

    @Override
    public Plu addPlu(Plu plu) throws IOException, SQLException {
        plu.setId(plus_id);
        plus_id++;
        plus.add(plu);
        return plu;
    }

    @Override
    public void removePlu(int id) throws IOException, JTillException, SQLException {
        for (int i = 0; i < plus.size(); i++) {
            if (plus.get(i).getId() == id) {
                plus.remove(i);
                return;
            }
        }
        throw new JTillException(id + " not found");
    }

    @Override
    public void removePlu(Plu p) throws IOException, JTillException, SQLException {
        removePlu(p.getId());
    }

    @Override
    public Plu getPlu(int id) throws IOException, JTillException, SQLException {
        for (Plu p : plus) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new JTillException(id + " not found");
    }

    @Override
    public Plu getPluByCode(String code) throws IOException, JTillException, SQLException {
        for (Plu p : plus) {
            if (p.getCode().equals(code)) {
                return p;
            }
        }
        throw new JTillException(code + " not found");
    }

    @Override
    public List<Plu> getAllPlus() throws IOException, SQLException {
        return plus;
    }

    @Override
    public Plu updatePlu(Plu p) throws IOException, JTillException, SQLException {
        for (int i = 0; i < plus.size(); i++) {
            if (plus.get(i).equals(p)) {
                plus.set(i, p);
                return p;
            }
        }
        throw new JTillException(p.getId() + " not found");
    }

    @Override
    public Customer addCustomer(Customer customer) throws IOException, SQLException {
        customer.setId(customers_id);
        customers_id++;
        customers.add(customer);
        return customer;
    }

    @Override
    public void removeCustomer(int id) throws IOException, CustomerNotFoundException, SQLException {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == id) {
                customers.remove(i);
                return;
            }
        }
        throw new CustomerNotFoundException(id + " could not be found");
    }

    @Override
    public void removeCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException {
        removeCustomer(c.getId());
    }

    @Override
    public Customer getCustomer(int id) throws IOException, CustomerNotFoundException, SQLException {
        for (Customer c : customers) {
            if (c.getId() == id) {
                return c;
            }
        }
        throw new CustomerNotFoundException(id + " could not be found");
    }

    @Override
    public List<Customer> getCustomerByName(String name) throws IOException, CustomerNotFoundException, SQLException {
        List<Customer> cl = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getName().toLowerCase().contains(name.toLowerCase())) {
                cl.add(c);
            }
        }
        return cl;
    }

    @Override
    public List<Customer> getAllCustomers() throws IOException, SQLException {
        return customers;
    }

    @Override
    public Customer updateCustomer(Customer c) throws IOException, CustomerNotFoundException, SQLException {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).equals(c)) {
                customers.set(i, c);
                return c;
            }
        }
        throw new CustomerNotFoundException(c.getId() + " could not be found");
    }

    @Override
    public List<Customer> customerLookup(String terms) throws IOException, SQLException {
        List<Customer> cl = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getName().toLowerCase().contains(terms.toLowerCase())) {
                cl.add(c);
            }
        }
        return cl;
    }

    @Override
    public Sale addSale(Sale s) throws IOException, SQLException {
        s.setId(sales_id);
        sales_id++;
        sales.add(s);
        return s;
    }

    @Override
    public List<Sale> getAllSales() throws IOException, SQLException {
        return sales;
    }

    @Override
    public Sale getSale(int id) throws IOException, SQLException, SaleNotFoundException {
        for (Sale s : sales) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new SaleNotFoundException(id + " could not be found");
    }

    @Override
    public List<Sale> getSalesInRange(Time start, Time end) throws IOException, SQLException {
        return new ArrayList<>();
    }

    @Override
    public Sale updateSale(Sale s) throws IOException, SQLException, SaleNotFoundException {
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).equals(s)) {
                sales.set(i, s);
                return s;
            }
        }
        throw new SaleNotFoundException(s.getId() + " could not be found");
    }

    @Override
    public void suspendSale(Sale sale, Staff staff) throws IOException {
    }

    @Override
    public Sale resumeSale(Staff s) throws IOException {
        return null;
    }

    @Override
    public List<Sale> getUncashedSales(String t) throws IOException, SQLException {
        return new ArrayList<>();
    }

    @Override
    public Staff addStaff(Staff s) throws IOException, SQLException {
        return null;
    }

    @Override
    public void removeStaff(int id) throws IOException, StaffNotFoundException, SQLException {
    }

    @Override
    public void removeStaff(Staff s) throws IOException, StaffNotFoundException, SQLException {
    }

    @Override
    public Staff getStaff(int id) throws IOException, StaffNotFoundException, SQLException {
        for (Staff s : staff) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new StaffNotFoundException(id + " could not be found");
    }

    @Override
    public List<Staff> getAllStaff() throws IOException, SQLException {
        return staff;
    }

    @Override
    public Staff updateStaff(Staff s) throws IOException, StaffNotFoundException, SQLException {
        for (int i = 0; i < staff.size(); i++) {
            if (staff.get(i).equals(s)) {
                staff.set(i, s);
                return s;
            }
        }
        throw new StaffNotFoundException(s.getId() + " could not be found");
    }

    @Override
    public int getStaffCount() throws IOException, SQLException {
        return staff.size();
    }

    @Override
    public Staff login(String username, String password) throws IOException, LoginException, SQLException {
        for (Staff s : staff) {
            if (s.getUsername().equalsIgnoreCase(username)) {
                s.login(password);
                return s;
            }
        }
        throw new LoginException(username + " could not be found");
    }

    @Override
    public Staff tillLogin(int id) throws IOException, LoginException, SQLException {
        for (Staff s : staff) {
            if (s.getId() == id) {
                if (loggedIn.contains(s)) {
                    throw new LoginException("You are already logged in elsewhere");
                }
                loggedIn.add(s);
                return s;
            }
        }
        throw new LoginException(id + " could not be found");
    }

    @Override
    public void logout(Staff s) throws IOException, StaffNotFoundException {
    }

    @Override
    public void tillLogout(Staff s) throws IOException, StaffNotFoundException {
        loggedIn.remove(s);
    }

    @Override
    public Category addCategory(Category c) throws IOException, SQLException {
        return null;
    }

    @Override
    public Category updateCategory(Category c) throws IOException, SQLException, CategoryNotFoundException {
        return null;
    }

    @Override
    public void removeCategory(Category c) throws IOException, SQLException, CategoryNotFoundException {
    }

    @Override
    public void removeCategory(int id) throws IOException, SQLException, CategoryNotFoundException {
    }

    @Override
    public Category getCategory(int id) throws IOException, SQLException, CategoryNotFoundException {
        for (Category c : categories) {
            if (c.getId() == id) {
                return c;
            }
        }
        throw new CategoryNotFoundException(id + " could not be found");
    }

    @Override
    public List<Category> getAllCategorys() throws IOException, SQLException {
        return categories;
    }

    @Override
    public List<Product> getProductsInCategory(int id) throws IOException, SQLException, CategoryNotFoundException {
        return new ArrayList<>();
    }

    @Override
    public Discount addDiscount(Discount d) throws IOException, SQLException {
        return null;
    }

    @Override
    public Discount updateDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException {
        return null;
    }

    @Override
    public void removeDiscount(Discount d) throws IOException, SQLException, DiscountNotFoundException {
    }

    @Override
    public void removeDiscount(int id) throws IOException, SQLException, DiscountNotFoundException {
    }

    @Override
    public Discount getDiscount(int id) throws IOException, SQLException, DiscountNotFoundException {
        for (Discount d : discounts) {
            if (d.getId() == id) {
                return d;
            }
        }
        throw new DiscountNotFoundException(id + " could not be found");
    }

    @Override
    public List<Discount> getAllDiscounts() throws IOException, SQLException {
        return discounts;
    }

    @Override
    public Tax addTax(Tax t) throws IOException, SQLException {
        return null;
    }

    @Override
    public void removeTax(Tax t) throws IOException, SQLException, TaxNotFoundException {
    }

    @Override
    public void removeTax(int id) throws IOException, SQLException, TaxNotFoundException {
    }

    @Override
    public Tax getTax(int id) throws IOException, SQLException, TaxNotFoundException {
        for (Tax t : taxes) {
            if (t.getId() == id) {
                return t;
            }
        }
        throw new TaxNotFoundException(id + " could not be found");
    }

    @Override
    public Tax updateTax(Tax t) throws IOException, SQLException, TaxNotFoundException {
        return t;
    }

    @Override
    public List<Tax> getAllTax() throws IOException, SQLException {
        return taxes;
    }

    @Override
    public List<Product> getProductsInTax(int id) throws IOException, SQLException, TaxNotFoundException {
        return new ArrayList<>();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Screen addScreen(Screen s) throws IOException, SQLException {
        return s;
    }

    @Override
    public TillButton addButton(TillButton b) throws IOException, SQLException {
        return b;
    }

    @Override
    public void removeScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
    }

    @Override
    public void removeButton(TillButton b) throws IOException, SQLException, ButtonNotFoundException {
    }

    @Override
    public Screen getScreen(int id) throws IOException, SQLException, ScreenNotFoundException {
        for (Screen s : screens) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new ScreenNotFoundException(id + " could not be found");
    }

    @Override
    public TillButton getButton(int id) throws IOException, SQLException, ButtonNotFoundException {
        for (TillButton b : buttons) {
            if (b.getId() == id) {
                return b;
            }
        }
        throw new ButtonNotFoundException(id + " could not be found");
    }

    @Override
    public Screen updateScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        return s;
    }

    @Override
    public TillButton updateButton(TillButton b) throws IOException, SQLException, ButtonNotFoundException {
        return b;
    }

    @Override
    public List<Screen> getAllScreens() throws IOException, SQLException {
        return screens;
    }

    @Override
    public List<TillButton> getAllButtons() throws IOException, SQLException {
        return buttons;
    }

    @Override
    public List<TillButton> getButtonsOnScreen(Screen s) throws IOException, SQLException, ScreenNotFoundException {
        List<TillButton> bons = new ArrayList<>();
        for (TillButton b : buttons) {
            if (b.getScreen().equals(s)) {
                bons.add(b);
            }
        }
        return bons;
    }

    @Override
    public void deleteAllScreensAndButtons() throws IOException, SQLException {
        screens.clear();
        buttons.clear();
    }

    @Override
    public Till addTill(Till t) throws IOException, SQLException {
        return t;
    }

    @Override
    public void removeTill(int id) throws IOException, SQLException, TillNotFoundException {
    }

    @Override
    public Till getTill(int id) throws IOException, SQLException, TillNotFoundException {
        return null;
    }

    @Override
    public List<Till> getAllTills() throws IOException, SQLException {
        return new ArrayList<>();
    }

    @Override
    public Till connectTill(String t) throws IOException {
        return null;
    }

    @Override
    public void disconnectTill(Till t) {
    }

    @Override
    public List<Till> getConnectedTills() throws IOException {
        return null;
    }

    @Override
    public boolean checkUsername(String username) throws IOException, SQLException {
        return false;
    }

    @Override
    public boolean isTillLoggedIn(Staff s) throws IOException, StaffNotFoundException, SQLException {
        return false;
    }

    @Override
    public String getSetting(String key, String value) throws IOException {
        return "";
    }

    @Override
    public Settings getSettingsInstance() throws IOException {
        return null;
    }

    @Override
    public GUIInterface getGUI() {
        return this.g;
    }

    @Override
    public WasteReport addWasteReport(WasteReport wr) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public void removeWasteReport(int id) throws IOException, SQLException, JTillException {
    }

    @Override
    public WasteReport getWasteReport(int id) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public List<WasteReport> getAllWasteReports() throws IOException, SQLException {
        return null;
    }

    @Override
    public WasteReport updateWasteReport(WasteReport wr) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public WasteItem addWasteItem(WasteReport wr, WasteItem wi) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public void removeWasteItem(int id) throws IOException, SQLException, JTillException {
    }

    @Override
    public WasteItem getWasteItem(int id) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public List<WasteItem> getAllWasteItems() throws IOException, SQLException {
        return null;
    }

    @Override
    public WasteItem updateWasteItem(WasteItem wi) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public WasteReason addWasteReason(WasteReason wr) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public void removeWasteReason(int id) throws IOException, SQLException, JTillException {
    }

    @Override
    public WasteReason getWasteReason(int id) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public List<WasteReason> getAllWasteReasons() throws IOException, SQLException {
        return null;
    }

    @Override
    public WasteReason updateWasteReason(WasteReason wr) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public Supplier addSupplier(Supplier s) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public void removeSupplier(int id) throws IOException, SQLException, JTillException {
    }

    @Override
    public Supplier getSupplier(int id) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public List<Supplier> getAllSuppliers() throws IOException, SQLException {
        return null;
    }

    @Override
    public Supplier updateSupplier(Supplier s) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public Department addDepartment(Department d) throws IOException, SQLException {
        return null;
    }

    @Override
    public void removeDepartment(int id) throws IOException, SQLException, JTillException {
    }

    @Override
    public Department getDepartment(int id) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public List<Department> getAllDepartments() throws IOException, SQLException {
        return null;
    }

    @Override
    public Department updateDepartment(Department d) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public SaleItem addSaleItem(Sale s, SaleItem i) throws IOException, SQLException {
        return null;
    }

    @Override
    public void removeSaleItem(int id) throws IOException, SQLException, JTillException {
    }

    @Override
    public SaleItem getSaleItem(int id) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public List<SaleItem> getAllSaleItems() throws IOException, SQLException {
        return null;
    }

    @Override
    public SaleItem updateSaleItem(SaleItem i) throws IOException, SQLException, JTillException {
        return null;
    }

    @Override
    public List<SaleItem> submitSaleItemQuery(String query) throws IOException, SQLException {
        return null;
    }

}
