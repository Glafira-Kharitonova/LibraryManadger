import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
// Менеджер библиотеки с графическим интерфейсом
public class LibraryManagerGUI {
    static class Book implements Serializable {
        private static final long serialVersionUID = 1L;
        String title;
        String author;
        public Book(String title, String author) {
            this.title = title;
            this.author = author;
        }
        @Override
        public String toString() {
            return title + " - " + author;
        }
    }
    // Текущий список книг в библиотеке
    private static List<Book> books = new ArrayList<>();
    // Файл для сериализации, сохранения данных
    private static final String SERIALIZATION_FILE = "library_data_GUI.ser";

    // Компоненты графического интерфейса
    private static JFrame mainFrame;
    private static JList<String> bookList;
    private static DefaultListModel<String> listModel;

    public static void main(String[] args) {
        // Загрузка данных при новом запуске
        autoLoadSerializedData();
        // Создание графического интерфейса
        createAndShowGUI();
    }

    private static void createAndShowGUI() {
        // Главное окно
        mainFrame = new JFrame("Менеджер библиотеки");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // При нажатии крестика программа завершится
        mainFrame.setSize(800, 600);
        // Панель с кнопками
        JPanel buttonPanel = createButtonPanel();
        // Отображение книг
        JPanel displayPanel = createDisplayPanel();
        // Создаем общую панель для поиска и сортировки
        JPanel searchPanel = createSearchPanel();
        JPanel sortPanel = createSortPanel();
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(sortPanel);
        bottomPanel.add(searchPanel);
        // Добавляем все панели на главное окно
        mainFrame.add(buttonPanel, BorderLayout.NORTH);
        mainFrame.add(displayPanel, BorderLayout.CENTER);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);
        // Показываем окно
        mainFrame.setVisible(true);
        // Обновляем список книг
        refreshBookList();
    }

    private static boolean isValidTitle(String title) {
        String regex = "^[a-zA-Zа-яА-ЯёЁ0-9\\s?!+:;№\"'.,/-]+$";
        return title.matches(regex);
    }

    private static boolean isValidAuthor(String author) {
        String regex = "^[a-zA-Zа-яА-ЯёЁ\\s-.]+$";
        return author.matches(regex);
    }

    private static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        // Создаем кнопки
        JButton addButton = new JButton("Добавить книгу");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton saveButton = new JButton("Сохранить в файл");
        JButton loadButton = new JButton("Загрузить из файла");
        JButton exitButton = new JButton("Выход");
        // Добавляем обработчики событий
        addButton.addActionListener(e -> addBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());
        exitButton.addActionListener(e -> exitApplication());
        // Добавляем кнопки на панель
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(exitButton);
        return panel;
    }

    private static JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Создаем модель для списка книг
        listModel = new DefaultListModel<>();
        bookList = new JList<>(listModel);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Добавляем заголовок над списком
        JLabel listLabel = new JLabel("Список книг:");
        listLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Добавляем прокрутку для списка
        JScrollPane scrollPane = new JScrollPane(bookList);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        panel.add(listLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("Поиск:");
        JTextField searchField = new JTextField(30);
        JButton searchButton = new JButton("Найти");
        // Обработка поиска
        searchButton.addActionListener(e -> performSearch(searchField.getText()));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { performSearch(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { performSearch(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { performSearch(searchField.getText()); }
        });
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);
        return panel;
    }

    private static JPanel createSortPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton sortButton = new JButton("Сортировка в алфавитном порядке по названию");
        // Обработка кнопки сортировки
        sortButton.addActionListener(e -> {books.sort((b1, b2) -> b1.title.compareToIgnoreCase(b2.title));
            refreshBookList();
        });
        panel.add(sortButton);
        return panel;
    }

    private static void refreshBookList() {
        listModel.clear();
        for (Book book : books) listModel.addElement(book.toString());
        mainFrame.setTitle("Менеджер библиотеки - Книг в библиотеке: " + books.size());
    }

    private static void addBook() {
        // Окно ввода данных
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Название книги:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Автор:"));
        inputPanel.add(authorField);

        int result = JOptionPane.showConfirmDialog(mainFrame, inputPanel, "Добавить новую книгу", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Вы ничего не ввели!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isValidTitle(title)) {
                JOptionPane.showMessageDialog(mainFrame, "Название книги содержит недопустимые символы!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isValidAuthor(author)) {
                JOptionPane.showMessageDialog(mainFrame, "Имя автора содержит недопустимые символы!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            books.add(new Book(title, author));
            refreshBookList();
            JOptionPane.showMessageDialog(mainFrame, "Книга успешно добавлена!");
        }
    }

    private static void editBook() {
        int selectedIndex = bookList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Выберите книгу для редактирования!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Book book = books.get(selectedIndex);
        JTextField titleField = new JTextField(book.title, 20);
        JTextField authorField = new JTextField(book.author, 20);
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Название книги:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Автор:"));
        inputPanel.add(authorField);

        int result = JOptionPane.showConfirmDialog(mainFrame, inputPanel, "Редактировать книгу", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newTitle = titleField.getText().trim();
            String newAuthor = authorField.getText().trim();
            if (newTitle.isEmpty() || newAuthor.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Вы ничего не ввели!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isValidTitle(newTitle)) {
                JOptionPane.showMessageDialog(mainFrame, "Название содержит недопустимые символы!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isValidAuthor(newAuthor)) {
                JOptionPane.showMessageDialog(mainFrame, "Имя автора содержит недопустимые символы!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            book.title = newTitle;
            book.author = newAuthor;
            refreshBookList();
            JOptionPane.showMessageDialog(mainFrame, "Изменения внесены!");
        }
    }

    private static void deleteBook() {
        int selectedIndex = bookList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Выберите книгу для удаления!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Book book = books.get(selectedIndex);
        int choice = JOptionPane.showConfirmDialog(mainFrame, "Вы уверены, что хотите удалить книгу:\n" + book.title + " - " + book.author + "?", "Подтверждение удаления", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            books.remove(selectedIndex);
            refreshBookList();
            JOptionPane.showMessageDialog(mainFrame, "Книга удалена!");
        }
    }

    private static void performSearch(String searchText) {
        if (searchText.isEmpty()) {
            refreshBookList();
            return;
        }
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.title.toLowerCase().contains(searchText.toLowerCase()) ||
                    book.author.toLowerCase().contains(searchText.toLowerCase())) {
                results.add(book);
            }
        }
        // Обновляем список с подходящими результатами
        listModel.clear();
        for (Book book : results) {
            listModel.addElement(book.toString());
        }
    }

    private static void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить библиотеку");
        if (fileChooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Book book : books) writer.println(book.title + ", " + book.author);
                JOptionPane.showMessageDialog(mainFrame, "Данные успешно сохранены в файл.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame, "Ошибка при сохранении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Загрузить библиотеку");
        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            List<Book> loadedBooks = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) loadedBooks.add(new Book(parts[0].trim(), parts[1].trim()));
                }
                books.addAll(loadedBooks);
                refreshBookList();
                JOptionPane.showMessageDialog(mainFrame, "Загружено книг: " + loadedBooks.size());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame, "Ошибка при загрузке: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private static void exitApplication() {
        if (!books.isEmpty()) {
            int result = JOptionPane.showConfirmDialog(mainFrame, "Сохранить данные перед выходом?", "Выход", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) saveSerializedData(); // Сохранение данных с помощью сериализации
            else if (result == JOptionPane.CANCEL_OPTION) return;
        }
        System.exit(0);
    }

    private static void saveSerializedData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE))) {
            oos.writeObject(books);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Ошибка при сохранении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void autoLoadSerializedData() {
        File file = new File(SERIALIZATION_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE))) {
            @SuppressWarnings("unchecked") // аннотация для того, чтобы предупреждения не выводились
            List<Book> loadedBooks = (List<Book>) ois.readObject();
            books = loadedBooks;
        } catch (IOException | ClassNotFoundException e) {
        }
    }
}