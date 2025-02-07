// Base class
class Book {
    String title;
    String author;
    double price;

    public Book(String title, String author, double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public void displayDetails() {
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Price: Rs" + price);
    }
}

// Derived class
class EBook extends Book {
    double fileSize; // in MB
    String format;

    public EBook(String title, String author, double price, double fileSize, String format) {
        super(title, author, price);
        this.fileSize = fileSize;
        this.format = format;
    }

    public void displayEbookDetails() {
        displayDetails();
        System.out.println("File Size: " + fileSize + " MB");
        System.out.println("Format: " + format);
    }
}

// Main class for testing
public class SingleInheritance {
    public static void main(String[] args) {
        EBook ebook = new EBook("Java Programming", "James Gosling", 29.99, 15.5, "PDF");
        ebook.displayEbookDetails();
    }
}
