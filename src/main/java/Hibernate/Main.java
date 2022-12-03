package Hibernate;

import Hibernate.Model.Category;
import Hibernate.Model.HibernateUtil;
import Hibernate.Model.Product;
import Hibernate.Model.Sales;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Wybierz: dodaj / lista / usun / aktualizuj / wyjdz");
            String text = scanner.nextLine();
            if (text.equalsIgnoreCase("dodaj")) {
                adding(scanner);
            } else if (text.equalsIgnoreCase("lista")) {
                readList(scanner);
            } else if (text.equalsIgnoreCase("usun")) {
                deleting(scanner);
            } else if (text.equalsIgnoreCase("aktualizuj")) {
                updating(scanner);
            } else if (text.equalsIgnoreCase("wyjdz")) {
                System.out.println("Opuszcenie aplikacji");
                break;
            }
        } while (true);
    }
    private static Product productParameter(Scanner scanner) {
        System.out.println("Podaj nazwę:");
        String name = scanner.nextLine();
        System.out.println("Podaj kategorie productu");
        String productCategory = scanner.nextLine().toUpperCase();
        Category category = Category.valueOf(productCategory);
        return Product.builder()
                .name(name)
                .category(category)
                .build();
    }

    private static Sales salesParameter(Product product,Scanner scanner) {
        System.out.println("Podaj cene:");
        String priceString = scanner.nextLine();
        Double price = Double.parseDouble(priceString);
        System.out.println("Podaj ilosc");
        String quantityString = scanner.nextLine();
        Double quantity = Double.parseDouble(quantityString);
        return Sales.builder()
                .product(product)
                .price(price)
                .quantity(quantity)
                .build();
    }

    private static void adding(Scanner scanner){
        System.out.println("Dodac: produkt or sprzedaz?");
        String text = scanner.nextLine();
        if (text.equalsIgnoreCase("produkt")) {
            addProduct(scanner);
        } else if (text.equalsIgnoreCase("sprzedaz")) {
            addSales(scanner);
        } else {
            System.err.println("Nieznana komenda.");
        }
    }
    private static void addProduct(Scanner scanner) {
        Product p = productParameter(scanner);

        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            session.persist(p);

            transaction.commit();
        } catch (Exception e) {
            System.err.println("Błąd dodawania do bazy danych");
        }
    }



    private static void addSales(Scanner scanner) {


        System.out.println("Podaj id produktu");
        String id = scanner.nextLine();
        Long productId = Long.parseLong(id);
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Product productCheck = session.get(Product.class, productId);
            if (productCheck != null) {
                Transaction transaction = session.beginTransaction();
                Sales s = salesParameter(productCheck,scanner);
                session.persist(s);
                transaction.commit();
            } else {
                System.err.println("Nie ma takiego produktu");
            }
        }
    }

    private static void readList(Scanner scanner){
        System.out.println("Wybierz: lista produktow(1), lista sprzedazy(2), lista sprzedazy konkretnego produktu(3)");
        String text = scanner.nextLine();
        if (text.equalsIgnoreCase("1")) {
            allProductList();
        } else if (text.equalsIgnoreCase("2")) {
            allSalesList();
        } else if (text.equalsIgnoreCase("3")) {
            readIdSale(scanner);
        } else {
            System.err.println("Nieznana komenda.");
        }
    }
    private static void allProductList() {
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            TypedQuery<Product> zapytanie = session.createQuery("FROM Product", Product.class);
            List<Product> lista = zapytanie.getResultList();
            for (Product product : lista) {
                System.out.println(product);
            }
        }
    }

    private static void allSalesList() {
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            TypedQuery<Sales> zapytanie = session.createQuery("FROM Sales",Sales.class);
            List<Sales> lista = zapytanie.getResultList();
            for (Sales sales : lista) {
                System.out.println(sales);
            }
        }
    }
    private static void readIdSale(Scanner scanner){
        System.out.println("Podaj id produktu");
        String id = scanner.nextLine();
        Long productId = Long.parseLong(id);
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            TypedQuery<Sales> zapytanie = session.createQuery("FROM Sales s join Product p on p.id = s.product_id WHERE s.product_id=:i",Sales.class);
            zapytanie.setParameter("i", productId);
            List<Sales> lista = zapytanie.getResultList();
            for (Sales sales : lista) {
                System.out.println(sales);
            }
        }
    }
    private static void deleting(Scanner scanner){
        System.out.println("Usunac: produkt or sprzedaz?");
        String text = scanner.nextLine();
        if (text.equalsIgnoreCase("sprzedaz")) {
            deleteSales(scanner);
        } else if (text.equalsIgnoreCase("produkt")) {
            deleteProduct(scanner);
        } else if (text.equalsIgnoreCase("produkt2")) {
            deleteProduct2(scanner);
        } else {
            System.err.println("Nieznana komenda.");
        }
    }

    private static void deleteSales(Scanner scanner){
        System.out.println("Podaj id sprzedazy");
        String id = scanner.nextLine();
        Long productId = Long.parseLong(id);
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Sales sales = session.get(Sales.class, productId);
            if (sales != null) {
                session.remove(sales);
                transaction.commit();
            } else {
                System.err.println("Nie ma takiej sprzedazy");
            }
        }
    }
    private static void deleteProduct(Scanner scanner) {
        System.out.println("Podaj id produktu");
        String id = scanner.nextLine();
        Long productId = Long.parseLong(id);
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Product product = session.get(Product.class, productId);
            if (product != null){
                if(!product.getSales().isEmpty()){
                    for (Sales sales: product.getSales()) {
                        session.remove(sales);
                    }
                } session.remove(product);
                transaction.commit();
            } else {
                System.err.println("Nie ma takiego produktu");
            }
        }
    }
    private static void deleteProduct2(Scanner scanner){
        System.out.println("Podaj id produktu");
        String id = scanner.nextLine();
        Long productId = Long.parseLong(id);
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Product product = session.get(Product.class, productId);
            if (product != null){
                if(!product.getSales().isEmpty()){

                    Sales sales = session.get(Sales.class,productId);
                    sales.setProduct(null);
                    session.merge(sales);
                    /* TypedQuery<Sales> query = session.createQuery("UPDATE Sales s SET s.product_id = :productId" +
                                " where s.product_id = :productId2");
                        query.setParameter("productId", "");
                        query.setParameter("productId2", productId);
                        query.executeUpdate();*/

                } session.remove(product);
                transaction.commit();
            } else {
                System.err.println("Nie ma takiego produktu");
            }
        }
    }
    private static void updating(Scanner scanner){
        System.out.println("Zmodyfikowac: produkt or sprzedaz?");
        String text = scanner.nextLine();
        if (text.equalsIgnoreCase("produkt")) {
            productUpdate(scanner);
        } else if (text.equalsIgnoreCase("sprzedaz")) {
            salesUpdate(scanner);
        } else {
            System.err.println("Nieznana komenda.");
        }
    }
    private static void productUpdate(Scanner scanner){
        System.out.println("Podaj id produktu");
        String id = scanner.nextLine();
        Long productId = Long.parseLong(id);
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Product productCheck = session.get(Product.class, productId);
            if (productCheck !=null){
                Product productUpdate = productParameter(scanner);
                productUpdate.setId(productId);
                session.merge(productUpdate);
            } else {
                System.err.println("Nie ma takiego produktu");
            }
            transaction.commit();
        }
    }
    private static void salesUpdate(Scanner scanner){
        System.out.println("Podaj id sprzedazy");
        String id = scanner.nextLine();
        Long salesId = Long.parseLong(id);
        try (Session session = HibernateUtil.INSTANCE.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Sales salesCheck = session.get(Sales.class, salesId);
            if (salesCheck !=null){
                System.out.println("Podaj id produktu");
                String ids = scanner.nextLine();
                Long productId = Long.parseLong(ids);
                Product productCheck = session.get(Product.class, productId);
                if (productCheck !=null) {
                    Sales salesUpdate = salesParameter(productCheck, scanner);
                    salesUpdate.setAddTime(LocalDateTime.now());
                    salesUpdate.setId(salesId);
                    session.merge(salesUpdate);
                } else {
                    System.err.println("Nie ma takiego produktu");
                }
            } else {
                System.err.println("Nie ma takiej sprzedazy");
            }
            transaction.commit();
        }
    }
}
