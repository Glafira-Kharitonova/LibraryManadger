import java.util.*;
import java.io.*;

// Менеджер библиотеки
public class LibraryManager {
    static class Book implements Serializable {
        private static final long serialVersionUID = 1L;
        String title;
        String author;
        // Конструктор класса - создает новый объект Book с заданными параметрами
        public Book(String title, String author) {
            this.title = title;
            this.author = author;
        }

        @Override // Аннотация, указывающая на переопределение метода родительского класса
        public String toString() {
            return "Название: " + title + ", Автор: " + author;
        }
    }
    // Текущий список книг в библиотеке
    private static List<Book> books = new ArrayList<>();
    // Для чтения ввода пользователя с консоли
    private static Scanner scanner = new Scanner(System.in);
    // Файл для сериализации, сохранения данных
    private static final String SERIALIZATION_FILE = "library_data.ser";

    public static void main(String[] args) {
        System.out.println("=== Менеджер библиотеки ===");

        // Загрузка данных при новом запуске
        autoLoadSerializedData();

        // Меню доступных опций для пользователя
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

            // Чтение выбора пользователя и дальнейшая обработка
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
                    askToSaveBeforeExit(); // Спрашиваем о сохранении данных перед выходом
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

    // Метод для добавления новой книги
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

        // Создание и добавление книги
        Book newBook = new Book(title, author);
        books.add(newBook);
        System.out.println("Книга успешно добавлена!");
    }

