/**
 * Author: Johnathan McAllister (McAdmin)
 * Date: 2026-07-02
 * Course: CEN 4025C
 * Professor: Dr. Mary Walauskis
 * <p>
 * Purpose:
 * - track a user's tasks on a ToDO list
 * <p>
 * Constraints:
 * - Use Hibernate
 */

package com.app.webserver.main;

import com.app.webserver.model.Task;
import com.app.webserver.model.User;
import com.app.webserver.util.JpaUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static com.app.webserver.util.DateUtil.parse;
import static com.app.webserver.util.PassUtil.verify;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    static User loggedOnUser = null;

    public static void main(String[] args) {

        showIntro();

        if (!JpaUtil.hasAny(User.class)) {
            System.out.println("No users found.");
            System.out.println("Create the administrator account.");
            createUser();
        }

        if (loggedOnUser == null) {
            login();
        }

        while (loggedOnUser != null){

            showMenu();
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addTask();
                case 2 -> editTask();
                case 3 -> deleteTask();
                case 4 -> listTasks();
                //ToDO: create menu option and function to addUser()
                case 0 -> {
                    System.out.println("Goodbye!");
                    logout();
                }
                default -> System.out.println("Invalid choice.");
            }
        }

    }

    public static void createUser() {
        User user = new User();

        System.out.print("Enter First Name: ");
        user.setFirstName(scanner.nextLine());

        System.out.print("Enter Last Name: ");
        user.setLastName(scanner.nextLine());

        System.out.print("Enter email address: ");
        user.setEmail(scanner.nextLine());

        System.out.print("Enter Password: ");
        user.setPassword(scanner.nextLine());

        //ToDo: Add Role attribute for admin to determine if the option to addUsers is present

        User save = JpaUtil.save(user);
    }


    public static void login() {
        User user = null;

        while (user == null) {
            System.out.print("Enter your email: ");
            String email = scanner.nextLine().trim();

            Optional<User> userOptional =
                    JpaUtil.findOneByField(User.class, "email", email);

            if (userOptional.isEmpty()) {
                System.out.println("Invalid email or password.");
            } else {
                user = userOptional.get();
            }
        }

        boolean validPassword = false;

        while (!validPassword) {
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            if (verify(password, user.getPassword())) {
                loggedOnUser = user;
                validPassword = true;
                System.out.println("Login successful.");
            } else {
                System.out.println("Wrong password.");
            }
        }
    }

    public static void logout() {
        loggedOnUser = null;
    }

    public static void showMenu() {
        System.out.println("\n⠛⠛⠛⠛ M E N U ⠛⠛⠛⠛");
        System.out.println("1. Add Task");
        System.out.println("2. Edit Task");
        System.out.println("3. Delete Task");
        System.out.println("4. List Tasks");
        //ToDO: create menu option and function to addUser()
        System.out.println("0. Log Out");
        System.out.print("Choose an option: ");
    }

    public static void addTask() {
        boolean valid = false;
        Task task = new Task();

        task.setUser(loggedOnUser);

        System.out.print("Enter task title: ");
        task.setTitle(scanner.nextLine());

        System.out.print("Enter task description: ");
        task.setDescription(scanner.nextLine());

        System.out.print("Enter priority (Numbers 1-10): ");
        task.setPriority(Integer.parseInt(scanner.nextLine()));

        while (!valid) {
            try {
                System.out.print("Enter due date (examples: 2026-06-13, 06/13/2026, 2026-06-13 14:30, 06/13/2026 2:30 PM): ");

                task.setDueDate(parse(scanner.nextLine().trim()));

                valid = true;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        Task save = JpaUtil.save(task);

    }

    private static void editTitle(Task task) {
        System.out.println("Current title: " + task.getTitle());
        System.out.print("New title: ");
        task.setTitle(scanner.nextLine());
    }

    private static void editPriority(Task task) {
        System.out.println("Current priority: " + task.getPriority());
        System.out.print("New priority (1-10): ");
        task.setPriority(Integer.parseInt(scanner.nextLine()));
    }

    private static void editDescription(Task task) {
        System.out.println("Current description: " + task.getDescription());
        System.out.print("New description: ");
        task.setDescription(scanner.nextLine());
    }

    private static void editCompleted(Task task) {
        System.out.println("Current completed value: " + task.getCompleted());
        System.out.print("Completed? true/false: ");
        task.setCompleted(Boolean.parseBoolean(scanner.nextLine()));
    }

    private static void editDueDate(Task task) {
        System.out.println("Current due date: " + task.getDueDate());

        boolean valid = false;

        while (!valid) {
            try {
                System.out.print("New due date: ");
                task.setDueDate(parse(scanner.nextLine().trim()));
                valid = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void editAttr(Task task, String attrName) {
        switch (attrName) {
            case "title" -> editTitle(task);
            case "priority" -> editPriority(task);
            case "description" -> editDescription(task);
            case "duedate" -> editDueDate(task);
            case "completed" -> editCompleted(task);
            case "id", "user" -> System.out.println("This attribute cannot be modified.");
            default -> System.out.println("Invalid attribute.");
        }
    }

    private static boolean editMenu(Task task) {
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Save changes");
        System.out.println("2. Modify another attribute");
        System.out.println("3. Cancel changes");

        System.out.print("Choice: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> {
                JpaUtil.update(task);
                System.out.println("Task updated successfully.");
                return false;
            }
            case "2" -> {
                return true;
            }
            case "3" -> {
                System.out.println("Changes cancelled.");
                return false;
            }
            default -> {
                System.out.println("Invalid choice. Continuing edit mode.");
                return true;
            }
        }
    }

    public static void editTask() {
        boolean editing = true;

        Task task = selectTask();

        while (editing) {
            showTask(task);
            System.out.print("Enter attribute name to edit: ");
            String attrName = scanner.nextLine();
            editAttr(task, attrName);

            editing = editMenu(task);
        }
    }

    public static void deleteTask() {
        listTasks();

        System.out.print("Enter task number to delete: ");
        Task task = getTask(Integer.parseInt(scanner.nextLine()));

        if (task != null) {
            JpaUtil.delete(Task.class, task.getId());
        } else {
            System.out.println("Invalid task number.");
        }


    }

    public static Task selectTask() {
        Task task = null;

        do {
            listTasks();

            System.out.print("Enter task number to edit: ");
            try {
                task = getTask(Integer.parseInt(scanner.nextLine()));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

        } while (task == null);

        return task;
    }

    public static List<Task> getTasks() {
        return JpaUtil.findByField(Task.class, "user", loggedOnUser);
    }

    /*public static Task getTask(int index) {
     *   return getTasks().get(index);
     *  }
    **/

    public static Task getTask(int taskNumber) {
        List<Task> tasks = getTasks();

        if (taskNumber < 0 || taskNumber >= tasks.size()) {
            return null;
        }

        return tasks.get(taskNumber);
    }

    public static void showTask(Task task) {
        String tableCol = "%-40s %-25s %-10s %-20s %-50s %-25s %-10s%n";
        String[] headers = {
                "ID",
                "Title",
                "Priority",
                "Due Date",
                "Description",
                "User",
                "Completed"
        };

        System.out.printf(tableCol,(Object[]) headers);

        System.out.printf(
                tableCol,
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.getDueDate(),
                task.getDescription(),
                loggedOnUser.getEmail(),
                task.getCompleted()
        );
    }

    public static void listTasks() {

        for (int i = 0; i < getTasks().size(); i++) {
            Task task = getTask(i);
            System.out.println("Task " + i + ": " + task.getTitle());
        }

    }

    private static void showIntro() {
        System.out.println("███████╗██╗ ██████╗ ███████╗███╗   ██╗");
        System.out.println("██╔════╝██║██╔════╝ ██╔════╝████╗  ██║");
        System.out.println("█████╗  ██║██║  ███╗█████╗  ██╔██╗ ██║");
        System.out.println("██╔══╝  ██║██║   ██║██╔══╝  ██║╚██╗██║");
        System.out.println("███████╗██║╚██████╔╝███████╗██║ ╚████║");
        System.out.println("╚══════╝╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝");

        System.out.println();

        System.out.println("   ⠀⠀⠀⠀⠀⠀⠀⢀⣀⣠⣤⣤⣴⣦⣤⣤⣄⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀");
        System.out.println("⠀⠀⠀⠀⠀⠀⢀⣤⣾⣿⣿⣿⣿⠿⠿⠿⠿⣿⣿⣿⣿⣶⣤⡀⠀⠀⠀⠀⠀⠀");
        System.out.println("⠀⠀⠀⠀⣠⣾⣿⣿⡿⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀⠉⠛⢿⣿⣿⣶⡀⠀⠀⠀⠀");
        System.out.println("⠀⠀⠀⣴⣿⣿⠟⠁⠀⠀⠀⣶⣶⣶⣶⡆⠀⠀⠀⠀⠀⠀⠈⠻⣿⣿⣦⠀⠀⠀");
        System.out.println("⠀⠀⣼⣿⣿⠋⠀⠀⠀⠀⠀⠛⠛⢻⣿⣿⡀⠀⠀⠀⠀⠀⠀⠀⠙⣿⣿⣧⠀⠀");
        System.out.println("⠀⢸⣿⣿⠃⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⣿⣷⠀⠀⠀⠀⠀⠀⠀⠀⠸⣿⣿⡇⠀");
        System.out.println("⠀⣿⣿⡿⠀⠀⠀⠀⠀⠀⠀⠀⢀⣾⣿⣿⣿⣇⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⣿⠀");
        System.out.println("⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⢠⣿⣿⡟⢹⣿⣿⡆⠀⠀⠀⠀⠀⠀⠀⣹⣿⣿⠀");
        System.out.println("⠀⣿⣿⣷⠀⠀⠀⠀⠀⠀⣰⣿⣿⠏⠀⠀⢻⣿⣿⡄⠀⠀⠀⠀⠀⠀⣿⣿⡿⠀");
        System.out.println("⠀⢸⣿⣿⡆⠀⠀⠀⠀⣴⣿⡿⠃⠀⠀⠀⠈⢿⣿⣷⣤⣤⡆⠀⠀⣰⣿⣿⠇⠀");
        System.out.println("⠀⠀⢻⣿⣿⣄⠀⠀⠾⠿⠿⠁⠀⠀⠀⠀⠀⠘⣿⣿⡿⠿⠛⠀⣰⣿⣿⡟⠀⠀");
        System.out.println("⠀⠀⠀⠻⣿⣿⣧⣄⠀⠀⠀⠀⠀⠀ ⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⣿⣿⠏⠀⠀⠀");
        System.out.println("⠀⠀⠀⠀⠈⠻⣿⣿⣷⣤⣄⡀⠀⠀⠀⠀⠀⠀⢀⣠⣴⣾⣿⣿⠟⠁⠀⠀⠀⠀");
        System.out.println("⠀⠀⠀⠀⠀⠀⠈⠛⠿⣿⣿⣿⣿⣿⣶⣶⣿⣿⣿⣿⣿⠿⠋⠁⠀⠀⠀⠀⠀⠀");
        System.out.println("⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠛⠛⠛⠛⠛⠛⠉⠉⠀⠀");

        System.out.println();

        System.out.println("⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛");
        System.out.println("⠀⠀E I G E N   T O D O   A P P⠀⠀");
        System.out.println("⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛");
    }
}
