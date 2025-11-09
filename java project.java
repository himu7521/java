import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class LibraryManagementSystem extends JFrame {

    // Book and issue lists (replacing database)
    java.util.List<Book> books = new ArrayList<>();
    java.util.List<IssuedBook> issuedBooks = new ArrayList<>();

    JTextField titleField, authorField, yearField, searchField, deleteField;
    JTable bookTable, issueTable;
    DefaultTableModel bookModel, issueModel;

    Color primaryColor = new Color(33, 150, 243);
    Color secondaryColor = new Color(76, 175, 80);
    Color backgroundColor = new Color(255, 255, 255);
    Color errorColor = new Color(244, 67, 54);

    int nextBookId = 1;
    int nextIssueId = 1;

    public LibraryManagementSystem() {
        showLibrarianUI(); // or showStudentUI() if needed
    }

    void showStudentUI() {
        setTitle("📚 Library Management System (Student)");
        setSize(850, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(backgroundColor);

        bookModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Year"}, 0);
        bookTable = new JTable(bookModel);
        JScrollPane bookTableScroll = new JScrollPane(bookTable);
        bookTableScroll.setBorder(BorderFactory.createTitledBorder("📖 Book List"));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(backgroundColor);
        controlPanel.setBorder(BorderFactory.createTitledBorder("🔍 Search"));

        searchField = createTextField(10);
        JButton searchButton = createButton("Search", primaryColor);
        JButton refreshButton = createButton("🔄 Refresh", Color.DARK_GRAY);

        searchButton.addActionListener(e -> searchBooks());
        refreshButton.addActionListener(e -> loadBooks());

        controlPanel.add(new JLabel("Search Title:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(refreshButton);

        mainPanel.add(bookTableScroll, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
        loadBooks();
    }

    void showLibrarianUI() {
        setTitle("📚 Library Management System (Librarian)");
        setSize(850, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(backgroundColor);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createTitledBorder("➕ Add New Book"));

        titleField = createTextField();
        authorField = createTextField();
        yearField = createTextField();
        JButton addButton = createButton("Add Book", secondaryColor);
        addButton.addActionListener(e -> addBook());

        formPanel.add(new JLabel("Title:")); formPanel.add(titleField);
        formPanel.add(new JLabel("Author:")); formPanel.add(authorField);
        formPanel.add(new JLabel("Year:")); formPanel.add(yearField);
        formPanel.add(new JLabel()); formPanel.add(addButton);

        bookModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Year"}, 0);
        bookTable = new JTable(bookModel);
        JScrollPane bookTableScroll = new JScrollPane(bookTable);
        bookTableScroll.setBorder(BorderFactory.createTitledBorder("📖 Book List"));

        issueModel = new DefaultTableModel(new String[]{"ID", "Student Name", "Issue Date", "Book Title"}, 0);
        issueTable = new JTable(issueModel);
        JScrollPane issueTableScroll = new JScrollPane(issueTable);
        issueTableScroll.setBorder(BorderFactory.createTitledBorder("📦 Issued Books"));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(backgroundColor);
        controlPanel.setBorder(BorderFactory.createTitledBorder("🔍 Search & ❌ Delete"));

        searchField = createTextField(10);
        deleteField = createTextField(5);
        JButton searchButton = createButton("Search", primaryColor);
        JButton deleteButton = createButton("Delete by ID", errorColor);
        JButton refreshButton = createButton("🔄 Refresh", Color.DARK_GRAY);

        searchButton.addActionListener(e -> searchBooks());
        deleteButton.addActionListener(e -> deleteBook());
        refreshButton.addActionListener(e -> loadBooks());

        controlPanel.add(new JLabel("Search Title:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(new JLabel("Delete ID:"));
        controlPanel.add(deleteField);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);

        JPanel issuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        issuePanel.setBackground(backgroundColor);
        issuePanel.setBorder(BorderFactory.createTitledBorder("📦 Issue Book"));

        JTextField issueIdField = createTextField(5);
        JTextField studentNameField = createTextField(10);
        JButton issueButton = createButton("📥 Issue", secondaryColor);

        issueButton.addActionListener(e -> issueBook(issueIdField.getText().trim(), studentNameField.getText().trim()));

        issuePanel.add(new JLabel("Book ID:"));
        issuePanel.add(issueIdField);
        issuePanel.add(new JLabel("Student Name:"));
        issuePanel.add(studentNameField);
        issuePanel.add(issueButton);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.add(controlPanel);
        bottomPanel.add(issuePanel);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(bookTableScroll, BorderLayout.CENTER);
        mainPanel.add(issueTableScroll, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
        loadBooks();
    }

    void loadBooks() {
        bookModel.setRowCount(0);
        issueModel.setRowCount(0);

        for (Book b : books) {
            bookModel.addRow(new Object[]{b.id, b.title, b.author, b.year});
        }

        for (IssuedBook ib : issuedBooks) {
            issueModel.addRow(new Object[]{ib.id, ib.studentName, ib.issueDate, ib.bookTitle});
        }
    }

    void addBook() {
        if (titleField.getText().isEmpty() || authorField.getText().isEmpty() || yearField.getText().isEmpty()) {
            showError(this, "Please fill in all fields.");
            return;
        }
        try {
            Book b = new Book(nextBookId++, titleField.getText(), authorField.getText(),
                    Integer.parseInt(yearField.getText()));
            books.add(b);
            JOptionPane.showMessageDialog(this, "✅ Book added!");
            titleField.setText(""); authorField.setText(""); yearField.setText("");
            loadBooks();
        } catch (NumberFormatException e) {
            showError(this, "⚠ Year must be a number.");
        }
    }

    void deleteBook() {
        try {
            int id = Integer.parseInt(deleteField.getText());
            boolean removed = books.removeIf(b -> b.id == id);
            if (removed) {
                JOptionPane.showMessageDialog(this, "✅ Book deleted.");
            } else {
                showError(this, "⚠ No book found with that ID.");
            }
            loadBooks();
        } catch (Exception e) {
            showError(this, "⚠ Invalid ID.");
        }
    }

    void searchBooks() {
        bookModel.setRowCount(0);
        String keyword = searchField.getText().toLowerCase();
        for (Book b : books) {
            if (b.title.toLowerCase().contains(keyword)) {
                bookModel.addRow(new Object[]{b.id, b.title, b.author, b.year});
            }
        }
    }

    void issueBook(String bookId, String studentName) {
        if (bookId.isEmpty() || studentName.isEmpty()) {
            showError(this, "⚠ Please fill in all issue fields.");
            return;
        }
        try {
            int id = Integer.parseInt(bookId);
            Book book = books.stream().filter(b -> b.id == id).findFirst().orElse(null);
            if (book == null) {
                showError(this, "⚠ Book not found.");
                return;
            }
            IssuedBook ib = new IssuedBook(nextIssueId++, studentName, new java.util.Date(), book.title);
            issuedBooks.add(ib);
            JOptionPane.showMessageDialog(this, "✅ Book issued to " + studentName);
            loadBooks();
        } catch (NumberFormatException e) {
            showError(this, "⚠ Invalid Book ID.");
        }
    }

    void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    JTextField createTextField() {
        return createTextField(15);
    }

    JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tf.setBorder(new LineBorder(Color.GRAY, 1, true));
        return tf;
    }

    JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystem::new);
    }

    // Inner classes for book data
    static class Book {
        int id, year;
        String title, author;
        Book(int id, String title, String author, int year) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.year = year;
        }
    }

    static class IssuedBook {
        int id;
        String studentName, bookTitle;
        Date issueDate;
        IssuedBook(int id, String studentName, Date issueDate, String bookTitle) {
            this.id = id;
            this.studentName = studentName;
            this.issueDate = issueDate;
            this.bookTitle = bookTitle;
        }
    }
}
