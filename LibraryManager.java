import java.util.*;
import java.io.*;

public class LibraryManager {
    static class Book implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        String title;
        String author;
        // constructor creates a new Book object with given parameters
        public Book(String title, String author) {
            this.title = title;
            this.author = author;
        }

        @Override // Annotation indicating override of parent class method
        public String toString() {
            return "Название: " + title + ", Автор: " + author;
        }
    }
    // Current list of books in the library
    private static List<Book> books = new ArrayList<>();
    // For reading user input from console
    private static final Scanner scanner = new Scanner(System.in);
    // File for serialization, data storage
    private static final String SERIALIZATION_FILE = "library_data.ser";

    public static void main(String[] args) {
        System.out.println("== Менеджер библиотеки ==");
        // Load data on new startup
        autoLoadSerializedData();
        // Menu of available options for the user
        while (true) {
            System.out.println("---- Меню ----");
            System.out.println("1. Добавить книгу");
            System.out.println("2. Редактировать книгу");
            System.out.println("3. Удалить книгу");
            System.out.println("4. Вывести список книг");
            System.out.println("5. Найти книгу");
            System.out.println("6. Сохранить в файл");
            System.out.println("7. Загрузить из файла");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");
            // Reading user choice and further processing
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": addBook(); break;
                case "2": editBook(); break;
                case "3": deleteBook(); break;
                case "4": displayBooks(); break;
                case "5": searchBooks(); break;
                case "6": saveToFile(); break;
                case "7": loadFromFile(); break;
                case "0":
                    askToSaveBeforeExit(); // Ask about saving data before exit
                    return;
                default: System.out.println("Не понимаю Вас. Попробуйте снова. Нужно ввести лишь одну цифру.");
            }
        }
    }

    private static boolean isValidTitle(String title) {
        String regex = "^[a-zA-Zа-яА-ЯёЁ0-9\\s?!+:;№\"'.,/-]+$";
        return title.matches(regex);
    }
    private static boolean isValidAuthor(String author) {
        String regex = "^[a-zA-Zа-яА-ЯёЁ\\s-.]+$";
        return author.matches(regex);
    }

    // Method for adding a new book
    private static void addBook() {
        System.out.println("\nДобавление новой книги:");
        String title;
        while (true) {
            System.out.print("Введите название: ");
            title = scanner.nextLine().trim();
            if (title.isEmpty()) {
                System.out.println("Ошибка: вы ничего не ввели!");
                continue;
            }
            if (!isValidTitle(title)) {
                System.out.println("Ошибка: название книги может содержать только русские и латинские буквы, цифры и специальные символы.");
                continue;
            }
            break;
        }

        String author;
        while (true) {
            System.out.print("Введите автора: ");
            author = scanner.nextLine().trim();
            if (author.isEmpty()) {
                System.out.println("Ошибка: вы ничего не ввели!");
                continue;
            }
            if (!isValidAuthor(author)) {
                System.out.println("Ошибка: имя автора может содержать только русские и латинские буквы и символ: -");
                continue;
            }
            break;
        }
        Book newBook = new Book(title, author);
        books.add(newBook);
        System.out.println("Книга успешно добавлена!");
    }

    // Method for editing an existing book
    private static void editBook() {
        if (books.isEmpty()) {
            System.out.println("Список книг пуст!");
            return;
        }
        System.out.println("\n--- Редактирование книги ---");
        displayBooks();
        int index;
        while (true) {
            System.out.print("Введите номер книги для редактирования (1-" + books.size() + "): ");
            try {
                index = Integer.parseInt(scanner.nextLine()) - 1;
                // Check validity of entered index
                if (index >= 0 && index < books.size()) break;
                else System.out.println("Ошибка: введите номер от 1 до " + books.size() + "!");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректный номер!");
            }
        }
        Book book = books.get(index); // Get book by index
        System.out.println("\nЧто вы хотите отредактировать?");
        System.out.println("1. Только название");
        System.out.println("2. Только автора");
        System.out.println("3. Название и автора");
        System.out.print("Выберите вариант (1-3): ");

        String choice;
        while (true) {
            choice = scanner.nextLine();
            if (choice.matches("[1-3]")) break;
            else {
                System.out.println("Ошибка: введите цифру от 1 до 3!");
                System.out.print("Выберите вариант (1-3): ");
            }
        }
        // Edit depending on choice
        switch (choice) {
            case "1": editTitle(book); break; // Title
            case "2": editAuthor(book); break; // Author
            case "3": // Title and author
                editTitle(book);
                editAuthor(book);
                break;
        }
        System.out.println("Книга отредактирована!");
    }

    // Method for editing title
    private static void editTitle(Book book) {
        while (true) {
            System.out.print("Введите новое название [" + book.title + "]: ");
            String newTitle = scanner.nextLine().trim();
            if (newTitle.isEmpty()) {
                System.out.println("Название не изменено.");
                break;
            }
            if (!isValidTitle(newTitle)) {
                System.out.println("Ошибка: название может содержать только русские и латинские буквы, цифры, пробелы и специальные символы.");
                continue;
            }
            book.title = newTitle;
            break;
        }
    }

    // Method for editing author
    private static void editAuthor(Book book) {
        while (true) {
            System.out.print("Введите нового автора [" + book.author + "]: ");
            String newAuthor = scanner.nextLine().trim();
            if (newAuthor.isEmpty()) {
                System.out.println("Автор не изменен.");
                break;
            }
            if (!isValidAuthor(newAuthor)) {
                System.out.println("Ошибка: автор может содержать только русские и латинские буквы, пробелы и символы: -.");
                continue;
            }
            book.author = newAuthor;
            break;
        }
    }

    private static void deleteBook() {
        if (books.isEmpty()) {
            System.out.println("Список книг пуст!");
            return;
        }
        System.out.println("\n--- Удаление книги ---");
        displayBooks();
        int index;
        while (true) {
            System.out.print("Введите номер книги для удаления (1-" + books.size() + "): ");
            try {
                index = Integer.parseInt(scanner.nextLine()) - 1;
                if (index >= 0 && index < books.size()) break;
                else System.out.println("Ошибка: введите номер от 1 до " + books.size() + "!");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректный номер!");
            }
        }
        Book bookToDelete = books.get(index);
        while (true) {
            System.out.println("\nВы выбрали для удаления:");
            System.out.println("Название: " + bookToDelete.title);
            System.out.println("Автор: " + bookToDelete.author);
            System.out.print("Вы уверены, что хотите удалить эту книгу? (да/нет): ");

            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("да") || choice.equals("д")) {
                books.remove(index);
                System.out.println("Книга удалена!");
                break;
            } else if (choice.equals("нет") || choice.equals("н")) {
                System.out.println("Удаление отменено.");
                break;
            } else System.out.println("Ошибка: введите 'да' или 'нет'!");
        }
    }

    // Method for displaying all books
    private static void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("Список книг пуст!");
            return;
        }
        System.out.println("\n--- Список книг ---");
        for (int i = 0; i < books.size(); i++) System.out.println((i + 1) + ". " + books.get(i));
    }

    // Method for searching books by various attributes
    private static void searchBooks() {
        if (books.isEmpty()) {
            System.out.println("Список книг пуст!");
            return;
        }
        while (true) {
            System.out.println("\n--- Поиск книг ---");
            String choice;
            while (true) {
                System.out.println("1. По названию");
                System.out.println("2. По автору");
                System.out.print("Выберите критерий поиска (1 или 2): ");
                choice = scanner.nextLine().trim();
                if (choice.equals("1") || choice.equals("2")) break;
                else System.out.println("Ошибка: введите 1 или 2!");
            }
            // Check search input
            String searchValue;
            while (true) {
                System.out.print("Введите значение для поиска: ");
                searchValue = scanner.nextLine().trim();
                if (searchValue.isEmpty()) {
                    System.out.println("Ошибка: вы ничего не ввели!");
                    continue;
                }
                // Check input validity
                if (choice.equals("1")) {
                    if (!isValidTitle(searchValue)) {
                        System.out.println("Ошибка: поисковый запрос может содержать только русские и латинские буквы, цифры, пробелы и специальные символы");
                        continue;
                    }
                } else {
                    if (!isValidAuthor(searchValue)) {
                        System.out.println("Ошибка: поисковый запрос может содержать только русские и латинские буквы, пробелы и символы: -.");
                        continue;
                    }
                }
                break;
            }
            List<Book> results = new ArrayList<>(); // Found books
            // Search
            switch (choice) {
                case "1": // Search by title
                    for (Book book : books) {
                        if (book.title.toLowerCase().contains(searchValue.toLowerCase())) {
                            results.add(book);
                        }
                    }
                    break;
                case "2": // Search by author
                    for (Book book : books) {
                        if (book.author.toLowerCase().contains(searchValue.toLowerCase())) {
                            results.add(book);
                        }
                    }
                    break;
            }
            // Display search results
            if (results.isEmpty()) {
                System.out.println("\nКниги не найдены!");
                // Offer to retry
                System.out.println("\nЧто вы хотите сделать?");
                System.out.println("1. Попробовать другой поисковый запрос");
                System.out.println("2. Выбрать другой критерий поиска (название/автор)");
                System.out.println("3. Вернуться в главное меню");
                System.out.print("Выберите действие (1-3): ");
                String retryChoice;
                while (true) {
                    retryChoice = scanner.nextLine().trim();
                    if (retryChoice.equals("1") || retryChoice.equals("2") || retryChoice.equals("3")) break;
                    else {
                        System.out.println("Ошибка: введите 1, 2 или 3!");
                        System.out.print("Выберите действие (1-3): ");
                    }
                }
                switch (retryChoice) {
                    case "1": continue; // New search query with same criteria
                    case "2": break; // Start over with criteria selection
                    case "3": return; // Exit method
                }
            } else {
                System.out.println("\nНайдено книг: " + results.size());
                System.out.println("--- Результаты поиска ---");
                for (int i = 0; i < results.size(); i++) System.out.println((i + 1) + ". " + results.get(i));
            }
        }
    }

    // Method for saving books to file
    private static void saveToFile() {
        System.out.print("Введите имя файла для сохранения: ");
        String filename = scanner.nextLine();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write each book to file
            for (Book book : books) writer.println(book.title + ", " + book.author);
            System.out.println("Данные успешно сохранены в файл.");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    // Method for loading books from file
    private static void loadFromFile() {
        System.out.print("Введите имя файла для загрузки: ");
        String filename = scanner.nextLine();
        // Check file existence
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Файл не существует!");
            return;
        }
        List<Book> loadedBooks = new ArrayList<>(); // Temporary list for loaded books
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    Book book = new Book(parts[0].trim(), parts[1].trim());
                    loadedBooks.add(book);
                }
            }
            books.addAll(loadedBooks);
            System.out.println("Данные успешно загружены из файла: " + filename);
            System.out.println("Загружено книг: " + loadedBooks.size());
            System.out.println("Всего книг в библиотеке: " + books.size());
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    // Method for asking about saving before exit
    private static void askToSaveBeforeExit() {
        if (books.isEmpty()) {
            System.out.println("До свидания!");
            return;
        }
        while (true) {
            System.out.print("Хотите сохранить данные перед выходом? (да/нет): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("да") || answer.equals("д")) {
                saveSerializedData();
                System.out.println("До свидания!");
                break;
            } else if (answer.equals("нет") || answer.equals("н")) {
                File file = new File(SERIALIZATION_FILE);
                if (file.exists()) file.delete();
                System.out.println("Данные не сохранены. До свидания!");
                break;
            } else System.out.println("Пожалуйста, введите 'да' или 'нет'");
        }
    }

    // Method for data serialization (saving to binary file)
    private static void saveSerializedData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE))) {
            oos.writeObject(books);
            System.out.println("Данные успешно сохранены.");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    // Automatic data loading on startup
    private static void autoLoadSerializedData() {
        File file = new File(SERIALIZATION_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE))) {
            @SuppressWarnings("unchecked") // annotation to suppress warnings
            List<Book> loadedBooks = (List<Book>) ois.readObject();
            books = loadedBooks;
            System.out.println("Автоматически загружено книг: " + books.size());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Не удалось загрузить сохраненные данные: " + e.getMessage());
        }
    }
}