    // Метод для редактирования существующей книги
    private static void editBook() {
        if (books.isEmpty()) {
            System.out.println("Список книг пуст!");
            return;
        }

        System.out.println("\n--- Редактирование книги ---");
        displayBooks(); // Показ списка книг для выбора

        int index;
        while (true) {
            System.out.print("Введите номер книги для редактирования (1-" + books.size() + "): ");
            try {
                index = Integer.parseInt(scanner.nextLine()) - 1; // -1 т.к. индексы с 0

                // Проверка корректности введенного индекса
                if (index >= 0 && index < books.size()) {
                    break; // Корректный номер, выходим из цикла
                } else {
                    System.out.println("Ошибка: введите номер от 1 до " + books.size() + "!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректный номер!");
            }
        }
        Book book = books.get(index); // Получение книги по индексу

        // Выбор что редактировать
        System.out.println("\nЧто вы хотите отредактировать?");
        System.out.println("1. Только название");
        System.out.println("2. Только автора");
        System.out.println("3. Название и автора");
        System.out.print("Выберите вариант (1-3): ");

        String choice;
        while (true) {
            choice = scanner.nextLine();
            if (choice.matches("[1-3]")) { // Проверяем что введена цифра от 1 до 3
                break;
            } else {
                System.out.println("Ошибка: введите цифру от 1 до 3!");
                System.out.print("Выберите вариант (1-3): ");
            }
        }
        // Редактирование в зависимости от выбора
        switch (choice) {
            case "1": // Только название
                editTitle(book);
                break;
            case "2": // Только автора
                editAuthor(book);
                break;
            case "3": // Название и автора
                editTitle(book);
                editAuthor(book);
                break;
        }
        System.out.println("Книга успешно отредактирована!");
    }
    // Метод для редактирования названия
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

    // Метод для редактирования автора
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
            System.out.println("Список книг пуст! Нечего удалять.");
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
                // Удаляем книгу из списка
                books.remove(index);
                System.out.println("✓ Книга успешно удалена!");
                break;
            } else if (choice.equals("нет") || choice.equals("н")) {
                System.out.println("Удаление отменено.");
                break;
            } else {
                System.out.println("Ошибка: введите 'да' или 'нет'!");
            }
        }
    }

    // Метод для отображения всех книг
    private static void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("Список книг пуст!");
            return;
        }
        System.out.println("\n--- Список книг ---");
        // Цикл по всем книгам с нумерацией
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i));
        }
    }

    // Метод для поиска книг по различным атрибутам
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
            // Проверка ввода поискового запроса
            String searchValue;
            while (true) {
                System.out.print("Введите значение для поиска: ");
                searchValue = scanner.nextLine().trim();
                if (searchValue.isEmpty()) {
                    System.out.println("Ошибка: вы ничего не ввели!");
                    continue;
                }
                // Проверка корректности ввода в зависимости от выбранного критерия
                if (choice.equals("1")) {
                    if (!isValidTitle(searchValue)) {
                        System.out.println("Ошибка: поисковый запрос может содержать только русские и латинские буквы, цифры, пробелы и специальные символы");
                        continue;
                    }
                } else if (choice.equals("2")) {
                    if (!isValidAuthor(searchValue)) {
                        System.out.println("Ошибка: поисковый запрос может содержать только русские и латинские буквы, пробелы и символы: -.");
                        continue;
                    }
                }
                break;
            }
            List<Book> results = new ArrayList<>(); // Список для найденных книг
            // Поиск в зависимости от выбранного критерия
            switch (choice) {
                case "1": // Поиск по названию
                    for (Book book : books) {
                        if (book.title.toLowerCase().contains(searchValue.toLowerCase())) {
                            results.add(book);
                        }
                    }
                    break;
                case "2": // Поиск по автору
                    for (Book book : books) {
                        if (book.author.toLowerCase().contains(searchValue.toLowerCase())) {
                            results.add(book);
                        }
                    }
                    break;
            }
            // Вывод результатов поиска
            if (results.isEmpty()) {
                System.out.println("\nКниги не найдены!");

                // Предложение повторить поиск
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
                    case "1": continue; // Продолжаем цикл - новый поисковый запрос с тем же критерием
                    case "2": break; // Продолжаем цикл - начнем заново с выбора критерия
                    case "3": return; // Выходим из метода - возврат в главное меню
                }
            } else {
                System.out.println("\nНайдено книг: " + results.size());
                System.out.println("--- Результаты поиска ---");
                for (int i = 0; i < results.size(); i++) {
                    System.out.println((i + 1) + ". " + results.get(i));
                }
            }
        }
    }

    // Метод для сохранения книг в файл
    private static void saveToFile() {
        System.out.print("Введите имя файла для сохранения: ");
        String filename = scanner.nextLine();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Запись каждой книги в файл в формате CSV
            for (Book book : books) {
                writer.println(book.title + ", " + book.author);
            }
            System.out.println("Данные успешно сохранены в файл: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    // Метод для загрузки книг из файла
    private static void loadFromFile() {
        System.out.print("Введите имя файла для загрузки: ");
        String filename = scanner.nextLine();
        // Проверка существования файла
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Файл не существует!");
            return;
        }

        List<Book> loadedBooks = new ArrayList<>(); // Временный список для загруженных книг
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
    // Метод для запроса сохранения перед выходом
    private static void askToSaveBeforeExit() {
        if (books.isEmpty()) {
            System.out.println("До свидания!");
            return;
        }
        System.out.println("\nУ вас есть несохраненные данные!");
        System.out.println("Количество книг в библиотеке: " + books.size());

        while (true) {
            System.out.print("Хотите сохранить данные перед выходом? (да/нет): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("да") || answer.equals("д")) {
                saveSerializedData();
                System.out.println("До свидания!");
                break;
            } else if (answer.equals("нет") || answer.equals("н")) {
                System.out.println("Данные не сохранены. До свидания!");
                break;
            } else {
                System.out.println("Пожалуйста, введите 'да' или 'нет'");
            }
        }
    }

    // Метод для сериализации данных (сохранение в бинарный файл)
    private static void saveSerializedData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE))) {
            oos.writeObject(books);
            System.out.println("Данные успешно сохранены.");
            System.out.println("Сохранено книг: " + books.size());
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    // Автоматическая загрузка данных при запуске
    private static void autoLoadSerializedData() {
        File file = new File(SERIALIZATION_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE))) {
            @SuppressWarnings("unchecked") // аннотация для того, чтобы предупреждения не выводились
            List<Book> loadedBooks = (List<Book>) ois.readObject();
            books = loadedBooks;
            System.out.println("Автоматически загружено книг: " + books.size());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Не удалось загрузить сохраненные данные: " + e.getMessage());
        }
    }
}